package com.galapea.techblog.volunteer_matching;

import com.galapea.techblog.volunteer_matching.griddb.GridDbClient;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/home", produces = MediaType.APPLICATION_JSON_VALUE)
public class HomeResource {
    private final GridDbClient gridDbClient;

    public HomeResource(final GridDbClient gridDbClient) {
        this.gridDbClient = gridDbClient;
    }

    @GetMapping("/databaseStatus")
    public ResponseEntity<String> getDatabaseStatus() {
        String status = gridDbClient.checkConnection() ? "Connected to GridDBCloud" : "Not connected to GridDBCloud";
        return ResponseEntity.ok('"' + status + '"');
    }
}
