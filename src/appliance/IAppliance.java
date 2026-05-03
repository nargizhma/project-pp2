package appliance;

import model.MenuItem;

/**
 * Interface for every kitchen appliance in The Silicon Spatula.
 *
 * Processor selection is done by calling canProcess() across all
 * appliances in a list – no instanceof checks, no if-else chains.
 */
public interface IAppliance {

    /**
     * Returns true if this appliance is capable of preparing the given item.
     * Decision is based on the item's ApplianceType, NOT its runtime class.
     */
    boolean canProcess(MenuItem item);

    /**
     * Prepares the item. Called only after canProcess() returns true.
     * Implementations may log internally but must NOT touch InventoryManager.
     */
    void process(MenuItem item);

    /** Human-readable appliance name shown in logs. */
    String getApplianceName();
}
