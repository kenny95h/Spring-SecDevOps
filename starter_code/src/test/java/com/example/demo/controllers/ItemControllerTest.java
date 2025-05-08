package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.ItemRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ItemControllerTest {

    private ItemController itemController;

    private ItemRepository itemRepo=mock(ItemRepository.class);

    @BeforeEach
    public void setUp() {
        itemController = new ItemController();
        TestUtils.injectObjects(itemController, "itemRepository", itemRepo);
    }

    @Test
    public void get_items() throws Exception {
        Item mockItemOne = new Item();
        mockItemOne.setId(1L);
        mockItemOne.setName("Round Widget");
        mockItemOne.setPrice(new BigDecimal("2.99"));

        Item mockItemTwo = new Item();
        mockItemTwo.setId(2L);
        mockItemTwo.setName("Square Widget");
        mockItemTwo.setPrice(new BigDecimal("1.99"));
        List<Item> mockItems = Arrays.asList(mockItemOne, mockItemTwo);

        when(itemRepo.findAll()).thenReturn(mockItems);

        final ResponseEntity<List<Item>> response = itemController.getItems();

        assertNotNull(response);

        assertEquals(HttpStatus.OK, response.getStatusCode());

        List<Item> items = response.getBody();

        assertEquals("Round Widget", items.get(0).getName());
        assertEquals(new BigDecimal("2.99"), items.get(0).getPrice());
        assertEquals("Square Widget", items.get(1).getName());
        assertEquals(new BigDecimal("1.99"), items.get(1).getPrice());


    }

    @Test
    public void get_item_id_success() throws Exception {
        Item mockItem = new Item();
        mockItem.setId(1L);
        mockItem.setName("Round Widget");
        mockItem.setPrice(new BigDecimal("2.99"));

        when(itemRepo.findById(1L)).thenReturn(Optional.of(mockItem));

        final ResponseEntity<Item> response = itemController.getItemById(1L);

        assertNotNull(response);

        assertEquals(HttpStatus.OK, response.getStatusCode());

        Item item = response.getBody();

        assertEquals("Round Widget", item.getName());
        assertEquals(new BigDecimal("2.99"), item.getPrice());


    }

    @Test
    public void get_item_id_does_not_exist() throws Exception {
        when(itemRepo.findById(20L)).thenReturn(Optional.empty());

        final ResponseEntity<Item> response = itemController.getItemById(20L);

        assertNotNull(response);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());

    }

    @Test
    public void get_item_name_success() throws Exception {
        Item mockItem = new Item();
        mockItem.setId(2L);
        mockItem.setName("Square Widget");
        mockItem.setPrice(new BigDecimal("1.99"));

        List<Item> mockItems = Collections.singletonList(mockItem);
        when(itemRepo.findByName("Square Widget")).thenReturn(mockItems);

        final ResponseEntity<List<Item>> response = itemController.getItemsByName("Square Widget");

        assertNotNull(response);

        assertEquals(HttpStatus.OK, response.getStatusCode());

        List<Item> items = response.getBody();

        assertEquals(2L, items.get(0).getId());
        assertEquals(new BigDecimal("1.99"), items.get(0).getPrice());


    }

    @Test
    public void get_item_name_does_not_exist() throws Exception {
        when(itemRepo.findByName("Fake Item")).thenReturn(Collections.emptyList());

        final ResponseEntity<List<Item>> response = itemController.getItemsByName("Fake Item");

        assertNotNull(response);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());

    }
}
