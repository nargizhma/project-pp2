package com.siliconSpatula.appliance;

import com.siliconSpatula.model.ApplianceType;
import com.siliconSpatula.model.MenuItem;

/**
 * Grill – processes all GRILL-type items (burgers, chicken, wraps).
 */
public class Grill implements IAppliance {

    @Override
    public boolean canProcess(MenuItem item) {
        return item.getApplianceType() == ApplianceType.GRILL;
    }

    @Override
    public void process(MenuItem item) {
        // Actual cooking is timed by RestaurantEngine; this confirms the action.
    }

    @Override
    public String getApplianceName() { return "Grill"; }
}
