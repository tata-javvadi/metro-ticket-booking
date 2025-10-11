package com.metro.booking.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;
import java.time.Duration;

@Repository
@RequiredArgsConstructor
public class StationRedisRepository {
    private final RedisTemplate<String, String> redisTemplate;

    public void addActiveTicket(String stationId, String ticketId, Duration ttl) {
        redisTemplate.opsForSet().add("station:" + stationId, ticketId);
        redisTemplate.expire("station:" + stationId, ttl);
    }

    public boolean isTicketActive(String stationId, String ticketId) {
        Boolean result = redisTemplate.opsForSet().isMember("station:" + stationId, ticketId);
        return result != null && result;
    }

    public void removeTicket(String stationId, String ticketId) {
        redisTemplate.opsForSet().remove("station:" + stationId, ticketId);
    }
}
