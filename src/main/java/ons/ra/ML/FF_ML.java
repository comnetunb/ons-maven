package ons.ra.ML;

import ons.EONLightPath;
import ons.EONLink;
import ons.EONPhysicalTopology;
import ons.Flow;
import ons.LightPath;
import ons.Modulation;
import ons.MyStatistics;
import ons.ra.*;

import ons.util.WeightedGraph;
import ons.util.YenKSP;
import java.util.ArrayList;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;

import ons.tools.Classifier;

/**
 * The proposed m-Adap by Xin Wan in this RSA solver.
 * Esse algoritmo usa o classificador do Guilherme na solução do SNR
 *
 * @author lucas
 */
public class FF_ML implements RA {

    private ControlPlaneForRA cp;
    private WeightedGraph graph;
    private MyStatistics st = MyStatistics.getMyStatisticsObject();
    private static Classifier classifier;

    @Override
    public void simulationInterface(ControlPlaneForRA cp) {
        this.cp = cp;
        this.graph = cp.getPT().getWeightedGraph();
        try {
            classifier = new Classifier();
        } catch (Exception ex) {
            Logger.getLogger(FF_ML.class.getName()).log(Level.SEVERE, null, ex);
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

        //System.out.println(flow.getID());
        
        EONLightPath lp = null;

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

        int ksp = 3;
        // k-Shortest Paths routing   

        // Try existent lightpaths first (Grooming)
        /*
        lps[0] = getLeastLoadedLightpath(flow);
        if (lps[0] instanceof ons.LightPath) {
            if (cp.acceptFlow(flow.getID(), lps)) {
                return;
            }
        }
        */

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

                // First-Fit spectrum assignment in BPSK ons.Modulation
                requiredSlots = Modulation.convertRateToSlot(flow.getRate(), ((EONPhysicalTopology) cp.getPT()).getSlotSize(), modulation);

                lp = cp.createCandidateEONLightPath(flow.getSource(), flow.getDestination(), links,
                        0, (0 + requiredSlots - 1), modulation);

                if (SNRVerify(lp)) {
                    int[] firstSlot;
                    if (!afs && !hasSlotAvailable(links, requiredSlots)) {
                        afs = true;
                    }
                    firstSlot = ((EONPhysicalTopology) cp.getPT()).getAvaiableSlotsRoute(links, requiredSlots);
                    if (!frag && firstSlot != null) {
                        frag = true;
                    }
                    for (int j = 0; j < firstSlot.length; j++) {
                        // Now you create the lightpath to use the createLightpath VT
                        //Relative index modulation: BPSK = 0; QPSK = 1; 8QAM = 2; 16QAM = 3;
                        lp = cp.createCandidateEONLightPath(flow.getSource(), flow.getDestination(), links,
                                firstSlot[j], (firstSlot[j] + requiredSlots - 1), modulation);
                        
                        snr = calculateSNR(lp);
                        
                        if (!Modulation.QoTVerify(modulation, snr)) {
                            qotn = true;
                        } else {
                            if ((id = cp.getVT().createLightpath(lp)) >= 0) {
                                lps[0] = cp.getVT().getLightpath(id);
                                cp.acceptFlow(flow.getID(), lps);
                                return;
                            }
                        }
                    }
                }
            }
            modulation--;
            if (modulation == -1) {
                cp.blockFlow(flow.getID());
                if (afs) {
                    st.blockAFS(flow);
                } else {
                    if (frag) {
                        st.blockFrag(flow);
                    } else {
                        if (qotn) {
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
            if (!((EONLink) cp.getPT().getLink(links[i])).hasSlotsAvaiable(requiredSlots)) {
                return false;
            }
        }
        return true;
    }

    private boolean SNRVerify(EONLightPath lp) {
        if (lp == null) {
            return false;
        }
        
        double[] input = new double[7];
        
        int quantSlotsRequeridos = lp.getLastSlot() - lp.getFirstSlot() + 1;

        int SumNs = 0; //somatorio de spans
        double tamRota = 0; //tamanho da rota
        int sumViz = 0; //somatorio de vizinhos
        int MaiorNrVizLink = 0; //maior numero de vizinhos dos links

        for (int l = 0; l < lp.getLinks().length; l++) {
            double weight = cp.getPT().getLink(lp.getLinks()[l]).getWeight();
            tamRota += weight;
            double Ns = (int) Math.ceil(weight / cp.getPT().getSpanSize());
            SumNs += Ns;
            int viz = cp.getVT().getLightpathsInLink(lp.getLinks()[l]).size();
            sumViz += viz;
            if (viz > MaiorNrVizLink) {
                MaiorNrVizLink = viz;
            }
        }
        double taxa = ((quantSlotsRequeridos * EONPhysicalTopology.getSlotSize()) * (lp.getModulation() + 1)) / 1000;
        int Modulacao = lp.getModulation();
        int nLinks = lp.getLinks().length;

        input[0] = taxa;
        input[1] = Modulacao;
        input[2] = nLinks;
        input[3] = SumNs;
        input[4] = tamRota;
        input[5] = sumViz;
        input[6] = MaiorNrVizLink;

        return classifier.classify(input) != 0;
    }
}
