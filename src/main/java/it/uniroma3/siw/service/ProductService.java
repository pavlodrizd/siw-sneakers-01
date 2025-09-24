package it.uniroma3.siw.service;

import it.uniroma3.siw.model.Product;
import it.uniroma3.siw.model.Type;
import it.uniroma3.siw.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ProductService {

	@Autowired
	private ProductRepository productRepository;

	@Autowired
	private TypeService typeService;

	private static final String UPLOAD_DIR = "src/main/resources/static/images/products/";

	public Product findById(Long id) {
		return productRepository.findById(id).orElse(null);
	}

	public List<Product> findAll() {
		return this.productRepository.findAllByOrderByIdAsc();
	}

	@Transactional
	public Product save(Product product, MultipartFile imageFile) throws IOException {
		if (!productRepository.existsByName(product.getName())) {
			if (imageFile != null && !imageFile.isEmpty()) {
				String uniqueFileName = saveImage(imageFile);
				product.setImagePath(uniqueFileName);
				product.setStartingPrice(product.getStartingPrice());
			}
			return productRepository.save(product);

		}
		return null;
	}

	private String saveImage(MultipartFile imageFile) throws IOException {
		String uniqueFileName = UUID.randomUUID().toString() + "_" + imageFile.getOriginalFilename();
		Path uploadPath = Paths.get(UPLOAD_DIR);
		if (!Files.exists(uploadPath)) {
			Files.createDirectories(uploadPath);
		}
		Path filePath = uploadPath.resolve(uniqueFileName);
		Files.copy(imageFile.getInputStream(), filePath);
		return uniqueFileName;
	}

	@Transactional
	public void deleteById(Long id) {
		Product product = this.findById(id);
		if (product != null) {
			Type typeToCheck = product.getType(); // 1. Salva la tipologia prima di eliminare
			productRepository.delete(product); // 2. Elimina il prodotto
			typeService.checkAndCleanOrphanedType(typeToCheck); // 3. Controlla e pulisci
		}
	}

	public List<Product> findByTypeId(Long typeId) {
		return productRepository.findByTypeId(typeId);
	}

	// NUOVO METODO DI RICERCA UNIFICATO
	public List<Product> search(String query, Long typeId) {
		if (query != null && !query.isEmpty() && typeId != null) {
			// Caso 1: Ricerca per nome E per tipologia
			List<Product> productsByName = productRepository.findByNameContainingIgnoreCase(query);
			return productsByName.stream().filter(p -> p.getType().getId().equals(typeId)).collect(Collectors.toList());
		} else if (query != null && !query.isEmpty()) {
			// Caso 2: Ricerca solo per nome
			return productRepository.findByNameContainingIgnoreCase(query);
		} else if (typeId != null) {
			// Caso 3: Ricerca solo per tipologia
			return productRepository.findByTypeId(typeId);
		} else {
			// Caso 4: Nessun parametro, restituisce tutto
			return (List<Product>) productRepository.findAll();
		}
	}

	@Transactional
	public Product update(Long id, Product updatedDetails, MultipartFile imageFile) throws IOException {
		Product originalProduct = this.findById(id);
		if (originalProduct != null) {
			// 1. Salva la VECCHIA tipologia prima di aggiornarla
			Type oldType = originalProduct.getType();

			// 2. Aggiorna tutti i campi del prodotto, inclusa la nuova tipologia
			originalProduct.setName(updatedDetails.getName());

			// --- CORREZIONE QUI ---
			// Aggiorna i campi dell'asta invece del prezzo fisso
			originalProduct.setStartingPrice(updatedDetails.getStartingPrice());
			originalProduct.setAuctionEndDate(updatedDetails.getAuctionEndDate());
			// --------------------

			originalProduct.setDescription(updatedDetails.getDescription());
			originalProduct.setType(updatedDetails.getType());

			if (imageFile != null && !imageFile.isEmpty()) {
				String uniqueFileName = saveImage(imageFile);
				originalProduct.setImagePath(uniqueFileName);
			}

			Product savedProduct = this.productRepository.save(originalProduct);

			// 3. Controlla se la vecchia tipologia è cambiata ed è ora orfana
			if (oldType != null && !oldType.equals(savedProduct.getType())) {
				typeService.checkAndCleanOrphanedType(oldType);
			}

			return savedProduct;
		}
		return null;
	}

}