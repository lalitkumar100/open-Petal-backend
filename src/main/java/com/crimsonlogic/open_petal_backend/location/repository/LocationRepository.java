package com.crimsonlogic.open_petal_backend.location.repository;

import com.crimsonlogic.open_petal_backend.location.entity.Location;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface LocationRepository extends JpaRepository<Location, Long> {

    Optional<Location> findByIdAndIsDeletedFalse(Long id);

    List<Location> findByIsDeletedFalseOrderByCreatedAtDesc();
}
