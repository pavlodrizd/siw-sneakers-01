package it.uniroma3.siw.controller;

import it.uniroma3.siw.model.Credentials;
import it.uniroma3.siw.model.Product;
import it.uniroma3.siw.model.User;
import it.uniroma3.siw.repository.ProductRepository;
import it.uniroma3.siw.service.CommentService;
import it.uniroma3.siw.service.CredentialsService;
import it.uniroma3.siw.service.ProductService;
import it.uniroma3.siw.service.UserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class UserController {

	@Autowired
	private UserService userService;
	@Autowired
	private CredentialsService credentialsService;
	@Autowired
	private CommentService commentService;

	/**
	 * Mostra la pagina del profilo personale dell'utente loggato. Gestisce sia la
	 * vista per l'utente normale che per l'admin.
	 */
	@Autowired
	private ProductRepository productRepository;

	@Autowired
	private ProductService productService;

	@Transactional // <-- AGGIUNGI QUESTA ANNOTAZIONE FONDAMENTALE
	@GetMapping("/profile")
	public String showProfile(Model model) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		User user = userService.getLoggedUser(authentication);

		if (user == null) {
			return "redirect:/login";
		}

		// ===================================================================
		// --- NUOVA LOGICA: DETRAZIONE CREDITO PER ASTE VINTE ---
		// ===================================================================
		// 1. Trova tutte le aste vinte dall'utente
		List<Product> wonAuctions = productRepository.findByHighestBidderAndAuctionEndDateBefore(user,
				LocalDateTime.now());

		// 2. Per ogni asta vinta, se non è già stata "saldata", scala il credito
		for (Product wonProduct : wonAuctions) {
			if (!wonProduct.isAuctionSettled()) {
				// Sottrai il costo dal credito disponibile dell'utente
				BigDecimal currentCredit = user.getAvailableCredit();
				BigDecimal cost = wonProduct.getCurrentBid();
				user.setAvailableCredit(currentCredit.subtract(cost));

				// Marca l'asta come "saldata" per non scalarla di nuovo
				wonProduct.setAuctionSettled(true);

				// Salva le modifiche nel database
				userService.saveUser(user);
				productRepository.save(wonProduct); // Assicurati di avere un metodo save in ProductService
			}
		}
		// ===================================================================
		// --- FINE NUOVA LOGICA ---
		// ===================================================================

		// Ora, i calcoli successivi useranno il credito GIÀ AGGIORNATO
		List<Product> winningAuctions = productRepository.findByHighestBidderAndAuctionEndDateAfter(user,
				LocalDateTime.now());

		BigDecimal lockedCredit = winningAuctions.stream().map(Product::getCurrentBid).reduce(BigDecimal.ZERO,
				BigDecimal::add);

		BigDecimal totalCredit = user.getAvailableCredit() != null ? user.getAvailableCredit() : BigDecimal.ZERO;
		BigDecimal spendableCredit = totalCredit.subtract(lockedCredit);

		// Aggiungi tutti i dati al modello (incluso 'wonAuctions' che ora è definito
		// prima)
		model.addAttribute("user", user);
		model.addAttribute("winningAuctions", winningAuctions);
		model.addAttribute("wonAuctions", wonAuctions);
		model.addAttribute("lockedCredit", lockedCredit);
		model.addAttribute("spendableCredit", spendableCredit);

		boolean isAdmin = authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority)
				.anyMatch(role -> role.equals("ROLE_ADMIN"));

		if (isAdmin) {
			model.addAttribute("allComments", this.commentService.findAll());

			List<Product> allActiveAuctions = productRepository
					.findByAuctionEndDateAfterOrderByCurrentBidDesc(LocalDateTime.now());
			model.addAttribute("allActiveAuctions", allActiveAuctions);

			List<Product> allTerminatedAuctions = productRepository.findByAuctionEndDateBefore(LocalDateTime.now());
			model.addAttribute("allTerminatedAuctions", allTerminatedAuctions);
		}

		return "profile";
	}

	/**
	 * Mostra il form per modificare il profilo dell'utente loggato.
	 */
	@GetMapping("/profile/edit")
	public String showEditProfileForm(Authentication auth, Model model) {
		User user = userService.getLoggedUser(auth);
		if (user == null) {
			return "redirect:/login";
		}

		model.addAttribute("user", user);
		model.addAttribute("credentials", user.getCredentials());
		return "formEditProfile";
	}

	/**
	 * Processa i dati inviati dal form di modifica del profilo. Aggiorna nome,
	 * cognome ed email.
	 */
	@PostMapping("/profile/edit")
	public String updateProfile(@RequestParam("name") String name, @RequestParam("surname") String surname,
			@RequestParam("email") String email, Authentication auth, RedirectAttributes redirectAttributes) {

		User currentUser = userService.getLoggedUser(auth);
		boolean success = userService.updateUserProfile(currentUser, name, surname, email);

		if (success) {
			redirectAttributes.addFlashAttribute("success_message", "Profilo aggiornato con successo!");
		} else {
			redirectAttributes.addFlashAttribute("error_message",
					"L'email inserita è già utilizzata da un altro account.");
		}

		return "redirect:/profile";
	}
}
