package it.uniroma3.siw.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.*;

import it.uniroma3.siw.model.Credentials;
import it.uniroma3.siw.repository.CredentialsRepository;
import jakarta.transaction.Transactional;

@Service
public class CredentialsService {

    @Autowired
    private CredentialsRepository credentialsRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Transactional
    public Credentials saveCredentials(Credentials credentials) {
        // Criptiamo la password prima di salvarla sul DB
        credentials.setPassword(passwordEncoder.encode(credentials.getPassword()));
        // Impostiamo il ruolo di default per i nuovi registrati
        credentials.setRole("DEFAULT");
        return this.credentialsRepository.save(credentials);
    }

    public Credentials getCredentials(String username) {
        return this.credentialsRepository.findByUsername(username).orElse(null);
    }
}