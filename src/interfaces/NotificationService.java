package interfaces;

import model.User;

public interface NotificationService {
    void sendNotification(User user, String message);
}
