package com.sachdeva.Aibot.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


    @Service
    public class NotificationService {

        @Autowired
        private RedisService redisService;

        public void handleBotInteraction(Long userId, String botName) {
            String message = botName + " replied to your post";

            if (isNotificationCooldownActive(userId)) {
                // User notified recently → queue it
                redisService.addPendingNotification(userId, message);
                System.out.println("Notification queued for User " + userId);
            } else {
                // No recent notification → send immediately
                System.out.println("Push Notification Sent to User " + userId
                        + ": " + message);
                redisService.setNotificationCooldown(userId);
            }
        }

        private boolean isNotificationCooldownActive(Long userId) {
            return redisService.isNotificationCooldownActive(userId);
        }
    }

