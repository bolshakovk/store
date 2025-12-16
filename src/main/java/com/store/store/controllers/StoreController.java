package com.store.store.controllers;

import com.store.store.models.enitities.Store;
import com.store.store.services.StoreService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/stores")
public class StoreController {

    private final StoreService storeService;

    public StoreController(StoreService storeService) {
        this.storeService = storeService;
    }

    @GetMapping
    public java.util.List<Store> list() {
        return storeService.getAll();
    }

    @PostMapping
    public Store create(@RequestBody Store store) {
        return storeService.create(store);
    }

    @GetMapping("/{id}")
    public Store details(@PathVariable Long id) {
        return storeService.getById(id);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        storeService.delete(id);
    }
}