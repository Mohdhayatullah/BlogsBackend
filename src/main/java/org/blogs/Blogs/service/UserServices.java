package org.blogs.Blogs.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.blogs.Blogs.dto.CloudinaryDto;
import org.blogs.Blogs.dto.LoginDto;
import org.blogs.Blogs.dto.ProfileDto;
import org.blogs.Blogs.dto.SignUpDto;
import org.blogs.Blogs.entity.UserEntity;

import org.blogs.Blogs.repository.UserRepository;
import org.blogs.Blogs.util.CloudinaryService;
import org.blogs.Blogs.util.JwtUtil;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class UserServices {

    private final UserRepository repository;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwt;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService service;
    private final CloudinaryService cloudinaryService;



    @Transactional
    public SignUpDto registerUser(SignUpDto dto){
        if(repository.existsByEmail(dto.getEmail())){
            throw new RuntimeException("Email already exist");
        }
//
        UserEntity newUser = toEntity(dto);
        repository.save(newUser);
        service.sendWelcomeEmail(newUser.getEmail(), newUser.getFullName());
        return toDto(newUser);
    }

    public Map<String,String> login(LoginDto dto) {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(dto.getEmail(),dto.getPassword()));

            String token = jwt.generateToken(dto.getEmail());
            return Map.of(
                    "token",token
            );
        }catch (Exception e){
            throw new RuntimeException("Invalid email or password");
        }
    }

    public UserEntity getCurrentProfile(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return userRepository.findByEmail(authentication.getName())
                .orElseThrow(()-> new UsernameNotFoundException("User not found with this email"));
    }


    private UserEntity toEntity(SignUpDto dto){
        return UserEntity.builder()
                .fullName(dto.getFullName())
                .email(dto.getEmail())
                .password(passwordEncoder.encode(dto.getPassword()))
                .otp(dto.getOtp())
                .phoneNumber(dto.getPhoneNumber())
                .build();
    }

    private SignUpDto toDto(UserEntity dto){
        return SignUpDto.builder()
                .fullName(dto.getFullName())
                .email(dto.getEmail())
                .password(dto.getPassword())
                .otp(dto.getOtp())
                .phoneNumber(dto.getPhoneNumber())
                .build();
    }



    public UserEntity forgetPassword(String pass){
        UserEntity user = getCurrentProfile();
        UserEntity newUser =
                userRepository.findByEmail(user.getEmail())
                        .orElseThrow(()-> new UsernameNotFoundException("User not found"));
        newUser.setPassword(passwordEncoder.encode(pass));
        return userRepository.save(newUser);
    }

    // Profile Api
    public ProfileDto getData(){
        UserEntity currentProfileUser = getCurrentProfile();
        return toProfileDto(currentProfileUser);
    }

    public ProfileDto putData(ProfileDto dto, MultipartFile file) {

        UserEntity user = getCurrentProfile();
        if (user == null) {
            throw new UsernameNotFoundException("User not authenticated");
        }

        if (dto.getFullName() != null) {
            user.setFullName(dto.getFullName());
        }

        if (dto.getPhoneNumber() != null) {
            user.setPhoneNumber(dto.getPhoneNumber());
        }

        // ⚠️ optional: restrict email update
        if (dto.getEmail() != null) {
            user.setEmail(dto.getEmail());
        }

        // ✅ FIXED
        if (file != null && !file.isEmpty()) {
            CloudinaryDto cloudinaryDto =
                    cloudinaryService.uploadOrReplaceImage(user.getPublic_id(), file);

            user.setPublic_id(cloudinaryDto.getPublicId());
            user.setImageUrl(cloudinaryDto.getSecureUrl());
        }

        userRepository.save(user);
        return toProfileDto(user);
    }

    // mapping method to assist DTO and sensitive data prevent
    private ProfileDto toProfileDto(UserEntity user){
        return ProfileDto.builder()
                .id(user.getId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .imageUrl(user.getImageUrl())
                .build();
    }

    private UserEntity toUserEntity(ProfileDto user){
        return UserEntity.builder()
                .fullName(user.getFullName())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .imageUrl(user.getImageUrl())
                .build();
    }

}
