package ons;

/**
 *
 * @author lucasrc
 */
public class EONLightPath extends LightPath {

    private final int firstSlot;
    private final int lastSlot;
    /**
    * Relative index modulation:
    * BPSK = 0
    * QPSK = 1
    * 8QAM = 2
    * 16QAM = 3
    */
    private final int modulation;
    private final int bw; //bandwidth in Mbps 
    private int bwAvailable; //bandwidth available in Mbps 
    private int[] cores; //for SDM-EON
   
    public EONLightPath(long id, int src, int dst, int[] links, int firstSlot, int lastSlot, int modulation, int slotSize) {
        super(id, src, dst, links);
        this.firstSlot = firstSlot;
        this.lastSlot = lastSlot;
        this.modulation = modulation;
        this.bw = Modulation.convertSlotToRate(((lastSlot - firstSlot) + 1), slotSize, modulation);
        this.bwAvailable = Modulation.convertSlotToRate(((lastSlot - firstSlot) + 1), slotSize, modulation);
        this.cores = new int[links.length];
        for (int i = 0; i < cores.length; i++) {
            cores[i] = 0;
        }
    }
    
    public EONLightPath(long id, int src, int dst, int[] links, int[] cores, int firstSlot, int lastSlot, int modulation, int slotSize) {
        super(id, src, dst, links);
        this.firstSlot = firstSlot;
        this.lastSlot = lastSlot;
        this.modulation = modulation;
        this.bw = Modulation.convertSlotToRate(((lastSlot - firstSlot) + 1), slotSize, modulation);
        this.bwAvailable = Modulation.convertSlotToRate(((lastSlot - firstSlot) + 1), slotSize, modulation);
        if(cores.length != links.length) {
            throw (new IllegalArgumentException("cores length is different from links length"));
        }
        this.cores = new int[cores.length];
        System.arraycopy(cores, 0, this.cores, 0, cores.length);
    }
    
    public EONLightPath(long id, int src, int dst, int[] links, int firstSlot, int lastSlot, int modulation, int slotSize, String typeProtection) {
        super(id, src, dst, links);
        this.firstSlot = firstSlot;
        this.lastSlot = lastSlot;
        this.modulation = modulation;
        this.bw = Modulation.convertSlotToRate(((lastSlot - firstSlot) + 1), slotSize, modulation);
        this.bwAvailable = Modulation.convertSlotToRate(((lastSlot - firstSlot) + 1), slotSize, modulation);
        this.cores = new int[links.length];
        for (int i = 0; i < cores.length; i++) {
            cores[i] = 0;
        }
        this.typeProtection = getTypeProtection(typeProtection);
    }
    
    public EONLightPath(long id, int src, int dst, int[] links, int firstSlot, int lastSlot, int modulation, int slotSize, int[] cores, String typeProtection) {
        super(id, src, dst, links);
        this.firstSlot = firstSlot;
        this.lastSlot = lastSlot;
        this.modulation = modulation;
        this.bw = Modulation.convertSlotToRate(((lastSlot - firstSlot) + 1), slotSize, modulation);
        this.bwAvailable = Modulation.convertSlotToRate(((lastSlot - firstSlot) + 1), slotSize, modulation);
        this.cores = new int[cores.length];
        if(cores.length != links.length) {
            throw (new IllegalArgumentException("cores length is different from links length"));
        }
        System.arraycopy(cores, 0, this.cores, 0, cores.length);
        this.typeProtection = getTypeProtection(typeProtection);
    }

    public int getFirstSlot() {
        return firstSlot;
    }

    public int getLastSlot() {
        return lastSlot;
    }
    
    public int[] getCores() {
        return cores;
    }
    
    /**
     * Retrieves the number of cores in this link
     * @return the number of cores in this link
     */
    public int getNumCores() {
        return cores.length;
    }
    
    /**
     * Returns the number of slots containing the lightpath.
     * @return the number of slots in this lightpath
     */
    public int getSlots(){
        return lastSlot - firstSlot + 1;
    }

    public int getModulation() {
        return modulation;
    }

    public int getBwAvailable() {
        return bwAvailable;
    }
    
    public int getBw() {
        return bw;
    }
    
    /**
     * Add a ons.Flow in the Lightpath
     * @param bw the Mbps value
     */
    public void addFlowOnLightPath(int bw) {
        if (bw > this.bwAvailable){
            throw (new IllegalArgumentException());
        } else {
            this.bwAvailable = this.bwAvailable - bw;
        }
    }
    
    /**
     * Remove a ons.Flow on the Lighpath
     * @param bw the Mbps value
     */
    public void removeFlowOnLightPath(int bw) {
        if (bw > this.bw){
            //For floating-point rounding problems
            if(Math.abs(bw - this.bw) > 1) {
                throw (new IllegalArgumentException());
            } else {
                this.bwAvailable = this.bw;
            }
        } else {
            this.bwAvailable = this.bwAvailable + bw;
        }
    }
    
    @Override
    public String toString() {
        String lightpath = Long.toString(id) + "; " + Integer.toString(src) + " " + Integer.toString(dst) + "; ";
        for (int i = 0; i < links.length; i++) {
            lightpath += Integer.toString(links[i]) + " (" + Integer.toString(firstSlot) + "->" + Integer.toString(lastSlot) + ") ";
        }
        lightpath += "[" + Integer.toString(getBwAvailable()) + "] BKPs-(";
        for (Long bkp : getLpBackup()) {
            lightpath += Long.toString(bkp) + ";";
        }
        lightpath += ") ";
        lightpath += "Mod-[" + Modulation.getModulationName(modulation) + "]";
        if(isBackup()) lightpath += " [BACKUP]";
        return lightpath;
    }
    
    @Override
    public String toTrace() {
        String lightpath = Long.toString(id) + " " + Integer.toString(src) + " " + Integer.toString(dst) + " " + Modulation.getModulationName(modulation) + " ";
        for (int i = 0; i < links.length; i++) {
            lightpath += Integer.toString(links[i]) + " (" + Integer.toString(firstSlot) + "->" + Integer.toString(lastSlot) + ") ";
        }
        return lightpath;
    }

}
