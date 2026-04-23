package com.sachdeva.Aibot.Controller;

import com.sachdeva.Aibot.Model.Comment;
import com.sachdeva.Aibot.Model.Post;
import com.sachdeva.Aibot.Repositories.Commentrepo;
import com.sachdeva.Aibot.Repositories.Postrepo;
import com.sachdeva.Aibot.Service.NotificationService;
import com.sachdeva.Aibot.Service.RedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/posts")
public class Postcontroller {
    @Autowired
    private Postrepo postrepo;
    @Autowired
    private Commentrepo commentrepo;
    @Autowired
    private RedisService redisService;
    @Autowired
    private NotificationService notificationService;

    @PostMapping
    public ResponseEntity<Post>createPost( @RequestBody Post post){
        Post savedPost=postrepo.save(post);
        return ResponseEntity.ok(savedPost);
    }

    @PostMapping("/{postId}/comment")
    public ResponseEntity<Comment>addComment(@PathVariable Long postId, @RequestBody Comment comment){
        comment.setPostId(postId);
        Comment savedComment=commentrepo.save(comment);
        return ResponseEntity.ok(savedComment);
    }

    @PostMapping("/{postId}/like")
    public ResponseEntity<String> likePost(@PathVariable Long postId){
        return postrepo.findById(postId)
                .map(post -> ResponseEntity.ok("Post liked sucessfully"))
                .orElse(ResponseEntity.notFound().build());

    }

    @PostMapping("/{postId}/bot-comment")
    public ResponseEntity<?> addComment_Bot(@PathVariable Long postId,@RequestBody Comment comment){
        if (comment.getDepth_level() > 20){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Comment thread too deep max level 20");
        }
        if (redisService.isBotCountExceed(postId)){
            return  ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body("Bot reply limit Exceeds");
        }
        Long botId = comment.getAuthorId();
        Long humanId = comment.getPostId();
        if (redisService.isCooldownActive(botId, humanId)) {
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body("Bot is on cooldown for this user");
        }

        // All caps passed - save comment
        comment.setPostId(postId);
        comment.setAuthorType("BOT");
        Comment saved = commentrepo.save(comment);
        notificationService.handleBotInteraction(
                comment.getPostId(),
                "Bot " + comment.getAuthorId()
        );


        // Set cooldown and update score
        redisService.setCooldown(botId, humanId);
        redisService.updateViralityScore(postId, "BOT_REPLY");

        return ResponseEntity.ok(saved);

    }




}
