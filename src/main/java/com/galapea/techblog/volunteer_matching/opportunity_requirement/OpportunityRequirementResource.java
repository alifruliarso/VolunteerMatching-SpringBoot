package com.galapea.techblog.volunteer_matching.opportunity_requirement;

import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/opportunityRequirements", produces = MediaType.APPLICATION_JSON_VALUE)
public class OpportunityRequirementResource {

    private final OpportunityRequirementService opportunityRequirementService;

    public OpportunityRequirementResource(final OpportunityRequirementService opportunityRequirementService) {
        this.opportunityRequirementService = opportunityRequirementService;
    }

    @GetMapping
    public ResponseEntity<List<OpportunityRequirementDTO>> getAllOpportunityRequirements() {
        return ResponseEntity.ok(opportunityRequirementService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<OpportunityRequirementDTO> getOpportunityRequirement(
            @PathVariable(name = "id") final String id) {
        return ResponseEntity.ok(opportunityRequirementService.get(id));
    }

    @PostMapping
    public ResponseEntity<String> createOpportunityRequirement(
            @RequestBody @Valid final OpportunityRequirementDTO opportunityRequirementDTO) {
        final String createdId = opportunityRequirementService.create(opportunityRequirementDTO);
        return new ResponseEntity<>(createdId, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> updateOpportunityRequirement(
            @PathVariable(name = "id") final String id,
            @RequestBody @Valid final OpportunityRequirementDTO opportunityRequirementDTO) {
        opportunityRequirementService.update(id, opportunityRequirementDTO);
        return ResponseEntity.ok(id);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOpportunityRequirement(@PathVariable(name = "id") final String id) {
        opportunityRequirementService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
