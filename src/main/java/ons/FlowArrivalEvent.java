package ons;

/**
 * Methods to treat the incoming of a ons.Flow object.
 * 
 * @author andred
 */
public class FlowArrivalEvent extends Event {
	
    private Flow flow;
    
    /**
     * Creates a new ons.FlowArrivalEvent object.
     * 
     * @param flow the arriving flow
     */
    public FlowArrivalEvent(Flow flow) {
        this.flow = flow;
    }
    
    /**
     * Retrives the flow attribute of the ons.FlowArrivalEvent object.
     * 
     * @return the ons.FlowArrivalEvent's flow attribute
     */
    public Flow getFlow() {
        return this.flow;
    }
    
    /**
     * Prints all information related to the arriving flow.
     * 
     * @return string containing all the values of the flow's parameters
     */
    public String toString() {
        return "Arrival: "+flow.toString();
    }
}
