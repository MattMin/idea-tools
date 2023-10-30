package com.oeong.action;

import com.intellij.icons.AllIcons;

/**
 * @descriptions: 截图
 * @author: Zzw
 * @date: 2023/10/19 9:49
 */
public class ScreenshotAction extends CustomAction {

    public ScreenshotAction() {
        super("com.oeong.action.ScreenshotAction", "(drag the mouse to select the screenshot area, ESC key to exit, " +
                "enter key to confirm the screenshot area, after the screenshot is completed, " +
                "it will be saved to the clipboard)", "Screenshot", AllIcons.CodeWithMe.CwmScreenOn);
    }




}
