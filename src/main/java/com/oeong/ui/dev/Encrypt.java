package com.oeong.ui.dev;

import com.intellij.openapi.project.Project;
import com.oeong.enums.EncryptEnum;
import com.oeong.notice.Notifier;
import lombok.Getter;
import org.jdesktop.swingx.JXRadioGroup;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.function.BiFunction;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * @descriptions:
 * @author: Zzw
 * @date: 2023/11/6 9:37
 */
public class Encrypt {
    static Map<String, BiFunction<String, EncryptEnum, String>> encryptMap = new HashMap<>();

    static {
        encryptMap.put("URL", Encrypt::urlTool);
        encryptMap.put("SHA256", Encrypt::SHA256Tool);
        encryptMap.put("SHA1", Encrypt::SHA1Tool);
        encryptMap.put("Base64", Encrypt::Base64Tool);
        encryptMap.put("MD5", Encrypt::MD5Tool);
        encryptMap.put("SHA512", Encrypt::SHA512Tool);
    }

    private Project project;
    private JTextArea inputArea;
    private JTextArea resultArea;
    private JRadioButton urlRadioButton;
    private JRadioButton SHA256RadioButton;
    private JRadioButton SHA1RadioButton;
    private JRadioButton Base64RadioButton;
    private JRadioButton MD5RadioButton;
    private JRadioButton SHA512RadioButton;
    private JButton encryptButton;
    private JButton decryptButton;
    @Getter
    private JPanel container;
    private JPanel buttonPanel;
    private JXRadioGroup<JRadioButton> group;

    private String radioSelected = "";

    public Encrypt(Project project) {
        urlRadioButton = new JRadioButton("URL");
        SHA256RadioButton = new JRadioButton("SHA256");
        SHA1RadioButton = new JRadioButton("SHA1");
        SHA512RadioButton = new JRadioButton("SHA512");
        Base64RadioButton = new JRadioButton("Base64");
        MD5RadioButton = new JRadioButton("MD5");
        urlRadioButton.setSelected(true);
        group.add(urlRadioButton);
        group.add(SHA256RadioButton);
        group.add(SHA1RadioButton);
        group.add(Base64RadioButton);
        group.add(MD5RadioButton);
        group.add(SHA512RadioButton);
        urlRadioButton.setSelected(true);
        List<JRadioButton> enableList = Arrays.asList(SHA256RadioButton, SHA1RadioButton, MD5RadioButton, SHA512RadioButton);
        // 添加监听器到按钮组上
        group.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JRadioButton selectedValue = group.getSelectedValue();
                String text = selectedValue.getText();
                if (enableList.contains(selectedValue)) {
                    decryptButton.setEnabled(Boolean.FALSE);
                } else {
                    decryptButton.setEnabled(Boolean.TRUE);
                }
                if (!text.equals(radioSelected)) {
                    changeRadio(text);
                }
            }
        });
        this.project = project;
    }

    private void changeRadio(String text) {
        ActionListener[] actionListeners = encryptButton.getActionListeners();
        if (actionListeners != null) {
            for (ActionListener actionListener : actionListeners) {
                encryptButton.removeActionListener(actionListener);
            }
        }

        encryptButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                BiFunction<String, EncryptEnum, String> function = encryptMap.get(text);
                String input = inputArea.getText();
                if (input == null || "".equals(input)) {
                    Notifier.notifyError("请输入需要加密的内容");
                    return;
                }
                String result = function.apply(input, EncryptEnum.encrypt);
                resultArea.setText(result);
                resultArea.updateUI();
            }
        });


        ActionListener[] decryptActionListeners = decryptButton.getActionListeners();
        if (decryptActionListeners != null) {
            for (ActionListener actionListener : decryptActionListeners) {
                decryptButton.removeActionListener(actionListener);
            }
        }
        decryptButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                BiFunction<String, EncryptEnum, String> function = encryptMap.get(text);
                String input = inputArea.getText();
                if (input == null || "".equals(input)) {
                    Notifier.notifyError("请输入需要解密的内容");
                    return;
                }
                String result = function.apply(input, EncryptEnum.decrypt);
                resultArea.setText(result);
                resultArea.updateUI();
            }
        });

        radioSelected = text;
    }

    public static String urlTool(String text, EncryptEnum type) {
        if (type == EncryptEnum.encrypt) {
            return URLEncoder.encode(text, UTF_8);
        } else if (type == EncryptEnum.decrypt) {
            return URLDecoder.decode(text, UTF_8);
        }
        return null;
    }

    public static String SHA1Tool(String text, EncryptEnum type) {
        if (type == EncryptEnum.encrypt) {
            return sha(text, "SHA-1");
        } else if (type == EncryptEnum.decrypt) {
            Notifier.notifyError("SHA1不支持解密");
        }
        return null;
    }

    public static String SHA256Tool(String text, EncryptEnum type) {
        if (type == EncryptEnum.encrypt) {
            return sha(text, "SHA-256");
        } else if (type == EncryptEnum.decrypt) {
            Notifier.notifyError("SHA256不支持解密");
        }
        return null;
    }

    public static String SHA512Tool(String text, EncryptEnum type) {
        if (type == EncryptEnum.encrypt) {
            return sha(text, "SHA-512");
        } else if (type == EncryptEnum.decrypt) {
            Notifier.notifyError("SHA256不支持解密");
        }
        return null;
    }

    public static String MD5Tool(String text, EncryptEnum type) {
        if (type == EncryptEnum.encrypt) {
            return sha(text, "MD5");
        } else if (type == EncryptEnum.decrypt) {
            Notifier.notifyError("MD5不支持解密");
        }
        return null;
    }

    public static String Base64Tool(String text, EncryptEnum type) {
        if (type == EncryptEnum.encrypt) {
            return Base64.getEncoder().encodeToString(text.getBytes());
        } else if (type == EncryptEnum.decrypt) {
            byte[] decodedBytes = Base64.getDecoder().decode(text);
            return new String(decodedBytes);
        }
        return null;
    }

    private static String sha(String text, String algorithm) {
        try {
            MessageDigest md = MessageDigest.getInstance(algorithm);
            byte[] hashedBytes = md.digest(text.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : hashedBytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
        }
        return null;
    }


}
