package appliance;

import model.ApplianceType;
import model.MenuItem;

/**
 * SauceDispenser – handles SAUCE_DISPENSER-type items (dipping sauces).
 */
public class SauceDispenser implements IAppliance {

    @Override
    public boolean canProcess(MenuItem item) {
        return item.getApplianceType() == ApplianceType.SAUCE_DISPENSER;
    }

    @Override
    public void process(MenuItem item) {
        // Sauce dispensed; timing handled by engine
    }

    @Override
    public String getApplianceName() { return "Sauce Dispenser"; }
}
