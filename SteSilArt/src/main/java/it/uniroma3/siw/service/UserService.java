package it.uniroma3.siw.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.uniroma3.siw.model.User;
import it.uniroma3.siw.repository.UserRepository;
import jakarta.transaction.Transactional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Transactional
    public User saveUser(User user) {
        return this.userRepository.save(user);
    }

    public User getUser(Long id) {
        return this.userRepository.findById(id).orElse(null);
    }
    
    @Transactional
    public User getUserByEmail(String email) {
        // Cerchiamo l'utente e restituiamo l'oggetto o null se non esiste
        return userRepository.findByEmail(email).orElse(null);
    }
}
