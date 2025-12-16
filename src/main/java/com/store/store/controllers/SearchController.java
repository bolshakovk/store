package com.store.store.controllers;

import com.store.store.services.PackingListService;
import com.store.store.models.enitities.PackingList;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/search")
public class SearchController {

    private final PackingListService packingListService;

    public SearchController(PackingListService packingListService) {
        this.packingListService = packingListService;
    }

    @GetMapping
    public List<PackingList> search(@RequestParam("q") String query) {
        return packingListService.search(query);
    }
}