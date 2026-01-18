package com.galapea.techblog.volunteer_matching.organization_member;

import java.util.List;

public interface OrganizationMemberService {

    List<OrganizationMemberDTO> findAll();

    OrganizationMemberDTO get(String id);

    String create(OrganizationMemberDTO organizationMemberDTO);

    void update(String id, OrganizationMemberDTO organizationMemberDTO);

    void delete(String id);

    boolean idExists(String id);
}
