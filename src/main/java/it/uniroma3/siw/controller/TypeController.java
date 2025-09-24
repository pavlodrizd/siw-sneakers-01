package it.uniroma3.siw.controller;

import it.uniroma3.siw.model.Product;
import it.uniroma3.siw.model.Type;
import it.uniroma3.siw.service.ProductService;
import it.uniroma3.siw.service.TypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.Optional;

@Controller
public class TypeController {

	@Autowired
	private TypeService typeService;

	@Autowired // <-- CORREZIONE 1: Aggiunto @Autowired
	private ProductService productService;

	// CORREZIONE 2: Rimosso ProductRepository perché non serve
	// @Autowired
	// private ProductRepository productRepository;

	@GetMapping("/types")
	public String showTypes(Model model) {
		model.addAttribute("types", typeService.findAll());
		return "types";
	}

	@GetMapping("/type/{id}")
	public String showType(@PathVariable("id") Long id, Model model) {
		Optional<Type> typeOpt = typeService.findById(id);
		if (typeOpt.isPresent()) {
			Type type = typeOpt.get();
			// Ora questa chiamata funzionerà correttamente
			List<Product> products = productService.findByTypeId(id);

			model.addAttribute("type", type);
			model.addAttribute("products", products);
			return "type";
		} else {
			return "redirect:/types";
		}
	}
}