package com.example.springsecurityclient.repositories;

import com.example.springsecurityclient.entities.PasswordResetToken;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {

    Optional<PasswordResetToken> findTokenByExpiredAndToken(boolean expired, String token);

    @Query("Select p from PasswordResetToken p where p.user.id = :userId")
    Optional<PasswordResetToken> findPasswordResetTokenByUser(@Param("userId") Long userId);

    @Modifying
    @Transactional
    @Query("Update PasswordResetToken p set p.expired = false where p.id = :id")
    void refreshToken(@Param("id") Long id);
}
