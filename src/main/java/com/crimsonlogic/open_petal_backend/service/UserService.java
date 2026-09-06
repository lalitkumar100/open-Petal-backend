package com.crimsonlogic.open_petal_backend.service;

import com.crimsonlogic.open_petal_backend.dto.user.AvailabilitySlot;
import com.crimsonlogic.open_petal_backend.entity.User;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;

public interface UserService {
    User createUser(User user);
    Optional<User> getUserById(Long id);
    Optional<User> getUserByEmail(String email);
    List<User> getAllUsers();
    Page<User> getPaginatedUsers(int page, int size, String sortBy, String sortDir, String searchType, String searchQuery);
    User updateUser(Long id, User user);
    void deleteUser(Long id);
    void blockUser(Long id, String reason);
    void unblockUser(Long id, String reason);

    List<AvailabilitySlot> addAvailabilitySlot(Long userId, AvailabilitySlot newSlot);
    List<AvailabilitySlot> getUserAvailabilitySlots(Long userId);
    List<AvailabilitySlot> removeAvailabilitySlot(Long userId, AvailabilitySlot slotToRemove);
}
