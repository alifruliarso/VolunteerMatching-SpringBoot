package com.galapea.techblog.volunteer_matching.skill;

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
@RequestMapping(value = "/api/skills", produces = MediaType.APPLICATION_JSON_VALUE)
public class SkillResource {

    private final SkillService skillService;

    public SkillResource(final SkillService skillService) {
        this.skillService = skillService;
    }

    @GetMapping
    public ResponseEntity<List<SkillDTO>> getAllSkills() {
        return ResponseEntity.ok(skillService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<SkillDTO> getSkill(@PathVariable(name = "id") final String id) {
        return ResponseEntity.ok(skillService.get(id));
    }

    @PostMapping
    public ResponseEntity<String> createSkill(@RequestBody @Valid final SkillDTO skillDTO) {
        final String createdId = skillService.create(skillDTO);
        return new ResponseEntity<>('"' + createdId + '"', HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> updateSkill(
            @PathVariable(name = "id") final String id, @RequestBody @Valid final SkillDTO skillDTO) {
        skillService.update(id, skillDTO);
        return ResponseEntity.ok('"' + id + '"');
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSkill(@PathVariable(name = "id") final String id) {
        skillService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
