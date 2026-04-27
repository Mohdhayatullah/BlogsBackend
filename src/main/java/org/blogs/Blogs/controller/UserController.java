package org.blogs.Blogs.controller;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.blogs.Blogs.dto.LoginDto;
import org.blogs.Blogs.dto.ProfileDto;
import org.blogs.Blogs.dto.SignUpDto;
import org.blogs.Blogs.service.UserServices;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final UserServices userServices;

    // ✅ TEST
    @GetMapping("/test")
    public ResponseEntity<String> test() {
        return ResponseEntity.ok("I am running");
    }


    // ✅ REGISTER
    @PostMapping("/register")
    public ResponseEntity<SignUpDto> register(@RequestBody SignUpDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(userServices.registerUser(dto));
    }

    // ✅ LOGIN
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginDto dto) {
        return ResponseEntity.ok(userServices.login(dto));
    }

    // ✅ GET PROFILE
    @GetMapping("/profile")
    public ResponseEntity<ProfileDto> getProfile() {
        return ResponseEntity.ok(userServices.getData());
    }

    // ✅ UPDATE PROFILE (WITH IMAGE)
    @PutMapping(value = "/profile", consumes = "multipart/form-data")
    public ResponseEntity<ProfileDto> updateProfile(
            @RequestPart("data") String data,
            @RequestPart(value = "file", required = false) MultipartFile file) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        System.out.println("this is data that comes from client side: "+data);
        ProfileDto profile = objectMapper.readValue(data, ProfileDto.class);
        System.out.println(profile.getFullName()+profile.getEmail()+profile.getPhoneNumber());
        return ResponseEntity.ok(userServices.putData(profile, file));
    }

    // ✅ CHANGE PASSWORD (FIXED NAME)
    @PatchMapping("/password")
    public ResponseEntity<?> changePassword(@RequestParam String pass) {
        userServices.forgetPassword(pass);
        return ResponseEntity.ok("Password updated successfully");
    }
}
