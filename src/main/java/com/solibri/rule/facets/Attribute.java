package com.solibri.rule.facets;

import com.solibri.rule.utils.Result;
import com.solibri.smc.api.filter.ComponentFilter;
import de.buildingsmart.ids.ApplicabilityType;
import de.buildingsmart.ids.AttributeType;
import de.buildingsmart.ids.RequirementsType;
import de.buildingsmart.ids.SpecificationType;

public class Attribute implements FacetBase {

//    public Attribute(SpecificationType specification) {
//        super(specification);
//    }

    @Override
    public void setApplicability(ApplicabilityType applicability) {

    }

    @Override
    public void setRequirement(RequirementsType requirement) {

    }

    @Override
    public ComponentFilter setFilter() {
        return null;
    }
}
