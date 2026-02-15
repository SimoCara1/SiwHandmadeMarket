package it.uniroma3.siw.model;

import jakarta.persistence.*;

@Entity
@Table(name = "users") // "user" è parola riservata in Postgres, meglio usare "users"
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    
    private String firstName;
    private String lastName;
    private String email;
    private String role; 
    
    @OneToOne 
    private Cart cart;

    // Costruttore vuoto 
    public User() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public String getLastName() {return lastName;}
	public void setLastName(String lastName) {this.lastName = lastName;}
	public String getEmail() {return email;}
	public void setEmail(String email) {this.email = email;}
	public String getRole() {return role;}
	public void setRole(String role) {this.role = role;}    
}
