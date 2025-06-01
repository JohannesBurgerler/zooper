package com.trade4life.zooper.repository;

import com.trade4life.zooper.model.ERole;
import com.trade4life.zooper.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {

    @Query(value = "SELECT * from roles where name = :name", nativeQuery = true)
    Optional<Role> findByName(ERole name);
}
