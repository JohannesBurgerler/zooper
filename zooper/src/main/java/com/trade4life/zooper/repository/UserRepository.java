package com.trade4life.zooper.repository;

import com.trade4life.zooper.model.User;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    @Query("SELECT * from User u WHERE u.email = :email")
    Optional<User> findByEmail(@Param("email") String email);

    @Modifying
    @Transactional
    @Query(value = "INSERT INTO users (email, password, username) VALUES (:email, :password)", nativeQuery = true)
    void manualRegister(@Param("email") String email, @Param("password") String password, @Param("username") String username);
}
