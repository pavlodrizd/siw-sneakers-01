package it.uniroma3.siw.service;

import it.uniroma3.siw.model.Credentials;
import it.uniroma3.siw.model.Product;
import it.uniroma3.siw.model.User;
import it.uniroma3.siw.repository.CredentialsRepository;
import it.uniroma3.siw.repository.ProductRepository;
import it.uniroma3.siw.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private CredentialsService credentialsService;

	@Autowired
	private CredentialsRepository credentialsRepository;

	@Autowired
	ProductRepository productRepository;

	/**
	 * Salva un utente nel database.
	 */
	@Transactional
	public User saveUser(User user) {
		return userRepository.save(user);
	}

	/**
	 * Trova un utente tramite il suo ID. Restituisce l'utente o null se non
	 * trovato.
	 */
	public User findById(Long id) {
		return userRepository.findById(id).orElse(null);
	}

	/**
	 * Restituisce la lista di tutti gli utenti registrati.
	 */
	public List<User> findAll() {
		return (List<User>) userRepository.findAll();
	}

	/**
	 * Recupera l'utente attualmente autenticato. Restituisce l'utente o null se non
	 * è autenticato.
	 */
	public User getLoggedUser(Authentication auth) {
		if (auth == null || !auth.isAuthenticated()) {
			return null;
		}
		String username = auth.getName();
		Optional<Credentials> credentials = credentialsService.getCredentials(username);
		return credentials.map(Credentials::getUser).orElse(null);
	}

	/**
	 * ====================================================== NUOVO METODO AGGIUNTO
	 * PER AGGIORNARE IL PROFILO UTENTE
	 * ======================================================
	 * 
	 * @return true se l'aggiornamento ha successo, false se l'email è già in uso.
	 */
	@Transactional
	public boolean updateUserProfile(User user, String newName, String newSurname, String newEmail) {
		// Aggiorna i dati anagrafici dell'utente
		user.setName(newName);
		user.setSurname(newSurname);
		this.saveUser(user);

		Credentials credentials = user.getCredentials();
		String oldEmail = credentials.getEmail();

		// Controlla se l'email è stata modificata
		if (!oldEmail.equals(newEmail)) {
			// Se è diversa, verifica che la nuova email non sia già utilizzata
			if (credentialsService.existsByEmail(newEmail)) {
				return false; // Aggiornamento fallito: email duplicata
			}
			// Se è libera, aggiornala
			credentials.setEmail(newEmail);
			credentialsService.saveCredentials(credentials);
		}

		return true; // Aggiornamento riuscito
	}

	public User getUserByEmail(String email) {
		// 1. Find the credentials using the email
		Optional<Credentials> credentials = credentialsRepository.findByEmail(email);

		// 2. If credentials are found, return the associated user
		if (credentials.isPresent()) {
			return credentials.get().getUser();
		}

		// 3. Otherwise, no user was found with that email
		return null;
	}

	/**
	 * Cancella un utente tramite il suo ID.
	 */
	@Transactional
	public void deleteUser(Long userId) {
		userRepository.deleteById(userId);
	}

	public BigDecimal getSpendableCreditForCurrentUser() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication == null || !authentication.isAuthenticated()
				|| authentication instanceof AnonymousAuthenticationToken) {
			return null; // Nessun utente loggato
		}

		String email = authentication.getName();
		User user = this.getUserByEmail(email);

		if (user == null || user.getAvailableCredit() == null) {
			return BigDecimal.ZERO;
		}

		BigDecimal totalCredit = user.getAvailableCredit();

		// Trova tutte le aste attive che l'utente sta vincendo
		List<Product> activeWinningAuctions = productRepository.findByHighestBidderAndAuctionEndDateAfter(user,
				LocalDateTime.now());

		// Calcola il credito "bloccato" in queste aste
		BigDecimal lockedCredit = activeWinningAuctions.stream().map(Product::getCurrentBid).reduce(BigDecimal.ZERO,
				BigDecimal::add);

		// Restituisce il credito totale meno quello bloccato
		return totalCredit.subtract(lockedCredit);
	}

	@Transactional
	public void updateUserCredit(Long userId, BigDecimal newCredit) {
		User user = this.findById(userId);
		if (user != null) {
			user.setAvailableCredit(newCredit);
			userRepository.save(user);
		}
	}
}
