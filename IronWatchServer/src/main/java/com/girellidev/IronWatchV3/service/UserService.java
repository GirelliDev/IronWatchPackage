package com.girellidev.ironwatchv3.service;

import com.girellidev.ironwatchv3.model.User;
import com.girellidev.ironwatchv3.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    private final UserRepository repository;

    public UserService(UserRepository repository) {
        this.repository = repository;
    }

    public User createUser(String username, String password) {
        User user = new User(username, password);
        return repository.save(user);
    }

    public List<User> listUsers() {
        return repository.findAll();
    }
}