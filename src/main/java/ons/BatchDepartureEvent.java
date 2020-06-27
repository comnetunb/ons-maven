package ons;

/**
 * Methods to treat the outgoing of a ons.Flow object.
 * 
 * @author andred
 */
public class BatchDepartureEvent extends Event{

    private long id;
    
    /**
     * Creates a new ons.FlowDepartureEvent object.
     * 
     * @param id unique identifier of the outgoing flow
     */
    public BatchDepartureEvent(long id) {
        this.id = id;
    }
    
    /**
     * Retrieves the identifier of the ons.FlowDepartureEvent object.
     * 
     * @return the ons.FlowDepartureEvent's id attribute
     */
    public long getID() {
        return this.id;
    }
    
    /**
     * Prints all information related to the outgoing flow.
     * 
     * @return string containing all the values of the flow's parameters
     */
    public String toString() {
        return "Departure: "+Long.toString(id);
    }
}
