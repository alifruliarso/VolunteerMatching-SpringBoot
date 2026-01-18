package com.galapea.techblog.volunteer_matching.organization;

import com.galapea.techblog.volunteer_matching.security.CustomUserDetails;
import com.galapea.techblog.volunteer_matching.util.WebUtils;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
@RequestMapping("/organizations")
public class OrganizationController {

    private final OrganizationService organizationService;

    public OrganizationController(final OrganizationService organizationService) {
        this.organizationService = organizationService;
    }

    @GetMapping
    public String list(final Model model, @AuthenticationPrincipal final CustomUserDetails userDetails) {
        if (userDetails != null
                && userDetails.getOrganizations() != null
                && !userDetails.getOrganizations().isEmpty()) {
            OrganizationDTO org = userDetails.getOrganizations().get(0);
            return "redirect:/organizations/edit/" + org.getId();
        }
        // model.addAttribute("organizations", organizationService.findAll());
        return "organization/list";
    }

    @GetMapping("/add")
    public String add(@ModelAttribute("organization") final OrganizationDTO organizationDTO) {
        return "organization/add";
    }

    @PostMapping("/add")
    public String add(
            @ModelAttribute("organization") @Valid final OrganizationDTO organizationDTO,
            final BindingResult bindingResult,
            final RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "organization/add";
        }
        organizationService.create(organizationDTO);
        redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, WebUtils.getMessage("organization.create.success"));
        return "redirect:/organizations";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable(name = "id") final String id, final Model model) {
        model.addAttribute("organization", organizationService.get(id));
        return "organization/edit";
    }

    @PostMapping("/edit/{id}")
    public String edit(
            @PathVariable(name = "id") final String id,
            @ModelAttribute("organization") @Valid final OrganizationDTO organizationDTO,
            final BindingResult bindingResult,
            final RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "organization/edit";
        }
        organizationService.update(id, organizationDTO);
        redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, WebUtils.getMessage("organization.update.success"));
        return "redirect:/organizations";
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable(name = "id") final String id, final RedirectAttributes redirectAttributes) {
        organizationService.delete(id);
        redirectAttributes.addFlashAttribute(WebUtils.MSG_INFO, WebUtils.getMessage("organization.delete.success"));
        return "redirect:/organizations";
    }
}
