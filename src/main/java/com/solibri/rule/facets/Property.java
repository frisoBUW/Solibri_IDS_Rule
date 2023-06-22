package com.solibri.rule.facets;

import com.solibri.rule.utils.Result;
import com.solibri.smc.api.filter.ComponentFilter;
import com.solibri.smc.api.model.PropertyReference;
import com.solibri.smc.api.model.PropertySet;
import de.buildingsmart.ids.ApplicabilityType;
import de.buildingsmart.ids.PropertyType;
import de.buildingsmart.ids.RequirementsType;
import de.buildingsmart.ids.SpecificationType;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Property implements FacetBase, PropertyReference {

    private final SpecificationType specification;

    public Property(SpecificationType specification) {
        this.specification = specification;
    }

    @Override
    public void setApplicability(ApplicabilityType applicability) {

    }

    @Override
    public void setRequirement(RequirementsType requirement) {

    }


    /**
     * This method is used to specifically deal with Property(Sets) from IDS. Since ComponentFilter from the Solibri
     * API is a functional Interface, we can define custom ComponentFilters. Functional Interfaces are inferred by the
     * Java Compiler when an interface has ++one++ abstract method.
     * We can use lambdas to select components based on stream comparisons of the Solibri Pset Names and Ids Pset Names
     * @return ComponentFilter
     */
    @Override
    public ComponentFilter setFilter() {
        return component -> {
            // Create a Set of PropertySet names from the component
            Set<PropertySet> solPropertySets = new HashSet<>(component.getPropertySets());

            // Get the properties from the IDS specification
            List<ApplicabilityType.Property> idsProperties = specification.getApplicability().getProperty();

            // Check if the user-defined PropertySet, Property, and Value exist in the Solibri collections
            boolean psetCheck = checkExists(
                    solPropertySets,
                    idsProperties,
                    PropertySet::getName,
                    idsPropertySet -> idsPropertySet.getPropertySet().getSimpleValue());

            boolean propCheck = checkExists(
                    solPropertySets.stream().flatMap(pset -> pset.getProperties().stream()).collect(Collectors.toSet()),
                    idsProperties,
                    com.solibri.smc.api.model.Property::getName,
                    idsProperty -> idsProperty.getName().getSimpleValue());

            boolean propValCheck = checkExists(
                    solPropertySets.stream().flatMap(pset -> pset.getProperties().stream()).collect(Collectors.toSet()),
                    idsProperties,
                    com.solibri.smc.api.model.Property::getValueAsString,
                    idsProperty -> idsProperty.getValue().getSimpleValue());

            return psetCheck && propCheck && propValCheck;
        };
    }

    private <T, U> boolean checkExists(Collection<T> solCollection, Collection<U> idsCollection,
                                       Function<T, String> solMapper, Function<U, String> idsMapper) {
        Set<String> solSet = solCollection.stream().map(solMapper).collect(Collectors.toSet());

        return idsCollection.stream().map(idsMapper).anyMatch(solSet::contains);
    }

//    @Override
//    public ComponentFilter setFilter() {
//        return component -> {
//            // Get the collection of PropertySet of the component
//            Collection<PropertySet> solPropertySets = component.getPropertySets();
//
//            // Check if any user-defined PropertySet, Property, and Value exist in the collection
//            return specification.getApplicability().getProperty().stream().anyMatch(property ->
//                    solPropertySets.stream().anyMatch(solPropertySet ->
//                            solPropertySet.getName().equals(property.getPropertySet().getSimpleValue()) &&
//                            solPropertySet.getProperties().stream().anyMatch(solProperty ->
//                                    solProperty.getName().equals(property.getName().getSimpleValue()) &&
//                                    solProperty.getValue().toString().equals(property.getValue().getSimpleValue())
//                                    )
//                    )
//            );
//        };
//    }

//    @Override
//    public ComponentFilter setFilter() {
//        return component -> {
//            // Get the collection of PropertySet of the component
//            Collection<PropertySet> solPropertySets = component.getPropertySets();
//
//            // Get the collection of Property from the specification
//            List<ApplicabilityType.Property> idsApplicabilityProperties = specification.getApplicability().getProperty();
//
//            // For each IDS Property
//            for (ApplicabilityType.Property idsProperty : idsApplicabilityProperties) {
//                String idsPropertySetName = idsProperty.getPropertySet().getSimpleValue();
//                String idsPropertyName = idsProperty.getName().getSimpleValue();
//                String idsPropertyValue = idsProperty.getValue().getSimpleValue();
//
//                // Check if there exists a PropertySet in the solibri properties that matches the IDS Property
//                for (PropertySet solPropertySet : solPropertySets) {
//                    if (!solPropertySet.getName().equals(idsPropertySetName)) {
//                        continue;
//                    }
//
//                    // Get the Property of the solibri PropertySet that matches the name of the IDS Property
//                    Optional<com.solibri.smc.api.model.Property<Object>> solPropertyOptional = solPropertySet.getProperty(idsPropertyName);
//                    if (!solPropertyOptional.isPresent()) {
//                        continue;
//                    }
//
//                    com.solibri.smc.api.model.Property<?> solProperty = solPropertyOptional.get();
//                    if (solProperty.getValue().toString().equals(idsPropertyValue)) {
//                        // Found a match, return true
//                        return true;
//                    }
//                }
//            }
//
//            // No matching PropertySet, Property, and Value found in the solibri properties
//            return false;
//        };
//    }

//    @Override
//    public ComponentFilter setFilter() {
//        return component -> {
//            // Get the collection of PropertySet of the component
//            Collection<PropertySet> solPropertySets = component.getPropertySets();
//            solPropertySets.stream()
//                    .flatMap(pset -> pset.getProperties().stream())
//                    .map(com.solibri.smc.api.model.Property::getName)
//                    .collect(Collectors.toSet());
//            return
//        }
//    }


    @Override
    public String getPropertySetName() {
        return null;
//        return property.getPropertySet().getSimpleValue();
    }

    @Override
    public String getPropertyName() {
        return null;
//        return property.getName().getSimpleValue();
    }
}
