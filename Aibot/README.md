# AiBot - Spring Boot Redis Microservice

## Tech Stack
- Java 17
- Spring Boot 3.x
- PostgreSQL
- Redis (Spring Data Redis)

## Setup Instructions

### Prerequisites
- Docker Desktop
- Java 17+
- Maven

### Run the project
1. Clone the repository
2. Start Docker containers:
   docker-compose up -d
3. Run the Spring Boot application from IntelliJ or:
   ./mvnw spring-boot:run

## API Endpoints

| Method | URL | Description |
|--------|-----|-------------|
| POST | /api/posts | Create a post |
| POST | /api/posts/{id}/comments | Add human comment |
| POST | /api/posts/{id}/like | Like a post |
| POST | /api/posts/{id}/bot-comment | Bot comment with guardrails |

## Phase 2 - How Thread Safety is Guaranteed

### Horizontal Cap (Max 100 bot replies)
Redis INCR command is used to increment the bot counter atomically. Even if 200 concurrent requests hit simultaneously, Redis processes each increment one at a time sequentially. This guarantees the counter never exceeds 100. If count exceeds 100, we immediately DECREMENT back and reject with 429 Too Many Requests.

### Vertical Cap (Max 20 depth levels)
Simple check on depthLevel field in the request body. If depthLevel > 20, request is rejected with 400 Bad Request. No Redis needed here.

### Cooldown Cap (Bot cannot interact with same human twice in 10 min)
Redis SET with TTL of 10 minutes is used. The key format is cooldown:bot_{id}:human_{id}. If key exists, bot is blocked with 403 Forbidden. Redis automatically deletes the key after 10 minutes.

### Why Not Java synchronized or HashMap?
The app is completely stateless. All state lives in Redis, not in Java memory. This means multiple instances of the app can run simultaneously and they all share the same Redis state. Using HashMap or static variables would break in a multi-instance environment.

## Phase 3 - Notification Engine

### Redis Throttler
- First bot interaction with a user → immediate console log + 15 min cooldown set in Redis
- Subsequent bot interactions within 15 min → notification queued in Redis List (user:{id}:pending_notifs)

### CRON Sweeper
- Runs every 5 minutes
- Scans all users with pending notifications
- Logs summarized message: "Bot X and N others interacted with your posts"
- Clears the Redis list for that user
