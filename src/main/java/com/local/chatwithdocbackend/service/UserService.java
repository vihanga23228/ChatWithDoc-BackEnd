package com.local.chatwithdocbackend.service;

import com.local.chatwithdocbackend.dto.CreateUserRequest;
import com.local.chatwithdocbackend.dto.UserDto;
import com.local.chatwithdocbackend.entity.User;
import com.local.chatwithdocbackend.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public UserDto createUser(CreateUserRequest request) {
        String userId = UUID.randomUUID().toString();
        String encodedPassword = passwordEncoder.encode(request.getPassword());
        User user = new User(
            userId,
            request.getName(),
            request.getEmail(),
            encodedPassword,
            request.getRole()
        );
        User saved = userRepository.save(user);
        return new UserDto(saved.getId(), saved.getName(), saved.getEmail(), saved.getRole());
    }
}
