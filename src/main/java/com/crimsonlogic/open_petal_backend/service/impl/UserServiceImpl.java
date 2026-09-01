package com.crimsonlogic.open_petal_backend.service.impl;

import com.crimsonlogic.open_petal_backend.dto.user.AvailabilitySlot;
import com.crimsonlogic.open_petal_backend.entity.User;
import com.crimsonlogic.open_petal_backend.entity.Login;
import com.crimsonlogic.open_petal_backend.enumerator.AccountStatus;
import com.crimsonlogic.open_petal_backend.repository.UserRepository;
import com.crimsonlogic.open_petal_backend.repository.LoginRepository;
import com.crimsonlogic.open_petal_backend.service.UserService;
import com.crimsonlogic.open_petal_backend.exception.RecordNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final LoginRepository loginRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, LoginRepository loginRepository) {
        this.userRepository = userRepository;
        this.loginRepository = loginRepository;
    }

    @Override
    public User createUser(User user) {
        return userRepository.save(user);
    }

    @Override
    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    @Override
    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public User updateUser(Long id, User userDetails) {
        return userRepository.findById(id).map(existingUser -> {
            existingUser.setFirstName(userDetails.getFirstName());
            existingUser.setLastName(userDetails.getLastName());
            existingUser.setPhone(userDetails.getPhone());
            existingUser.setDob(userDetails.getDob());
            existingUser.setGender(userDetails.getGender());
            return userRepository.save(existingUser);
        }).orElseThrow(() -> new RecordNotFoundException("User not found with id " + id));
    }

    @Override
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    @Override
    public void blockUser(Long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new RecordNotFoundException("User not found with id " + id));
        Login login = user.getLogin();
        if (login != null) {
            login.setStatus(AccountStatus.BLOCKED);
            loginRepository.save(login);
        }
    }

    @Override
    public void unblockUser(Long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new RecordNotFoundException("User not found with id " + id));
        Login login = user.getLogin();
        if (login != null) {
            login.setStatus(AccountStatus.ACTIVE);
            loginRepository.save(login);
        }
    }

    package com.crimsonlogic.open_petal_backend.service.impl;

import com.crimsonlogic.open_petal_backend.dto.user.AvailabilitySlot;
import com.crimsonlogic.open_petal_backend.entity.User;
import com.crimsonlogic.open_petal_backend.entity.Login;
import com.crimsonlogic.open_petal_backend.enumerator.AccountStatus;
import com.crimsonlogic.open_petal_backend.repository.UserRepository;
import com.crimsonlogic.open_petal_backend.repository.LoginRepository;
import com.crimsonlogic.open_petal_backend.service.UserService;
import com.crimsonlogic.open_petal_backend.exception.RecordNotFoundException;
import com.crimsonlogic.open_petal_backend.util.AvailabilitySlotManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

    @Service
    @Transactional
    public class UserServiceImpl implements UserService {

        private final UserRepository userRepository;
        private final LoginRepository loginRepository;

        @Autowired
        public UserServiceImpl(UserRepository userRepository, LoginRepository loginRepository) {
            this.userRepository = userRepository;
            this.loginRepository = loginRepository;
        }

        @Override
        public User createUser(User user) {
            return userRepository.save(user);
        }

        @Override
        @Transactional(readOnly = true)
        public Optional<User> getUserById(Long id) {
            return userRepository.findById(id);
        }

        @Override
        @Transactional(readOnly = true)
        public Optional<User> getUserByEmail(String email) {
            return userRepository.findByEmail(email);
        }

        @Override
        @Transactional(readOnly = true)
        public List<User> getAllUsers() {
            return userRepository.findAll();
        }

        @Override
        public User updateUser(Long id, User userDetails) {
            return userRepository.findById(id).map(existingUser -> {
                existingUser.setFirstName(userDetails.getFirstName());
                existingUser.setLastName(userDetails.getLastName());
                existingUser.setPhone(userDetails.getPhone());
                existingUser.setDob(userDetails.getDob());
                existingUser.setGender(userDetails.getGender());
                existingUser.setDescription(userDetails.getDescription());
                return userRepository.save(existingUser);
            }).orElseThrow(() -> new RecordNotFoundException("User not found with id " + id));
        }

        @Override
        public void deleteUser(Long id) {
            userRepository.deleteById(id);
        }

        @Override
        public void blockUser(Long id) {
            User user = userRepository.findById(id)
                    .orElseThrow(() -> new RecordNotFoundException("User not found with id " + id));
            Login login = user.getLogin();
            if (login != null) {
                login.setStatus(AccountStatus.BLOCKED);
                loginRepository.save(login);
            }
        }

        @Override
        public void unblockUser(Long id) {
            User user = userRepository.findById(id)
                    .orElseThrow(() -> new RecordNotFoundException("User not found with id " + id));
            Login login = user.getLogin();
            if (login != null) {
                login.setStatus(AccountStatus.ACTIVE);
                loginRepository.save(login);
            }
        }

        // =========================================================================
        // Weekly Availability Slot Management Methods
        // =========================================================================

        @Override
        public List<AvailabilitySlot> addAvailabilitySlot(Long userId, AvailabilitySlot newSlot) {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RecordNotFoundException("User not found with id " + userId));

            List<AvailabilitySlot> currentSlots = user.getAvailableTimeInWeek();
            if (currentSlots == null) {
                currentSlots = new ArrayList<>();
            }

            // Auto-validates slot constraints (1-24h span), overrides overlaps, and merges touching blocks
            List<AvailabilitySlot> updatedSlots = AvailabilitySlotManager.addOrMergeSlot(currentSlots, newSlot);
            user.setAvailableTimeInWeek(updatedSlots);

            userRepository.save(user);
            return updatedSlots;
        }

        @Override
        public List<AvailabilitySlot> getUserAvailabilitySlots(Long userId) {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RecordNotFoundException("User not found with id " + userId));
            return user.getAvailableTimeInWeek() != null ? user.getAvailableTimeInWeek() : new ArrayList<>();
        }

        @Override
        public List<AvailabilitySlot> removeAvailabilitySlot(Long userId, AvailabilitySlot slotToRemove) {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RecordNotFoundException("User not found with id " + userId));

            List<AvailabilitySlot> currentSlots = user.getAvailableTimeInWeek();
            if (currentSlots != null) {
                currentSlots.removeIf(slot ->
                        slot.getDayOfWeek() == slotToRemove.getDayOfWeek() &&
                                slot.getStartTime().equals(slotToRemove.getStartTime()) &&
                                slot.getEndTime().equals(slotToRemove.getEndTime())
                );
                user.setAvailableTimeInWeek(currentSlots);
                userRepository.save(user);
            }
            return user.getAvailableTimeInWeek();
        }
    }
}
