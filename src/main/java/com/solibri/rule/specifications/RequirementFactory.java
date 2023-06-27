package com.solibri.rule.specifications;

import com.solibri.rule.facets.Material;
import com.solibri.rule.facets.Property;
import com.solibri.smc.api.filter.ComponentFilter;
import de.buildingsmart.ids.RequirementsType;
import de.buildingsmart.ids.SpecificationType;



public class RequirementFactory {

    public static ComponentFilter createComponentFilter(SpecificationType specification) {

        Property propertyFacet = new Property(specification);
        Material materialFacet = new Material(specification);

        ComponentFilter propertyFilter = propertyFacet.setRequirement();
        ComponentFilter materialFilter = materialFacet.setRequirement();

        boolean propertyExists = specification.getRequirements().getEntityAndPartOfAndClassification()
                .stream()
                .anyMatch(obj -> obj instanceof Property);

        boolean materialExists = specification.getRequirements().getEntityAndPartOfAndClassification()
                .stream()
                .anyMatch(obj -> obj instanceof Material);

        // if both facets are present
        if (propertyExists && materialExists) {
            return ComponentFilter.all(propertyFilter, materialFilter);  // use 'or' if the conditions should apply in disjunction
        }
        // if only Property facet is present
        else if (propertyExists) {
            return propertyFilter;
        }
        // if only Material facet is present
        else if (materialExists) {
            return materialFilter;
        }
        // if neither facets are present, we could return a filter that always passes
        else {
            return component -> true;
        }
    }
}