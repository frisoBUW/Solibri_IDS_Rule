package com.solibri.rule.facets;

import com.solibri.rule.utils.Result;
import com.solibri.smc.api.filter.ComponentFilter;
import com.solibri.smc.api.model.PropertyReference;
import com.solibri.smc.api.model.PropertySet;
import de.buildingsmart.ids.ApplicabilityType;
import de.buildingsmart.ids.PropertyType;
import de.buildingsmart.ids.RequirementsType;
import de.buildingsmart.ids.SpecificationType;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
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

//    @Override
//    public ComponentFilter setFilter() {
//        return component -> {
//            // Get the collection of PropertySet of the component
//            Collection<PropertySet> propertySets = component.getPropertySets();
//            // Get the collection of Property from the specification
//            List<ApplicabilityType.Property> properties = specification.getApplicability().getProperty();
//
//            // Check if the user-defined PropertySet exists in the collection
//            return propertySets.stream()
//                    .anyMatch(propertySet -> properties.stream()
//                            .anyMatch(property -> property.getPropertySet().getSimpleValue().equals(propertySet.getName())));
//        };
//    }

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
            // Get the collection of PropertySet of the component
            Collection<PropertySet> solPropertySets = component.getPropertySets();
            Stream<String> solPropertySetsStrings = solPropertySets.stream().map(PropertySet::getName);
            Set<String> solPropertySetsSet = solPropertySetsStrings.collect(Collectors.toSet());
            // Get the collection of Property from the specification
            List<ApplicabilityType.Property> idsApplicabilityProperties = specification.getApplicability().getProperty();
            Stream<String> idsPropertySetsStrings = idsApplicabilityProperties.stream().map(property -> property.getPropertySet().getSimpleValue());
            // Check if the user-defined PropertySet exists in the collection
            return idsPropertySetsStrings.anyMatch(solPropertySetsSet::contains);
        };
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
