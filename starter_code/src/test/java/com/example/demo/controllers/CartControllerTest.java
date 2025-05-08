package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.ItemRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateUserRequest;
import com.example.demo.model.requests.ModifyCartRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CartControllerTest {

    private CartController cartController;

    private UserRepository userRepo=mock(UserRepository.class);

    private CartRepository cartRepo=mock(CartRepository.class);

    private ItemRepository itemRepo=mock(ItemRepository.class);

    @BeforeEach
    public void setUp() {
        cartController = new CartController();
        TestUtils.injectObjects(cartController, "userRepository", userRepo);
        TestUtils.injectObjects(cartController, "cartRepository", cartRepo);
        TestUtils.injectObjects(cartController, "itemRepository", itemRepo);

    }

    @Test
    public void add_item_to_cart() throws Exception {
        // Setup the user
        User user = new User();
        user.setId(0L);
        user.setUsername("test");
        user.setPassword("testpassword");

        // Mock the behavior of userRepo to return the user when requested
        when(userRepo.findByUsername("test")).thenReturn(user);

        // Mock the behavior of itemRepo to return an item when requested
        Item item = new Item();
        item.setId(1L);
        item.setName("Test Item");
        item.setPrice(new BigDecimal("10.00"));
        when(itemRepo.findById(1L)).thenReturn(Optional.of(item));

        // Create a new cart for the user
        Cart mockCart = new Cart();
        mockCart.setId(1L);
        mockCart.setItems(new ArrayList<>());
        mockCart.setTotal(new BigDecimal("0.00"));
        mockCart.setUser(user);
        user.setCart(mockCart);
        when(cartRepo.findByUser(user)).thenReturn(mockCart);

        // Setup the ModifyCartRequest
        ModifyCartRequest r = new ModifyCartRequest();
        r.setUsername("test");
        r.setItemId(1L);
        r.setQuantity(5);

        // Run the method to test from the controller with provided request details
        final ResponseEntity<Cart> response = cartController.addTocart(r);

        // Check response is as expected
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        Cart returnedCart = response.getBody();
        assertNotNull(returnedCart);
        assertEquals(1L, returnedCart.getId());
        assertEquals("test", returnedCart.getUser().getUsername());
        assertEquals(1L, returnedCart.getItems().get(0).getId());
        assertEquals(5, returnedCart.getItems().size());
    }

    @Test
    public void add_item_to_cart_item_does_not_exist() throws Exception {
        // Setup the user
        User user = new User();
        user.setId(0L);
        user.setUsername("test");
        user.setPassword("testpassword");

        // Mock the behavior of userRepo to return the user when requested
        when(userRepo.findByUsername("test")).thenReturn(user);

        // Create a new cart for the user
        Cart mockCart = new Cart();
        mockCart.setId(1L);
        mockCart.setItems(new ArrayList<>());
        mockCart.setTotal(new BigDecimal("0.00"));
        mockCart.setUser(user);
        user.setCart(mockCart);
        when(cartRepo.findByUser(user)).thenReturn(mockCart);

        // Setup the ModifyCartRequest
        ModifyCartRequest r = new ModifyCartRequest();
        r.setUsername("test");
        r.setItemId(1L);
        r.setQuantity(5);

        // Run the method to test from the controller with provided request details
        final ResponseEntity<Cart> response = cartController.addTocart(r);

        // Check response is as expected
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());

    }

    @Test
    public void remove_item_from_cart() throws Exception {
        // Setup the user
        User user = new User();
        user.setId(0L);
        user.setUsername("test");
        user.setPassword("testpassword");

        // Mock the behavior of userRepo to return the user when requested
        when(userRepo.findByUsername("test")).thenReturn(user);

        // Mock the behavior of itemRepo to return an item when requested
        Item item = new Item();
        item.setId(1L);
        item.setName("Test Item");
        item.setPrice(new BigDecimal("10.00"));
        when(itemRepo.findById(1L)).thenReturn(Optional.of(item));

        // Create a new cart for the user
        Cart mockCart = new Cart();
        mockCart.setId(1L);
        mockCart.setItems(new ArrayList<>());
        mockCart.setTotal(new BigDecimal("0.00"));
        mockCart.setUser(user);
        user.setCart(mockCart);
        when(cartRepo.findByUser(user)).thenReturn(mockCart);

        // Setup the ModifyCartRequest to add item to cart
        ModifyCartRequest addRequest = new ModifyCartRequest();
        addRequest.setUsername("test");
        addRequest.setItemId(1L);
        addRequest.setQuantity(5);
        cartController.addTocart(addRequest); // Add item to cart

        // Setup the ModifyCartRequest to remove item from cart
        ModifyCartRequest removeRequest = new ModifyCartRequest();
        removeRequest.setUsername("test");
        removeRequest.setItemId(1L);
        removeRequest.setQuantity(1); // Specify quantity to remove
        final ResponseEntity<Cart> response = cartController.removeFromcart(removeRequest);

        // Check response is as expected
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        Cart returnedCart = response.getBody();
        assertNotNull(returnedCart);
        assertEquals(1L, returnedCart.getId());
        assertEquals("test", returnedCart.getUser().getUsername());
        assertEquals(1L, returnedCart.getItems().get(0).getId()); // Check the first item
        assertEquals(4, returnedCart.getItems().size()); // Check that one item was removed
        assertEquals(new BigDecimal("40.00"), returnedCart.getTotal()); // Check the total price after removal
    }
}
