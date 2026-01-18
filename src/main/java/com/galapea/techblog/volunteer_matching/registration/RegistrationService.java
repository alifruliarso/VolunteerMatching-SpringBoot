package com.galapea.techblog.volunteer_matching.registration;

import java.util.List;
import java.util.Optional;

public interface RegistrationService {

    List<RegistrationDTO> findAll();

    List<RegistrationDTO> findAllByOrgId(String orgId);

    RegistrationDTO get(String id);

    Optional<RegistrationDTO> getByUserIdAndOpportunityId(String userId, String opportunityId);

    String create(RegistrationDTO registrationDTO);

    void update(String id, RegistrationDTO registrationDTO);

    void delete(String id);

    boolean idExists(String id);

    String register(String userId, String opportunityId);

    Long countByOpportunityId(String opportunityId);

    void approve(String id);

    void reject(String id);
}
