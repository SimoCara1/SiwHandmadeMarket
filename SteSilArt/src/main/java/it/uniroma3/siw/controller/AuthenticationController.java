package it.uniroma3.siw.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import it.uniroma3.siw.model.Credentials;
import it.uniroma3.siw.model.User;
import it.uniroma3.siw.service.CredentialsService;
import it.uniroma3.siw.service.UserService;
import jakarta.transaction.Transactional;

@Controller
public class AuthenticationController {
    
    @Autowired
    private CredentialsService credentialsService;
    
    @Autowired
    private UserService userService;

    @GetMapping("/")
    public String index() {
        return "index";
    }
    
    // Mostra il form di registrazione
    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        model.addAttribute("user", new User());
        model.addAttribute("credentials", new Credentials());
        return "registerUser";
    }

    // Gestisce l'invio del form
    @Transactional
    @PostMapping("/register")
    public String registerUser(@ModelAttribute("user") User user, 
                               @ModelAttribute("credentials") Credentials credentials, 
                               Model model) {

        // 1. PRIMO CONTROLLO: L'email è già nel DB?
        if (userService.getUserByEmail(user.getEmail()) != null) {
            model.addAttribute("error", "Questa email è già associata a un account.");
            return "registerUser"; // Torna al form senza salvare nulla
        }

        // 2. SECONDO CONTROLLO: Lo username è già preso?
        if (credentialsService.getCredentials(credentials.getUsername()) != null) {
            model.addAttribute("error", "Questo username è già in uso. Scegline un altro.");
            return "registerUser"; // Torna al form senza salvare nulla
        }

        
        userService.saveUser(user); 

        credentials.setUser(user);
        
        credentialsService.saveCredentials(credentials);

        return "/login"; 
    }

    @GetMapping("/login")
    public String showLoginForm() {
        return "login";
    }
}
