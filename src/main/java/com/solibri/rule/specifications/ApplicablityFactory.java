package com.solibri.rule.specifications;

import com.solibri.rule.facets.Material;
import com.solibri.rule.facets.Property;
import com.solibri.smc.api.filter.ComponentFilter;
import de.buildingsmart.ids.SpecificationType;

public class ApplicablityFactory {

    public static ComponentFilter createComponentFilter(SpecificationType specification) {

        Property propertyFacet = new Property(specification);
        Material materialFacet = new Material(specification);

        ComponentFilter propertyFilter = propertyFacet.setApplicability();
        ComponentFilter materialFilter = materialFacet.setApplicability();

        // if both facets are present
        if (specification.getApplicability().getProperty() != null
                && specification.getApplicability().getMaterial() != null) {
            return ComponentFilter.all(propertyFilter, materialFilter);  // use 'or' if the conditions should apply in disjunction
        }
        // if only Property facet is present
        else if (specification.getApplicability().getProperty() != null) {
            return propertyFilter;
        }
        // if only Material facet is present
        else if (specification.getApplicability().getMaterial() != null) {
            return materialFilter;
        }
        // if neither facets are present, we could return a filter that always passes
        else {
            return component -> true;
        }
    }
}

