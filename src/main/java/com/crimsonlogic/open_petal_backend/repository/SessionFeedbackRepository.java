package com.crimsonlogic.open_petal_backend.repository;

import com.crimsonlogic.open_petal_backend.entity.SessionFeedback;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SessionFeedbackRepository extends JpaRepository<SessionFeedback, Long> {

    Optional<SessionFeedback> findBySessionIdAndReviewerId(Long sessionId, Long reviewerId);

    @Query("SELECT AVG(f.rating) FROM SessionFeedback f WHERE f.reviewee.id = :userId AND f.skillRated.id = :skillId")
    Double getAverageRatingForUserSkill(@Param("userId") Long userId, @Param("skillId") Long skillId);

}
