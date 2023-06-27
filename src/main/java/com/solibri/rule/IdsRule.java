package com.solibri.rule;

import com.solibri.rule.facets.Entity;
import com.solibri.rule.specifications.ApplicablityFactory;
import com.solibri.rule.specifications.RequirementFactory;
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
import de.buildingsmart.ids.EntityType;
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

	public Map<SpecificationType, List<ComponentFilter>> componentFilters;
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

//			ComponentFilter acceptAll = ComponentFilter.ACCEPT_ALL;
//			Set<Component> components = componentSelector.select(acceptAll); //For the moment, its basically passing everything
//			LOG.info("Components: " + components.size());
		}
		return isRelevant ? PreCheckResult.createRelevant() : PreCheckResult.createIrrelevant();
	}

	private Map<SpecificationType, List<ComponentFilter>> populateComponentFilters(List<SpecificationType> specifications) {
//		List<ComponentFilter> componentApplicabilityFilters = new ArrayList<>();
		Map<SpecificationType, List<ComponentFilter>> componentFilterMap = new HashMap<>();
		for (SpecificationType specification : specifications) {

			Entity entity = new com.solibri.rule.facets.Entity(specification);
			ComponentFilter applicabilityFilter = entity.setApplicability();
			ComponentFilter requirementFilter = entity.setRequirement();

			List<ComponentFilter> filters = new ArrayList<>(Arrays.asList(applicabilityFilter, requirementFilter));
			componentFilterMap.put(specification, filters);
			//			componentApplicabilityFilters.add(applicabilityFilter);
			//			componentApplicabilityFilters.add(applicabilityFilter);
			// FacetFactory(specification)
			//
//			String entity = specification.getApplicability().getEntity().getName().getSimpleValue(); //ToDo implement error handling if no entity is provided or entity is not checkable
//			String convertedName = convertToEnumFormat(entity);
//			ComponentType componentType;
//			try {
//				componentType = ComponentType.valueOf(convertedName);
//			} catch (IllegalArgumentException e) {
//				componentType = null;
//			}
//
//			if (componentType != null) {
//				LOG.info("The ComponentType for entity " + entity + " is " + componentType);
//
////				ComponentFilter filter = new com.solibri.rule.facets.Material(specification).setApplicability();
////				ComponentFilter filter = new com.solibri.rule.facets.Property(specification).setApplicability();
//
////				ComponentFilter applicabilityFilter = ApplicablityFactory.createComponentFilter(specification);
////				ComponentFilter requirementFilter = RequirementFactory.createComponentFilter(specification);
////				componentFilters.add(filter);
//
//				} else {
//				LOG.info("The entity is not a valid! Entity: " + entity);
//			}
		}
		return componentFilterMap;
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


		resultsContainer = new ArrayList<>();

		for (Map.Entry<SpecificationType, List<ComponentFilter>> entry : componentFilters.entrySet()) {

			SpecificationType spec = entry.getKey();
			ComponentFilter applicabilityFilter = entry.getValue().get(0);
			ComponentFilter requirementFilter = entry.getValue().get(1);
			List<ComponentFilter> filters = entry.getValue();

			ResultCategory resultCategoryParent = resultFactory.createCategory(ids.getInfo().getTitle(), ids.getInfo().getDescription());
			ResultCategory resultCategoryChildren = resultFactory.createCategory("TestCat", spec.toString(), resultCategoryParent);

			Collection<Component> targets = SMC.getModel().getComponents(applicabilityFilter);
			LOG.info("Subset size? - " + targets.size());


			for (Component target : targets) {
				String name = "TestName " + target.getName();
				String description = "There is an issue with XYZ ABC " + target.getName();

//				if (componentCheck(target)) {
//					checkingSelection.pass(target);
//				} else {
				Result result = resultFactory
					.create(name, description, resultCategoryChildren)
					.withInvolvedComponent(target)
					.withSeverity(Severity.LOW);
				checkingSelection.fail(target, result);
				resultsContainer.add(result);
			}

			Integer filterCount = checkingSelection.getRemaining().size();
			LOG.info("The checking selection has: " + filterCount + " elements!");
		}
		checkingSelection.passRemaining();
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