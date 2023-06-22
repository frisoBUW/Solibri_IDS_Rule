package com.solibri.rule;

import com.solibri.smc.api.checking.RuleResources;
import com.solibri.smc.api.checking.StringParameter;
import com.solibri.smc.api.ui.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class IdsRuleUI {
    /**
     * The heatmap visualization rule.
     */
    private final IdsRule idsRule;

    /**
     * The UI definition container.
     */
    private final UIContainer uiDefinition;

    /**
     * The resources of the rule.
     */
    private final RuleResources resources;

    /**
     * Constructor.
     *
     * @param idsRule the ids rule
     */
    public IdsRuleUI(IdsRule idsRule) {
        this.idsRule = idsRule;
        this.resources = RuleResources.of(idsRule);
        this.uiDefinition = createUIDefinition();
    }

    /**
     * Returns the UI definition of the Rule.
     *
     * @return the UI definition container of the Rule
     */
    public UIContainer getDefinitionContainer() {
        return uiDefinition;
    }

    private UIContainer createUIDefinition() {
        /*
         * Create the vertical component container.
         */
        UIContainer uiContainer = UIContainerVertical.create(resources.getString("UI.IdsRule.TITLE"), BorderType.LINE);

        /*
         * Add the description.
         */
        uiContainer.addComponent(UILabel.create(resources.getString("UI.IdsRule.DESCRIPTION")));

        /*
         * Add the first filter for components to check.
         */
        uiContainer.addComponent(createFilterContainer());

        /*
         * Add some other container.
         */
        uiContainer.addComponent(createContainerWithParameters());

        /*
         * Add String parameter
         */
        uiContainer.addComponent(createCustomStringParameterComponent());

        return uiContainer;
    }


//    private UIComponent createCustomStringParameterComponent() {
//        UIContainer stringContainer = UIContainerVertical.create(resources.getString("MyStringParameter.TITLE"), BorderType.LINE);
//        stringContainer.addComponent(UIRuleParameter.create(idsRule.filename));
//        return stringContainer;
//    }

    /**
     * A custom UI for a string parameter that presents a text panel and a button.
     * When the button is pressed, a file chooser dialog is opened. When a file is selected,
     * its path is inserted into the text panel.
     */
    private final class CustomFileChooserComponent implements RuleParameterCustomUi {

        private final StringParameter filePathParameter;
        private final JTextPane textPane = new JTextPane();
        private final OpenFileChooserAction openFileChooserAction = new OpenFileChooserAction();

        private class OpenFileChooserAction extends AbstractAction {
            OpenFileChooserAction() {
                putValue(Action.NAME, resources.getString("OpenFileChooserButton.NAME"));
            }
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                int returnValue = fileChooser.showOpenDialog(null);
                if (returnValue == JFileChooser.APPROVE_OPTION) {
                    File selectedFile = fileChooser.getSelectedFile();
                    filePathParameter.setValue(selectedFile.getAbsolutePath());
                    textPane.setText(filePathParameter.getValue());
                }
            }
        }

        private CustomFileChooserComponent(StringParameter filePathParameter) {
            this.filePathParameter = filePathParameter;
        }

        @Override
        public void setEnabler(UIComponent enabler) {
        }

        @Override
        public Optional<UIComponent> getEnabler() {
            return Optional.empty();
        }

        @Override
        public java.awt.Component getComponent() {
            JButton openFileChooserButton = new JButton(openFileChooserAction);
            final int gap = 5;
            JPanel panel = new JPanel(new BorderLayout(gap, gap));
            textPane.setText(filePathParameter.getValue());
            panel.add(openFileChooserButton, BorderLayout.WEST);
            panel.add(textPane, BorderLayout.CENTER);
            return panel;
        }

        @Override
        public void setEditable(boolean editable) {
            openFileChooserAction.setEnabled(editable);
            textPane.setEditable(editable);
        }
    }

    private UIComponent createCustomStringParameterComponent() {
        UIContainer stringContainer = UIContainerVertical.create(resources.getString("MyStringParameter.TITLE"), BorderType.LINE);

        // Create the custom file chooser component and add it to the container.
        CustomFileChooserComponent fileChooserComponent = new CustomFileChooserComponent(idsRule.filename);
        stringContainer.addComponent(fileChooserComponent);

        return stringContainer;
    }

    /**
     * This method creates the top panel of the parameters UI.
     */
    private UIContainer createFilterContainer() {
        /*
         * Add a container that contains just a FilterParameter.
         */
        UIContainer filterContainer = UIContainerVertical.create(resources.getString("uiFilterContainer.TITLE"), BorderType.LINE);
        /*
         * Rule parameters are added to the user interface by using
         * UIRuleParameter objects.
         */
        filterContainer.addComponent(UIRuleParameter.create(idsRule.componentFilterParameter));

        return filterContainer;
    }

    /**
     * This method creates the middle panel of the parameters UI.
     */
    private UIContainer createContainerWithParameters() {
        /*
         * This container uses horizontal layout to place a column of parameters
         * next to an image.
         */
        UIContainer horizontalContainer = UIContainerHorizontal.create(resources.getString("uiParameterContainer.TITLE"), BorderType.LINE);

        /*
         * Create a vertical container without title of border.
         */
        UIContainer parameterContainer = UIContainerVertical.create();

        /*
         * Add different types of parameters into the parameter container.
         * In a vertical container the UI component that is added first will
         * be placed topmost.
         */
        parameterContainer.addComponent(UIRuleParameter.create(idsRule.doubleParameter));
        parameterContainer.addComponent(UIRuleParameter.create(idsRule.booleanParameter));
        parameterContainer.addComponent(UIRuleParameter.create(idsRule.propertyReferenceParameter));
        parameterContainer.addComponent(UIRuleParameter.create(idsRule.enumerationParameterForComboBox));

        /*
         * Add the parameter container to the horizontal container. As the
         * parameter container is added first, it will be at the leftmost side
         * of the container in the UI.
         */
        horizontalContainer.addComponent(parameterContainer);

        return horizontalContainer;
    }

}
