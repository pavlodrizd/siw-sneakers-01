package it.uniroma3.siw.repository;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import it.uniroma3.siw.model.Type;

// L'annotazione @Repository è facoltativa qui perché estendi già un'interfaccia di Spring Data
public interface TypeRepository extends CrudRepository<Type, Long> {

    /**
     * Cerca un tipo per nome, ignorando maiuscole/minuscole.
     */
    public Type findByNameIgnoreCase(String name);

    /**
     * Verifica se esiste un tipo con un determinato nome.
     * Questo è il metodo che mancava.
     */
    public boolean existsByName(String name);
    
    public Optional<Type> findByName(String name);
}