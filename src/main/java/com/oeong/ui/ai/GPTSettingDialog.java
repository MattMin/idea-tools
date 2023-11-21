package com.oeong.ui.ai;

import com.oeong.gpt.core.OpenAISettingsState;

import javax.swing.*;

public class GPTSettingDialog extends JDialog {

    // https://platform.openai.com/docs/models/gpt-3-5
    private JComboBox modelBox;
    private JTextField keyField;
    private JButton okButton;
    private JButton cancelButton;
    private JPanel contentPane;
    private JTextField urlField;
    OpenAISettingsState state = OpenAISettingsState.getInstance();

    public GPTSettingDialog() {
        setContentPane(contentPane);
        setModal(true);
        setTitle("openAI Setting");
        setSize(500, 200);
        SwingUtilities.invokeLater(() -> setLocationRelativeTo(null)); // 居中
        getRootPane().setDefaultButton(okButton);

        String openaiKey = state.apiKey;
        String openaiUrl = state.OPENAI_URL;
        String openaiModel = state.gpt35Model;

        keyField.setText(openaiKey != null ? openaiKey : "");
        modelBox.setSelectedItem(openaiModel != null ? openaiModel : "gpt-3.5-turbo");
        urlField.setText(openaiUrl != null ? openaiUrl : "https://api.openai-proxy.com/v1/chat/completions");

        cancelButton.addActionListener(e -> onCancel());
        okButton.addActionListener(e -> onOK());
    }

    private void onOK() {
        String apiKey = keyField.getText();
        Object selectedItem = modelBox.getSelectedItem();
        String url = urlField.getText();
        assert selectedItem != null;

        state.apiKey = apiKey;
        state.gpt35Model = selectedItem.toString();
        state.OPENAI_URL = url;

        dispose();
    }

    private void onCancel() {
        dispose();
    }
}
