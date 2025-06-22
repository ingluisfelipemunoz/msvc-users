package com.felipe.springcloud.msvc.users.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.felipe.springcloud.msvc.users.entities.Role;
import com.felipe.springcloud.msvc.users.entities.User;
import com.felipe.springcloud.msvc.users.repositories.RoleRepository;
import com.felipe.springcloud.msvc.users.repositories.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UserService implements IUserService {

    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Transactional(readOnly = true)
    public Iterable<User> findAll() {
        return userRepository.findAll();
    }

    @Transactional
    public User save(User user) {
        // Encrypt the password before saving
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setEnabled(true);
        user.setRoles(getDefaultRoles(user));
        return userRepository.save(user);
    }

    @Transactional
    public void delete(Long id) {
        userRepository.deleteById(id);
    }

    @Override
    @Transactional
    public Optional<User> update(Long id, User user) {
        Optional<User> userOptional = this.findById(id);

        return userOptional.map(userDb -> {
            // set fields
            userDb.setEmail(user.getEmail());
            userDb.setUsername(user.getUsername());
            if (user.isEnabled() == null) {
                userDb.setEnabled(true);
            } else {
                userDb.setEnabled(user.isEnabled());
            }

            // set roles
            userDb.setRoles(this.getDefaultRoles(user));

            return Optional.of(userRepository.save(userDb));
        }).orElseGet(() -> Optional.empty());
    }

    @Override
    @Transactional
    public Optional<User> resetPassword(String username, String newPassword) {
        Optional<User> userOpt = userRepository.findByUsername(username);
        return userOpt.map(user -> {
            user.setPassword(passwordEncoder.encode(newPassword));
            return Optional.of(userRepository.save(user));
        }).orElseGet(Optional::empty);
    }

    private List<Role> getDefaultRoles(User user) {
        List<Role> roles = new ArrayList<>();
        Optional<Role> role = roleRepository.findByName("ROLE_USER");
        role.ifPresent(roles::add);

        if (user.isAdmin()) {
            Optional<Role> adminRoleOptional = roleRepository.findByName("ROLE_ADMIN");
            adminRoleOptional.ifPresent(roles::add);
        }

        log.info("=== User Roles Assignment ===");
        log.info("Username: {}", user.getUsername());
        log.info("Is Admin: {}", user.isAdmin());
        log.info("Assigned Roles: {}", roles.stream().map(Role::getName).toList());
        log.info("===========================");

        return roles;
    }
}
