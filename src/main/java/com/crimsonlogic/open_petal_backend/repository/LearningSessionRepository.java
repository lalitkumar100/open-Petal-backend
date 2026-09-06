package com.crimsonlogic.open_petal_backend.repository;

import com.crimsonlogic.open_petal_backend.entity.LearningSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import com.crimsonlogic.open_petal_backend.enums.SessionStatus;

@Repository
public interface LearningSessionRepository extends JpaRepository<LearningSession, Long> {

    @Query("SELECT COUNT(s) > 0 FROM LearningSession s WHERE s.learner.id = :learnerId AND s.mentor.id = :mentorId AND s.primarySkill.id = :primarySkillId AND s.status IN :statuses AND s.isDeleted = false")
    boolean existsActiveSessionRequest(
            @Param("learnerId") Long learnerId, 
            @Param("mentorId") Long mentorId, 
            @Param("primarySkillId") Long primarySkillId, 
            @Param("statuses") List<SessionStatus> statuses);

    @Query("SELECT COUNT(s) > 0 FROM LearningSession s WHERE s.mentor.id = :mentorId AND s.sessionDate = :sessionDate AND s.status IN ('SCHEDULED', 'STARTED') AND s.isDeleted = false AND (s.startTime < :endTime AND s.endTime > :startTime)")
    boolean existsOverlappingSession(
            @Param("mentorId") Long mentorId, 
            @Param("sessionDate") java.time.LocalDate sessionDate, 
            @Param("startTime") java.time.LocalTime startTime, 
            @Param("endTime") java.time.LocalTime endTime);

    @Query("SELECT s FROM LearningSession s " +
           "JOIN FETCH s.learner l " +
           "JOIN FETCH s.mentor m " +
           "JOIN FETCH s.primarySkill p " +
           "WHERE (l.id = :userId OR m.id = :userId) AND s.isDeleted = false " +
           "ORDER BY s.sessionDate DESC, s.startTime DESC")
    List<LearningSession> findAllUserSessions(@Param("userId") Long userId);

    java.util.List<LearningSession> findByMentorIdAndIsDeletedFalse(Long mentorId);
    
    java.util.List<LearningSession> findByStatusAndIsDeletedFalse(SessionStatus status);

    @Query("SELECT s FROM LearningSession s " +
           "JOIN FETCH s.learner l " +
           "JOIN FETCH s.mentor m " +
           "JOIN FETCH s.primarySkill p " +
           "WHERE s.isDeleted = false " +
           "ORDER BY s.createdAt DESC")
    java.util.List<LearningSession> findAllGlobalSessions();

    @Query("SELECT s FROM LearningSession s " +
           "JOIN FETCH s.learner l " +
           "JOIN FETCH s.mentor m " +
           "JOIN FETCH s.primarySkill p " +
           "LEFT JOIN FETCH s.offeredSkill os " +
           "LEFT JOIN FETCH s.location loc " +
           "WHERE s.id = :sessionId AND s.isDeleted = false")
    java.util.Optional<LearningSession> findSessionWithDetailsById(@Param("sessionId") Long sessionId);
}
