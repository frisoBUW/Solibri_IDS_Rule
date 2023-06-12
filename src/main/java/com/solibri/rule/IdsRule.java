package com.solibri.rule;

import com.solibri.rule.IdsParser;
import com.solibri.smc.api.SMC;
import com.solibri.smc.api.checking.*;
import com.solibri.smc.api.filter.ComponentFilter;
import com.solibri.smc.api.ifc.IfcEntityType;
import com.solibri.smc.api.model.Component;
import de.buildingsmart.ids.Ids;
import de.buildingsmart.ids.SpecificationType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;
import java.util.*;

import static com.solibri.smc.api.ifc.IfcEntityType.*;

public final class IdsRule extends OneByOneRule {

	public Ids ids = new IdsParser().parseIdsFile("C:\\Projects\\_Python\\SA\\Data\\IDS\\IDS_Test_5D.xml");

	public final FilterParameter rpComponentFilter = this.getDefaultFilterParameter();

	private static final String COMPONENT_FILTER_PARAMETER_ID2 = "rpComponentFilter2";

	private final RuleParameters params = RuleParameters.of(this);

	public final FilterParameter rpComponentFilter2 = params.createFilter(COMPONENT_FILTER_PARAMETER_ID2);

	//    Actually ids stuff
	public final StringParameter filename = params.createString("MyStringParameter");

	private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	private final RuleResources resources = RuleResources.of(this);

	public void setIds(Ids ids) {
		this.ids = ids;
	}

	@Override
	public PreCheckResult preCheck() {
		String stringParameterValue = filename.getValue();
		boolean isRelevant = isFileExtensionValid(stringParameterValue);
		if (isRelevant) {
			LOG.info("Ids Contents: " + ids.getInfo().getTitle());
			for (SpecificationType specification : ids.getSpecifications().getSpecification()) {
				LOG.info("Specification: " + specification.getName() + " -- Entity: " + specification.getApplicability().getEntity().getName().getSimpleValue());
			}
		}
		return isRelevant ? PreCheckResult.createRelevant() : PreCheckResult.createIrrelevant();
	}

	private boolean isFileExtensionValid(String filePath) {
		String lowerCaseFilePath = filePath.toLowerCase();
		return lowerCaseFilePath.endsWith(".xml") || lowerCaseFilePath.endsWith(".ids");
	}

	@Override
	public Collection<Result> check(Component component, ResultFactory resultFactory) {

		String stringParameterValue = filename.getValue();


		Collection<Component> applicableElements = SMC.getModel().getComponents(rpComponentFilter.getValue());


		ResultCategory resultCategory = resultFactory.createCategory("TestUniqueName", "TestDisplayName");
		Result result = resultFactory.create(stringParameterValue, "DescriptionTrue", resultCategory);

		Collection<Result> results = new ArrayList<>();

		for (Component element : applicableElements) {
			IfcEntityType entity = element.getIfcEntityType().get();
			if (entity == IfcRoof) {
				results.add(result);
			} else break;
		}


		return results;
	}

	Result createResult(ResultFactory resultFactory, IfcEntityType entity) {
		return resultFactory.create(resources.getString("Result.TestFactory.Name"),
				resources.getString("Result.TestFactory.Description",
						entity.toString()));
	}
}