package com.solibri.rule.facets;

import com.solibri.smc.api.filter.ComponentFilter;
import de.buildingsmart.ids.ApplicabilityType;
import de.buildingsmart.ids.RequirementsType;
import de.buildingsmart.ids.SpecificationType;

public interface FacetBase {
//    protected SpecificationType specification;
//    protected static ComponentFilter componentFilter;
//    public FacetBase(SpecificationType specification) {
//        this.specification = specification;
//    }

//    public static ComponentFilter getComponentFilter() {
//        return componentFilter;
//    }

    public ComponentFilter setApplicability();
    public ComponentFilter setRequirement();
}
