package appliance;

import model.ApplianceType;
import model.MenuItem;

/**
 * Oven – handles OVEN-type items (desserts: muffin cake, cheesecake).
 */
public class Oven implements IAppliance {

    @Override
    public boolean canProcess(MenuItem item) {
        return item.getApplianceType() == ApplianceType.OVEN;
    }

    @Override
    public void process(MenuItem item) {
        // Baking logic; timing handled by RestaurantEngine
    }

    @Override
    public String getApplianceName() { return "Oven"; }
}