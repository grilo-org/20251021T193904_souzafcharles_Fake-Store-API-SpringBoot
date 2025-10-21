package com.github.souzafcharles.api.endpoint.user.service;

import com.github.souzafcharles.api.exceptions.custom.DatabaseException;
import com.github.souzafcharles.api.exceptions.custom.DuplicateEmailException;
import com.github.souzafcharles.api.exceptions.custom.ResourceNotFoundException;
import com.github.souzafcharles.api.endpoint.user.model.dto.UserRequestDTO;
import com.github.souzafcharles.api.endpoint.user.model.dto.UserResponseDTO;
import com.github.souzafcharles.api.endpoint.user.model.entity.User;
import com.github.souzafcharles.api.endpoint.user.repository.UserRepository;
import com.github.souzafcharles.api.utils.Messages;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Page<UserResponseDTO> getAllUsers(Pageable pageable) {
        List<UserResponseDTO> allUsers = userRepository.findAll().stream()
                .map(UserResponseDTO::new)
                .toList();

        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), allUsers.size());
        return new PageImpl<>(allUsers.subList(start, end), pageable, allUsers.size());
    }

    public UserResponseDTO getUserById(String id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.forUser(id));
        return new UserResponseDTO(user);
    }

    public UserResponseDTO createUser(UserRequestDTO dto) {
        userRepository.findByEmail(dto.email())
                .ifPresent(existing -> { throw new DuplicateEmailException(dto.email()); });

        User user = new User();
        user.setUsername(dto.username());
        user.setEmail(dto.email());
        user.setPassword(dto.password());

        try {
            return new UserResponseDTO(userRepository.save(user));
        } catch (DataIntegrityViolationException e) {
            throw new DatabaseException(e.getMessage());
        }
    }

    public UserResponseDTO updateUser(String id, UserRequestDTO dto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.forUser(id));

        if (dto.email() != null) {
            userRepository.findByEmail(dto.email())
                    .filter(existing -> !existing.getId().equals(id))
                    .ifPresent(existing -> { throw new DuplicateEmailException(dto.email()); });
        }

        user.setUsername(dto.username());
        user.setEmail(dto.email());
        user.setPassword(dto.password());

        try {
            return new UserResponseDTO(userRepository.save(user));
        } catch (DataIntegrityViolationException e) {
            throw new DatabaseException(e.getMessage());
        }
    }

    public void deleteUser(String id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.forUser(id));
        try {
            userRepository.delete(user);
        } catch (DataIntegrityViolationException e) {
            throw new DatabaseException(e.getMessage());
        }
    }

    public UserResponseDTO getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .map(UserResponseDTO::new)
                .orElseThrow(() -> ResourceNotFoundException.forUser(username));
    }

    public UserResponseDTO getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .map(UserResponseDTO::new)
                .orElseThrow(() -> ResourceNotFoundException.forUser(email));
    }

    public List<UserResponseDTO> searchUsersByUsername(String keyword) {
        return userRepository.findByUsernameContainingIgnoreCase(keyword).stream()
                .map(UserResponseDTO::new)
                .toList();
    }

    public long countUsers() {
        return userRepository.countByUsernameIsNotNull();
    }
}