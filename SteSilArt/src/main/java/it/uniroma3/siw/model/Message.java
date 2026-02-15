package it.uniroma3.siw.model;

import java.time.LocalDateTime;

import org.hibernate.annotations.*;

import jakarta.persistence.*;

@Entity
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    
    @ManyToOne
    private User sender;
    
    @ManyToOne
    private User recipient;
    
 // Nel file Message.java

    @ManyToOne
    @OnDelete(action = OnDeleteAction.CASCADE) 
    private Product relatedProduct;
    
 // Nel file Message.java aggiungi queste righe:

    private boolean isRead = false; //

    public boolean isRead() {
        return isRead;
    }

    public void setIsRead(boolean isRead) {
        this.isRead = isRead;
    }
    
    private String content;
    private LocalDateTime sentAt;
	public Long getId() {return id;}
	public void setId(Long id) {this.id = id;}
	public User getSender() {return sender;}
	public void setSender(User sender) {this.sender = sender;}
	public User getRecipient() {return recipient;}
	public void setRecipient(User recipient) {this.recipient = recipient;}
	public Product getRelatedProduct() {return relatedProduct;}
	public void setRelatedProduct(Product relatedProduct) {this.relatedProduct = relatedProduct;}
	public String getContent() {return content;}
	public void setContent(String content) {this.content = content;}
	public LocalDateTime getSentAt() {return sentAt;}
	public void setSentAt(LocalDateTime sentAt) {this.sentAt = sentAt;}
}
