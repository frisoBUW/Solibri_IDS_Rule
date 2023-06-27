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
    public ComponentFilter setApplicability() {
        return null;
    }

    @Override
    public ComponentFilter setRequirement() {
        return null;
    }

}
