package com.store.store.controllers;

import com.store.store.models.enitities.Mp;
import com.store.store.services.MpService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/stores/{storeId}/mp")
public class MpController {

    private final MpService mpService;

    public MpController(MpService mpService) {
        this.mpService = mpService;
    }

    @GetMapping
    public List<Mp> list(@PathVariable Long storeId) {
        return mpService.getByStore(storeId);
    }

    @PostMapping
    public Mp create(@PathVariable Long storeId, @RequestBody Mp mp) {
        return mpService.create(storeId, mp);
    }

    @GetMapping("/{mpId}")
    public Mp details(@PathVariable Long storeId, @PathVariable Long mpId) {
        return mpService.getById(mpId);
        // packing lists are included in Mp entity (one-to-many)
    }

    @DeleteMapping("/{mpId}")
    public void delete(@PathVariable Long storeId, @PathVariable Long mpId) {
        mpService.delete(mpId);
    }
}