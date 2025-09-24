package it.uniroma3.siw.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.format.annotation.DateTimeFormat;

@Entity
public class Product {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	private String name;

	private boolean auctionSettled = false;

	@Column(length = 1000)
	private String description;

	private String imagePath;

	// --- NEW FIELDS FOR AUCTION LOGIC ---

	private BigDecimal startingPrice;

	@DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
	private LocalDateTime auctionEndDate;

	@Column(name = "current_bid")
	private BigDecimal currentBid;

	// --- CORREZIONE QUI: Aggiunto il campo mancante ---
	@ManyToOne
	private User highestBidder;

	// --- RELATIONSHIPS ---
	@ManyToOne
	private Type type;

	@OneToMany(mappedBy = "product", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	private List<Comment> comments = new ArrayList<>();

	@ManyToMany
	private Set<Product> similarProducts = new HashSet<>();

	// A product can have many bids
	@OneToMany(mappedBy = "product", cascade = CascadeType.ALL)
	private List<Bid> bids = new ArrayList<>();

	/**
	 * NUOVO METODO: Restituisce la lista delle offerte ordinate cronologicamente.
	 * Questo assicura che il grafico venga sempre disegnato nell'ordine corretto.
	 * 
	 * @return Una lista di Bid ordinata per data.
	 */
	public List<Bid> getBidsChronological() {
		if (this.bids == null) {
			return new ArrayList<>();
		}
		// Crea una nuova lista ordinata per non modificare l'originale
		return this.bids.stream().sorted(Comparator.comparing(Bid::getTimestamp)).collect(Collectors.toList());
	}

	// --- CONSTRUCTORS, GETTERS, and SETTERS ---

	public Product() {
	}

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

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}

	public List<Comment> getComments() {
		return comments;
	}

	public void setComments(List<Comment> comments) {
		this.comments = comments;
	}

	public Set<Product> getSimilarProducts() {
		return similarProducts;
	}

	public void setSimilarProducts(Set<Product> similarProducts) {
		this.similarProducts = similarProducts;
	}

	public String getImagePath() {
		return imagePath;
	}

	public void setImagePath(String imagePath) {
		this.imagePath = imagePath;
	}

	// --- Getters and Setters for new fields ---

	public BigDecimal getStartingPrice() {
		return startingPrice;
	}

	public void setStartingPrice(BigDecimal startingPrice) {
		this.startingPrice = startingPrice;
	}

	public LocalDateTime getAuctionEndDate() {
		return auctionEndDate;
	}

	public void setAuctionEndDate(LocalDateTime auctionEndDate) {
		this.auctionEndDate = auctionEndDate;
	}

	public List<Bid> getBids() {
		return bids;
	}

	public void setBids(List<Bid> bids) {
		this.bids = bids;
	}

	public User getHighestBidder() {
		return highestBidder;
	}

	public void setHighestBidder(User highestBidder) {
		this.highestBidder = highestBidder;
	}

	public BigDecimal getCurrentBid() {
		return currentBid;
	}

	public void setCurrentBid(BigDecimal currentBid) {
		this.currentBid = currentBid;
	}

	public boolean isAuctionSettled() {
		return auctionSettled;
	}

	public void setAuctionSettled(boolean auctionSettled) {
		this.auctionSettled = auctionSettled;
	}

	// --- EQUALS and HASHCODE ---

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		Product product = (Product) o;
		return Objects.equals(id, product.id);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}
}
