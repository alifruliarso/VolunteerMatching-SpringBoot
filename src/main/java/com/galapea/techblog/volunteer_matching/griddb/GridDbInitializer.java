package com.galapea.techblog.volunteer_matching.griddb;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.galapea.techblog.volunteer_matching.opportunity.OpportunityDTO;
import com.galapea.techblog.volunteer_matching.opportunity.OpportunityGridDBService;
import com.galapea.techblog.volunteer_matching.opportunity_requirement.OpportunityRequirementDTO;
import com.galapea.techblog.volunteer_matching.opportunity_requirement.OpportunityRequirementGridDBService;
import com.galapea.techblog.volunteer_matching.organization.OrganizationDTO;
import com.galapea.techblog.volunteer_matching.organization.OrganizationGridDBService;
import com.galapea.techblog.volunteer_matching.organization_member.OrganizationMemberGridDBService;
import com.galapea.techblog.volunteer_matching.registration.RegistrationGridDBService;
import com.galapea.techblog.volunteer_matching.skill.SkillDTO;
import com.galapea.techblog.volunteer_matching.skill.SkillGridDBService;
import com.galapea.techblog.volunteer_matching.user.UserDTO;
import com.galapea.techblog.volunteer_matching.user.UserGridDBService;
import com.galapea.techblog.volunteer_matching.user.UserRole;
import com.galapea.techblog.volunteer_matching.volunteer_skill.VolunteerSkillGridDBService;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

@Component
public class GridDbInitializer implements CommandLineRunner {

    private static final Logger LOGGER = LoggerFactory.getLogger(GridDbInitializer.class);

    private final UserGridDBService userService;
    private final SkillGridDBService skillService;
    private final OrganizationGridDBService organizationService;
    private final OrganizationMemberGridDBService organizationMemberService;
    private final OpportunityGridDBService opportunityService;
    private final OpportunityRequirementGridDBService opportunityRequirementService;
    private final RegistrationGridDBService registrationService;
    private final VolunteerSkillGridDBService volunteerSkillService;

    public GridDbInitializer(
            UserGridDBService userService,
            SkillGridDBService skillService,
            OrganizationGridDBService organizationService,
            OrganizationMemberGridDBService organizationMemberService,
            OpportunityGridDBService opportunityService,
            OpportunityRequirementGridDBService opportunityRequirementService,
            RegistrationGridDBService registrationService,
            VolunteerSkillGridDBService volunteerSkillService) {
        this.userService = userService;
        this.skillService = skillService;
        this.organizationService = organizationService;
        this.organizationMemberService = organizationMemberService;
        this.opportunityService = opportunityService;
        this.opportunityRequirementService = opportunityRequirementService;
        this.registrationService = registrationService;
        this.volunteerSkillService = volunteerSkillService;
    }

    @Override
    public void run(String... args) {
        LOGGER.info("Initializing GridDB containers via createTable() on services");
        try {
            userService.createTable();
        } catch (Exception e) {
            LOGGER.warn("createTable() failed for UserGridDBService", e);
        }

        try {
            skillService.createTable();
        } catch (Exception e) {
            LOGGER.warn("createTable() failed for SkillGridDBService", e);
        }

        try {
            organizationService.createTable();
        } catch (Exception e) {
            LOGGER.warn("createTable() failed for OrganizationGridDBService", e);
        }

        try {
            organizationMemberService.createTable();
        } catch (Exception e) {
            LOGGER.warn("createTable() failed for OrganizationMemberGridDBService", e);
        }

        try {
            opportunityService.createTable();
        } catch (Exception e) {
            LOGGER.warn("createTable() failed for OpportunityGridDBService", e);
        }

        try {
            opportunityRequirementService.createTable();
        } catch (Exception e) {
            LOGGER.warn("createTable() failed for OpportunityRequirementGridDBService", e);
        }

        try {
            registrationService.createTable();
        } catch (Exception e) {
            LOGGER.warn("createTable() failed for RegistrationGridDBService", e);
        }

        try {
            volunteerSkillService.createTable();
        } catch (Exception e) {
            LOGGER.warn("createTable() failed for VolunteerSkillGridDBService", e);
        }
        // Seed one user, one organization and healthcare-related skills
        UserDTO seededUser = new UserDTO();
        seededUser.setId("usr_0njm1smprjsvq");
        seededUser.setEmail("admin@health.example");
        seededUser.setFullName("Health Admin");
        seededUser.setRole(UserRole.ORGANIZER);
        String seededUserId = userService.create(seededUser);
        LOGGER.info("Seeded user id={}", seededUserId);

        OrganizationDTO seededOrg = new OrganizationDTO();
        seededOrg.setId("org_0njm1sn84jv58");
        seededOrg.setName("Example Health Clinic");
        seededOrg.setWebsiteUrl("https://www.examplehealth.org");
        seededOrg.setAdminUserId(seededUserId);
        String seededOrgId = organizationService.create(seededOrg);
        LOGGER.info("Seeded organization id={}", seededOrgId);

        UserDTO memberUser = new UserDTO();
        memberUser.setId("usr_0njfreferfe");
        memberUser.setEmail("user1@member.com");
        memberUser.setFullName("Member User");
        memberUser.setRole(UserRole.VOLUNTEER);
        String memberUserId = userService.create(memberUser);
        LOGGER.info("Seeded member user id={}", memberUserId);

        seedSkills();
        seedOpportunities();
        seedSecondOrg();

        LOGGER.info("GridDB initialization finished");
    }

    private void seedSkills() {
        try {
            // Load healthcare skills from JSON file
            ObjectMapper mapper = new ObjectMapper();
            ClassPathResource resource = new ClassPathResource("skills.json");
            InputStream inputStream = resource.getInputStream();
            @SuppressWarnings("unchecked")
            Map<String, Object> data = mapper.readValue(inputStream, Map.class);
            @SuppressWarnings("unchecked")
            List<Map<String, String>> healthcareSkillsList = (List<Map<String, String>>) data.get("healthcareSkills");

            if (healthcareSkillsList == null || healthcareSkillsList.isEmpty()) {
                LOGGER.warn("No healthcare skills found in skills.json");
                return;
            }

            if (skillService
                    .getOneByName(healthcareSkillsList.get(0).get("name"))
                    .isPresent()) {
                LOGGER.info("Healthcare skills already seeded, skipping seeding process");
                return;
            }

            List<SkillDTO> skillDTOs = new ArrayList<>();
            for (Map<String, String> skillMap : healthcareSkillsList) {
                try {
                    SkillDTO sd = new SkillDTO();
                    sd.setId(skillMap.get("id"));
                    sd.setName(skillMap.get("name"));
                    skillDTOs.add(sd);
                } catch (Exception e) {
                    LOGGER.warn("Failed to seed skill: {}", skillMap.get("name"), e);
                }
            }
            skillService.createMultiple(skillDTOs);
            LOGGER.info("Seeded {} healthcare skills", skillDTOs.size());
        } catch (Exception e) {
            LOGGER.warn("Seeding healthcare skills failed", e);
        }
    }

    private void seedOpportunities() {
        try {
            // Opportunity 1: No requirements
            OpportunityDTO opportunity1 = new OpportunityDTO();
            opportunity1.setId("opp_0njm1smprjsvq01");
            opportunity1.setTitle("Community Health Screening");
            opportunity1.setDescription("Assist with general health screening and patient registration");
            opportunity1.setAddress("123 Health Street, Medical City");
            opportunity1.setOrgId("org_0njm1sn84jv58");
            opportunity1.setSlotsTotal(10L);
            opportunityService.create(opportunity1);
            LOGGER.info("Seeded opportunity without requirements: id={}", opportunity1.getId());

            // Opportunity 2: With skill requirements
            OpportunityDTO opportunity2 = new OpportunityDTO();
            opportunity2.setId("opp_0njm1smprjsvq02");
            opportunity2.setTitle("Advanced Medical Support");
            opportunity2.setDescription("Provide advanced medical support requiring specific healthcare skills");
            opportunity2.setAddress("456 Clinical Avenue, Medical City");
            opportunity2.setOrgId("org_0njm1sn84jv58");
            opportunity2.setSlotsTotal(5L);
            String opp2Id = opportunityService.create(opportunity2);
            LOGGER.info("Seeded opportunity with requirements: id={}", opp2Id);

            // Add skill requirements to opportunity 2
            try {
                ObjectMapper mapper = new ObjectMapper();
                ClassPathResource resource = new ClassPathResource("skills.json");
                InputStream inputStream = resource.getInputStream();
                @SuppressWarnings("unchecked")
                Map<String, Object> data = mapper.readValue(inputStream, Map.class);
                @SuppressWarnings("unchecked")
                List<Map<String, String>> healthcareSkillsList =
                        (List<Map<String, String>>) data.get("healthcareSkills");

                if (healthcareSkillsList != null && !healthcareSkillsList.isEmpty()) {
                    // Add first two skills as mandatory requirements
                    for (int i = 0; i < Math.min(2, healthcareSkillsList.size()); i++) {
                        OpportunityRequirementDTO requirement = new OpportunityRequirementDTO();
                        requirement.setOpportunityId(opp2Id);
                        requirement.setSkillId(healthcareSkillsList.get(i).get("id"));
                        requirement.setIsMandatory(true);
                        opportunityRequirementService.create(requirement);
                        LOGGER.info(
                                "Added requirement to opportunity: skill={}",
                                healthcareSkillsList.get(i).get("name"));
                    }
                    // Add next skill as optional
                    if (healthcareSkillsList.size() > 2) {
                        OpportunityRequirementDTO optionalRequirement = new OpportunityRequirementDTO();
                        optionalRequirement.setOpportunityId(opp2Id);
                        optionalRequirement.setSkillId(
                                healthcareSkillsList.get(2).get("id"));
                        optionalRequirement.setIsMandatory(false);
                        opportunityRequirementService.create(optionalRequirement);
                        LOGGER.info(
                                "Added optional requirement to opportunity: skill={}",
                                healthcareSkillsList.get(2).get("name"));
                    }
                }
            } catch (Exception e) {
                LOGGER.warn("Failed to add skill requirements to opportunity", e);
            }
        } catch (Exception e) {
            LOGGER.warn("Seeding opportunities failed", e);
        }
    }

    private void seedSecondOrg() {
        UserDTO seededUser = new UserDTO();
        seededUser.setId("usr_0adminvitacomp");
        seededUser.setEmail("admin@vitacomp.com");
        seededUser.setFullName("Admin vitacomp");
        seededUser.setRole(UserRole.ORGANIZER);
        String seededUserId = userService.create(seededUser);
        LOGGER.info("Seeded user id={}", seededUserId);

        OrganizationDTO seededOrg = new OrganizationDTO();
        seededOrg.setId("org_0v1t4comp2");
        seededOrg.setName("Vitamins Company");
        seededOrg.setWebsiteUrl("https://www.vitacomp.com");
        seededOrg.setAdminUserId(seededUserId);
        String seededOrgId = organizationService.create(seededOrg);
        LOGGER.info("Seeded organization id={}", seededOrgId);

        // Opportunity 1: No requirements
        OpportunityDTO opportunity1 = new OpportunityDTO();
        opportunity1.setId("opp_0vitacompggg01");
        opportunity1.setTitle("Vitamin Screening Drive");
        opportunity1.setDescription("Assist with vitamin deficiency screening and consultation");
        opportunity1.setAddress("789 Wellness Blvd, Health City");
        opportunity1.setOrgId(seededOrgId);
        opportunity1.setSlotsTotal(50L);
        opportunityService.create(opportunity1);
        LOGGER.info("Seeded opportunity without requirements: id={}", opportunity1.getId());
    }
}
