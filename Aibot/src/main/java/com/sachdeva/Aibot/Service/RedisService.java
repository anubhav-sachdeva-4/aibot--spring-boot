package com.sachdeva.Aibot.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Service
public class RedisService {
    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    public void updateViralityScore(Long postId,String interactionType){
        String key ="post"+ postId+":virality_score";
        switch (interactionType){
            case "BOT_REPLY" -> redisTemplate.opsForValue().increment(key,1);
            case "HUMAN_LIKES" -> redisTemplate.opsForValue().increment(key,20);
            case "HUMAN_COMMENT" -> redisTemplate.opsForValue().increment(key,50);

        }
    }
    public String getViralityScore(Long postId){
        String key ="post"+ postId+":virality_score";
        String score = redisTemplate.opsForValue().get(key);
        return score!=null?score:"0";
    }
    public Boolean isBotCountExceed(Long postId){
        String key="post"+ postId+":bot_count ";
        Long count = redisTemplate.opsForValue().increment(key);
        if (count>100){
            redisTemplate.opsForValue().decrement(key);
            return true;
        }

            return false;


    }
    public boolean isCooldownActive(Long botId, Long humanId) {
        String key = "cooldown:bot_" + botId + ":human_" + humanId;
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }

    public void setCooldown(Long botId, Long humanId) {
        String key = "cooldown:bot_" + botId + ":human_" + humanId;
        redisTemplate.opsForValue().set(key, "true", 10, TimeUnit.MINUTES);
    }

    // ============ NOTIFICATION ENGINE ============

    public boolean isNotificationCooldownActive(Long userId) {
        String key = "notif_cooldown:user_" + userId;
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }

    public void setNotificationCooldown(Long userId) {
        String key = "notif_cooldown:user_" + userId;
        redisTemplate.opsForValue().set(key, "true", 15, TimeUnit.MINUTES);
    }

    public void addPendingNotification(Long userId, String message) {
        String key = "user:" + userId + ":pending_notifs";
        redisTemplate.opsForList().rightPush(key, message);
    }

    public List<String> popAllNotifications(Long userId) {
        String key = "user:" + userId + ":pending_notifs";
        List<String> notifications = redisTemplate.opsForList().range(key, 0, -1);
        redisTemplate.delete(key);
        return notifications;
    }

    public Set<String> getPendingNotificationKeys() {
        return redisTemplate.keys("user:*:pending_notifs");
    }

}
