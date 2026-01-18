package com.galapea.techblog.volunteer_matching.opportunity_requirement;

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
@RequestMapping("/opportunityRequirements")
public class OpportunityRequirementController {

    private final OpportunityRequirementService opportunityRequirementService;

    public OpportunityRequirementController(final OpportunityRequirementService opportunityRequirementService) {
        this.opportunityRequirementService = opportunityRequirementService;
    }

    @GetMapping
    public String list(final Model model, @AuthenticationPrincipal final CustomUserDetails userDetails) {
        return "redirect:/opportunities";
    }

    @GetMapping("/add")
    public String add(
            @ModelAttribute("opportunityRequirement") final OpportunityRequirementDTO opportunityRequirementDTO) {
        return "opportunityRequirement/add";
    }

    @PostMapping("/add")
    public String add(
            @ModelAttribute("opportunityRequirement") @Valid final OpportunityRequirementDTO opportunityRequirementDTO,
            final BindingResult bindingResult,
            final RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "opportunityRequirement/add";
        }
        opportunityRequirementService.create(opportunityRequirementDTO);
        redirectAttributes.addFlashAttribute(
                WebUtils.MSG_SUCCESS, WebUtils.getMessage("opportunityRequirement.create.success"));
        return "redirect:/opportunityRequirements";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable(name = "id") final String id, final Model model) {
        model.addAttribute("opportunityRequirement", opportunityRequirementService.get(id));
        return "opportunityRequirement/edit";
    }

    @PostMapping("/edit/{id}")
    public String edit(
            @PathVariable(name = "id") final String id,
            @ModelAttribute("opportunityRequirement") @Valid final OpportunityRequirementDTO opportunityRequirementDTO,
            final BindingResult bindingResult,
            final RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "opportunityRequirement/edit";
        }
        opportunityRequirementService.update(id, opportunityRequirementDTO);
        redirectAttributes.addFlashAttribute(
                WebUtils.MSG_SUCCESS, WebUtils.getMessage("opportunityRequirement.update.success"));
        return "redirect:/opportunityRequirements";
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable(name = "id") final String id, final RedirectAttributes redirectAttributes) {
        opportunityRequirementService.delete(id);
        redirectAttributes.addFlashAttribute(
                WebUtils.MSG_INFO, WebUtils.getMessage("opportunityRequirement.delete.success"));
        return "redirect:/opportunityRequirements";
    }
}
