package ons;

/**
 *
 * Methods to treat the incoming of a ons.Batch object.
 */
public class BatchArrivalEvent extends Event {

    private final Batch batch;

    /**
     * Create a new ons.BatchArrivalEvent object.
     * @param batch
     */
    public BatchArrivalEvent(Batch batch) {
        this.batch = batch;
    }

    /**
     * Retrives the ons.Batch attribute of the ons.BatchArrivalEvent object.
     *
     * @return the ons.BatchArrivalEvent's ons.Batch attribute
     */
    public Batch getBatch() {
        return this.batch;
    }

    /**
     * Retrives the size of ons.Batch or amount of flow inside one.
     *
     * @return the size of ons.BatchArrivalEvent's ons.Batch attribute
     */

    public int getBatchSize() {
        return this.batch.getSize();
    }

    /**
     * Prints all information related to the arriving ons.Batch.
     *
     * @return string containing all the values of the batch's parameters
     */
    @Override
    public String toString() {
        return "BatchArrival: " + batch.toString();
    }
}
