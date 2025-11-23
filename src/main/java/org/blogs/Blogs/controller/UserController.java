package org.blogs.Blogs.controller;


import lombok.RequiredArgsConstructor;
import org.blogs.Blogs.dto.LoginDto;
import org.blogs.Blogs.dto.ProfileDto;
import org.blogs.Blogs.dto.SignUpDto;
import org.blogs.Blogs.service.BlogsService;
import org.blogs.Blogs.service.EmailService;
import org.blogs.Blogs.service.UserServices;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class UserController {
    private final UserServices userServices;
    private final BlogsService blogsService;
    private final EmailService service;


    @PostMapping("/regis")
    public ResponseEntity<SignUpDto> register(@RequestBody SignUpDto dto){
        try{
            return ResponseEntity.status(HttpStatus.CREATED).body(userServices.registerUser(dto));
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/login")
    public ResponseEntity<LoginDto> login(@RequestBody LoginDto dto) {
        try {
            LoginDto user = userServices.login(dto);
            return ResponseEntity.status(HttpStatus.ACCEPTED).body(user);
        } catch (IllegalAccessException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

//    @GetMapping("/test")
//    public String teat(){
//        service.sendMail("faizubhai766@gmail.com","TEst","Application status 200k");
//        return "I am 200k, do you know what is 200k";
//    }

    @GetMapping("/p")
    public ResponseEntity<ProfileDto> getProfile(){
        try{
            return ResponseEntity.status(HttpStatus.OK).body(userServices.getData());
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @PutMapping("/put")
    public ResponseEntity<ProfileDto> putProfile(@RequestBody ProfileDto profile){
        try{
            System.out.println(profile.getPhotoUrl());
            return ResponseEntity.status(HttpStatus.OK).body(userServices.putData(profile));
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

}
