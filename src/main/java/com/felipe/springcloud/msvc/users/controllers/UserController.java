package com.felipe.springcloud.msvc.users.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.felipe.springcloud.msvc.users.entities.User;
import com.felipe.springcloud.msvc.users.services.IUserService;

import java.util.Map;
import java.util.Optional;

@RestController
public class UserController {

    @Autowired
    private IUserService userService;

    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody User user) {
        return new ResponseEntity<>(userService.save(user), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@RequestBody User user, @PathVariable Long id) {
        return userService.update(id, user).map(updatedUser -> {
            return ResponseEntity.status(HttpStatus.CREATED).body(updatedUser);
        }).orElseGet(() -> {
            return ResponseEntity.notFound().build();
        });
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        return userService.findById(id).map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/username/{username}")
    public ResponseEntity<User> getUserByUsername(@PathVariable String username) {
        return userService.findByUsername(username).map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<Iterable<User>> getAllUsers() {
        return ResponseEntity.ok(userService.findAll());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody Map<String, String> body) {
        String username = body.get("username");
        String newPassword = body.get("newPassword");
        return userService.resetPassword(username, newPassword)
                .map(user -> ResponseEntity.ok().build())
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
