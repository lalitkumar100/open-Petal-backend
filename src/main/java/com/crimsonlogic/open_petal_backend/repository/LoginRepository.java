package com.crimsonlogic.open_petal_backend.repository;

import com.crimsonlogic.open_petal_backend.entity.Login;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LoginRepository extends JpaRepository<Login, Long> {
    Optional<Login> findByEmail(String email);
    boolean existsByEmail(String email);
}
