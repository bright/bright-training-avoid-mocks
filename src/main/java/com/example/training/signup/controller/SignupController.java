package com.example.training.signup.controller;

import com.example.training.signup.model.SignupRequest;
import com.example.training.signup.model.SignupResponse;
import com.example.training.signup.service.SignupService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Controller for handling user signup requests.
 * 
 * PROBLEM: This controller passes HttpServletRequest and HttpServletResponse directly
 * to the service layer, which makes the service layer tightly coupled to the web layer
 * and difficult to test.
 */
@Controller
@RequestMapping("/signup")
public class SignupController {

    private final SignupService signupService;

    @Autowired
    public SignupController(SignupService signupService) {
        this.signupService = signupService;
    }

    /**
     * Displays the signup form.
     */
    @GetMapping
    public String showSignupForm(Model model) {
        model.addAttribute("signupRequest", new SignupRequest());
        return "signup/form";
    }

    /**
     * Processes the signup form submission.
     * 
     * Note: This method passes HttpServletRequest and HttpServletResponse to the service layer,
     * which is a problematic design that tightly couples the service to the web layer.
     */
    @PostMapping
    public String processSignup(
            @ModelAttribute SignupRequest signupRequest,
            HttpServletRequest request,
            HttpServletResponse response,
            RedirectAttributes redirectAttributes) {
        
        SignupResponse signupResponse = signupService.processSignup(signupRequest, request, response);
        
        if (signupResponse.isSuccess()) {
            redirectAttributes.addFlashAttribute("message", "Signup successful!");
            return "redirect:" + signupResponse.getRedirectUrl();
        } else {
            redirectAttributes.addFlashAttribute("error", signupResponse.getMessage());
            redirectAttributes.addFlashAttribute("signupRequest", signupRequest);
            return "redirect:/signup";
        }
    }

    /**
     * Alternative endpoint that directly passes the HttpServletRequest and HttpServletResponse
     * to the service layer without extracting the data first.
     * 
     * This is an even more problematic approach as it completely delegates request handling
     * to the service layer.
     */
    @PostMapping("/direct")
    public String processSignupDirect(
            HttpServletRequest request,
            HttpServletResponse response,
            RedirectAttributes redirectAttributes) {
        
        SignupResponse signupResponse = signupService.processSignup(request, response);
        
        if (signupResponse.isSuccess()) {
            redirectAttributes.addFlashAttribute("message", "Signup successful!");
            return "redirect:" + signupResponse.getRedirectUrl();
        } else {
            redirectAttributes.addFlashAttribute("error", signupResponse.getMessage());
            return "redirect:/signup";
        }
    }
}