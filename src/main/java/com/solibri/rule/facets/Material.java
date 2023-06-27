package com.solibri.rule.facets;

import com.solibri.smc.api.filter.ComponentFilter;
import de.buildingsmart.ids.RequirementsType;
import de.buildingsmart.ids.SpecificationType;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Material implements FacetBase {

    private final SpecificationType specification;

    public Material(SpecificationType specification) {
        this.specification = specification;
    }

    @Override
    public ComponentFilter setApplicability() {
        return component -> {
            // Check if the IDS specification has a Material facet
            if (specification.getApplicability().getMaterial() == null) {
                // If not, return true to avoid any filtering based on Material
                return true;
            }
            List<com.solibri.smc.api.model.Material> solMaterials = component.getMaterials();

            String idsApplicabilityMaterial = specification.getApplicability().getMaterial().getValue().getSimpleValue();

            // Check if the IDS-defined Material Name exists at the current component
            return solMaterials.stream()
                    .anyMatch(material -> {
                        assert specification.getApplicability().getMaterial() != null;
                        return material.getName().contains(idsApplicabilityMaterial);
                    });
        };
    }

    @Override
    public ComponentFilter setRequirement() {
        return component -> {
            // Check if the IDS specification has a Material facet
            if (getIdsRequirementMaterial(specification) == null) {
                // If not, return true to avoid any filtering based on Material
                return true;
            }
            // Get the material from the component
            List<com.solibri.smc.api.model.Material> solMaterials = component.getMaterials();
            // Get the material from the IDS specification
            RequirementsType.Material idsRequirementMaterial = getIdsRequirementMaterial(specification);

            // Check if the IDS-defined Material exists at the current component
            return solMaterials.stream()
                    .anyMatch(material -> {
                        assert idsRequirementMaterial != null;
                        return material.getName().contains(idsRequirementMaterial.getValue().getSimpleValue());
                    });
        };
    }

    // New method to fetch IDS Requirement Material
    private RequirementsType.Material getIdsRequirementMaterial(SpecificationType specification) {
        if (specification.getRequirements() == null ||
                specification.getRequirements().getEntityAndPartOfAndClassification() == null) {
            return null;
        }

        Object entity = specification.getRequirements().getEntityAndPartOfAndClassification().stream()
                .filter(obj -> obj instanceof RequirementsType.Material)
                .findFirst()
                .orElse(null);

        return (entity instanceof RequirementsType.Material) ? (RequirementsType.Material) entity : null;
    }
}
