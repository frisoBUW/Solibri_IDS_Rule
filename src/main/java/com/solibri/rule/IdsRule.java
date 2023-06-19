package com.solibri.rule;

import com.solibri.smc.api.SMC;
import com.solibri.smc.api.checking.*;
import com.solibri.smc.api.filter.ComponentFilter;
import com.solibri.smc.api.ifc.IfcEntityType;
import com.solibri.smc.api.model.Component;
import com.solibri.smc.api.model.ComponentType;
import com.solibri.smc.api.model.components.Wall;
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

	//    Actually ids stuff
	public final StringParameter filename = params.createString("MyStringParameter");

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
			componentFilters = new ArrayList<>();
			for (SpecificationType specification : ids.getSpecifications().getSpecification()) {
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
					// The entity is a valid ComponentType, so you can use it in your application.
					LOG.info("The ComponentType for entity " + entity + " is " + componentType);
					componentFilters.add(ComponentFilter.componentTypeIs(componentType));
				} else {
					LOG.info("The entity is not a valid! Entity: " + entity);
					// The entity is not a valid ComponentType.
					// You could handle this case however is appropriate for your application.
				}
			}
			// Idea: passing all Components to checkingSelection
			LOG.info("Filter: " + componentFilters);
//			LOG.info("First look ComponentSelector, does it have Components? - " + componentSelector.hasSelectedComponents());
//			ComponentFilter idsFilter = ComponentFilter.any(componentFilters.toArray(ComponentFilter[]::new));
//			Set<Component> components = componentSelector.select(idsFilter); //For the moment, its basically passing
//			LOG.info("Second look ComponentSelector, does it have Components? - " + componentSelector.hasSelectedComponents());
//			LOG.info("Components: " + components.size());

			LOG.info("First look ComponentSelector, does it have Components? - " + componentSelector.hasSelectedComponents());
			ComponentFilter acceptAll = ComponentFilter.ACCEPT_ALL;
			Set<Component> components = componentSelector.select(acceptAll); //For the moment, its basically passing
			LOG.info("Second look ComponentSelector, does it have Components? - " + componentSelector.hasSelectedComponents());
			LOG.info("Components: " + components.size());

		}
		return isRelevant ? PreCheckResult.createRelevant() : PreCheckResult.createIrrelevant();
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
		for (ComponentFilter targetComponentFilter : componentFilters) {
			LOG.info("First look checkingSelection, does it have Components? - " + checkingSelection.getRemaining());
			Collection<Component> targets = SMC.getModel().getComponents(targetComponentFilter);
			LOG.info("Subset size? - " + targets.size());

			Collection<Result> results = new ArrayList<>();
			for (Component target : targets) {
				String name = "TestName " + target.getName();
				String description = "There is an issue with XYZ ABC " + target.getName();

				Result result = resultFactory
						.create(name, description)
						.withInvolvedComponent(target)
						.withSeverity(Severity.LOW);
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

//	@Override
//	public void postCheck() {
//		Rule.super.postCheck();
//
//		for (Result result : resultsContainer) {
//			result.setDecision(Decision.ACCEPTED);
//		}
//	}
}