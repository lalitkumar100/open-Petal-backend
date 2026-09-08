package com.crimsonlogic.open_petal_backend.repository;

import com.crimsonlogic.open_petal_backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

@Repository
public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {
    Optional<User> findByEmail(String email);
    long countByCreatedAtAfter(java.time.LocalDateTime time);

    @org.springframework.data.jpa.repository.Query("SELECT DISTINCT u FROM User u " +
           "JOIN u.skillsOffered us " +
           "WHERE u.id != :userId AND us.skill.id IN :learningSkillIds AND us.isDeleted = false")
    java.util.List<User> findMentorsBySkills(@org.springframework.data.repository.query.Param("userId") Long userId, 
                                             @org.springframework.data.repository.query.Param("learningSkillIds") java.util.List<Long> learningSkillIds);

    @org.springframework.data.jpa.repository.Query("SELECT DISTINCT u FROM User u " +
           "JOIN u.skillsOffered us " +
           "JOIN u.learningGoals lg " +
           "WHERE u.id != :userId " +
           "AND us.skill.id IN :learningSkillIds AND us.isDeleted = false " +
           "AND lg.skill.id IN :offeredSkillIds AND lg.isDeleted = false")
    java.util.List<User> findBarterPartnersBySkills(@org.springframework.data.repository.query.Param("userId") Long userId, 
                                                    @org.springframework.data.repository.query.Param("learningSkillIds") java.util.List<Long> learningSkillIds, 
                                                    @org.springframework.data.repository.query.Param("offeredSkillIds") java.util.List<Long> offeredSkillIds);
}
