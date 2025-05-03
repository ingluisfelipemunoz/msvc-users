package com.felipe.springcloud.msvc.users.services;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.felipe.springcloud.msvc.users.entities.Role;
import com.felipe.springcloud.msvc.users.entities.User;
import com.felipe.springcloud.msvc.users.repositories.RoleRepository;
import com.felipe.springcloud.msvc.users.repositories.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UserService implements IUserService {

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

        user.setRoles(getDefaultRoles());
        return userRepository.save(user);
    }

    @Transactional
    public void delete(Long id) {
        userRepository.deleteById(id);
    }

    @Override
    public Optional<User> update(Long id, User user) {
        Optional<User> userOptional = this.findById(id);

        return userOptional.map(userDb -> {
            // set fields
            userDb.setEmail(user.getEmail());
            userDb.setUsername(user.getUsername());
            if (user.isEnabled() != null) {
                userDb.setEnabled(user.isEnabled());
            }
            userDb.setEnabled(user.isEnabled());

            // set roles
            userDb.setRoles(this.getDefaultRoles());

            return Optional.of(this.save(userDb));
        }).orElseGet(() -> Optional.empty());
    }

    private List<Role> getDefaultRoles() {
        List<Role> roles = new ArrayList<>();
        Optional<Role> role = roleRepository.findByName("ROLE_USER");
        role.ifPresent(roles::add);
        return roles;
    }
}
