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
