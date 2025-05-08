package com.example.demo.controllers;

import org.junit.Before;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateUserRequest;

import java.util.Optional;

public class UserControllerTest {

    private UserController userController;

    private UserRepository userRepo=mock(UserRepository.class);

    private CartRepository cartRepo=mock(CartRepository.class);

    private BCryptPasswordEncoder encoder=mock(BCryptPasswordEncoder.class);

    @BeforeEach
    public void setUp() {
        userController = new UserController();
        TestUtils.injectObjects(userController, "userRepository", userRepo);
        TestUtils.injectObjects(userController, "cartRepository", cartRepo);
        TestUtils.injectObjects(userController, "bCryptPasswordEncoder", encoder);

    }

    @Test
    public void create_user_success() throws Exception {
        // when the given password is encoded, the specified hash password is returned
        when(encoder.encode("testPassword")).thenReturn("thisIsHashed");

        // Setup create user request with given params
        CreateUserRequest r = new CreateUserRequest();
        r.setUsername("test");
        r.setPassword("testPassword");
        r.setConfirmPassword("testPassword");

        // run method to test from controller with provided request details
        final ResponseEntity<User> response = userController.createUser(r);

        // Check response is as expected
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        User u = response.getBody();
        assertNotNull(u);
        assertEquals(0, u.getId());
        assertEquals("test",u.getUsername());
        assertEquals("thisIsHashed", u.getPassword());
    }

    @Test
    public void create_user_shortpassword_failure() throws Exception {

        CreateUserRequest r = new CreateUserRequest();
        r.setUsername("testShort");
        r.setPassword("short");
        r.setConfirmPassword("short");

        final ResponseEntity<User> response = userController.createUser(r);

        assertNotNull(response);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());

        User u = response.getBody();

        assertNull(u);
    }

    @Test
    public void get_user_username_success() throws Exception {
        // Create a mock user
        User mockUser = new User();
        mockUser.setId(0L);
        mockUser.setUsername("testUser");
        mockUser.setPassword("testPassword");

        // Mock the behavior of finding the user by username
        when(userRepo.findByUsername("testUser")).thenReturn(mockUser);

        final ResponseEntity<User> response = userController.findByUserName("testUser");

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        User u = response.getBody();
        assertNotNull(u);
        assertEquals(0L, u.getId());
        assertEquals("testUser", u.getUsername());
    }

    @Test
    public void get_user_username_does_not_exist() throws Exception {
        final ResponseEntity<User> response = userController.findByUserName("fakeUser");

        assertNotNull(response);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());

    }

    @Test
    public void get_user_id_success() throws Exception {
        // Create a mock user
        User mockUser = new User();
        mockUser.setId(0L);
        mockUser.setUsername("testUser");
        mockUser.setPassword("testPassword");

        // Mock the behavior of saving the user
        when(userRepo.save(any(User.class))).thenReturn(mockUser);

        // Create the user
        CreateUserRequest r = new CreateUserRequest();
        r.setUsername("testUser");
        r.setPassword("testPassword");
        r.setConfirmPassword("testPassword");
        userController.createUser(r);

        // Mock the behavior of finding the user by ID
        when(userRepo.findById(0L)).thenReturn(Optional.of(mockUser));

        final ResponseEntity<User> response = userController.findById(0L);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        User u = response.getBody();
        assertNotNull(u);
        assertEquals(0L, u.getId());
        assertEquals("testUser", u.getUsername());
    }

    @Test
    public void get_user_id_does_not_exist() throws Exception {
        final ResponseEntity<User> response = userController.findById(10L);

        assertNotNull(response);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

}