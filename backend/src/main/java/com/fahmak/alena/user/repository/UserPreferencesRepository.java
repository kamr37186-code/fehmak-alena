package com.fahmak.alena.user.repository;

import com.fahmak.alena.user.entity.UserPreferences;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserPreferencesRepository extends JpaRepository<UserPreferences, Long> {
    @Query("SELECT p FROM UserPreferences p JOIN FETCH p.user WHERE p.user.email = :email")
    Optional<UserPreferences> findByUserEmail(@Param("email") String email);
}

