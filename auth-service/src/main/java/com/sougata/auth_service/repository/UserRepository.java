package com.sougata.auth_service.repository;

import com.sougata.auth_service.constant.AuthProvider;
import com.sougata.auth_service.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByEmail(String email);

    Optional<User> findByEmailAndAuthProvider(String email, AuthProvider authProvider);

    boolean existsByEmail(String email);

    boolean existsBySocialAuthId(String socialAuthId);

    boolean existsBySocialAuthIdAndAuthProvider(String socialAuthId, AuthProvider authProvider);

    Optional<User> findBySocialAuthIdAndAuthProvider(String socialAuthId, AuthProvider authProvider);
}
