package ons;

/**
 * Methods to treat the outgoing of a ons.Flow object.
 * 
 * @author andred
 */
public class OrdinaryEvent extends Event{
    
    private String description;
    
    /**
     * Creates a new ons.OrdinaryEvent object.
     * 
     * @param description the description of this ordinary event
     */
    public OrdinaryEvent(String description) {
        this.description = description;
    }

    /**
     * Retrieves the description of the ons.OrdinaryEvent object.
     * 
     * @return the ons.OrdinaryEvent's description attribute
     */
    public String getDescription() {
        return description;
    }
    
    /**
     * Prints all information related to the ordinary event.
     * 
     * @return string containing all the values of the ordinary event.
     */
    public String toString() {
        return "Ordinary event-" + "Description: " + description;
    }
}
