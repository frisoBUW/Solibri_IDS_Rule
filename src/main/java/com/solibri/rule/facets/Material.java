package com.solibri.rule.facets;

import com.solibri.rule.utils.Result;
import com.solibri.smc.api.filter.ComponentFilter;
import de.buildingsmart.ids.ApplicabilityType;
import de.buildingsmart.ids.RequirementsType;
import de.buildingsmart.ids.SpecificationType;

import java.util.List;

public class Material implements FacetBase {

    private final SpecificationType specification;

    public Material(SpecificationType specification) {
        this.specification = specification;
    }

    @Override
    public void setApplicability(ApplicabilityType applicability) {

    }

    @Override
    public void setRequirement(RequirementsType requirement) {

    }

    @Override
    public ComponentFilter setFilter() {
        return component -> {
            List<com.solibri.smc.api.model.Material> materials = component.getMaterials();
            return materials.stream()
                    .anyMatch(material -> material.getName().contains(specification.getApplicability().getMaterial().getValue().getSimpleValue()));
        };
    }
}
