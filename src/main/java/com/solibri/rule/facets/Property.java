package com.solibri.rule.facets;

import com.solibri.smc.api.filter.ComponentFilter;
import com.solibri.smc.api.model.PropertySet;
import de.buildingsmart.ids.ApplicabilityType;
import de.buildingsmart.ids.RequirementsType;
import de.buildingsmart.ids.SpecificationType;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Property implements FacetBase {

    private final SpecificationType specification;

    public Property(SpecificationType specification) {
        this.specification = specification;
    }

    /**
     * This method is used to specifically deal with Property(Sets) from IDS. Since ComponentFilter from the Solibri
     * API is a functional Interface, we can define custom ComponentFilters. Functional Interfaces are inferred by the
     * Java Compiler when an interface has ++one++ abstract method.
     * We can use lambdas to select components based on stream comparisons of the Solibri Pset Names and Ids Pset Names
     * @return ComponentFilter
     */
    @Override
    public ComponentFilter setApplicability() {
        return component -> {
            // Create a Set of PropertySets from the currently passed in component
            Set<PropertySet> solPropertySets = new HashSet<>(component.getPropertySets());

            // Get the properties from the IDS specification
            List<ApplicabilityType.Property> idsProperties = specification.getApplicability().getProperty();

            // Check if the IDS-defined PropertySet, PropertyName, and PropertyValue exist at the current component
            boolean psetNameCheck = checkExists(
                    solPropertySets,
                    idsProperties,
                    PropertySet::getName,
                    idsPropertySet -> idsPropertySet.getPropertySet().getSimpleValue());

            boolean propNameCheck = checkExists(
                    solPropertySets.stream().flatMap(pset -> pset.getProperties().stream()).collect(Collectors.toSet()),
                    idsProperties,
                    com.solibri.smc.api.model.Property::getName,
                    idsProperty -> idsProperty.getName().getSimpleValue());

            boolean propValueCheck = checkExists(
                    solPropertySets.stream().flatMap(pset -> pset.getProperties().stream()).collect(Collectors.toSet()),
                    idsProperties,
                    com.solibri.smc.api.model.Property::getValueAsString,
                    idsProperty -> idsProperty.getValue().getSimpleValue());

            return psetNameCheck && propNameCheck && propValueCheck;
        };
    }

    private <T, U> boolean checkExists(Collection<T> solCollection, Collection<U> idsCollection,
                                       Function<T, String> solMapper, Function<U, String> idsMapper) {
        Set<String> solSet = solCollection.stream().map(solMapper).collect(Collectors.toSet());

        return idsCollection.stream().map(idsMapper).anyMatch(solSet::contains);
    }

    @Override
    public ComponentFilter setRequirement() {
        return component -> {
            // Create a Set of PropertySets from the currently passed in component
            Set<PropertySet> solPropertySets = new HashSet<>(component.getPropertySets());
            // Get the properties from the IDS specification
            // Fetch IDS Requirement Properties
            List<RequirementsType.Property> idsRequirementProperties = getIdsRequirementProperties(specification);

            // Check if the IDS-defined PropertySet, PropertyName, and PropertyValue exist at the current component
            boolean psetNameCheck = checkExists(
                    solPropertySets,
                    idsRequirementProperties,
                    PropertySet::getName,
                    idsPropertySet -> idsPropertySet.getPropertySet().getSimpleValue());

            boolean propNameCheck = checkExists(
                    solPropertySets.stream().flatMap(pset -> pset.getProperties().stream()).collect(Collectors.toSet()),
                    idsRequirementProperties,
                    com.solibri.smc.api.model.Property::getName,
                    idsProperty -> idsProperty.getName().getSimpleValue());

            boolean propValueCheck = checkExists(
                    solPropertySets.stream().flatMap(pset -> pset.getProperties().stream()).collect(Collectors.toSet()),
                    idsRequirementProperties,
                    com.solibri.smc.api.model.Property::getValueAsString,
                    idsProperty -> idsProperty.getValue().getSimpleValue());

            return psetNameCheck && propNameCheck && propValueCheck;
        };
    }

    // New method to fetch IDS Requirement Properties
    private List<RequirementsType.Property> getIdsRequirementProperties(SpecificationType specification) {
        List<Object> entitiesAndClassifications = specification.getRequirements().getEntityAndPartOfAndClassification();
        List<RequirementsType.Property> idsRequirementProperties = new ArrayList<>();
        for (Object obj : entitiesAndClassifications) {
            if (obj instanceof RequirementsType.Property) {
                RequirementsType.Property propElement = (RequirementsType.Property) obj;
                idsRequirementProperties.add(propElement);
            }
        }
        return idsRequirementProperties;
    }



}
