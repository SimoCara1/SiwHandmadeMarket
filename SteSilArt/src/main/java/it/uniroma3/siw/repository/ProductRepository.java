package it.uniroma3.siw.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import it.uniroma3.siw.model.Product;
import it.uniroma3.siw.model.User;

public interface ProductRepository extends CrudRepository<Product, Long> {
    // Per visualizzare tutti i prodotti di un certo artigiano
    public List<Product> findByAuthor(User author);
    
    // Per cercare prodotti per categoria (utile per il catalogo)
    public Iterable<Product> findByCategory(String category);
    
}
