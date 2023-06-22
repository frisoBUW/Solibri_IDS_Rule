package com.solibri.rule;

import com.solibri.smc.api.SMC;
import com.solibri.smc.api.checking.*;
import com.solibri.smc.api.filter.ComponentFilter;
import com.solibri.smc.api.ifc.IfcEntityType;
import com.solibri.smc.api.model.*;
import com.solibri.smc.api.model.components.Wall;
import com.solibri.smc.api.ui.BorderType;
import com.solibri.smc.api.ui.UIContainer;
import com.solibri.smc.api.ui.UIContainerVertical;
import de.buildingsmart.ids.ApplicabilityType;
import de.buildingsmart.ids.Ids;
import de.buildingsmart.ids.SpecificationType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;
import java.util.*;

import static com.solibri.smc.api.ifc.IfcEntityType.*;

public final class IdsRule implements Rule {

	public Ids ids;

	private final RuleParameters params = RuleParameters.of(this);
	//Some Rule Params, just for visualizing, currently they aren't used
	final FilterParameter componentFilterParameter = params.createFilter("rpComponentFilter");
	final DoubleParameter doubleParameter = params.createDouble("rpDoubleParameter", PropertyType.LENGTH);
	final BooleanParameter booleanParameter = params.createBoolean("rpBooleanParameter");
	final PropertyReferenceParameter propertyReferenceParameter = params.createPropertyReference("rpPropertyReferenceParameter");
	final EnumerationParameter enumerationParameterForRadioButtons = params.createEnumeration("rpEnumerationParameterForRadioButtons",
			Arrays.asList("rpEnumerationParameterForRadioButtons.OPTION1", "rpEnumerationParameterForRadioButtons.OPTION2",
					"rpEnumerationParameterForRadioButtons.OPTION3"));
	final EnumerationParameter enumerationParameterForComboBox = params.createEnumeration("rpEnumerationParameterForComboBox",
			Arrays.asList("rpEnumerationParameterForComboBox.OPTION1", "rpEnumerationParameterForComboBox.OPTION2",
					"rpEnumerationParameterForComboBox.OPTION3"));
	final TableParameter tableParameter = params.createTable("rpTableParameter",
			Arrays.asList("rpTableParameter.LENGTH", "rpTableParameter.HEIGHT"), Arrays.asList(PropertyType.LENGTH, PropertyType.LENGTH));


	//    Actually ids stuff
	final StringParameter filename = params.createString("MyStringParameter");

	/**
	 * Add the UI definition.
	 */
	private final IdsRuleUI uiDefinition = new IdsRuleUI(this);

	private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

//	private final RuleResources resources = RuleResources.of(this);

	public List<ComponentFilter> componentFilters;
	public List<Result> resultsContainer;

	public void setIds(Ids ids) {
		this.ids = ids;
	}

	private boolean isFileExtensionValid(String filePath) {
		String lowerCaseFilePath = filePath.toLowerCase();
		return lowerCaseFilePath.endsWith(".xml") || lowerCaseFilePath.endsWith(".ids");
	}

	/**
	 * This implements the initial checking phase. The ComponentSelector is passed to this method. If some criteria
	 * are met, first a component filter needs to be set.
	 * ToDo: Use the ids applicability to set the filter for the pre selected components.
	 *
	 * @return PreCheckResult
	 */
	@Override
	public PreCheckResult preCheck(ComponentSelector componentSelector) {
		String filepath = filename.getValue();
		boolean isRelevant = isFileExtensionValid(filepath); //ToDo throw exception if file not found
		setIds(new IdsParser().parseIdsFile(filepath));
		LOG.info("Ids Contents: " + ids.getInfo().getTitle());
		if (isRelevant) {
			componentFilters = populateComponentFilters(ids.getSpecifications().getSpecification());

			LOG.info("Filter: " + componentFilters);

			ComponentFilter acceptAll = ComponentFilter.ACCEPT_ALL;
			Set<Component> components = componentSelector.select(acceptAll); //For the moment, its basically passing everything
			LOG.info("Components: " + components.size());
		}
		return isRelevant ? PreCheckResult.createRelevant() : PreCheckResult.createIrrelevant();
	}

	private List<ComponentFilter> populateComponentFilters(List<SpecificationType> specifications) {
		List<ComponentFilter> componentFilters = new ArrayList<>();
		for (SpecificationType specification : specifications) {
			// FacetFactory(specification)
			//
			String entity = specification.getApplicability().getEntity().getName().getSimpleValue(); //ToDo implement error handling if no entity is provided or entity is not checkable
			String convertedName = convertToEnumFormat(entity);
			LOG.info("Converted Name: " + convertedName);
			ComponentType componentType;
			try {
				componentType = ComponentType.valueOf(convertedName);
			} catch (IllegalArgumentException e) {
				LOG.info("No ComponentType found for entity: " + entity);
				componentType = null;
			}

			if (componentType != null) {
				LOG.info("The ComponentType for entity " + entity + " is " + componentType);
//				ComponentFilter filter = component -> {
//					// Filtering logic: Example filtering based on component name
//					return component.getName().startsWith("Wand");
//				};

//				ComponentFilter filter = component -> {
//					// Get the collection of PropertySet of the component
//					Collection<PropertySet> propertySets = component.getPropertySets();
//
//					// Check if the user-defined PropertySet exists in the collection
//					return propertySets.stream()
//							.anyMatch(propertySet -> propertySet.getName().equals("ArchiCADProperties"));
//				};

//				ComponentFilter filter = component -> {
//					// Get the list of materials of the component
//					List<Material> materials = component.getMaterials();
//
//					// Check if the user-defined material exists in the list
//					return materials.stream()
//							.anyMatch(material -> material.getName().contains(specification.getApplicability().getMaterial().getValue().getSimpleValue()));
//				};

//				ComponentFilter filter = new com.solibri.rule.facets.Material(specification).setFilter();
				ComponentFilter filter = new com.solibri.rule.facets.Property(specification).setFilter();
				componentFilters.add(filter);
//				for (ApplicabilityType.Property prop : specification.getApplicability().getProperty()) {
//					PropertyReference propertyReference1 = new com.solibri.rule.facets.Property(prop);
//					String test = "Außenwände";
//					ComponentFilter tempFilter1 = ComponentFilter.propertyValueEquals(propertyReference1, test);
//					componentFilters.add(tempFilter1);
//				}
//				ComponentFilter test = ComponentFilter.all(component ->
//						component.getIfcEntityType().equals(IfcEntityType.IfcWall));

//				ComponentFilter test1 = ComponentFilter.all(component ->
//						component.getIfcEntityType().filter(type -> type.equals(IfcEntityType.IfcWall)).isPresent());
//				componentFilters.add(test1);

//				componentFilters.add(ComponentFilter.componentTypeIs(componentType));

//				IfcEntityType entityType = IfcEntityType.valueOf(entity);
//				Class<?> entityClass = entityType.getClass();
//				componentFilters.add(ComponentFilter.componentClassIs(IfcEntityType.IfcWall.getClass()));
				} else {
				LOG.info("The entity is not a valid! Entity: " + entity);
			}
		}
		return componentFilters;
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

	@Override
	public void check(CheckingSelection checkingSelection, ResultFactory resultFactory) {

		LOG.info("Filter: " + componentFilters);
		resultsContainer = new ArrayList<>();
		Collection<Result> results = new ArrayList<>();
		for (ComponentFilter targetComponentFilter : componentFilters) {
//			for (Component classi : checkingSelection.getRemaining()) {
//				LOG.info("First look checkingSelection, does it have Components? - " + classi.getClass());
//			}
//			LOG.info("First look checkingSelection, does it have Components? - " + checkingSelection.getRemaining().getClass());
			Collection<Component> targets = SMC.getModel().getComponents(targetComponentFilter);
			LOG.info("Subset size? - " + targets.size());


			for (Component target : targets) {
				String name = "TestName " + target.getName();
				String description = "There is an issue with XYZ ABC " + target.getName();
				ResultCategory resultCategoryParent = resultFactory.createCategory(ids.getInfo().getTitle(), ids.getInfo().getDescription());
				ResultCategory resultCategoryChildren = resultFactory.createCategory("ChildName", "ChildDescription", resultCategoryParent);
//				if (componentCheck(target)) {
//					checkingSelection.pass(target);
//				} else {
				Result result = resultFactory
					.create(name, description)
					.withInvolvedComponent(target)
					.withSeverity(Severity.LOW)
					.withCategory(resultCategoryChildren);
				results.addAll(results);
				checkingSelection.fail(target, result);
				resultsContainer.add(result);
			}

			Integer filterCount = checkingSelection.getRemaining().size();
			LOG.info("The checking selection has: " + filterCount + " elements!");
			checkingSelection.passRemaining();
		}
		//Todo: Move to custom rule Interface. Check if implementing own interface is possible, otherwise find other way.
		//Todo: It might be necessary to create results even if nothing happend
	}

	@Override
	public UIContainer getParametersUIDefinition() {
		return uiDefinition.getDefinitionContainer();
	}


	public boolean componentCheck(Component component) {
		for (PropertySet propertySet : component.getPropertySets("Pset_WallCommon")) {
			Optional<Property<Boolean>> isExternalProperty = propertySet.getProperty("IsExternal");
			if (isExternalProperty.isPresent() && isExternalProperty.get().getValue().isPresent()
					&& isExternalProperty.get().getValue().get()) {
				return true;
			}
		}

		return false; // Added a return statement when the loop completes without finding a match
	}

	//	@Override
//	public void postCheck() {
//		Rule.super.postCheck();
//
//		for (Result result : resultsContainer) {
//			result.setDecision(Decision.ACCEPTED);
//		}
//	}

}