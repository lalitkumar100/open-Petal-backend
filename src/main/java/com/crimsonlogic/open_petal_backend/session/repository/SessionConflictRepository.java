package com.crimsonlogic.open_petal_backend.session.repository;

import com.crimsonlogic.open_petal_backend.session.entity.SessionConflict;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface SessionConflictRepository extends JpaRepository<SessionConflict, Long> {

    @Query("SELECT sc FROM SessionConflict sc " +
           "JOIN FETCH sc.session s " +
           "JOIN FETCH sc.raisedBy rb " +
           "WHERE s.id = :sessionId AND sc.isDeleted = false")
    java.util.Optional<SessionConflict> findBySessionId(@Param("sessionId") Long sessionId);

    @Query("SELECT c FROM SessionConflict c " +
           "JOIN FETCH c.session s " +
           "JOIN FETCH s.learner l " +
           "JOIN FETCH s.mentor m " +
           "JOIN FETCH s.primarySkill p " +
           "JOIN FETCH c.raisedBy rb " +
           "WHERE (l.id = :userId OR m.id = :userId) " +
           "AND c.isDeleted = false AND s.isDeleted = false " +
           "ORDER BY c.createdAt DESC")
    List<SessionConflict> findAllMyConflicts(@Param("userId") Long userId);

    @Query("SELECT c FROM SessionConflict c " +
           "JOIN FETCH c.session s " +
           "JOIN FETCH s.learner l " +
           "JOIN FETCH s.mentor m " +
           "JOIN FETCH s.primarySkill p " +
           "JOIN FETCH c.raisedBy rb " +
           "LEFT JOIN FETCH c.resolvedBy rba " +
           "WHERE c.isDeleted = false AND s.isDeleted = false " +
           "ORDER BY c.createdAt DESC")
    List<SessionConflict> findAllGlobalConflicts();

    @Query("SELECT c FROM SessionConflict c " +
           "JOIN FETCH c.session s " +
           "JOIN FETCH s.learner l " +
           "JOIN FETCH s.mentor m " +
           "JOIN FETCH s.primarySkill p " +
           "JOIN FETCH c.raisedBy rb " +
           "LEFT JOIN FETCH c.resolvedBy rba " +
           "WHERE c.id = :conflictId " +
           "AND c.isDeleted = false AND s.isDeleted = false")
    java.util.Optional<SessionConflict> findAdminConflictDetailById(@Param("conflictId") Long conflictId);
}
