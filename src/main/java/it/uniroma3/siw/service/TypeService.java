package it.uniroma3.siw.service;

import it.uniroma3.siw.model.Type; // Modificato
import it.uniroma3.siw.repository.ProductRepository;
import it.uniroma3.siw.repository.TypeRepository; // Modificato
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // Utile per i metodi di scrittura

import java.util.List;
import java.util.Optional;

@Service
public class TypeService {

	@Autowired
	private TypeRepository typeRepository; // Modificato

	@Autowired
	private ProductRepository productRepository;

	@Transactional // Buona pratica per i metodi che salvano dati
	public Type save(Type type) {
		return typeRepository.save(type);
	}

	public Optional<Type> findById(Long id) {
		return typeRepository.findById(id);
	}

	public List<Type> findAll() {
		return (List<Type>) typeRepository.findAll();
	}

	public boolean existsByName(String name) {
		return typeRepository.existsByName(name);
	}

	@Transactional
	public Type findOrCreateByName(String name) {
		// Chiama findByName, che restituisce un Optional<Type>, su cui puoi usare
		// orElseGet
		return typeRepository.findByName(name).orElseGet(() -> {
			// Se l'Optional è vuoto (la tipologia non esiste), esegui questo codice
			Type newType = new Type(name);
			return typeRepository.save(newType);
		});
	}

	@Transactional
	public void checkAndCleanOrphanedType(Type type) {
		if (type == null) {
			return; // Non fare nulla se la tipologia è nulla
		}

		// Usa il metodo che abbiamo già creato per contare i prodotti per tipo
		if (productRepository.countByTypeId(type.getId()) == 0) {
			typeRepository.delete(type);
		}
	}
}