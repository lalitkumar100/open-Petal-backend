package com.crimsonlogic.open_petal_backend.skill.repository;

import com.crimsonlogic.open_petal_backend.skill.entity.Skill;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SkillRepository extends JpaRepository<Skill, Long> {
    Optional<Skill> findByName(String name);
    Optional<Skill> findBySlug(String slug);
    boolean existsByName(String name);
    Page<Skill> findByCategoryIdAndIsDeletedFalse(Long categoryId, Pageable pageable);
    Page<Skill> findByCategoryIdAndNameContainingIgnoreCaseAndIsDeletedFalse(Long categoryId, String name, Pageable pageable);
}
