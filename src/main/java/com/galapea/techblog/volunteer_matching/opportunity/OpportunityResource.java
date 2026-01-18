package com.galapea.techblog.volunteer_matching.opportunity;

import com.galapea.techblog.volunteer_matching.registration.RegistrationDTO;
import com.galapea.techblog.volunteer_matching.registration.RegistrationService;
import com.galapea.techblog.volunteer_matching.user.UserDTO;
import com.galapea.techblog.volunteer_matching.user.UserService;
import com.galapea.techblog.volunteer_matching.util.WebUtils;
import java.util.Optional;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/opportunities", produces = MediaType.APPLICATION_JSON_VALUE)
public class OpportunityResource {
    private final OpportunityService opportunityService;
    private final UserService userService;
    private final RegistrationService registrationService;

    public OpportunityResource(
            final OpportunityService opportunityService,
            final UserService userService,
            final RegistrationService registrationService) {
        this.opportunityService = opportunityService;
        this.userService = userService;
        this.registrationService = registrationService;
    }

    @GetMapping("{id}/availabilityStatus")
    public ResponseEntity<String> getOrganizationMember(
            @PathVariable(name = "id") final String opportunityId,
            @AuthenticationPrincipal final UserDetails userDetails) {
        Optional<UserDTO> userOpt = userService.getOneByEmail(userDetails.getUsername());
        if (userOpt.isEmpty()) {
            return ResponseEntity.ok("Login to see status");
        }
        Optional<RegistrationDTO> reg =
                registrationService.getByUserIdAndOpportunityId(userOpt.get().getId(), opportunityId);
        if (reg.isPresent()) {
            return ResponseEntity.ok(WebUtils.getMessage("opportunity.status.registered"));
        }
        return ResponseEntity.ok("Not Registered");
    }

    @GetMapping("{id}/slotAvailable")
    public ResponseEntity<String> slotAvailable(@PathVariable(name = "id") final String opportunityId) {
        Optional<OpportunityDTO> opportunityOpt = opportunityService.getOneById(opportunityId);
        if (opportunityOpt.isPresent()) {
            OpportunityDTO opportunity = opportunityOpt.get();
            if (opportunity.getSlotsTotal() != null && opportunity.getSlotsTotal() > 0) {
                Long registeredCount = registrationService.countByOpportunityId(opportunityId);
                return ResponseEntity.ok(
                        String.valueOf(opportunity.getSlotsTotal() - registeredCount + " slots available"));
            }
        }
        return ResponseEntity.ok("No Slot Available");
    }

    @GetMapping("{id}/slotFilledProgress")
    public ResponseEntity<String> slotFilledProgress(@PathVariable(name = "id") final String opportunityId) {
        Optional<OpportunityDTO> opportunityOpt = opportunityService.getOneById(opportunityId);
        if (opportunityOpt.isPresent()) {
            OpportunityDTO opportunity = opportunityOpt.get();
            if (opportunity.getSlotsTotal() != null && opportunity.getSlotsTotal() > 0) {
                double registeredCount = registrationService.countByOpportunityId(opportunityId);
                double percentage = (registeredCount / (double) opportunity.getSlotsTotal()) * 100;
                return ResponseEntity.ok(String.valueOf(percentage));
            }
        }
        return ResponseEntity.ok("0");
    }
}
