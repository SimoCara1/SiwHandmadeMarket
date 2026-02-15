package it.uniroma3.siw.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import it.uniroma3.siw.model.Cart;
import it.uniroma3.siw.model.Credentials;
import it.uniroma3.siw.model.User;
import it.uniroma3.siw.repository.CartRepository;
import it.uniroma3.siw.repository.MessageRepository;
import it.uniroma3.siw.service.CredentialsService;

@ControllerAdvice
public class GlobalControllerAdvice {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private CredentialsService credentialsService;

    @ModelAttribute
    public void addAttributes(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        
        if (auth != null && auth.getPrincipal() instanceof UserDetails) {
            UserDetails userDetails = (UserDetails) auth.getPrincipal();
            Credentials credentials = credentialsService.getCredentials(userDetails.getUsername());
            
            if (credentials != null) {
                User user = credentials.getUser();
                model.addAttribute("loggedInUser", user);

                // 1. Conteggio Carrello
                Cart cart = cartRepository.findByUser(user).orElse(null);
                model.addAttribute("cartCount", (cart != null) ? cart.getProducts().size() : 0);

                // 2. Conteggio Messaggi Ricevuti NON LETTI
                long mCount = messageRepository.countByRecipientAndIsReadFalse(user);
                model.addAttribute("messageCount", mCount);
            }
        } else {
            model.addAttribute("cartCount", 0);
            model.addAttribute("messageCount", 0);
            model.addAttribute("loggedInUser", null);
        }
    }
}