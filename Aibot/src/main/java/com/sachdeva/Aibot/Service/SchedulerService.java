package com.sachdeva.Aibot.Service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
    public class SchedulerService {

        @Autowired
        private RedisService redisService;

        // Runs every 5 minutes
        @Scheduled(fixedRate = 30000    )
        public void sweepPendingNotifications() {
            System.out.println("CRON Sweeper running...");

            Set<String> keys = redisService.getPendingNotificationKeys();

            if (keys == null || keys.isEmpty()) {
                System.out.println("No pending notifications found");
                return;
            }

            for (String key : keys) {
                // Extract userId from key "user:{id}:pending_notifs"
                String userId = key.split(":")[1];

                List<String> notifications = redisService
                        .popAllNotifications(Long.parseLong(userId));

                if (notifications != null && !notifications.isEmpty()) {
                    String firstBot = notifications.get(0)
                            .replace(" replied to your post", "");
                    int othersCount = notifications.size() - 1;

                    if (othersCount > 0) {
                        System.out.println("Summarized Push Notification: "
                                + firstBot + " and " + othersCount
                                + " others interacted with your posts. (User "
                                + userId + ")");
                    } else {
                        System.out.println("Summarized Push Notification: "
                                + firstBot
                                + " interacted with your post. (User "
                                + userId + ")");
                    }
                }
            }
        }
    }

