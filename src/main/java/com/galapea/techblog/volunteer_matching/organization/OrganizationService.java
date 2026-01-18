package com.galapea.techblog.volunteer_matching.organization;

import java.util.List;
import java.util.Optional;

public interface OrganizationService {

    List<OrganizationDTO> findAll();

    OrganizationDTO get(String id);

    String create(OrganizationDTO organizationDTO);

    void update(String id, OrganizationDTO organizationDTO);

    void delete(String id);

    boolean idExists(String id);

    public Optional<OrganizationDTO> getOneByName(final String name);

    public Optional<OrganizationDTO> getOneByAdminUserId(final String adminUserId);
}
