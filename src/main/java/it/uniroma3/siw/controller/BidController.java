package it.uniroma3.siw.controller;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import it.uniroma3.siw.model.User;
import it.uniroma3.siw.service.BidService;
import it.uniroma3.siw.service.UserService;

@Controller
public class BidController {

	@Autowired
	private BidService bidService;

	@Autowired
	private UserService userService; // Assuming you have a service to get users

	@PostMapping("/products/{id}/bid")
	public String handlePlaceBid(@PathVariable("id") Long productId, @RequestParam("amount") BigDecimal amount,
			@AuthenticationPrincipal UserDetails userDetails, RedirectAttributes redirectAttributes) {

		if (userDetails == null) {
			// User is not authenticated, redirect to login
			return "redirect:/login";
		}

		String userEmail = userDetails.getUsername();

		// Get the full User object from the database
		User currentUser = userService.getUserByEmail(userEmail);

		try {
			bidService.placeBid(productId, amount, currentUser);
			redirectAttributes.addFlashAttribute("bid_success", "La tua offerta è stata piazzata correttamente.");
		} catch (Exception e) {
			redirectAttributes.addFlashAttribute("bid_error", e.getMessage());
		}

		return "redirect:/product/" + productId;
	}
}
