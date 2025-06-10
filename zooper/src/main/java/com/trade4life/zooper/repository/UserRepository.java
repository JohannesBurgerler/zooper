package com.trade4life.zooper.repository;

import com.trade4life.zooper.model.User;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    @Query(value = "SELECT * from User WHERE email = :email", nativeQuery = true)
    Optional<User> findByEmail(@Param("email") String email);

    @Query(value = "SELECT * from User WHERE username = :username", nativeQuery = true)
    Optional<User> findByUsername(@Param("username") String username);

    @Query(value = "SELECT id, username, email from User", nativeQuery = true)
    Optional<List<User>> findAllUsers();

    @Query("SELECT COUNT(u) > 0 FROM User u WHERE u.email = :email")
    boolean existsByEmail(@Param("email") String email);

    @Query("SELECT COUNT(u) > 0 FROM User u WHERE u.username = :username")
    boolean existsByUsername(@Param("username") String username);

    @Modifying
    @Transactional
    @Query(value = "INSERT INTO User (email, password, username) VALUES (:email, :password, :username)", nativeQuery = true)
    void manualRegister(@Param("email") String email, @Param("password") String password, @Param("username") String username);
}
