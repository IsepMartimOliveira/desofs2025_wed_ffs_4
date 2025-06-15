package com.example.psoft_22_23_project.usermanagement.services;

import com.example.psoft_22_23_project.usermanagement.model.User;

public interface NotificationService {
    /**
     * Sends a notification to the user.

     */
    void sendNotification(User user);

    /**
     * Sends a notification to all users.
     *
     * @param message the message to send
     */
}
