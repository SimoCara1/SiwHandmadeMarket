package it.uniroma3.siw.repository;

import org.springframework.data.repository.CrudRepository;
import it.uniroma3.siw.model.User;
import java.util.Optional;

public interface UserRepository extends CrudRepository<User, Long> {
    // Lasciala vuota o metti solo metodi per campi che ESISTONO in User
	public Optional<User> findByEmail(String email);
}