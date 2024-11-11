package com.lechros.dbtest.service;

import com.lechros.dbtest.domain.User;
import com.lechros.dbtest.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    @Transactional
    public boolean addUser(User user) {
        userRepository.save(user);
        return true;
    }
}
