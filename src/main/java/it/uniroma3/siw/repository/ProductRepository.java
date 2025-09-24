package it.uniroma3.siw.repository;

import it.uniroma3.siw.model.Product;
import it.uniroma3.siw.model.User;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface ProductRepository extends CrudRepository<Product, Long> {
	
    public List<Product> findAllByOrderByIdAsc(); // Trova tutto ordinato per ID ascendente

    /**
     * Restituisce una lista di prodotti che hanno esattamente quel nome.
     * @param name il nome da cercare
     * @return una lista di prodotti con il nome specificato
     */
	public List<Product> findByNameContainingIgnoreCase(String name);

    /**
     * Restituisce una lista di prodotti che appartengono a una specifica tipologia.
     * Cerca i prodotti tramite l'ID della tipologia associata.
     * @param typeId l'ID della tipologia
     * @return una lista di prodotti appartenenti alla tipologia specificata
     */
    public List<Product> findByTypeId(Long typeId);
    
    /**
     * Verifica se esiste già un prodotto con un determinato nome.
     * Utile per evitare duplicati prima di un salvataggio.
     * @param name il nome da controllare
     * @return true se un prodotto con quel nome esiste già, altrimenti false
     */
    public boolean existsByName(String name);
    
    public Long countByTypeId(Long typeId);
    
    List<Product> findByHighestBidderAndAuctionEndDateAfter(User highestBidder, LocalDateTime now);
    
    List<Product> findByHighestBidderAndAuctionEndDateBefore(User bidder, LocalDateTime now);
    
    List<Product> findByAuctionEndDateAfter(LocalDateTime now);
    
    List<Product> findByAuctionEndDateBefore(LocalDateTime now);
    
    List<Product> findByAuctionEndDateAfterOrderByCurrentBidDesc(LocalDateTime dateTime);

}