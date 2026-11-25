package com.crimsonlogic.open_petal_backend.search.service;

import com.crimsonlogic.open_petal_backend.connection.repository.ConnectionRequestRepository;
import com.crimsonlogic.open_petal_backend.common.exception.RecordNotFoundException;
import com.crimsonlogic.open_petal_backend.search.dto.SkillBadgeDto;
import com.crimsonlogic.open_petal_backend.search.dto.UserDetailsDto;
import com.crimsonlogic.open_petal_backend.search.dto.UserSearchSummaryDto;
import com.crimsonlogic.open_petal_backend.user.entity.User;
import com.crimsonlogic.open_petal_backend.user.repository.UserRepository;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SearchServiceImpl implements SearchService {

    private final UserRepository userRepository;
    private final com.crimsonlogic.open_petal_backend.connection.repository.ConnectionRequestRepository connectionRequestRepository;

    @Override
    @Transactional(readOnly = true)
    public Page<UserSearchSummaryDto> searchUsers(String searchType, String query, Pageable pageable) {
        Specification<User> spec = (root, queryCriteria, cb) -> {
            if (query == null || query.trim().isEmpty()) {
                return cb.conjunction();
            }

            String searchPattern = "%" + query.trim().toLowerCase() + "%";

            if ("skill".equalsIgnoreCase(searchType)) {
                // Join skillsOffered and learningGoals to search by skill name
                Join<Object, Object> offeredSkillsJoin = root.join("skillsOffered", JoinType.LEFT);
                Join<Object, Object> offeredSkillMasterJoin = offeredSkillsJoin.join("skill", JoinType.LEFT);

                Join<Object, Object> learningGoalsJoin = root.join("learningGoals", JoinType.LEFT);
                Join<Object, Object> learningSkillMasterJoin = learningGoalsJoin.join("skill", JoinType.LEFT);

                Predicate offeredSkillMatch = cb.like(cb.lower(offeredSkillMasterJoin.get("name")), searchPattern);
                Predicate learningSkillMatch = cb.like(cb.lower(learningSkillMasterJoin.get("name")), searchPattern);

                queryCriteria.distinct(true);
                return cb.or(offeredSkillMatch, learningSkillMatch);
            } else {
                // Default to searching by user info (name, email)
                Predicate firstNameMatch = cb.like(cb.lower(root.get("firstName")), searchPattern);
                Predicate lastNameMatch = cb.like(cb.lower(root.get("lastName")), searchPattern);
                Predicate emailMatch = cb.like(cb.lower(root.get("email")), searchPattern);

                return cb.or(firstNameMatch, lastNameMatch, emailMatch);
            }
        };

        Page<User> usersPage = userRepository.findAll(spec, pageable);
        return usersPage.map(this::convertToSummaryDto);
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetailsDto getUserDetails(Long userId, Long loggedInUserId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RecordNotFoundException("User not found with id: " + userId));

        UserDetailsDto dto = convertToDetailsDto(user);
        
        dto.setConnectionStatus("NONE");
        dto.setConnectionSender(false);
        dto.setConnectionRequestId(null);

        if (loggedInUserId != null && !loggedInUserId.equals(userId)) {
            connectionRequestRepository.findConnectionBetweenUsers(loggedInUserId, userId).ifPresent(cr -> {
                dto.setConnectionStatus(cr.getStatus().name());
                dto.setConnectionSender(cr.getSender().getId().equals(loggedInUserId));
                dto.setConnectionRequestId(cr.getId());
            });
        }

        return dto;
    }

    private UserSearchSummaryDto convertToSummaryDto(User user) {
        List<SkillBadgeDto> learningSkills = user.getLearningGoals() == null ? new ArrayList<>() :
                user.getLearningGoals().stream()
                        .filter(lg -> Boolean.FALSE.equals(lg.getIsDeleted()))
                        .map(lg -> SkillBadgeDto.builder()
                                .id(lg.getSkill() != null ? lg.getSkill().getId() : null)
                                .name(lg.getSkill() != null ? lg.getSkill().getName() : "Unknown")
                                .level(lg.getTargetLevel() != null ? lg.getTargetLevel().name() : "BEGINNER")
                                .build())
                        .collect(Collectors.toList());

        List<SkillBadgeDto> techSkills = user.getSkillsOffered() == null ? new ArrayList<>() :
                user.getSkillsOffered().stream()
                        .filter(us -> Boolean.FALSE.equals(us.getIsDeleted()))
                        .map(us -> SkillBadgeDto.builder()
                                .id(us.getSkill() != null ? us.getSkill().getId() : null)
                                .name(us.getSkill() != null ? us.getSkill().getName() : "Unknown")
                                .level(us.getSkillLevel() != null ? us.getSkillLevel().name() : "INTERMEDIATE")
                                .creditsPerSession(us.getCreditsPerSession())
                                .sessionDurationMin(us.getSessionDurationMin())
                                .build())
                        .collect(Collectors.toList());

        return UserSearchSummaryDto.builder()
                .userId(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .avatarUrl("https://i.pravatar.cc/150?img=" + (user.getId() % 70 + 1))
                .description(user.getDescription() != null ? user.getDescription() : "No bio available.")
                .learningSkills(learningSkills)
                .techSkills(techSkills)
                .build();
    }

    private UserDetailsDto convertToDetailsDto(User user) {
        String status = "ACTIVE";
        String role = "USER";
        if (user.getLogin() != null) {
            status = user.getLogin().getStatus() != null ? user.getLogin().getStatus().name() : "ACTIVE";
            role = user.getLogin().getRole() != null ? user.getLogin().getRole().name() : "USER";
        }

        String createdAt = user.getCreatedAt() != null ?
                user.getCreatedAt().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) : "";

        UserDetailsDto.UserInfoDto userInfo = UserDetailsDto.UserInfoDto.builder()
                .userId(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .status(status)
                .role(role)
                .credit(user.getCredit())
                .createdAt(createdAt)
                .build();

        List<SkillBadgeDto> skillsOffered = user.getSkillsOffered() == null ? new ArrayList<>() :
                user.getSkillsOffered().stream()
                        .filter(us -> Boolean.FALSE.equals(us.getIsDeleted()))
                        .map(us -> SkillBadgeDto.builder()
                                .id(us.getSkill() != null ? us.getSkill().getId() : null)
                                .name(us.getSkill() != null ? us.getSkill().getName() : "Unknown")
                                .level(us.getSkillLevel() != null ? us.getSkillLevel().name() : "INTERMEDIATE")
                                .creditsPerSession(us.getCreditsPerSession())
                                .sessionDurationMin(us.getSessionDurationMin())
                                .build())
                        .collect(Collectors.toList());

        List<SkillBadgeDto> learningGoals = user.getLearningGoals() == null ? new ArrayList<>() :
                user.getLearningGoals().stream()
                        .filter(lg -> Boolean.FALSE.equals(lg.getIsDeleted()))
                        .map(lg -> SkillBadgeDto.builder()
                                .id(lg.getSkill() != null ? lg.getSkill().getId() : null)
                                .name(lg.getSkill() != null ? lg.getSkill().getName() : "Unknown")
                                .level(lg.getTargetLevel() != null ? lg.getTargetLevel().name() : "BEGINNER")
                                .build())
                        .collect(Collectors.toList());

        return UserDetailsDto.builder()
                .userInfo(userInfo)
                .skillsOffered(skillsOffered)
                .learningGoals(learningGoals)
                .build();
    }
}
