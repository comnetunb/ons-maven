package ons.ra.ML;

import ai.onnxruntime.OrtException;
import ons.EONLightPath;
import ons.EONLink;
import ons.LightPath;
import ons.MyStatistics;
import ons.ra.*;

import ons.EONPhysicalTopology;
import ons.Flow;
import ons.Modulation;
import ons.tools.Dl4jClassifier;
import ons.tools.OnnxClassifier;
import ons.util.WeightedGraph;
import ons.util.YenKSP;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.TreeSet;

import ons.ra.RA;

/**
 * The proposed m-Adap by Xin Wan in this RSA solver.
 * @author lucas
 */
public class FF_Classifier implements RA {

    private ControlPlaneForRA cp;
    private WeightedGraph graph;
    private MyStatistics st = MyStatistics.getMyStatisticsObject();
    private final Dl4jClassifier dl4jClassifier = new Dl4jClassifier();
    private final OnnxClassifier onnxClassifier = new OnnxClassifier();

    @Override
    public void simulationInterface(ControlPlaneForRA cp) {
        this.cp = cp;
        this.graph = cp.getPT().getWeightedGraph();

        try {
            this.dl4jClassifier.load(FF_Classifier.class.getClassLoader().getResourceAsStream("ML/model.h5"));
            this.onnxClassifier.load(FF_Classifier.class.getClassLoader().getResourceAsStream("ML/model.onnx"));
        } catch (Exception ex) {
            System.exit(1);
        }
    }

    @Override
    public void setModulation(int modulation) {
    }

    @Override
    public void simulationEnd() {
    }

    @Override
    public void flowArrival(Flow flow) {
        int[] nodes;
        int[] links;
        long id;
        LightPath[] lps = new LightPath[1];
        double distanceLightpath;
        int modulation = EONPhysicalTopology.getMaxModulation();
        int requiredSlots;

        boolean afs = false;
        boolean frag = false;
        boolean qotn = false;
        boolean qoto = false;
        double snr;

        int ksp = 5;
        // k-Shortest Paths routing

        //System.out.println(flow.getID());

        // Try existent lightpaths first (Grooming)
//        lps[0] = getLeastLoadedLightpath(flow);
//        if (lps[0] instanceof ons.LightPath) {
//            if (cp.acceptFlow(flow.getID(), lps)) {
//                return;
//            }
//        }

        var state = getLinksState();

        var startTime = System.nanoTime();
        var rate = this.dl4jClassifier.predict(state);
        var endTime = System.nanoTime();
        var duration = (endTime - startTime) / 1000;
        System.out.println(String.format("Current alg has rate %d according to DL4J, took %d us", rate, duration));

        try {
            startTime = System.nanoTime();
            rate = this.onnxClassifier.predict(state);
            endTime = System.nanoTime();
            duration = (endTime - startTime) / 1000;
            System.out.println(String.format("Current alg has rate %d according to ONNX, took %d us", rate, duration));
        } catch (OrtException e) {
            e.printStackTrace();
        }

        ArrayList<Integer>[] kpaths = YenKSP.kShortestPaths(graph, flow.getSource(), flow.getDestination(), ksp);

        while (true) {

            for (int k = 0; k < kpaths.length; k++) {

                nodes = route(kpaths, k);
                // If no possible path found, block the call
                if (nodes.length == 0 || nodes == null) {
                    cp.blockFlow(flow.getID());
                    return;
                }

                // Create the links vector
                links = new int[nodes.length - 1];
                for (int j = 0; j < nodes.length - 1; j++) {
                    links[j] = cp.getPT().getLink(nodes[j], nodes[j + 1]).getID();
                }

                //The modulation scheme
                distanceLightpath = 0;
                for (int i = 0; i < links.length; i++) {
                    distanceLightpath += ((EONLink) cp.getPT().getLink(links[i])).getWeight();
                }
                if (distanceLightpath <= (double) Modulation.getModulationReach(modulation)) {

                    // First-Fit spectrum assignment in BPSK ons.Modulation
                    requiredSlots = Modulation.convertRateToSlot(flow.getRate(), ((EONPhysicalTopology) cp.getPT()).getSlotSize(), modulation);

                    int[] firstSlot;
                    if (!hasSlotAvailable(links, requiredSlots)) {
                        afs = true;
                    }
                    firstSlot = ((EONPhysicalTopology) cp.getPT()).getAvaiableSlotsRoute(links, requiredSlots);
                    if (firstSlot != null) {
                        frag = true;
                    }
                    for (int j = 0; j < firstSlot.length; j++) {
                        // Now you create the lightpath to use the createLightpath VT
                        //Relative index modulation: BPSK = 0; QPSK = 1; 8QAM = 2; 16QAM = 3;
                        EONLightPath lp = cp.createCandidateEONLightPath(flow.getSource(), flow.getDestination(), links,
                                firstSlot[j], (firstSlot[j] + requiredSlots - 1), modulation);
                        snr = calculateSNR(lp);
                        double snrThreshold = Modulation.getModulationSNRthreshold(lp.getModulation());
                        if (snr > snrThreshold) {
                            qotn = true;
                        }
                        if ((id = cp.getVT().createLightpath(lp)) >= 0) {
                            // Single-hop routing (end-to-end lightpath)
                            lps[0] = cp.getVT().getLightpath(id);
                            cp.acceptFlow(flow.getID(), lps);
                            return;
                        }
                    }
                }

            }
            modulation--;
            if (modulation == -1) {
                // Block the call
                cp.blockFlow(flow.getID());
                if(afs) {
                    st.blockAFS(flow);
                } else {
                    if(frag) {
                        st.blockFrag(flow);
                    } else {
                        if(qotn) {
                            st.blockQoTN(flow);
                        } else {
                            st.blockQoTO(flow);
                        }
                    }
                }
                return;
            }
        }
    }

    @Override
    public void flowDeparture(long id) {
    }

    private int[] route(ArrayList<Integer>[] kpaths, int k) {
        if (kpaths[k] != null) {
            int[] path = new int[kpaths[k].size()];
            for (int i = 0; i < path.length; i++) {
                path[i] = kpaths[k].get(i);
            }
            return path;
        } else {
            return null;
        }
    }

    private LightPath getLeastLoadedLightpath(Flow flow) {
        long abw_aux, abw = 0;
        LightPath lp_aux, lp = null;

        // Get the available lightpaths
        TreeSet<LightPath> lps = cp.getVT().getAvailableLightpaths(flow.getSource(),
                flow.getDestination(), flow.getRate());
        if (lps != null && !lps.isEmpty()) {
            while (!lps.isEmpty()) {
                lp_aux = lps.pollFirst();
                // Get the available bandwidth
                abw_aux = cp.getVT().getLightpathBWAvailable(lp_aux.getID());
                if (abw_aux > abw) {
                    abw = abw_aux;
                    lp = lp_aux;
                }
            }
        }
        return lp;
    }

    private double calculateSNR(EONLightPath lp) {
        return ((EONPhysicalTopology) cp.getPT()).getPI().computeSNRlightpath(lp);
    }

    private boolean hasSlotAvailable(int[] links, int requiredSlots) {
        for (int i = 0; i < links.length; i++) {
            if(!((EONLink) cp.getPT().getLink(links[i])).hasSlotsAvaiable(requiredSlots)) {
                return false;
            }
        }
        return true;
    }

    private float[][] getLinksState() {
        var pt = this.cp.getPT();
        var links = pt.getNumLinks();

        assert links == 86;

        var state = new float[1][86*320];
        for (int i = 0; i < links; i++) {
            var link = (EONLink) pt.getLink(i);
            var cores = link.getCores();

            assert cores.length == 1;
            var core = cores[0];
            var slots = core.getSlots();

            assert slots.length == 320;

            for (int j = 0; j < 320; j++) {
                state[0][320*i + j] = slots[j];
            }
        }

        return state;
    }
}
