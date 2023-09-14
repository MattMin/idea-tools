package com.oeong.ui;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.ui.JBColor;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBTextArea;
import com.intellij.util.ui.JBDimension;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class HashTool {

    public static JPanel createHashPanel(Project project) {
        JPanel parent = new JPanel();
        JBTextArea areaPanel = new JBTextArea("This is a sample");
        areaPanel.setLineWrap(true);
        areaPanel.setBorder(BorderFactory.createLineBorder(JBColor.BLUE));
        areaPanel.setPreferredSize(new JBDimension(400, 100));
        parent.setLayout(new GridLayout(3, 1));
        JPanel p1 = new JPanel();
        p1.add(new JBLabel("明文"));
        p1.add(areaPanel);
        parent.add(p1);
        JPanel btPanel = new JPanel();
        parent.add(btPanel);
        JButton bt = (JButton) btPanel.add(new JButton("SHA1"));
        bt.addActionListener(e -> {
        });
        btPanel.add(new JButton("SHA224"));
        btPanel.add(new JButton("SHA256"));
        btPanel.add(new JButton("SHA384"));
        btPanel.add(new JButton("SHA512"));
        JPanel p2 = new JPanel();
        var areaPanel2 = new JBTextArea("this is a sample");
        areaPanel2.setLineWrap(true);
        areaPanel2.setBorder(BorderFactory.createLineBorder(JBColor.BLUE));
        areaPanel2.setPreferredSize(new JBDimension(400, 100));
        p2.add(new JBLabel("密文"));
        p2.add(areaPanel2);
        parent.add(p2);

        var p = new JPanel(new FlowLayout(FlowLayout.CENTER));
        p.add(parent);
        return p;
    }


    /**
     * SHA1 实现
     *
     * @param project
     * @param inStr
     * @return
     * @throws Exception
     */
    @NotNull
    public static String shaEncode(Project project, String inStr) throws Exception {
        MessageDigest sha = null;
        try {
            sha = MessageDigest.getInstance("SHA");
        } catch (Exception e) {
            Messages.showMessageDialog(project, e.getMessage(),
                    "SHA错误",
                    Messages.getErrorIcon());
            return "";
        }

        byte[] byteArray = inStr.getBytes(StandardCharsets.UTF_8);
        byte[] md5Bytes = sha.digest(byteArray);
        StringBuilder hexValue = new StringBuilder();
        for (int i = 0; i < md5Bytes.length; i++) {
            int val = ((int) md5Bytes[i]) & 0xff;
            if (val < 16) {
                hexValue.append("0");
            }
            hexValue.append(Integer.toHexString(val));
        }
        return hexValue.toString();
    }


    public static String sha224(Project project, String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-224");
            byte[] messageDigest = md.digest(input.getBytes());
            BigInteger no = new BigInteger(1, messageDigest);
            String hashtext = no.toString(16);
            while (hashtext.length() < 32) {
                hashtext = "0" + hashtext;
            }
            return hashtext;
        } catch (NoSuchAlgorithmException e) {
            Messages.showMessageDialog(project, e.getMessage(), "SHA224异常", Messages.getErrorIcon());
        }
        return "";
    }

}
