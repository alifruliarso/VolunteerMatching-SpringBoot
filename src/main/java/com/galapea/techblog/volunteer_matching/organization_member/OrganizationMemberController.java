package com.galapea.techblog.volunteer_matching.organization_member;

import com.galapea.techblog.volunteer_matching.util.WebUtils;
import jakarta.validation.Valid;
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
@RequestMapping("/organizationMembers")
public class OrganizationMemberController {

    private final OrganizationMemberService organizationMemberService;

    public OrganizationMemberController(final OrganizationMemberService organizationMemberService) {
        this.organizationMemberService = organizationMemberService;
    }

    @ModelAttribute
    public void prepareContext(final Model model) {
        model.addAttribute("memberRoleValues", MemberRole.values());
    }

    @GetMapping
    public String list(final Model model) {
        model.addAttribute("organizationMembers", organizationMemberService.findAll());
        return "organizationMember/list";
    }

    @GetMapping("/add")
    public String add(@ModelAttribute("organizationMember") final OrganizationMemberDTO organizationMemberDTO) {
        return "organizationMember/add";
    }

    @PostMapping("/add")
    public String add(
            @ModelAttribute("organizationMember") @Valid final OrganizationMemberDTO organizationMemberDTO,
            final BindingResult bindingResult,
            final RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "organizationMember/add";
        }
        organizationMemberService.create(organizationMemberDTO);
        redirectAttributes.addFlashAttribute(
                WebUtils.MSG_SUCCESS, WebUtils.getMessage("organizationMember.create.success"));
        return "redirect:/organizationMembers";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable(name = "id") final String id, final Model model) {
        model.addAttribute("organizationMember", organizationMemberService.get(id));
        return "organizationMember/edit";
    }

    @PostMapping("/edit/{id}")
    public String edit(
            @PathVariable(name = "id") final String id,
            @ModelAttribute("organizationMember") @Valid final OrganizationMemberDTO organizationMemberDTO,
            final BindingResult bindingResult,
            final RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "organizationMember/edit";
        }
        organizationMemberService.update(id, organizationMemberDTO);
        redirectAttributes.addFlashAttribute(
                WebUtils.MSG_SUCCESS, WebUtils.getMessage("organizationMember.update.success"));
        return "redirect:/organizationMembers";
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable(name = "id") final String id, final RedirectAttributes redirectAttributes) {
        organizationMemberService.delete(id);
        redirectAttributes.addFlashAttribute(
                WebUtils.MSG_INFO, WebUtils.getMessage("organizationMember.delete.success"));
        return "redirect:/organizationMembers";
    }
}
