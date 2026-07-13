package com.sougata.auth_service.service;

import com.sougata.auth_service.constant.AuthProvider;
import com.sougata.auth_service.dto.JwtTokenResponseDto;
import com.sougata.auth_service.dto.LoginRequestDto;
import com.sougata.auth_service.dto.RegisterRequestDto;
import com.sougata.auth_service.exception.EmailIsAlreadyInUseException;
import com.sougata.auth_service.exception.WrongUsernameException;
import com.sougata.auth_service.model.User;
import com.sougata.auth_service.model.UserPrincipal;
import com.sougata.auth_service.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService {

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    @Autowired
    public AuthenticationService(UserRepository userRepository, JwtService jwtService, PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
    }

    public JwtTokenResponseDto registerUser(RegisterRequestDto dto) {

        var prevUser = userRepository.findByEmail(dto.email());

        if (prevUser.isPresent()) {
            var u = prevUser.get();

            if (u.getAuthProvider().name().equals(AuthProvider.EMAIL_PASSWORD.name())) {
                throw new EmailIsAlreadyInUseException("Email is already in use. Email: " + dto.email());
            }
        }

        User newUser = new User();

        newUser.setUsername(dto.username());
        newUser.setEmail(dto.email());
        newUser.setIsEmailVerified(false);
        newUser.setPasswordHash(passwordEncoder.encode(dto.password()));
        newUser.setAuthProvider(AuthProvider.EMAIL_PASSWORD);
        newUser.setAvatarUrl(dto.avatarUrl());

        User savedUser = userRepository.save(newUser);

        String jwt = jwtService.generateJwtToken(
                savedUser.getId().toString(), savedUser.getUsername(), savedUser.getEmail(), savedUser.getAvatarUrl()
        );

        return new JwtTokenResponseDto(jwt);
    }

    public JwtTokenResponseDto loginUser(LoginRequestDto dto) {

        var authToken = new UsernamePasswordAuthenticationToken(
                dto.email(), dto.password()
        );
        Authentication auth = authenticationManager.authenticate(authToken);

        if (auth.isAuthenticated() && auth.getPrincipal() instanceof UserPrincipal user) {

            String jwt = jwtService.generateJwtToken(
                    user.getId().toString(), user.getActualUsername(), user.getEmail(), user.getAvatarUrl()
            );

            return new JwtTokenResponseDto(jwt);
        }
        throw new WrongUsernameException();
    }

}
