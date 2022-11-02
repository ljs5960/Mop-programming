package com.nhnacademy.gw1;

public class AppNotificationService implements NotificationService{
    @Override
    public boolean send(String message) {
        if (message.contains("성공")) {
            return true;
        }
        return false;
    }
}
