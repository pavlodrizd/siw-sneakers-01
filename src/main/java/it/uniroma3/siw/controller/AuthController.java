package it.uniroma3.siw.controller;

import it.uniroma3.siw.controller.validator.CredentialsValidator;
import it.uniroma3.siw.model.Credentials;
import it.uniroma3.siw.model.User;
import it.uniroma3.siw.service.CredentialsService;
import it.uniroma3.siw.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class AuthController {

	@Autowired
	private CredentialsService credentialsService;
	@Autowired
	private UserService userService;
	@Autowired
	private CredentialsValidator credentialsValidator;
	@Autowired
	private PasswordEncoder passwordEncoder;

	/**
	 * Mostra il form di login.
	 */
	@GetMapping("/login")
	public String showLoginForm() {
		return "login";
	}

	/**
	 * Mostra il form di registrazione per un nuovo utente.
	 */
	@GetMapping("/register")
	public String showRegisterForm(Model model) {
		model.addAttribute("user", new User());
		model.addAttribute("credentials", new Credentials());
		return "formRegister";
	}

	/**
	 * Processa i dati del form di registrazione.
	 */
	@PostMapping("/register")
	public String registerUser(@Valid @ModelAttribute("user") User user, BindingResult userBindingResult,
			@Valid @ModelAttribute("credentials") Credentials credentials, BindingResult credentialsBindingResult,
			Model model) {

		// Applica la validazione custom per le credenziali (es. username duplicato)
		this.credentialsValidator.validate(credentials, credentialsBindingResult);

		// Se ci sono errori, torna al form di registrazione
		if (userBindingResult.hasErrors() || credentialsBindingResult.hasErrors()) {
			model.addAttribute("user", user);
			model.addAttribute("credentials", credentials);
			return "formRegister";
		}

		// Imposta il ruolo di default e salva il nuovo utente
		credentials.setRole(Credentials.DEFAULT_ROLE);
		credentials.setPassword(passwordEncoder.encode(credentials.getPassword()));
		credentials.setUser(user);
		user.setCredentials(credentials);

		userService.saveUser(user);

		return "redirect:/login"; // Reindirizza alla pagina di login dopo la registrazione
	}
}