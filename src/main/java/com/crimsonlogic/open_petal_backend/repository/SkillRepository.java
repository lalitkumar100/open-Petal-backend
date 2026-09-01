package com.crimsonlogic.open_petal_backend.repository;

import com.crimsonlogic.open_petal_backend.entity.Skill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SkillRepository extends JpaRepository<Skill, Long> {
    Optional<Skill> findByName(String name);
    Optional<Skill> findBySlug(String slug);
    boolean existsByName(String name);
}