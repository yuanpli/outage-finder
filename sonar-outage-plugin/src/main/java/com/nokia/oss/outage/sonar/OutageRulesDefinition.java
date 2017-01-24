package com.nokia.oss.outage.sonar;

import org.sonar.api.server.rule.RulesDefinition;
import org.sonar.api.server.rule.RulesDefinitionXmlLoader;
import org.sonar.squidbridge.rules.ExternalDescriptionLoader;

/**
 * Created by harchen on 2016/6/12.
 */
public class OutageRulesDefinition implements RulesDefinition {
    @Override
    public void define(Context context) {
        NewRepository repository = context
                .createRepository(Constants.REPOSITORY_KEY, Constants.JAVA)
                .setName(Constants.REPOSITORY);
        RulesDefinitionXmlLoader ruleLoader = new RulesDefinitionXmlLoader();
        ruleLoader.load(repository, OutageRulesDefinition.class.getResourceAsStream("/com/nokia/oss/outage/sonar/rules.xml"), "UTF-8");
        ExternalDescriptionLoader.loadHtmlDescriptions(repository, "/com/nokia/oss/outage/sonar/i10n");
        repository.done();
    }
}
