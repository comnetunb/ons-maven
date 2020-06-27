package ons;

public class EONOXC extends OXC {

    private final int capacity;
    private final int modulations[];

    public EONOXC(int id, int groomingInputPorts, int groomingOutputPorts, int capacity, int[] modulations) {
        super(id, groomingInputPorts, groomingOutputPorts);
        this.capacity = capacity;
        this.modulations = modulations;
    }

    public int getCapacity() {
        return capacity;
    }

    public boolean hasModulation(int modulation) {
        return this.modulations[modulation] == 1;
    }

}
