package it.uniroma3.siw.controller;

import it.uniroma3.siw.service.TypeService; // Assicurati di importare TypeService
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

	@Autowired
	private TypeService typeService; // Inietta il TypeService

	@GetMapping("/")
	public String index(Model model) {
		model.addAttribute("types", typeService.findAll()); // Aggiunge tutte le tipologie al modello
		return "index";
	}
}