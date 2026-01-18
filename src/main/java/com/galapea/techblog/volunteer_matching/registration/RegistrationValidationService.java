package com.galapea.techblog.volunteer_matching.registration;

import com.galapea.techblog.volunteer_matching.opportunity.OpportunityDTO;
import com.galapea.techblog.volunteer_matching.opportunity.OpportunityService;
import com.galapea.techblog.volunteer_matching.opportunity_requirement.OpportunityRequirementDTO;
import com.galapea.techblog.volunteer_matching.opportunity_requirement.OpportunityRequirementService;
import com.galapea.techblog.volunteer_matching.skill.SkillDTO;
import com.galapea.techblog.volunteer_matching.skill.SkillService;
import com.galapea.techblog.volunteer_matching.volunteer_skill.VolunteerSkillDTO;
import com.galapea.techblog.volunteer_matching.volunteer_skill.VolunteerSkillService;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class RegistrationValidationService {
    private final Logger log = LoggerFactory.getLogger(getClass());
    private final RegistrationService registrationService;
    private final OpportunityService opportunityService;
    private final OpportunityRequirementService opportunityRequirementService;
    private final VolunteerSkillService volunteerSkillService;
    private final SkillService skillService;

    public RegistrationValidationService(
            final RegistrationService registrationService,
            final OpportunityService opportunityService,
            final OpportunityRequirementService opportunityRequirementService,
            final VolunteerSkillService volunteerSkillService,
            final SkillService skillService) {
        this.registrationService = registrationService;
        this.opportunityService = opportunityService;
        this.opportunityRequirementService = opportunityRequirementService;
        this.volunteerSkillService = volunteerSkillService;
        this.skillService = skillService;
    }

    /**
     * Validates a registration request before allowing the user to register.
     *
     * @param userId the volunteer's user ID
     * @param opportunityId the opportunity ID
     * @throws AlreadyRegisteredException if user is already registered for this opportunity
     * @throws OpportunitySlotsFullException if the opportunity has no available slots
     * @throws MissingMandatorySkillException if user lacks a mandatory skill
     */
    public void validateRegistration(final String userId, final String opportunityId) {
        log.debug("Validating registration for user: {}, opportunity: {}", userId, opportunityId);

        // Check 1: User not already registered
        validateNotAlreadyRegistered(userId, opportunityId);

        // Check 2: Opportunity has available slots
        validateSlotsAvailable(opportunityId);

        // Check 3: User has mandatory skills
        validateMandatorySkills(userId, opportunityId);

        log.debug("Registration validation passed for user: {}, opportunity: {}", userId, opportunityId);
    }

    /**
     * Validates that the user is not already registered for the opportunity.
     *
     * @throws AlreadyRegisteredException if user is already registered
     */
    private void validateNotAlreadyRegistered(final String userId, final String opportunityId) {
        Optional<RegistrationDTO> existingReg = registrationService.getByUserIdAndOpportunityId(userId, opportunityId);
        if (existingReg.isPresent()) {
            log.debug(
                    "Registration validation failed - User already registered: user={}, opportunity={}",
                    userId,
                    opportunityId);
            throw new AlreadyRegisteredException(userId, opportunityId);
        }
    }

    /**
     * Validates that the opportunity has available slots.
     *
     * @throws OpportunitySlotsFullException if opportunity is at capacity
     */
    private void validateSlotsAvailable(final String opportunityId) {
        OpportunityDTO opportunity = opportunityService.get(opportunityId);
        Long registeredCount = registrationService.countByOpportunityId(opportunityId);

        if (registeredCount >= opportunity.getSlotsTotal()) {
            log.debug(
                    "Registration validation failed - Opportunity is full: opportunity={}, registered={}, total={}",
                    opportunityId,
                    registeredCount,
                    opportunity.getSlotsTotal());
            throw new OpportunitySlotsFullException(opportunityId, opportunity.getSlotsTotal(), registeredCount);
        }
    }

    /**
     * Validates that the user has all mandatory skills required by the opportunity.
     *
     * @throws MissingMandatorySkillException if user lacks a mandatory skill
     */
    private void validateMandatorySkills(final String userId, final String opportunityId) {
        List<VolunteerSkillDTO> userSkills = volunteerSkillService.findAllByUserId(userId);
        List<OpportunityRequirementDTO> opportunityRequirements =
                opportunityRequirementService.findAllByOpportunityId(opportunityId);

        for (OpportunityRequirementDTO requirement : opportunityRequirements) {
            // Skip optional requirements
            if (!requirement.getIsMandatory()) {
                continue;
            }

            // Check if user has this mandatory skill
            boolean hasSkill = userSkills.stream()
                    .anyMatch(userSkill -> userSkill.getSkillId().equals(requirement.getSkillId()));

            if (!hasSkill) {
                SkillDTO skill = skillService.get(requirement.getSkillId());
                String skillName = skill != null ? skill.getName() : "Unknown Skill";
                log.debug(
                        "Registration validation failed - Missing mandatory skill: user={}, opportunity={}, skill={}",
                        userId,
                        opportunityId,
                        skillName);
                throw new MissingMandatorySkillException(userId, opportunityId, requirement.getSkillId(), skillName);
            }
        }
    }
}
