package com.galapea.techblog.volunteer_matching.skill;

import com.galapea.techblog.volunteer_matching.security.SecurityExpressions;
import com.galapea.techblog.volunteer_matching.util.WebUtils;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
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
@RequestMapping("/skills")
public class SkillController {

    private final SkillService skillService;

    public SkillController(final SkillService skillService) {
        this.skillService = skillService;
    }

    @GetMapping
    public String list(final Model model) {
        model.addAttribute("skills", skillService.findAll());
        return "skill/list";
    }

    @GetMapping("/add")
    public String add(@ModelAttribute("skill") final SkillDTO skillDTO) {
        return "skill/add";
    }

    @PostMapping("/add")
    public String add(
            @ModelAttribute("skill") @Valid final SkillDTO skillDTO,
            final BindingResult bindingResult,
            final RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "skill/add";
        }
        skillService.create(skillDTO);
        redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, WebUtils.getMessage("skill.create.success"));
        return "redirect:/skills";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable(name = "id") final String id, final Model model) {
        model.addAttribute("skill", skillService.get(id));
        return "skill/edit";
    }

    @PostMapping("/edit/{id}")
    public String edit(
            @PathVariable(name = "id") final String id,
            @ModelAttribute("skill") @Valid final SkillDTO skillDTO,
            final BindingResult bindingResult,
            final RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "skill/edit";
        }
        skillService.update(id, skillDTO);
        redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, WebUtils.getMessage("skill.update.success"));
        return "redirect:/skills";
    }

    @PostMapping("/delete/{id}")
    @PreAuthorize(SecurityExpressions.ADMIN_ONLY)
    public String delete(@PathVariable(name = "id") final String id, final RedirectAttributes redirectAttributes) {
        skillService.delete(id);
        redirectAttributes.addFlashAttribute(WebUtils.MSG_INFO, WebUtils.getMessage("skill.delete.success"));
        return "redirect:/skills";
    }
}
