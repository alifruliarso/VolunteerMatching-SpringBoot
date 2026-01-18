package com.galapea.techblog.volunteer_matching.volunteer_skill;

import com.galapea.techblog.volunteer_matching.security.CustomUserDetails;
import com.galapea.techblog.volunteer_matching.skill.SkillDTO;
import com.galapea.techblog.volunteer_matching.skill.SkillService;
import com.galapea.techblog.volunteer_matching.util.WebUtils;
import jakarta.validation.Valid;
import java.util.Map;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
@RequestMapping("/volunteerSkills")
public class VolunteerSkillController {
    private static final Logger LOGGER = LoggerFactory.getLogger(VolunteerSkillController.class);

    private final VolunteerSkillService volunteerSkillService;
    private final SkillService skillService;

    public VolunteerSkillController(
            final VolunteerSkillService volunteerSkillService, final SkillService skillService) {
        this.volunteerSkillService = volunteerSkillService;
        this.skillService = skillService;
    }

    @ModelAttribute
    public void prepareContext(final Model model) {
        model.addAttribute("verificationStatusValues", VerificationStatus.values());
        Map<String, String> skillMap =
                skillService.findAll().stream().collect(Collectors.toMap(SkillDTO::getId, SkillDTO::getName));
        model.addAttribute("skillIdValues", skillMap);
        // model.addAttribute("skillIdValues", List.of(new SkillDTO("1", "Java"), new SkillDTO("2", "Spring Boot")));
    }

    @GetMapping
    public String list(final Model model, @AuthenticationPrincipal final CustomUserDetails userDetails) {
        String userId = userDetails.getUserId();
        LOGGER.info("<<<<<< List userId: {}", userId);
        model.addAttribute("volunteerSkills", volunteerSkillService.findAllByUserId(userId));
        return "volunteerSkill/list";
    }

    @GetMapping("/add")
    public String add(
            @ModelAttribute("volunteerSkill") final VolunterSkillAddRequest volunteerSkillDTO,
            @AuthenticationPrincipal final CustomUserDetails userDetails) {
        LOGGER.info("<<<<<< Add userId: {}", userDetails.getUserId());

        volunteerSkillDTO.setUserId(userDetails.getUserId());
        volunteerSkillDTO.setVerificationStatus(VerificationStatus.VERIFIED);
        return "volunteerSkill/add";
    }

    @PostMapping("/add")
    public String add(
            @ModelAttribute("volunteerSkill") @Valid final VolunterSkillAddRequest volunteerSkillDTO,
            final BindingResult bindingResult,
            final RedirectAttributes redirectAttributes,
            @AuthenticationPrincipal final CustomUserDetails userDetails) {
        volunteerSkillDTO.setUserId(userDetails.getUserId());
        LOGGER.info("<<<<<< Add POST getExpiryDate: {}", volunteerSkillDTO.getExpiryDate());
        if (bindingResult.hasErrors()) {
            return "volunteerSkill/add";
        }
        volunteerSkillService.create(volunteerSkillDTO);
        redirectAttributes.addFlashAttribute(
                WebUtils.MSG_SUCCESS, WebUtils.getMessage("volunteerSkill.create.success"));
        return "redirect:/volunteerSkills";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable(name = "id") final String id, final Model model) {
        model.addAttribute("volunteerSkill", volunteerSkillService.get(id));
        return "volunteerSkill/edit";
    }

    @PostMapping("/edit/{id}")
    public String edit(
            @PathVariable(name = "id") final String id,
            @ModelAttribute("volunteerSkill") @Valid final VolunteerSkillDTO volunteerSkillDTO,
            final BindingResult bindingResult,
            final RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "volunteerSkill/edit";
        }
        volunteerSkillService.update(id, volunteerSkillDTO);
        redirectAttributes.addFlashAttribute(
                WebUtils.MSG_SUCCESS, WebUtils.getMessage("volunteerSkill.update.success"));
        return "redirect:/volunteerSkills";
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable(name = "id") final String id, final RedirectAttributes redirectAttributes) {
        volunteerSkillService.delete(id);
        redirectAttributes.addFlashAttribute(WebUtils.MSG_INFO, WebUtils.getMessage("volunteerSkill.delete.success"));
        return "redirect:/volunteerSkills";
    }
}
