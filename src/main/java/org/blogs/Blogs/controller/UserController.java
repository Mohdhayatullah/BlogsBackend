package org.blogs.Blogs.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.blogs.Blogs.dto.LoginDto;
import org.blogs.Blogs.dto.ProfileDto;
import org.blogs.Blogs.dto.SignUpDto;
import org.blogs.Blogs.service.UserServices;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
@Slf4j
public class UserController {

    private final UserServices userServices;

    @Scheduled(cron = "0 */10 * * * *")
    public void scheduledTest() {
        log.info("Scheduled run every 10 minutes (cron)...");
    }

    @PostMapping("/register")
    public ResponseEntity<SignUpDto> register(@RequestBody SignUpDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(userServices.registerUser(dto));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginDto dto) {
        return ResponseEntity.ok(userServices.login(dto));
    }

    @GetMapping("/profile")
    public ResponseEntity<ProfileDto> getProfile() {
        return ResponseEntity.ok(userServices.getData());
    }

    @PutMapping(value = "/profile", consumes = "multipart/form-data")
    public ResponseEntity<ProfileDto> updateProfile(
            @RequestPart("data") String data,
            @RequestPart(value = "file", required = false) MultipartFile file) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        ProfileDto profile = objectMapper.readValue(data, ProfileDto.class);
        return ResponseEntity.ok(userServices.putData(profile, file));
    }

    @PatchMapping("/password")
    public ResponseEntity<?> changePassword(@RequestParam String pass) {
        userServices.forgetPassword(pass);
        return ResponseEntity.ok("Password updated successfully");
    }
}
