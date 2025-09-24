package it.uniroma3.siw.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;

@Entity
public class Bid {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private BigDecimal amount;
	private LocalDateTime timestamp;

	@ManyToOne
	@JsonIgnore // <-- CORREZIONE: Spezza il ciclo User -> Bid -> User
	private User bidder;

	@ManyToOne
	@JsonIgnore // <-- CORREZIONE: Spezza il ciclo Product -> Bid -> Product
	private Product product;

	// Constructors, Getters, Setters...

	public Bid() {
	}

	public Bid(BigDecimal amount, LocalDateTime timestamp, User bidder, Product product) {
		this.amount = amount;
		this.timestamp = timestamp;
		this.bidder = bidder;
		this.product = product;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public LocalDateTime getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(LocalDateTime timestamp) {
		this.timestamp = timestamp;
	}

	public User getBidder() {
		return bidder;
	}

	public void setBidder(User bidder) {
		this.bidder = bidder;
	}

	public Product getProduct() {
		return product;
	}

	public void setProduct(Product product) {
		this.product = product;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		Bid bid = (Bid) o;
		return Objects.equals(id, bid.id);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}
}
