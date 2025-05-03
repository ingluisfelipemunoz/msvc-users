package com.felipe.springcloud.msvc.users.services;

import java.util.Optional;

import com.felipe.springcloud.msvc.users.entities.User;

public interface IUserService {

    Optional<User> findById(Long id);

    Optional<User> findByUsername(String username);

    Iterable<User> findAll();

    User save(User user);

    Optional<User> update(Long id, User user);

    void delete(Long id);
}
