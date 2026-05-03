package com.siliconSpatula.appliance;

import com.siliconSpatula.model.ApplianceType;
import com.siliconSpatula.model.MenuItem;

/**
 * DrinkDispenser – handles DRINK_DISPENSER-type items (all beverages).
 */
public class DrinkDispenser implements IAppliance {

    @Override
    public boolean canProcess(MenuItem item) {
        return item.getApplianceType() == ApplianceType.DRINK_DISPENSER;
    }

    @Override
    public void process(MenuItem item) {
        // Dispense logic; timing handled by engine
    }

    @Override
    public String getApplianceName() { return "Drink Dispenser"; }
}
