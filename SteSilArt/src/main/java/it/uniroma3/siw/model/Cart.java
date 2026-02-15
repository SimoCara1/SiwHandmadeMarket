package it.uniroma3.siw.model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Cart {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @OneToOne
    private User user;

    @ManyToMany
    private List<Product> products;

    // 1. COSTRUTTORE VUOTO (Obbligatorio per JPA)
    public Cart() {
        this.products = new ArrayList<>();
    }

    // 2. COSTRUTTORE CON USER (Risolve l'errore "new Cart(user)")
    public Cart(User user) {
        this.user = user;
        this.products = new ArrayList<>();
    }

    // Getter e Setter
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    
    public List<Product> getProducts() { 
        if (this.products == null) {
            this.products = new ArrayList<>();
        }
        return products; 
    }
    
    public void setProducts(List<Product> products) { this.products = products; }
}