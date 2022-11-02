package com.nhnacademy.gw1;

public class SmsNotificationService implements NotificationService{

    private boolean isMessageSent;

    @Override
    public boolean send(String message) {
        return this.isMessageSent;
    }

    public void setIsMessageSent( boolean isMessageSent){
        this.isMessageSent = isMessageSent;
    }
}
