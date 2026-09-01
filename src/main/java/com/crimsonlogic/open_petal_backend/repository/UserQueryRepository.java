package com.crimsonlogic.open_petal_backend.repository;

import com.crimsonlogic.open_petal_backend.entity.UserQuery;
import com.crimsonlogic.open_petal_backend.enumerator.QueryStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserQueryRepository extends JpaRepository<UserQuery, Long> {
    List<UserQuery> findByUserIdOrderByCreatedAtDesc(Long userId);
    List<UserQuery> findByStatusOrderByCreatedAtDesc(QueryStatus status);
}
