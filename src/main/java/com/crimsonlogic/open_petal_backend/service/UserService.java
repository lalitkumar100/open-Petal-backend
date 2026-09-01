package com.crimsonlogic.open_petal_backend.service;

import com.crimsonlogic.open_petal_backend.dto.user.AvailabilitySlot;
import com.crimsonlogic.open_petal_backend.entity.User;
import java.util.List;
import java.util.Optional;

public interface UserService {
    User createUser(User user);
    Optional<User> getUserById(Long id);
    Optional<User> getUserByEmail(String email);
    List<User> getAllUsers();
    User updateUser(Long id, User user);
    void deleteUser(Long id);
    void blockUser(Long id);
    void unblockUser(Long id);

    List<AvailabilitySlot> addAvailabilitySlot(Long userId, AvailabilitySlot newSlot);
    List<AvailabilitySlot> getUserAvailabilitySlots(Long userId);
    List<AvailabilitySlot> removeAvailabilitySlot(Long userId, AvailabilitySlot slotToRemove);
}
