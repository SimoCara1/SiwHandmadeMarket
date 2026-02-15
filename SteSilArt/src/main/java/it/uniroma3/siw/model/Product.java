package it.uniroma3.siw.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import jakarta.persistence.*;

@Entity
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    
    private String name;
    private String description;
    private Double price;
    private String category;
    @ManyToOne
    private User author; // L'artigiano che ha creato il prodotto
   
 // Aggiungi fetch = FetchType.EAGER
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<Image> images = new ArrayList<>();
    
    public Product() {
    }
    // Costruttori, Getter e Setter manuali
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }
    public User getAuthor() { return author; }
    public void setAuthor(User author) { this.author = author; }
	public String getCategory() {return category;}
	public void setCategory(String category) {this.category = category;}
	public List<Image> getImages() { return images; }
    public void setImages(List<Image> images) { this.images = images; }
	
    @Override
	public int hashCode() {
		return Objects.hash(id);
	}
	@Override
	public boolean equals(Object obj) {if (this == obj)return true;
		if (obj == null)return false;
		if (getClass() != obj.getClass())return false;
		Product other = (Product) obj;
		return Objects.equals(id, other.id);
	}   
    
}


