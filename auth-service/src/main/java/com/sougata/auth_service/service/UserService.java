package com.sougata.auth_service.service;

import com.sougata.auth_service.dto.UserSummariesResDto;
import com.sougata.auth_service.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    public UserSummariesResDto getUserSummaries(List<UUID> userIds) {
        var users = userRepository.getUserSummaries(userIds);

        return new UserSummariesResDto(users);
    }
}
