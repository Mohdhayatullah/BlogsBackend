package org.blogs.Blogs.service;

import lombok.RequiredArgsConstructor;
import org.blogs.Blogs.entity.UserEntity;
import org.blogs.Blogs.repository.UserRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class CustomUserService implements UserDetailsService {

    private final UserRepository userRepository;
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        UserEntity user = userRepository.findByEmail(email).orElseThrow(()->
                new IllegalArgumentException("User not found with this email"));

        CustomUserDetails customUserDetails = new CustomUserDetails(user);
        return User.builder()
                .username(user.getEmail())
                .password(user.getPassword())
                .authorities(Collections.emptyList())
                .build();
    }
}
