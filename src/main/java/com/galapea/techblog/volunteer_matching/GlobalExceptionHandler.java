package com.galapea.techblog.volunteer_matching;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ForbiddenGridDbConnectionException.class)
    public String handleForbiddenGridDbConnectionException(ForbiddenGridDbConnectionException ex, Model model) {
        model.addAttribute("errorTitle", "Error connecting to GridDB Cloud.");
        model.addAttribute("errorMessage", ex.getMessage());
        return "error-forbidden";
    }
}
