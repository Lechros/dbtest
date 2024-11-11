package com.lechros.dbtest;

import com.lechros.dbtest.domain.User;
import com.lechros.dbtest.repository.UserRepository;
import com.lechros.dbtest.service.UserService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Import(DbUtils.class)
public class DbCleanTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DbUtils dbUtils;

    @AfterEach
    void afterTest_userCount_isZero() {
        dbUtils.resetAllTables();

        assertThat(userRepository.findAll()).hasSize(0);
    }

    @Test
    public void addUsers() throws InterruptedException {
        final int COUNT = 6;

        List<User> users = new ArrayList<>(COUNT);
        for (int i = 0; i < COUNT; i++) {
            users.add(userWithName("TEST_USER_NAME#" + i));
        }

        ExecutorService service = Executors.newFixedThreadPool(COUNT);
        CountDownLatch latch = new CountDownLatch(COUNT);

        for (int i = 0; i < COUNT; i++) {
            final int index = i;
            service.execute(() -> {
                userService.addUser(users.get(index));
                latch.countDown();
            });
        }

        latch.await();

        Thread.sleep(500);
        List<User> savedUsers = userRepository.findAll();
        assertThat(savedUsers).hasSize(COUNT);
    }

    private static User userWithName(String name) {
        User user = new User();
        user.setName(name);
        return user;
    }
}
