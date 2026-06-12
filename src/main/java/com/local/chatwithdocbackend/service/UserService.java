package com.local.chatwithdocbackend.service;

import com.local.chatwithdocbackend.dto.CreateUserRequest;
import com.local.chatwithdocbackend.dto.UserDto;
import com.local.chatwithdocbackend.entity.User;
import com.local.chatwithdocbackend.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserDto createUser(CreateUserRequest request) {
        String userId = UUID.randomUUID().toString();
        User user = new User(userId, request.getName(), request.getEmail());
        User saved = userRepository.save(user);
        return new UserDto(saved.getId(), saved.getName(), saved.getEmail());
    }
}
