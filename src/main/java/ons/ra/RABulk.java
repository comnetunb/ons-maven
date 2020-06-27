package ons.ra;

import ons.Batch;
import ons.BulkData;

/**
 *
 * @author lucas
 */
public interface RABulk extends RA {
    
    public void bulkDataArrival(BulkData bulk);
    
    public void batchArrival(Batch batch);
    
    public void bulkDeparture(long id);
    
    public void batchDeparture(long id);
    
}
