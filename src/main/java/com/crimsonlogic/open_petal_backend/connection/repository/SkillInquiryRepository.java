package com.crimsonlogic.open_petal_backend.connection.repository;

import com.crimsonlogic.open_petal_backend.connection.entity.SkillInquiry;
import com.crimsonlogic.open_petal_backend.connection.enums.InquiryStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SkillInquiryRepository extends JpaRepository<SkillInquiry, Long> {

    // For the mentor's dashboard to see incoming requests
    Page<SkillInquiry> findByReceiverIdAndStatus(Long receiverId, InquiryStatus status, Pageable pageable);

    // To prevent spam (check if a pending request already exists between these users for this skill)
    boolean existsBySenderIdAndReceiverIdAndSkillIdAndStatus(Long senderId, Long receiverId, Long skillId, InquiryStatus status);
}
