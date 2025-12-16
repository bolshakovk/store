package com.store.store.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/system")
public class SystemController {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @GetMapping("/status")
    public Map<String, Object> getSystemStatus() {
        Map<String, Object> status = new HashMap<>();

        // 1. Application Disk Space
        File root = new File(".");
        long totalSpace = root.getTotalSpace();
        long freeSpace = root.getFreeSpace();
        long usedSpace = totalSpace - freeSpace;

        status.put("appDiskTotal", formatBytes(totalSpace));
        status.put("appDiskFree", formatBytes(freeSpace));
        status.put("appDiskUsed", formatBytes(usedSpace));

        // 2. Database Size
        try {
            Long dbSizeBytes = jdbcTemplate.queryForObject("SELECT pg_database_size(current_database())", Long.class);
            status.put("dbSize", formatBytes(dbSizeBytes != null ? dbSizeBytes : 0));
        } catch (Exception e) {
            status.put("dbSize", "Unknown");
            e.printStackTrace();
        }

        return status;
    }

    private String formatBytes(long bytes) {
        if (bytes < 1024)
            return bytes + " B";
        int exp = (int) (Math.log(bytes) / Math.log(1024));
        String pre = "KMGTPE".charAt(exp - 1) + "";
        return String.format("%.1f %sB", bytes / Math.pow(1024, exp), pre);
    }
}
