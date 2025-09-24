package it.uniroma3.siw.controller;

import it.uniroma3.siw.model.Comment; // Modificato
import it.uniroma3.siw.model.Product; // Modificato
import it.uniroma3.siw.model.User;
import it.uniroma3.siw.service.CommentService; // Modificato
import it.uniroma3.siw.service.ProductService; // Modificato
import it.uniroma3.siw.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
//Assicurati di avere questi import
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;

@Controller
public class CommentController {

	@Autowired
	private CommentService commentService; // Modificato

	@Autowired
	private ProductService productService; // Modificato

	@Autowired
	private UserService userService;

	/**
	 * Mostra il form per creare un nuovo commento.
	 */
	@PreAuthorize("isAuthenticated()")
	@GetMapping("/products/{productId}/comments/new") // URL aggiornato
	public String showNewCommentForm(@PathVariable Long productId, Model model) {
		Product product = productService.findById(productId); // Logica aggiornata
		model.addAttribute("product", product);
		model.addAttribute("comment", new Comment());

		return "formNewComment"; // Vista aggiornata
	}

	/**
	 * Gestisce il submit del form di nuovo commento.
	 */
	@PreAuthorize("isAuthenticated()")
	@PostMapping("/products/{productId}/comments")
	public String addComment(@PathVariable Long productId, @Valid @ModelAttribute("comment") Comment comment,
			BindingResult bindingResult, Authentication auth, Model model) {
		Product product = productService.findById(productId);
		User user = userService.getLoggedUser(auth);

		if (bindingResult.hasErrors()) {
			model.addAttribute("product", product);
			return "formNewComment";
		}

		comment.setProduct(product);
		comment.setAuthor(user);

		// AGGIUNGI QUESTA RIGA FONDAMENTALE
		product.getComments().add(comment);

		// Il salvataggio del commento ora è sufficiente
		commentService.save(comment);

		// In alternativa, potresti salvare il prodotto, che per effetto a cascata
		// salverebbe anche il nuovo commento (se la relazione è configurata con
		// CascadeType.ALL o PERSIST)
		// productService.save(product, null); // L'immagine è null perché non la stiamo
		// aggiornando

		return "redirect:/product/" + productId;
	}

	/**
	 * Mostra il form per modificare un commento esistente. La sicurezza controlla
	 * che tu sia l'autore del commento o un admin.
	 */
	@GetMapping("/comment/{id}/edit")
	@PreAuthorize("isAuthenticated() and (@commentService.findById(#id).get().author.credentials.email == authentication.principal.username or hasRole('ADMIN'))")
	public String showEditCommentForm(@PathVariable("id") Long id, Model model,
			@RequestHeader("Referer") String referer) {
		Comment comment = commentService.findById(id).orElse(null);
		if (comment != null) {
			model.addAttribute("comment", comment);
			model.addAttribute("refererUrl", referer);
			return "formEditComment"; // Nuovo file HTML da creare
		}
		return "redirect:/products";
	}

	/**
	 * Processa i dati del form di modifica.
	 */
	@PostMapping("/comment/{id}/edit")
	@PreAuthorize("isAuthenticated() and (@commentService.findById(#id).get().author.credentials.email == authentication.principal.username or hasRole('ADMIN'))")
	public String updateComment(@PathVariable("id") Long id, @Valid @ModelAttribute("comment") Comment updatedComment,
			BindingResult bindingResult, @RequestParam("refererUrl") String refererUrl) {
		if (bindingResult.hasErrors()) {
			return "formEditComment";
		}
		Comment comment = commentService.update(id, updatedComment);
		return "redirect:" + refererUrl;
	}

	/**
	 * Cancella un commento in modo sicuro. Sostituisce i tuoi due vecchi metodi di
	 * cancellazione.
	 */
	@PostMapping("/comment/{id}/delete")
	@PreAuthorize("isAuthenticated() and (@commentService.findById(#id).get().author.credentials.email == authentication.principal.username or hasRole('ADMIN'))")
	public String deleteSecureComment(@PathVariable("id") Long id, @RequestHeader("Referer") String referer) {
		// 1. Trova il commento da eliminare
		Comment comment = commentService.findById(id).orElse(null);

		if (comment != null) {
			// 2. Ottieni il prodotto associato
			Product product = comment.getProduct();

			// 3. (CRUCIALE) Rimuovi il commento dalla lista del prodotto
			product.getComments().remove(comment);

			// 4. Ora elimina il commento dal database
			commentService.deleteById(id);

			// 5. Reindirizza alla pagina del prodotto
			return "redirect:" + referer;
		}

		// Se il commento non esiste, reindirizza a una pagina di default
		return "redirect:/products";
	}

	/**
	 * Cancellazione da dashboard admin.
	 */
	@PostMapping("/admin/comments/{id}/delete")
	@PreAuthorize("hasRole('ADMIN')")
	public String deleteCommentAdmin(@PathVariable Long id) {
		// 1. Trova il commento da eliminare tramite il suo ID
		Comment comment = commentService.findById(id).orElse(null);

		// 2. Controlla se il commento esiste
		if (comment != null) {
			// 3. Ottieni il prodotto a cui il commento appartiene
			Product product = comment.getProduct();

			// 4. (PASSAGGIO CHIAVE) Rimuovi il commento dalla collezione del prodotto
			// per garantire la coerenza della relazione.
			product.getComments().remove(comment);

			// 5. Ora elimina definitivamente il commento dal database
			commentService.deleteById(id);
		}

		// 6. Reindirizza alla dashboard dell'admin come da richiesta originale
		return "redirect:/admin/dashboard";
	}

}