package ons;

/**
 * ons.Event objects have only one attribute: scheduled time.
 * It is the time slot the event is set to happen.
 */
public abstract class Event {
    
    private double time;
   
    /**
     * Sets a new time for the ons.Event to happen.
     * 
     * @param time new scheduled period
     */
    public void setTime(double time){
        this.time = time;
    }
    
    /**
     * Retrieves current scheduled time for a given ons.Event.
     * 
     * @return value of the ons.Event's time attribute
     */
    public double getTime() {
        return this.time;
    }
}
