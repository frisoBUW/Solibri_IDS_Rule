package com.solibri.rule.facets;

import com.solibri.rule.utils.Result;
import com.solibri.smc.api.filter.ComponentFilter;
import com.solibri.smc.api.ifc.IfcEntityType;
import com.solibri.smc.api.model.ComponentType;
import com.solibri.smc.api.model.Material;
import de.buildingsmart.ids.ApplicabilityType;
import de.buildingsmart.ids.EntityType;
import de.buildingsmart.ids.RequirementsType;
import de.buildingsmart.ids.SpecificationType;

import java.lang.module.ModuleFinder;
import java.util.List;
import java.util.Optional;

public class Entity implements FacetBase {
    private final SpecificationType specification;

    private final String applicableEntity;
    private final String applicablePreDefinedType;

    public Entity(SpecificationType specification) {
        this.specification = specification;

        this.applicableEntity = specification.getApplicability() != null &&
                specification.getApplicability().getEntity() != null &&
                specification.getApplicability().getEntity().getName() != null ?
                specification.getApplicability().getEntity().getName().getSimpleValue() :
                "";
        this.applicablePreDefinedType = specification.getApplicability() != null &&
                specification.getApplicability().getEntity() != null &&
                specification.getApplicability().getEntity().getPredefinedType() != null ?
                specification.getApplicability().getEntity().getPredefinedType().getSimpleValue() : "";
    }

    @Override
    public ComponentFilter setApplicability() { //ToDo implement multiLevel checking
        return component -> {
            if (specification.getApplicability().getEntity() == null) {
                // If not, return true to avoid any filtering based on Entity
                return true;
            }

            Optional<IfcEntityType> solIfcEntityType = component.getIfcEntityType();

            // Check if the IDS-defined Entity matches the current component
            return solIfcEntityType.stream().anyMatch(ifcEntityType -> ifcEntityType.equals(IfcEntityType.valueOf(applicableEntity)));
            };
        }

    @Override
    public ComponentFilter setRequirement() {
        // Check if the IDS specification has an Entity requirement
        if (getIdsRequirementEntity(specification) == null) {
            // If not, return true to avoid any filtering based on Entity
            return component -> true;
        }

        // Get the IDS Entity requirement
        EntityType idsRequirementEntity = getIdsRequirementEntity(specification);

        IfcEntityType entityType = IfcEntityType.valueOf(applicableEntity);
        String entityTypeValue = String.valueOf(entityType.getClass());

        return component -> {
            // Check if the IDS-defined Entity exists at the current component
            // Assume that 'getType()' method returns the entity type of the component
            Class<?> componentEntityClass = component.getIfcEntityType().getClass();
            return entityTypeValue.contains("IfcWall");
        };
    }


    // New method to fetch IDS Requirement Entity
    private EntityType getIdsRequirementEntity(SpecificationType specification) {
        if (specification.getRequirements() == null ||
                specification.getRequirements().getEntityAndPartOfAndClassification() == null) {
            return null;
        }

        Object entity = specification.getRequirements().getEntityAndPartOfAndClassification().stream()
                .filter(obj -> obj instanceof EntityType)
                .findFirst()
                .orElse(null);

        return (entity instanceof EntityType) ? (EntityType) entity : null;
    }
}
