package com.sougata.auth_service.service;

import com.sougata.auth_service.constant.UserRole;
import com.sougata.auth_service.dto.JwtTokenResponseDto;
import com.sougata.auth_service.dto.LoginRequestDto;
import com.sougata.auth_service.dto.RegisterRequestDto;
import com.sougata.auth_service.exception.UserRoleMismatchException;
import com.sougata.auth_service.exception.UserRoleNotFoundException;
import com.sougata.auth_service.exception.WrongUsernameException;
import com.sougata.auth_service.model.User;
import com.sougata.auth_service.repository.UserRepository;
import com.sougata.auth_service.util.Generators;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
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
        User newUser = new User();

        if (dto.username() == null) {
            newUser.setUsername(Generators.getRandomUsername());
        } else {
            newUser.setUsername(dto.username());
        }

        newUser.setEmail(dto.email());
        newUser.setPhoneNumber(dto.phoneNumber());
        newUser.setAvatarUrl(dto.avatarUrl());
        newUser.setPasswordHash(passwordEncoder.encode(dto.password()));
        newUser.setRole(UserRole.USER);

        User savedUser = userRepository.save(newUser);

        String jwt = jwtService.generateJwtToken(savedUser.getId().toString(), savedUser.getRole().name());

        return new JwtTokenResponseDto(jwt);
    }

    public JwtTokenResponseDto loginUser(LoginRequestDto dto) {
        User retrievedUser = userRepository.findByEmailOrUsername(dto.identifier(), dto.identifier())
                .orElseThrow(WrongUsernameException::new);

        var authToken = new UsernamePasswordAuthenticationToken(
                retrievedUser.getId().toString(), retrievedUser.getPasswordHash()
        );
        Authentication auth = authenticationManager.authenticate(authToken);

        if (auth.isAuthenticated() && auth.getPrincipal() instanceof UserDetails user) {
            String role = user.getAuthorities()
                    .stream()
                    .findFirst()
                    .orElseThrow(UserRoleNotFoundException::new)
                    .getAuthority();
            if(!UserRole.USER.name().equals(role)) {
                throw new UserRoleMismatchException();
            }
            String jwt = jwtService.generateJwtToken(
                    user.getUsername(), role
            );

            return new JwtTokenResponseDto(jwt);
        }
        throw new WrongUsernameException();
    }

}
