package it.uniroma3.siw.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.uniroma3.siw.exception.InsufficientCreditException;
import it.uniroma3.siw.exception.InvalidBidException;
import it.uniroma3.siw.model.Bid;
import it.uniroma3.siw.model.Product;
import it.uniroma3.siw.model.User;
import it.uniroma3.siw.repository.BidRepository;
import it.uniroma3.siw.repository.ProductRepository;
import it.uniroma3.siw.repository.UserRepository;

@Service
public class BidService {

	@Autowired
	private ProductRepository productRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private BidRepository bidRepository;

	@Transactional
	public void placeBid(Long productId, BigDecimal bidAmount, User bidder)
			throws InsufficientCreditException, InvalidBidException {
		Product product = productRepository.findById(productId)
				.orElseThrow(() -> new InvalidBidException("Prodotto non trovato."));

		// --- VALIDAZIONE ---
		if (LocalDateTime.now().isAfter(product.getAuctionEndDate())) {
			throw new InvalidBidException("L'asta è già terminata.");
		}
		if (bidAmount.compareTo(product.getCurrentBid()) <= 0) {
			throw new InvalidBidException("La tua offerta deve essere superiore all'offerta attuale.");
		}

		// Logica di validazione del credito
		BigDecimal totalCredit = bidder.getAvailableCredit() != null ? bidder.getAvailableCredit() : BigDecimal.ZERO;
		List<Product> activeWinningBids = productRepository.findByHighestBidderAndAuctionEndDateAfter(bidder,
				LocalDateTime.now());

		BigDecimal lockedCredit = activeWinningBids.stream().filter(p -> !p.getId().equals(productId))
				.map(Product::getCurrentBid).reduce(BigDecimal.ZERO, BigDecimal::add);

		BigDecimal spendableCredit = totalCredit.subtract(lockedCredit);

		if (spendableCredit.compareTo(bidAmount) < 0) {
			throw new InsufficientCreditException("Credito insufficiente per piazzare questa offerta.");
		}

		// --- LOGICA DI SALVATAGGIO CORRETTA ---
		Bid newBid = new Bid();
		newBid.setAmount(bidAmount);
		newBid.setTimestamp(LocalDateTime.now());
		newBid.setBidder(bidder);
		newBid.setProduct(product);

		// Salva prima la nuova offerta
		bidRepository.save(newBid);

		// Aggiorna lo stato del prodotto
		product.getBids().add(newBid);
		product.setHighestBidder(bidder);

		// ✨ LA RIGA MANCANTE ✨
		// Aggiorna il campo persistente 'currentBid' con il nuovo importo dell'offerta
		product.setCurrentBid(bidAmount);

		// Salva esplicitamente il prodotto per persistere tutti gli aggiornamenti
		productRepository.save(product);
	}
}