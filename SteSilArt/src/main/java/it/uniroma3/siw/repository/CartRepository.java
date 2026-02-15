package it.uniroma3.siw.repository;

import java.util.Optional;
import org.springframework.data.repository.CrudRepository;
import it.uniroma3.siw.model.Cart;
import it.uniroma3.siw.model.User;

public interface CartRepository extends CrudRepository<Cart, Long> {
    // Metodo fondamentale per trovare il carrello dell'utente loggato
    public Optional<Cart> findByUser(User user);
}