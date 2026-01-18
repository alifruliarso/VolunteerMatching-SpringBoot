package com.galapea.techblog.volunteer_matching.registration;

import com.galapea.techblog.volunteer_matching.opportunity.OpportunityDTO;
import com.galapea.techblog.volunteer_matching.opportunity.OpportunityService;
import com.galapea.techblog.volunteer_matching.organization.OrganizationDTO;
import com.galapea.techblog.volunteer_matching.security.CustomUserDetails;
import com.galapea.techblog.volunteer_matching.security.SecurityExpressions;
import com.galapea.techblog.volunteer_matching.user.UserDTO;
import com.galapea.techblog.volunteer_matching.user.UserService;
import com.galapea.techblog.volunteer_matching.util.WebUtils;
import jakarta.validation.Valid;
import java.util.ArrayList;
import java.util.List;
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
@RequestMapping("/registrations")
public class RegistrationController {
    private final Logger log = LoggerFactory.getLogger(getClass());
    private final RegistrationService registrationService;
    private final UserService userService;
    private final OpportunityService opportunityService;

    public RegistrationController(
            final RegistrationService registrationService,
            final UserService userService,
            final OpportunityService opportunityService) {
        this.registrationService = registrationService;
        this.userService = userService;
        this.opportunityService = opportunityService;
    }

    @ModelAttribute
    public void prepareContext(final Model model) {
        model.addAttribute("statusValues", RegistrationStatus.values());
    }

    @GetMapping
    public String list(final Model model, @AuthenticationPrincipal final CustomUserDetails userDetails) {
        List<RegistrationDTO> allRegistrations = new ArrayList<>();
        if (userDetails != null
                && userDetails.getOrganizations() != null
                && !userDetails.getOrganizations().isEmpty()) {
            OrganizationDTO org = userDetails.getOrganizations().get(0);
            model.addAttribute("organization", org);
            allRegistrations = registrationService.findAllByOrgId(org.getId());
        } else {
            model.addAttribute("organization", null);
            allRegistrations = registrationService.findAll();
        }
        List<RegistrationResponse> responses = allRegistrations.stream()
                .map(r -> {
                    String fullName = "";
                    String opportunityTitle = "";
                    if (r.getUserId() != null && !r.getUserId().isBlank()) {
                        UserDTO u = userService.get(r.getUserId());
                        if (u != null && u.getFullName() != null) {
                            fullName = u.getFullName();
                        }
                    }
                    if (r.getOpportunityId() != null && !r.getOpportunityId().isBlank()) {
                        OpportunityDTO o = opportunityService.get(r.getOpportunityId());
                        if (o != null && o.getTitle() != null) {
                            opportunityTitle = o.getTitle();
                        }
                    }
                    String status = r.getStatus() == null ? "" : r.getStatus().name();
                    String registrationTime = r.getRegistrationTime() == null
                            ? ""
                            : r.getRegistrationTime().toString();
                    return new RegistrationResponse(
                            r.getId(),
                            r.getUserId(),
                            r.getOpportunityId(),
                            status,
                            status.compareToIgnoreCase(RegistrationStatus.PENDING.name()) == 0 ? true : false,
                            registrationTime,
                            fullName,
                            opportunityTitle);
                })
                .collect(Collectors.toList());
        model.addAttribute("registrations", responses);
        return "registration/list";
    }

    @GetMapping("/add")
    public String add(@ModelAttribute("registration") final RegistrationDTO registrationDTO) {
        return "registration/add";
    }

    @PostMapping("/add")
    @PreAuthorize(SecurityExpressions.ORGANIZER_ONLY)
    public String add(
            @ModelAttribute("registration") @Valid final RegistrationDTO registrationDTO,
            final BindingResult bindingResult,
            final RedirectAttributes redirectAttributes,
            @AuthenticationPrincipal final UserDetails userDetails) {
        if (bindingResult.hasErrors()) {
            return "registration/add";
        }
        log.debug("User: {}", userDetails);
        UserDTO user = userService
                .getOneByEmail(userDetails.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        registrationDTO.setUserId(user.getId());
        registrationDTO.setStatus(RegistrationStatus.PENDING);
        registrationService.create(registrationDTO);
        redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, WebUtils.getMessage("registration.create.success"));
        return "redirect:/registrations";
    }

    @GetMapping("/edit/{id}")
    @PreAuthorize(SecurityExpressions.ORGANIZER_ONLY)
    public String edit(@PathVariable(name = "id") final String id, final Model model) {
        model.addAttribute("registration", registrationService.get(id));
        return "registration/edit";
    }

    @PostMapping("/edit/{id}")
    @PreAuthorize(SecurityExpressions.ORGANIZER_ONLY)
    public String edit(
            @PathVariable(name = "id") final String id,
            @ModelAttribute("registration") @Valid final RegistrationDTO registrationDTO,
            final BindingResult bindingResult,
            final RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "registration/edit";
        }
        registrationService.update(id, registrationDTO);
        redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, WebUtils.getMessage("registration.update.success"));
        return "redirect:/registrations";
    }

    @PostMapping("/delete/{id}")
    @PreAuthorize(SecurityExpressions.ORGANIZER_ONLY)
    public String delete(@PathVariable(name = "id") final String id, final RedirectAttributes redirectAttributes) {
        registrationService.delete(id);
        redirectAttributes.addFlashAttribute(WebUtils.MSG_INFO, WebUtils.getMessage("registration.delete.success"));
        return "redirect:/registrations";
    }

    @PostMapping("/approve/{id}")
    @PreAuthorize(SecurityExpressions.ORGANIZER_ONLY)
    public String approve(@PathVariable(name = "id") final String id, final RedirectAttributes redirectAttributes) {
        registrationService.delete(id);
        redirectAttributes.addFlashAttribute(WebUtils.MSG_INFO, WebUtils.getMessage("registration.approve.success"));
        return "redirect:/registrations";
    }

    @PostMapping("/reject/{id}")
    @PreAuthorize(SecurityExpressions.ORGANIZER_ONLY)
    public String reject(@PathVariable(name = "id") final String id, final RedirectAttributes redirectAttributes) {
        registrationService.delete(id);
        redirectAttributes.addFlashAttribute(WebUtils.MSG_INFO, WebUtils.getMessage("registration.reject.success"));
        return "redirect:/registrations";
    }
}
