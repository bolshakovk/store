package com.store.store.controllers;

import com.store.store.services.PackingListService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/stores/{storeId}/mp/{mpId}/packing")
public class PackingListController {

    public PackingListController(PackingListService packingListService) {
        this.packingListService = packingListService;
    }

    private final PackingListService packingListService;

    @PostMapping(value = "/upload", consumes = "multipart/form-data")
    public void upload(@PathVariable Long storeId,
            @PathVariable Long mpId,
            @RequestParam("file") MultipartFile file) throws Exception {

        packingListService.uploadExcel(mpId, file);
    }

    @DeleteMapping("/{itemId}")
    public void delete(@PathVariable Long storeId,
            @PathVariable Long mpId,
            @PathVariable Long itemId) {

        packingListService.deleteItem(itemId);
    }
}