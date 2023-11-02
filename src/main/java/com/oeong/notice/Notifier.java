package com.oeong.notice;

import com.intellij.notification.Notification;
import com.intellij.notification.NotificationGroupManager;
import com.intellij.notification.Notifications;
import com.intellij.openapi.ui.MessageType;

public class Notifier {
    public static void notifyInfo(String content) {
        NotificationGroupManager groupManager = NotificationGroupManager.getInstance();
        Notification notification = groupManager.getNotificationGroup("Custom Notification Group")
                .createNotification(content, MessageType.INFO);
        Notifications.Bus.notify(notification);
    }

    public static void notifyWarn(String content) {
        NotificationGroupManager groupManager = NotificationGroupManager.getInstance();
        Notification notification = groupManager.getNotificationGroup("Custom Notification Group")
                .createNotification(content, MessageType.WARNING);
        Notifications.Bus.notify(notification);
    }

    public static void notifyError(String content) {
        NotificationGroupManager groupManager = NotificationGroupManager.getInstance();
        Notification notification = groupManager.getNotificationGroup("Custom Notification Group")
                .createNotification(content, MessageType.ERROR);
        Notifications.Bus.notify(notification);
    }
}