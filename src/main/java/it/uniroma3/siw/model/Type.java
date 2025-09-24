package it.uniroma3.siw.model;

import jakarta.persistence.*;
import java.util.List;
import java.util.Objects;

@Entity
public class Type {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	private String name;

	private String imagePath; // Puoi tenerlo, è utile!

	@OneToMany(mappedBy = "type")
	private List<Product> products; // NUOVA RELAZIONE

	public Type() {
		this.name = null;
	}

	public Type(String name) {
		this.name = name;
	}

	// Getters e Setters

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getImagePath() {
		return imagePath;
	}

	public void setImagePath(String imagePath) {
		this.imagePath = imagePath;
	}

	public List<Product> getProducts() {
		return products;
	}

	public void setProducts(List<Product> products) {
		this.products = products;
	}

	// Equals e HashCode (buona pratica)

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		Type type = (Type) o;
		return Objects.equals(name, type.name);
	}

	@Override
	public int hashCode() {
		return Objects.hash(name);
	}
}