package it.uniroma3.siw.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import it.uniroma3.siw.model.Message;
import it.uniroma3.siw.model.User;

public interface MessageRepository extends CrudRepository<Message, Long> {
    
    // Trova tutti i messaggi tra l'utente A e l'utente B per un certo prodotto
    @Query("SELECT m FROM Message m WHERE m.relatedProduct.id = :prodId " +
           "AND ((m.sender.id = :u1 AND m.recipient.id = :u2) " +
           "OR (m.sender.id = :u2 AND m.recipient.id = :u1)) " +
           "ORDER BY m.sentAt ASC")
    public List<Message> findChat(@Param("prodId") Long prodId, 
                                  @Param("u1") Long u1, 
                                  @Param("u2") Long u2);
    
    public long countByRecipientAndIsReadFalse(User recipient);

	public Iterable<Message> findByRecipientOrderBySentAtDesc(User user);
}
