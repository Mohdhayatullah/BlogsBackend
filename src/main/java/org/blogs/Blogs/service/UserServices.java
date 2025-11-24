package org.blogs.Blogs.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.blogs.Blogs.dto.LoginDto;
import org.blogs.Blogs.dto.ProfileDto;
import org.blogs.Blogs.dto.SignUpDto;
import org.blogs.Blogs.entity.UserEntity;

import org.blogs.Blogs.repository.UserRepository;
import org.blogs.Blogs.util.JwtUtil;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServices {

    private final UserRepository repository;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwt;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService service;



    @Transactional
    public SignUpDto registerUser(SignUpDto dto) throws IllegalAccessException {
        if(repository.existsByEmail(dto.getEmail())){
            throw new IllegalAccessException("Email already exist");
        }
        new UserEntity();
        UserEntity newUser;
        newUser = toEntity(dto);
        repository.save(newUser);
        service.sendMail(dto.getEmail(), "Registration successful","APPLICATION STATUS 200K\n\n😇Welcome to our BlogHub\n\nDiscover amazing stories, share your thoughts, and connect with writers from around the world.\n\n\n💕💕💕💕😍✅");
        return toDto(newUser);
    }

    public LoginDto login(LoginDto dto) throws IllegalAccessException {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(dto.getEmail(),dto.getPassword()));

            String token = jwt.generateToken(dto.getEmail());
            LoginDto loginDto = new LoginDto();
            loginDto.setEmail(dto.getEmail());
            loginDto.setToken(token);
            return loginDto;
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

//    private ProfileDto profileDto(Profile dto){
//        return ProfileDto.builder()
//                .id(dto.getId())
//                .fullName(dto.getUser().getFullName())
//                .email(dto.getUser().getEmail())
//                .phoneNumber(dto.getUser().getPhoneNumber())
//                .PhotoUrl(dto.getPhotoUrl())
//                .build();
//    }
//
//    private Profile profile(ProfileDto dto, UserEntity user){
//        //        if (dto.getEmail() != null){
//////            UserEntity entity = userRepository.findByEmail(dto.getEmail()).orElseThrow(()-> new IllegalArgumentException("user not found"));
////            entity.setId(entity.getId());
////            entity.setEmail(dto.getEmail());
////            entity.setFullName(dto.getFullName());
////            entity.setPhoneNumber(dto.getPhoneNumber());
////            dto1.setUser(entity);
////        }
//         return Profile.builder()
//                 .PhotoUrl(dto.getPhotoUrl())
//                 .user(user)
//                 .build();
//    }
//
//    // profile data
//    public ProfileDto getData(){
//        UserEntity cUser = getCurrentProfile();
//        Profile profile = repo.findByUser(cUser).orElseGet(()->{
//                Profile pro = new Profile();
//                pro.setUser(cUser);
//                pro.setPhotoUrl(new Profile().getPhotoUrl());
//                return repo.save(pro);
//                });
//
//        return profileDto(profile);
//    }
//
//    // profile data
//    public ProfileDto putData(ProfileDto profile){
////        Profile profile1 = userRepository.findByEmail(profile.getEmail()).orElseThrow(()-> new IllegalArgumentException("User Not found!, "));
////        profile1.setUser(profile.getEmail() != null ? profile.ugetEmail() : profile1.getUser());
////        profile1.setPhotoUrl(profile.getPhotoUrl() != null ? profile.getPhotoUrl() : profile1.getPhotoUrl());
//
//        UserEntity user = userRepository.findByEmail(profile.getEmail()).orElseThrow(()-> new IllegalArgumentException("user not found"));
//
//        user.setEmail(profile.getEmail() !=null? profile.getEmail() : user.getEmail());
//        user.setFullName(profile.getFullName() !=null? profile.getFullName() : user.getFullName());
//        user.setPhoneNumber(profile.getPhoneNumber() !=null? profile.getPhoneNumber() : user.getPhoneNumber());
//        Profile profile1 = repo.findByUser(user).orElseGet(()->{
//            Profile profile2 = new Profile();
//            profile2.setUser(user);
//            return profile2;
//        });
//        profile1.setPhotoUrl(profile.getPhotoUrl());
//        repo.save(profile1);
//        System.out.println(profile1.getUser());
//        System.out.println(profile1.getUser().getPhoneNumber());
//        System.out.println(profile1.getUser().getEmail());
//        System.out.println(profile1.getUser().getFullName());
//        System.out.println(profile1.getUser().getPhoneNumber());
//        System.out.println(profile1.getPhotoUrl());
//        return profileDto(profile1);
//    }



    public UserEntity forgetPassword(String pass){
        UserEntity user = getCurrentProfile();
        UserEntity newUser = userRepository.findByEmail(user.getEmail()).orElseThrow(()-> new IllegalArgumentException("User not found"));
        newUser.setPassword(pass);
        return userRepository.save(newUser);
    }

    // Profile Api
    public ProfileDto getData(){
        UserEntity currentProfileUser = getCurrentProfile();
//        return new ProfileDto(currentProfileUser.getId(),
//                currentProfileUser.getFullName(),
//                currentProfileUser.getEmail(),
//                currentProfileUser.getPhoneNumber(),
//                currentProfileUser.getPhotoUrl());
        return toProfileDto(currentProfileUser);
    }

    public ProfileDto putData(ProfileDto dto){
        UserEntity user = getCurrentProfile();
        user.setFullName(dto.getFullName() != null ? dto.getFullName() : user.getFullName());
        user.setPhoneNumber(dto.getPhoneNumber() != null ? dto.getPhoneNumber() : user.getPhoneNumber());
        user.setPhotoUrl(dto.getPhotoUrl() != null? dto.getPhotoUrl() : user.getPhotoUrl());
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
                .photoUrl(user.getPhotoUrl())
                .build();
    }

    private UserEntity toUserEntity(ProfileDto user){
        return UserEntity.builder()
                .fullName(user.getFullName())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .photoUrl(user.getPhotoUrl())
                .build();
    }

}
