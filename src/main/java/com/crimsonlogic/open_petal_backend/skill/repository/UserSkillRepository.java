package com.crimsonlogic.open_petal_backend.skill.repository;

import com.crimsonlogic.open_petal_backend.skill.entity.UserSkill;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserSkillRepository extends JpaRepository<UserSkill, Long> {

    List<UserSkill> findByUserId(Long userId);

    Optional<UserSkill> findByUserIdAndSkillId(Long userId, Long skillId);

    Optional<UserSkill> findByUserIdAndSkillIdAndIsDeletedFalse(Long userId, Long skillId);

    boolean existsByUserIdAndSkillId(Long userId, Long skillId);

    void deleteByUserIdAndSkillId(Long userId, Long skillId);

    long countBySkillIdAndIsDeletedFalseAndUserLoginIsDeletedFalse(Long skillId);
}
