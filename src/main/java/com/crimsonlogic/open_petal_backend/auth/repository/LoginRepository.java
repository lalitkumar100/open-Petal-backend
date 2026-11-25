package com.crimsonlogic.open_petal_backend.auth.repository;

import com.crimsonlogic.open_petal_backend.auth.entity.Login;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LoginRepository extends JpaRepository<Login, Long> {
    Optional<Login> findByEmail(String email);
    boolean existsByEmail(String email);
}
