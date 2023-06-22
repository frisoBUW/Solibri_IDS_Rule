package com.solibri.rule.facets;

import com.solibri.rule.utils.Result;
import com.solibri.smc.api.filter.ComponentFilter;
import com.solibri.smc.api.ifc.IfcEntityType;
import com.solibri.smc.api.model.ComponentType;
import de.buildingsmart.ids.ApplicabilityType;
import de.buildingsmart.ids.EntityType;
import de.buildingsmart.ids.RequirementsType;
import de.buildingsmart.ids.SpecificationType;

import java.lang.module.ModuleFinder;

public class Entity implements FacetBase {
    private SpecificationType specification;
    private ComponentFilter componentFilter;

    private final String applicableEntity = specification.getApplicability().getEntity().getName().getSimpleValue();
    private final String applicablePreDefinedType = specification.getApplicability().getEntity().getPredefinedType().getSimpleValue();
    //private final EntityType requiredEntity = specification.getRequirements(); //Issue

    public Entity(SpecificationType specification) {
        this.specification = specification;
    }

    @Override
    public void setApplicability(ApplicabilityType applicability) {
        String convertedName = convertToEnumFormat(applicableEntity); //ToDo implement error handling if no entity is provided or entity is not checkable
        ComponentType componentType;
        try {componentType = ComponentType.valueOf(convertedName);
        } catch (IllegalArgumentException e) {
            componentType = null;
        }
        if (componentType != null) {
            componentFilter = ComponentFilter.componentTypeIs(componentType);
            IfcEntityType entityType = IfcEntityType.valueOf(applicableEntity);
            Class<?> entityClass = entityType.getClass();
            ComponentFilter cf = ComponentFilter.componentClassIs(entityClass);
        }
    }

    @Override
    public void setRequirement(RequirementsType requirement) {

    }


    @Override
    public ComponentFilter setFilter() {
        return null;
     }


    public String convertToEnumFormat(String str) {
        // remove the "Ifc" prefix
        str = str.replaceFirst("Ifc", "");

        // check if the string contains "StandardCase" and replace it
        if (str.contains("StandardCase")) {
        str = str.replace("StandardCase", "");
        }

        // replace uppercase characters with underscore followed by the character, then convert all to upper case
        String converted = str.replaceAll("(.)(\\p{Upper})", "$1_$2").toUpperCase();

        // if there is a leading underscore (from the first uppercase after "Ifc"), remove it
        if (converted.startsWith("_")) {
        converted = converted.substring(1);
        }

        return converted;
    }
}

