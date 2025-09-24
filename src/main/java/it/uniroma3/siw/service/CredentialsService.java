package it.uniroma3.siw.service;

import it.uniroma3.siw.model.Credentials;
import it.uniroma3.siw.repository.CredentialsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class CredentialsService {

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private CredentialsRepository credentialsRepository;

	/**
	 * Salva le credenziali nel database, codificando la password prima di salvare.
	 * Questo è il metodo principale per creare o aggiornare le credenziali.
	 */
	@Transactional
	public Credentials saveCredentials(Credentials credentials) {
		// Se la password non è già codificata, la codifica.
		// Utile per non ri-codificare una password già codificata durante un
		// aggiornamento.
		if (credentials.getPassword() != null && !credentials.getPassword().startsWith("{bcrypt}")) {
			credentials.setPassword(this.passwordEncoder.encode(credentials.getPassword()));
		}
		return this.credentialsRepository.save(credentials);
	}

	/**
	 * Trova le credenziali tramite il loro ID. Restituisce un Optional per gestire
	 * in modo sicuro il caso in cui non vengano trovate.
	 */
	public Optional<Credentials> getCredentials(Long id) {
		return this.credentialsRepository.findById(id);
	}

	/**
	 * Trova le credenziali tramite l'email (che funge da username). Restituisce un
	 * Optional per una gestione più sicura.
	 */
	public Optional<Credentials> getCredentials(String email) {
		return this.credentialsRepository.findByEmail(email);
	}

	/**
	 * Verifica se esistono credenziali associate a una data email. Fondamentale per
	 * la logica di registrazione per evitare duplicati.
	 */
	public boolean existsByEmail(String email) {
		return credentialsRepository.existsByEmail(email);
	}

	/**
	 * Cancella le credenziali tramite ID.
	 */
	@Transactional
	public void deleteCredentials(Long credentialsId) {
		credentialsRepository.deleteById(credentialsId);
	}

	/**
	 * Metodo di utilità per verificare se l'utente loggato è un ADMIN.
	 */
	public boolean isAdminLogged(Authentication auth) {
		if (auth == null || !auth.isAuthenticated()) {
			return false;
		}
		return auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_" + Credentials.ADMIN_ROLE));
	}
}