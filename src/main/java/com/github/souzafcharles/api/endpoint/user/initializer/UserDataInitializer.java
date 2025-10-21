package com.github.souzafcharles.api.endpoint.user.initializer;

import com.github.souzafcharles.api.client.FakeStoreClient;
import com.github.souzafcharles.api.endpoint.user.model.dto.UserResponseDTO;
import com.github.souzafcharles.api.endpoint.user.model.entity.User;
import com.github.souzafcharles.api.endpoint.user.repository.UserRepository;
import com.github.souzafcharles.api.utils.Messages;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component("userDataInitializer")
public class UserDataInitializer {

    private static final Logger log = LoggerFactory.getLogger(UserDataInitializer.class);

    private final UserRepository userRepository;
    private final FakeStoreClient fakeStoreClient;

    public UserDataInitializer(UserRepository userRepository, FakeStoreClient fakeStoreClient) {
        this.userRepository = userRepository;
        this.fakeStoreClient = fakeStoreClient;
    }

    @PostConstruct
    public void init() {
        if (userRepository.count() > 0) {
            log.info(Messages.USER_ALREADY_INITIALIZED);
            return;
        }

        UserResponseDTO[] users = fakeStoreClient.getAllUsers().block();
        if (users == null || users.length == 0) {
            log.warn(Messages.USER_NO_RETURNED);
            return;
        }

        List<User> entities = Arrays.stream(users)
                .map(dto -> {
                    User user = new User();
                    user.setId(dto.id());
                    user.setUsername(dto.username());
                    user.setEmail(dto.email());
                    user.setPassword("123456");
                    return user;
                })
                .toList();

        userRepository.saveAll(entities);
        log.info(Messages.USER_SAVED_SUCCESS, entities.size());
    }
}