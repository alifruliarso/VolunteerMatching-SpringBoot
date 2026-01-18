package com.galapea.techblog.volunteer_matching.opportunity;

import com.galapea.techblog.volunteer_matching.opportunity_requirement.OpportunityRequirementDTO;
import com.galapea.techblog.volunteer_matching.opportunity_requirement.OpportunityRequirementForm;
import com.galapea.techblog.volunteer_matching.opportunity_requirement.OpportunityRequirementService;
import com.galapea.techblog.volunteer_matching.organization.OrganizationDTO;
import com.galapea.techblog.volunteer_matching.registration.AlreadyRegisteredException;
import com.galapea.techblog.volunteer_matching.registration.MissingMandatorySkillException;
import com.galapea.techblog.volunteer_matching.registration.OpportunitySlotsFullException;
import com.galapea.techblog.volunteer_matching.registration.RegistrationDTO;
import com.galapea.techblog.volunteer_matching.registration.RegistrationService;
import com.galapea.techblog.volunteer_matching.registration.RegistrationValidationService;
import com.galapea.techblog.volunteer_matching.security.CustomUserDetails;
import com.galapea.techblog.volunteer_matching.security.SecurityExpressions;
import com.galapea.techblog.volunteer_matching.skill.SkillDTO;
import com.galapea.techblog.volunteer_matching.skill.SkillService;
import com.galapea.techblog.volunteer_matching.user.UserDTO;
import com.galapea.techblog.volunteer_matching.user.UserService;
import com.galapea.techblog.volunteer_matching.util.WebUtils;
import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/opportunities")
public class OpportunityController {
    private final Logger log = LoggerFactory.getLogger(getClass());
    private final OpportunityService opportunityService;
    private final RegistrationService registrationService;
    private final RegistrationValidationService registrationValidationService;
    private final UserService userService;
    private final OpportunityRequirementService opportunityRequirementService;
    private final SkillService skillService;

    public OpportunityController(
            final OpportunityService opportunityService,
            final RegistrationService registrationService,
            final RegistrationValidationService registrationValidationService,
            final UserService userService,
            final OpportunityRequirementService opportunityRequirementService,
            final SkillService skillService) {
        this.opportunityService = opportunityService;
        this.registrationService = registrationService;
        this.registrationValidationService = registrationValidationService;
        this.userService = userService;
        this.opportunityRequirementService = opportunityRequirementService;
        this.skillService = skillService;
    }

    @GetMapping
    public String list(final Model model, @AuthenticationPrincipal final CustomUserDetails userDetails) {
        List<OpportunityDTO> allOpportunities = new ArrayList<>();
        UserDTO user = userDetails != null
                ? userService.getOneByEmail(userDetails.getUsername()).orElse(null)
                : null;
        if (userDetails != null
                && userDetails.getOrganizations() != null
                && !userDetails.getOrganizations().isEmpty()) {
            OrganizationDTO org = userDetails.getOrganizations().get(0);
            model.addAttribute("organization", org);
            allOpportunities = opportunityService.findAllByOrgId(org.getId());
        } else {
            model.addAttribute("organization", null);
            allOpportunities = opportunityService.findAll();
        }
        List<OpportunityResponse> opportunities = extractOpportunities(allOpportunities, user);
        model.addAttribute("opportunities", opportunities);
        return "opportunity/list";
    }

    private List<OpportunityResponse> extractOpportunities(List<OpportunityDTO> allOpportunities, UserDTO user) {
        List<OpportunityResponse> opportunities = allOpportunities.stream()
                .map(opportunity -> {
                    String registrationId = "";
                    if (user != null) {
                        Optional<RegistrationDTO> reg =
                                registrationService.getByUserIdAndOpportunityId(user.getId(), opportunity.getId());
                        if (reg.isPresent()) {
                            registrationId = reg.get().getId();
                        }
                    }
                    return new OpportunityResponse(
                            opportunity.getId(),
                            opportunity.getTitle(),
                            opportunity.getDescription(),
                            opportunity.getAddress(),
                            opportunity.getStartTime(),
                            opportunity.getEndTime(),
                            opportunity.getSlotsTotal(),
                            0L,
                            "",
                            "",
                            registrationId,
                            registrationId.isBlank() ? false : true,
                            null,
                            "",
                            List.of());
                })
                .collect(Collectors.toList());
        return opportunities;
    }

    @GetMapping("/add")
    @PreAuthorize(SecurityExpressions.ORGANIZER_ONLY)
    public String add(
            @ModelAttribute("opportunity") final OpportunityDTO opportunityDTO,
            final Model model,
            @AuthenticationPrincipal final CustomUserDetails userDetails) {
        if (userDetails != null
                && userDetails.getOrganizations() != null
                && !userDetails.getOrganizations().isEmpty()) {
            OrganizationDTO org = userDetails.getOrganizations().get(0);
            opportunityDTO.setOrgId(org.getId());
        }
        opportunityDTO.setId(opportunityService.nextId());
        return "opportunity/add";
    }

    @PostMapping("/add")
    @PreAuthorize(SecurityExpressions.ORGANIZER_ONLY)
    public String add(
            @ModelAttribute("opportunity") @Valid final OpportunityDTO opportunityDTO,
            final BindingResult bindingResult,
            final RedirectAttributes redirectAttributes) {
        log.info("Creating opportunity orgId: {}", opportunityDTO.getOrgId());
        if (bindingResult.hasErrors()) {
            bindingResult.getAllErrors().forEach(error -> log.error("Validation error: {}", error.toString()));
            return "opportunity/add";
        }
        opportunityService.create(opportunityDTO);
        redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, WebUtils.getMessage("opportunity.create.success"));
        return "redirect:/opportunities";
    }

    @GetMapping("/edit/{id}")
    @PreAuthorize(SecurityExpressions.ORGANIZER_ONLY)
    public String edit(@PathVariable(name = "id") final String id, final Model model) {
        model.addAttribute("opportunity", opportunityService.get(id));
        return "opportunity/edit";
    }

    @PostMapping("/edit/{id}")
    @PreAuthorize(SecurityExpressions.ORGANIZER_ONLY)
    public String edit(
            @PathVariable(name = "id") final String id,
            @ModelAttribute("opportunity") @Valid final OpportunityDTO opportunityDTO,
            final BindingResult bindingResult,
            final RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "opportunity/edit";
        }
        opportunityService.update(id, opportunityDTO);
        redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, WebUtils.getMessage("opportunity.update.success"));
        return "redirect:/opportunities";
    }

    @PostMapping("/delete/{id}")
    @PreAuthorize(SecurityExpressions.ORGANIZER_ONLY)
    public String delete(@PathVariable(name = "id") final String id, final RedirectAttributes redirectAttributes) {
        opportunityService.delete(id);
        redirectAttributes.addFlashAttribute(WebUtils.MSG_INFO, WebUtils.getMessage("opportunity.delete.success"));
        return "redirect:/opportunities";
    }

    @GetMapping("/{id}")
    public String view(
            @PathVariable(name = "id") final String id,
            final Model model,
            @AuthenticationPrincipal final CustomUserDetails userDetails) {
        OpportunityDTO opportunity = opportunityService.get(id);
        UserDTO user = userDetails != null
                ? userService.getOneByEmail(userDetails.getUsername()).orElse(null)
                : null;
        String registrationId = "";
        LocalDateTime registrationTime = null;
        String registrationStatus = "";
        if (user != null) {
            Optional<RegistrationDTO> reg =
                    registrationService.getByUserIdAndOpportunityId(user.getId(), opportunity.getId());
            if (reg.isPresent()) {
                registrationId = reg.get().getId();
                registrationTime = reg.get().getRegistrationTime();
                registrationStatus = reg.get().getStatus().name();
            }
        }
        String orgName = "";
        String orgId = opportunity.getOrgId();
        if (userDetails != null
                && userDetails.getOrganizations() != null
                && !userDetails.getOrganizations().isEmpty()) {
            OrganizationDTO org = userDetails.getOrganizations().get(0);
            orgName = org.getName();
            orgId = org.getId();
        }
        List<OpportunityRequirementDTO> opportunitySkills = opportunityRequirementService.findAllByOpportunityId(id);
        List<OpportunitySkill> skills = new ArrayList<>(opportunitySkills.size());
        List<SkillDTO> findAllByIdIn = skillService.findAllByIdIn(opportunitySkills.stream()
                .map(OpportunityRequirementDTO::getSkillId)
                .collect(Collectors.toList()));
        for (OpportunityRequirementDTO ors : opportunitySkills) {
            for (SkillDTO skillDTO : findAllByIdIn) {
                if (skillDTO.getId().equals(ors.getSkillId())) {
                    skills.add(new OpportunitySkill(skillDTO.getName(), ors.getIsMandatory()));
                    break;
                }
            }
        }
        OpportunityResponse view = new OpportunityResponse(
                opportunity.getId(),
                opportunity.getTitle(),
                opportunity.getDescription(),
                opportunity.getAddress(),
                opportunity.getStartTime(),
                opportunity.getEndTime(),
                opportunity.getSlotsTotal(),
                0L,
                orgId,
                orgName,
                registrationId,
                registrationId.isBlank() ? false : true,
                registrationTime,
                registrationStatus,
                skills);
        model.addAttribute("opportunity", view);
        return "opportunity/view";
    }

    @PostMapping("/{id}/registrations")
    public String registrations(
            @PathVariable(name = "id") final String opportunityId,
            final RedirectAttributes redirectAttributes,
            @AuthenticationPrincipal final UserDetails userDetails) {
        UserDTO user = userService
                .getOneByEmail(userDetails.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        log.debug("Processing registration for user: {}, opportunity: {}", user.getFullName(), opportunityId);

        try {
            // Validate registration using the validation service
            registrationValidationService.validateRegistration(user.getId(), opportunityId);

            // If validation passes, proceed with registration
            OpportunityDTO opportunityDTO = opportunityService.get(opportunityId);
            registrationService.register(user.getId(), opportunityId);
            log.debug(
                    "Registration Successful - user: {}, opportunity: {}",
                    user.getFullName(),
                    opportunityDTO.getTitle());
            redirectAttributes.addFlashAttribute(
                    WebUtils.MSG_INFO, WebUtils.getMessage("opportunity.registrations.success"));
            return "redirect:/opportunities/" + opportunityId;

        } catch (AlreadyRegisteredException e) {
            log.debug(
                    "Registration Failed - Already registered user: {}, opportunity: {}",
                    user.getFullName(),
                    opportunityId);
            redirectAttributes.addFlashAttribute(
                    WebUtils.MSG_ERROR, WebUtils.getMessage("opportunity.registrations.already_registered"));
            return "redirect:/opportunities/" + opportunityId;

        } catch (OpportunitySlotsFullException e) {
            log.debug(
                    "Registration Failed - Opportunity slots full: opportunity: {}, user: {}",
                    opportunityId,
                    user.getFullName());
            redirectAttributes.addFlashAttribute(
                    WebUtils.MSG_ERROR, WebUtils.getMessage("opportunity.registrations.full"));
            return "redirect:/opportunities/" + opportunityId;

        } catch (MissingMandatorySkillException e) {
            log.debug(
                    "Registration Failed - Missing mandatory skill: {}, user: {}",
                    e.getSkillName(),
                    user.getFullName());
            redirectAttributes.addFlashAttribute(
                    WebUtils.MSG_ERROR,
                    WebUtils.getMessage("opportunity.registrations.missing_skill", e.getSkillName()));
            return "redirect:/opportunities/" + opportunityId;
        }
    }

    @GetMapping("/{id}/requirements")
    public String view_requirements(
            @PathVariable(name = "id") final String opportunityId,
            final Model model,
            @AuthenticationPrincipal final CustomUserDetails userDetails) {
        OrganizationDTO orgFromUser = getOrgFromUser(userDetails);
        log.info("orgFromUser: {}", orgFromUser);
        if (orgFromUser == null) {
            return "redirect:/opportunities/" + opportunityId;
        }
        OpportunityDTO opportunity = opportunityService.get(opportunityId);
        if (opportunity == null || !opportunity.getOrgId().equals(orgFromUser.getId())) {
            return "redirect:/opportunities/" + opportunityId;
        }

        List<OpportunityRequirementDTO> requirements =
                opportunityRequirementService.findAllByOpportunityId(opportunityId);
        List<SkillDTO> allSkills = skillService.findAll();
        Map<String, String> skillNames =
                allSkills.stream().collect(Collectors.toMap(SkillDTO::getId, SkillDTO::getName));
        OpportunityRequirementForm requirementForm = new OpportunityRequirementForm();
        requirementForm.setRequirements(requirements);
        model.addAttribute("opportunity", opportunity);
        model.addAttribute("requirementForm", requirementForm);
        model.addAttribute("allSkills", allSkills);
        model.addAttribute("skillNames", skillNames);
        return "opportunity/requirement_edit";
    }

    @PostMapping("/{id}/requirements")
    @PreAuthorize(SecurityExpressions.ORGANIZER_ONLY)
    public String saveRequirements(
            @PathVariable(name = "id") final String opportunityId,
            @ModelAttribute("requirementForm") final OpportunityRequirementForm requirementForm,
            final RedirectAttributes redirectAttributes) {
        opportunityRequirementService.replaceAllForOpportunity(opportunityId, requirementForm.getRequirements());
        redirectAttributes.addFlashAttribute(
                WebUtils.MSG_SUCCESS, WebUtils.getMessage("opportunity.requirements.update.success"));
        return "redirect:/opportunities/" + opportunityId + "/requirements";
    }

    private OrganizationDTO getOrgFromUser(final CustomUserDetails userDetails) {
        OrganizationDTO orgFromUser = null;
        if (userDetails != null
                && userDetails.getOrganizations() != null
                && !userDetails.getOrganizations().isEmpty()) {
            orgFromUser = userDetails.getOrganizations().get(0);
        }
        return orgFromUser;
    }
}
