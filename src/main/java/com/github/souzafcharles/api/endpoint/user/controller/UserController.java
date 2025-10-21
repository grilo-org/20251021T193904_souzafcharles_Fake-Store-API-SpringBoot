package com.github.souzafcharles.api.endpoint.user.controller;

import com.github.souzafcharles.api.endpoint.user.model.dto.UserRequestDTO;
import com.github.souzafcharles.api.endpoint.user.model.dto.UserResponseDTO;
import com.github.souzafcharles.api.endpoint.user.service.UserService;
import com.github.souzafcharles.api.utils.Messages;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
@Tag(name = "User API", description = Messages.USER_TAG_DESCRIPTION)
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    @Operation(summary = Messages.USER_GET_ALL_SUMMARY, description = Messages.USER_GET_ALL_DESCRIPTION)
    public ResponseEntity<PagedModel<EntityModel<UserResponseDTO>>> getAll(
            Pageable pageable,
            PagedResourcesAssembler<UserResponseDTO> assembler
    ) {
        var page = userService.getAllUsers(pageable);
        var model = assembler.toModel(page, user ->
                EntityModel.of(user, WebMvcLinkBuilder.linkTo(
                        WebMvcLinkBuilder.methodOn(UserController.class).getById(user.id())
                ).withSelfRel())
        );
        return page.isEmpty()
                ? ResponseEntity.noContent().build()
                : ResponseEntity.ok(model);
    }

    @GetMapping("/{id}")
    @Operation(summary = Messages.USER_GET_BY_ID_SUMMARY, description = Messages.USER_GET_BY_ID_DESCRIPTION)
    public ResponseEntity<UserResponseDTO> getById(@PathVariable String id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @PostMapping
    @Operation(summary = Messages.USER_CREATE_SUMMARY, description = Messages.USER_CREATE_DESCRIPTION)
    public ResponseEntity<UserResponseDTO> create(@RequestBody @Valid UserRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.createUser(dto));
    }

    @PutMapping("/{id}")
    @Operation(summary = Messages.USER_UPDATE_SUMMARY, description = Messages.USER_UPDATE_DESCRIPTION)
    public ResponseEntity<UserResponseDTO> update(
            @PathVariable String id,
            @RequestBody @Valid UserRequestDTO dto
    ) {
        return ResponseEntity.ok(userService.updateUser(id, dto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = Messages.USER_DELETE_SUMMARY, description = Messages.USER_DELETE_DESCRIPTION)
    public ResponseEntity<Void> delete(@PathVariable String id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    @Operation(summary = Messages.USER_SEARCH_SUMMARY, description = Messages.USER_SEARCH_DESCRIPTION)
    public ResponseEntity<List<UserResponseDTO>> searchByUsername(@RequestParam String keyword) {
        return ResponseEntity.ok(userService.searchUsersByUsername(keyword));
    }

    @GetMapping("/by-username")
    @Operation(summary = Messages.USER_GET_BY_USERNAME_SUMMARY, description = Messages.USER_GET_BY_USERNAME_DESCRIPTION)
    public ResponseEntity<UserResponseDTO> getByUsername(@RequestParam String username) {
        return ResponseEntity.ok(userService.getUserByUsername(username));
    }

    @GetMapping("/by-email")
    @Operation(summary = Messages.USER_GET_BY_EMAIL_SUMMARY, description = Messages.USER_GET_BY_EMAIL_DESCRIPTION)
    public ResponseEntity<UserResponseDTO> getByEmail(@RequestParam String email) {
        return ResponseEntity.ok(userService.getUserByEmail(email));
    }

    @GetMapping("/count")
    @Operation(summary = Messages.USER_COUNT_SUMMARY, description = Messages.USER_COUNT_DESCRIPTION)
    public ResponseEntity<Long> countUsers() {
        return ResponseEntity.ok(userService.countUsers());
    }
}