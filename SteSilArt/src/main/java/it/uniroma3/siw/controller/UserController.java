package it.uniroma3.siw.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import it.uniroma3.siw.model.Credentials;
import it.uniroma3.siw.service.CredentialsService;

@Controller
public class UserController {

    @Autowired
    private CredentialsService credentialsService;

    @GetMapping("/profile")
    public String showProfile(Model model) {
        // 1. Recuperiamo i dettagli dell'account loggato da Spring Security
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        
        if (principal instanceof UserDetails) {
            UserDetails userDetails = (UserDetails) principal;
            // 2. Usiamo lo username per ottenere le credenziali e quindi l'User
            Credentials credentials = credentialsService.getCredentials(userDetails.getUsername());
            
            // 3. Passiamo l'oggetto User al template profile.html
            model.addAttribute("user", credentials.getUser());
        }
        
        return "profile";
    }
}