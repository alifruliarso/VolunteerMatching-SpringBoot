package com.galapea.techblog.volunteer_matching.skill;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class SkillDTO {

    @Size(max = 255)
    private String id;

    @NotNull
    @Size(max = 255)
    @SkillNameUnique
    private String name;

    public SkillDTO() {}

    public SkillDTO(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(final String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }
}
