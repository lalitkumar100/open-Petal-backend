package com.crimsonlogic.open_petal_backend.service;

import com.crimsonlogic.open_petal_backend.dto.user.AvailabilitySlot;
import com.crimsonlogic.open_petal_backend.entity.User;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;

/**
 * Service interface for user operations.
 */
public interface UserService {
    /**
     * Create User.
     */
    User createUser(User user);
    /**
     * Get User By Id.
     */
    Optional<User> getUserById(Long id);
    /**
     * Get User By Email.
     */
    Optional<User> getUserByEmail(String email);
    /**
     * Get All Users.
     */
    List<User> getAllUsers();
    /**
     * Get Paginated Users.
     */
    Page<User> getPaginatedUsers(int page, int size, String sortBy, String sortDir, String searchType, String searchQuery);
    /**
     * Update User.
     */
    User updateUser(Long id, User user);
    /**
     * Delete User.
     */
    void deleteUser(Long id);
    /**
     * Block User.
     */
    void blockUser(Long id, String reason);
    /**
     * Unblock User.
     */
    void unblockUser(Long id, String reason);

    /**
     * Add Availability Slot.
     */
    List<AvailabilitySlot> addAvailabilitySlot(Long userId, AvailabilitySlot newSlot);
    /**
     * Get User Availability Slots.
     */
    List<AvailabilitySlot> getUserAvailabilitySlots(Long userId);
    /**
     * Remove Availability Slot.
     */
    List<AvailabilitySlot> removeAvailabilitySlot(Long userId, AvailabilitySlot slotToRemove);
}
