package it.uniroma3.siw.model;

import jakarta.persistence.*;
import java.util.Base64;

@Entity
public class Image {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    
    @Column(columnDefinition = "bytea") // Molto più stabile su Postgres rispetto a @Lob
    private byte[] data;
    
    private String name;
    
    public String getBase64Data() {
        if (this.data != null) {
            return Base64.getEncoder().encodeToString(this.data);
        }
        return null;
    }
    

	public Long getId() {return id;}
	public void setId(Long id) {this.id = id;}
	public byte[] getData() {return data;}
	public void setData(byte[] data) {this.data = data;}
	public String getName() {return name;}
	public void setName(String name) {this.name = name;}
}
