package com.galapea.techblog.volunteer_matching.organization_member;

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
@RequestMapping(value = "/api/organizationMembers", produces = MediaType.APPLICATION_JSON_VALUE)
public class OrganizationMemberResource {

    private final OrganizationMemberService organizationMemberService;

    public OrganizationMemberResource(final OrganizationMemberService organizationMemberService) {
        this.organizationMemberService = organizationMemberService;
    }

    @GetMapping
    public ResponseEntity<List<OrganizationMemberDTO>> getAllOrganizationMembers() {
        return ResponseEntity.ok(organizationMemberService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrganizationMemberDTO> getOrganizationMember(@PathVariable(name = "id") final String id) {
        return ResponseEntity.ok(organizationMemberService.get(id));
    }

    @PostMapping
    public ResponseEntity<String> createOrganizationMember(
            @RequestBody @Valid final OrganizationMemberDTO organizationMemberDTO) {
        final String createdId = organizationMemberService.create(organizationMemberDTO);
        return new ResponseEntity<>('"' + createdId + '"', HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> updateOrganizationMember(
            @PathVariable(name = "id") final String id,
            @RequestBody @Valid final OrganizationMemberDTO organizationMemberDTO) {
        organizationMemberService.update(id, organizationMemberDTO);
        return ResponseEntity.ok('"' + id + '"');
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrganizationMember(@PathVariable(name = "id") final String id) {
        organizationMemberService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
