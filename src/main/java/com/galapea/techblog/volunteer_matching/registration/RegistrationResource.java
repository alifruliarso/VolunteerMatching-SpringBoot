package com.galapea.techblog.volunteer_matching.registration;

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
@RequestMapping(value = "/api/registrations", produces = MediaType.APPLICATION_JSON_VALUE)
public class RegistrationResource {

    private final RegistrationService registrationService;

    public RegistrationResource(final RegistrationService registrationService) {
        this.registrationService = registrationService;
    }

    @GetMapping
    public ResponseEntity<List<RegistrationDTO>> getAllRegistrations() {
        return ResponseEntity.ok(registrationService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<RegistrationDTO> getRegistration(@PathVariable(name = "id") final String id) {
        return ResponseEntity.ok(registrationService.get(id));
    }

    @PostMapping
    public ResponseEntity<String> createRegistration(@RequestBody @Valid final RegistrationDTO registrationDTO) {
        final String createdId = registrationService.create(registrationDTO);
        return new ResponseEntity<>('"' + createdId + '"', HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> updateRegistration(
            @PathVariable(name = "id") final String id, @RequestBody @Valid final RegistrationDTO registrationDTO) {
        registrationService.update(id, registrationDTO);
        return ResponseEntity.ok('"' + id + '"');
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRegistration(@PathVariable(name = "id") final String id) {
        registrationService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
