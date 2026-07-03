package ar.edu.unnoba.pdyc2026.notification_service.client;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "user-service")
public interface UserClient {
    @GetMapping("/internal/users/interested")
    List<String> getInterestedUsers(@RequestParam("eventId") Long eventId, @RequestParam("artistIds") List<Long> artistIds);
}