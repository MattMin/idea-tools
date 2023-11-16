package com.oeong.ui.ai;

import com.intellij.ide.util.PropertiesComponent;
import com.oeong.gpt.core.OpenAISettingsState;

import javax.swing.*;

public class GPTSettingDialog extends JDialog {

    // https://platform.openai.com/docs/models/gpt-3-5
    private JComboBox modelBox;
    private JTextField keyField;
    private JButton okButton;
    private JButton cancelButton;
    private JPanel contentPane;
    OpenAISettingsState state = OpenAISettingsState.getInstance();
    PropertiesComponent propertiesComponent = PropertiesComponent.getInstance();

    public GPTSettingDialog() {
        setContentPane(contentPane);
        setModal(true);
        setTitle("openAI Setting");
        setSize(400, 200);
        SwingUtilities.invokeLater(() -> setLocationRelativeTo(null)); // 居中
        getRootPane().setDefaultButton(okButton);

        String openaiKey = state.apiKey;
        String openaiModel = state.gpt35Model;


//        String openaiKey = propertiesComponent.getValue("openaiKey");
//        String openaiModel = propertiesComponent.getValue("openaiModel");

        keyField.setText(openaiKey != null ? openaiKey : "");
        modelBox.setSelectedItem(openaiModel != null ? openaiModel : "gpt-3.5-turbo");

        cancelButton.addActionListener(e -> onCancel());
        okButton.addActionListener(e -> onOK());
    }

    private void onOK() {
        String apiKey = keyField.getText();
        Object selectedItem = modelBox.getSelectedItem();
        assert selectedItem != null;

        state.apiKey = apiKey;
        state.gpt35Model = selectedItem.toString();

//        propertiesComponent.setValue("openaiKey", apiKey);
//        propertiesComponent.setValue("openaiModel", selectedItem.toString());

        dispose();
    }

    private void onCancel() {
        dispose();
    }
}
