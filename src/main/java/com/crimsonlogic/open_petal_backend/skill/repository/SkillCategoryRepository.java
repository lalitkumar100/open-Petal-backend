package com.crimsonlogic.open_petal_backend.skill.repository;

import com.crimsonlogic.open_petal_backend.skill.entity.SkillCategory;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SkillCategoryRepository extends JpaRepository<SkillCategory, Long> {

    boolean existsByNameIgnoreCase(String name);

    Optional<SkillCategory> findByNameIgnoreCase(String name);

    Page<SkillCategory> findByNameContainingIgnoreCase(String name, Pageable pageable);
}
