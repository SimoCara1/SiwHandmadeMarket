package it.uniroma3.siw.controller;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import it.uniroma3.siw.model.*;
import it.uniroma3.siw.repository.*;
import it.uniroma3.siw.service.CredentialsService;
import it.uniroma3.siw.service.UserService;

@Controller
public class MessageController {

    @Autowired private MessageRepository messageRepository;
    @Autowired private ProductRepository productRepository;
    @Autowired private CredentialsService credentialsService;
    @Autowired private UserService userService;

    // Redirige alla chat room esistente o ne crea una nuova
    @GetMapping("/messages/new/{productId}")
    public String startChat(@PathVariable("productId") Long productId, 
                            @RequestParam(value = "recipientId", required = false) Long recipientId) {
        Product product = productRepository.findById(productId).orElse(null);
        Long targetId = (recipientId != null) ? recipientId : product.getAuthor().getId();
        return "redirect:/chat/" + productId + "/" + targetId;
    }

    @GetMapping("/chat/{productId}/{otherUserId}")
    public String showChat(@PathVariable("productId") Long productId, 
                           @PathVariable("otherUserId") Long otherUserId, Model model) {
        
        User currentUser = credentialsService.getCredentials(
            SecurityContextHolder.getContext().getAuthentication().getName()).getUser();
        User otherUser = userService.getUser(otherUserId);
        Product product = productRepository.findById(productId).orElse(null);

        // Recuperiamo tutta la cronologia
        List<Message> conversation = messageRepository.findChat(productId, currentUser.getId(), otherUserId);
        
        // Marichiamo come letti i messaggi RICEVUTI in questa chat
        for(Message m : conversation) {
            if(m.getRecipient().getId().equals(currentUser.getId()) && !m.isRead()) {
                m.setIsRead(true);
                messageRepository.save(m);
            }
        }

        model.addAttribute("conversation", conversation);
        model.addAttribute("product", product);
        model.addAttribute("otherUser", otherUser);
        model.addAttribute("message", new Message()); 
        return "chat";
    }

    @PostMapping("/messages/send")
    public String sendMessage(@ModelAttribute("message") Message message, 
                              @RequestParam("productId") Long productId,
                              @RequestParam("recipientId") Long recipientId) {
        
        User currentUser = credentialsService.getCredentials(
            SecurityContextHolder.getContext().getAuthentication().getName()).getUser();
        User recipient = userService.getUser(recipientId);
        Product product = productRepository.findById(productId).orElse(null);

        message.setSender(currentUser);
        message.setRecipient(recipient);
        message.setRelatedProduct(product);
        message.setSentAt(LocalDateTime.now());
        message.setIsRead(false); // Il messaggio parte come non letto

        messageRepository.save(message);
        return "redirect:/chat/" + productId + "/" + recipientId;
    }

    @GetMapping("/my-messages")
    public String showMyMessages(Model model) {
        User currentUser = credentialsService.getCredentials(
            SecurityContextHolder.getContext().getAuthentication().getName()).getUser();

        model.addAttribute("receivedMessages", messageRepository.findByRecipientOrderBySentAtDesc(currentUser));
        // Metodo findBySenderOrderBySentAtDesc deve essere aggiunto al repository se non presente
        return "myMessages";
    }
}