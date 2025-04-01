package services;

import interfaces.NotificationService;
import model.User;

public class SMSNotificationService implements NotificationService {
    @Override
    public void sendNotification(User user, String message) {
        System.out.println("\nSending SMS to " + user.getPhoneNumber() + ":");
        System.out.println("--------------------------------");
        System.out.println("Hello " + user.getName() + ",");
        System.out.println(message);
        System.out.println("--------------------------------\n");
    }
}
