package com.crimsonlogic.open_petal_backend.query.repository;

import com.crimsonlogic.open_petal_backend.query.entity.UserQuery;
import com.crimsonlogic.open_petal_backend.query.enums.QueryStatus;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserQueryRepository extends JpaRepository<UserQuery, Long> {
    List<UserQuery> findByUserIdOrderByCreatedAtDesc(Long userId);
    List<UserQuery> findByStatusOrderByCreatedAtDesc(QueryStatus status);
}
