package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.UserOrder;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.OrderRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.ModifyCartRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class OrderControllerTest {

    private OrderController orderController;

    private UserRepository userRepo=mock(UserRepository.class);

    private OrderRepository orderRepo=mock(OrderRepository.class);

    @BeforeEach
    public void setUp() {
        orderController = new OrderController();
        TestUtils.injectObjects(orderController, "userRepository", userRepo);
        TestUtils.injectObjects(orderController, "orderRepository", orderRepo);
    }

    @Test
    public void submit_user_order() throws Exception {
        // Setup the user
        User user = new User();
        user.setId(0L);
        user.setUsername("test");
        user.setPassword("testpassword");

        Item mockItem = new Item();
        mockItem.setId(2L);
        mockItem.setName("Square Widget");
        mockItem.setPrice(new BigDecimal("1.99"));
        List<Item> items = Collections.singletonList(mockItem);

        Cart mockCart = new Cart();
        mockCart.setId(1L);
        mockCart.setItems(items);
        mockCart.setTotal(new BigDecimal("1.99"));
        mockCart.setUser(user);
        user.setCart(mockCart);

        // Mock the behavior of userRepo to return the user when requested
        when(userRepo.findByUsername("test")).thenReturn(user);

        // Mock the save method to return the mockOrder
        when(orderRepo.save(any(UserOrder.class))).thenAnswer(invocation -> {
            UserOrder orderToSave = invocation.getArgument(0);
            orderToSave.setId(1L); // Set the ID to match the mockOrder
            return orderToSave;
        });

        final ResponseEntity<UserOrder> response = orderController.submit(user.getUsername());

        // Check response is as expected
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        UserOrder returnedOrder = response.getBody();
        assertNotNull(returnedOrder);
        assertEquals(1L, returnedOrder.getId()); // Now this should match
        assertEquals("test", returnedOrder.getUser().getUsername());
        assertEquals(2L, returnedOrder.getItems().get(0).getId()); // Ensure the correct item ID
        assertEquals(1, returnedOrder.getItems().size());
    }
}
