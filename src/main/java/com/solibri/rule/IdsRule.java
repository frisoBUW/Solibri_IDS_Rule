package com.solibri.rule;

import com.solibri.rule.IdsParser;
import com.solibri.smc.api.checking.*;
import com.solibri.smc.api.filter.ComponentFilter;
import com.solibri.smc.api.model.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;
import java.util.*;

public final class IdsRule extends OneByOneRule {

	private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());


	private final RuleParameters params = RuleParameters.of(this);

	public final FilterParameter rpComponentFilter = this.getDefaultFilterParameter();

	private static final String COMPONENT_FILTER_PARAMETER_ID2 = "rpComponentFilter2";

	public final FilterParameter rpComponentFilter2 = params.createFilter(COMPONENT_FILTER_PARAMETER_ID2);

	//    Actually ids stuff
	private final StringParameter filename = params.createString("MyStringParameter");


	@Override
	public Collection<Result> check(Component component, ResultFactory resultFactory) {
		String stringParameterValue = filename.getValue();

		ComponentFilter requirementFilter = rpComponentFilter2.getValue();
		LOG.info("Ids Contents: {]" + requirementFilter);
		

		IdsParser idsParser = new IdsParser(stringParameterValue);
		LOG.info("Ids Contents: {]" + idsParser);

		ResultCategory resultCategory = resultFactory.createCategory("TestUniqueName", "TestDisplayName");

		Result result = resultFactory.create(stringParameterValue, "DescriptionTrue", resultCategory);
		return Collections.singleton(result);
	}
}