package it.uniroma3.siw.controller;

import it.uniroma3.siw.model.User;
import it.uniroma3.siw.service.CommentService;
import it.uniroma3.siw.service.UserService;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@PreAuthorize("hasRole('ADMIN')") // Protegge tutti i metodi di questo controller
public class AdminController {

	@Autowired
	private UserService userService;
	@Autowired
	private CommentService commentService;

	@GetMapping("/admin/dashboard")
	public String showAdminDashboard() {
		return "admin/dashboard"; // Richiede un file dashboard.html in templates/admin/
	}

	// MOSTRA LA PAGINA DI GESTIONE UTENTI
	@PreAuthorize("hasRole('ADMIN')")
	@GetMapping("/admin/manageUsers")
	public String manageUsers(Model model) {
		model.addAttribute("users", userService.findAll());
		return "admin/manageUsers";
	}

	// MOSTRA IL FORM PER MODIFICARE IL CREDITO
	@PreAuthorize("hasRole('ADMIN')")
	@GetMapping("/admin/editWallet/{id}")
	public String showEditWalletForm(@PathVariable("id") Long id, Model model) {
		User user = userService.findById(id);
		if (user != null) {
			model.addAttribute("user", user);
			return "admin/formEditWallet";
		}
		return "redirect:/admin/manageUsers";
	}

	// PROCESSA L'AGGIORNAMENTO DEL CREDITO
	@PreAuthorize("hasRole('ADMIN')")
	@PostMapping("/admin/editWallet/{id}")
	public String updateWallet(@PathVariable("id") Long id, @RequestParam("credit") BigDecimal credit) {
		userService.updateUserCredit(id, credit);
		return "redirect:/admin/manageUsers";
	}
}