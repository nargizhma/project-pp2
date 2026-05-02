package com.siliconSpatula.appliance;

import com.siliconSpatula.model.ApplianceType;
import com.siliconSpatula.model.MenuItem;

/**
 * AirPot – handles AIRPOT-type items (fries, desserts).
 */
public class AirPot implements IAppliance {

    @Override
    public boolean canProcess(MenuItem item) {
        return item.getApplianceType() == ApplianceType.AIRPOT;
    }

    @Override
    public void process(MenuItem item) {
        // Timing handled by engine
    }

    @Override
    public String getApplianceName() { return "AirPot"; }
}
