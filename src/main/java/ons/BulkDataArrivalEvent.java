package ons;

/**
 * Methods to treat the incoming of a ons.BulkData object.
 *
 */
public class BulkDataArrivalEvent extends Event {

    private final BulkData bulk;

    /**
     * Creates a new ons.BulkDataArrivalEvent object.
     *
     * @param bd the arriving Bulk Data request
     */
    public BulkDataArrivalEvent(BulkData bd) {
        this.bulk = bd;
    }

    /**
     * Retrives the bdUnicast attribute of the ons.BulkDataArrivalEvent object.
     *
     * @return the ons.BulkDataArrivalEvent's bd request attribute
     */
    public BulkData getBulkData() {
        return this.bulk;

    }

    /**
     * Prints all information related to the arriving ons.BulkData request.
     *
     * @return string containing all the values of the ons.BulkData request's
     * parameters
     */
    @Override
    public String toString() {
        return "Arrival: " + bulk.toString();
    }

}
