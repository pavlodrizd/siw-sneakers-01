package it.uniroma3.siw.usersDataLoader;

import it.uniroma3.siw.model.*;
import it.uniroma3.siw.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Component
public class SampleDataLoader implements CommandLineRunner {

	@Autowired
	private CredentialsRepository credentialsRepository;
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private ProductRepository productRepository;
	@Autowired
	private TypeRepository typeRepository;
	@Autowired
	private CommentRepository commentRepository;
	@Autowired
	private BidRepository bidRepository;
	@Autowired
	private PasswordEncoder passwordEncoder;

	@Override
	@Transactional
	public void run(String... args) throws Exception {
		// Pulisce i dati esistenti nell'ordine corretto per evitare violazioni di
		// vincoli
		commentRepository.deleteAll();
		bidRepository.deleteAll();
		productRepository.deleteAll();
		typeRepository.deleteAll();
		credentialsRepository.deleteAll();
		userRepository.deleteAll();

		// 1. CREA UTENTI con credito aumentato
		User adminUser = createUser("Admin", "Site", "admin@siw.it", "pass", Credentials.ADMIN_ROLE,
				new BigDecimal("9999999.00"));
		User marioRossi = createUser("Mario", "Rossi", "user@siw.it", "pass", Credentials.DEFAULT_ROLE,
				new BigDecimal("250000.00"));
		User lauraBianchi = createUser("Laura", "Bianchi", "user1@siw.it", "pass", Credentials.DEFAULT_ROLE,
				new BigDecimal("300000.00"));
		User lucaVerdi = createUser("Luca", "Verdi", "user2@siw.it", "pass", Credentials.DEFAULT_ROLE,
				new BigDecimal("180000.00"));

		// 2. CREA TIPI DI SCARPA
		Type highTop = createType("High");
		Type midTop = createType("Mid");
		Type lowTop = createType("Low");

		// 3. CREA PRODOTTI (scarpe) con dettagli dell'asta
		Product jordan1 = createProduct("Nike Air Jordan 1 High 'Chicago'",
				"L'iconica sneaker che ha dato inizio a tutto. Un classico senza tempo nella famosa colorazione Chicago.",
				new BigDecimal("250.00"), LocalDateTime.now().plusDays(3), highTop, "airjordan1chicago.jpeg");

		Product macklemoreJ6 = createProduct("Macklemore x Air Jordan 6 “Cactus”",
				"Una delle Jordan 6 più rare mai create, realizzata in esclusiva per il rapper Macklemore e i suoi amici.",
				new BigDecimal("14500.00"), LocalDateTime.now().plusDays(10), midTop,
				"Macklemore x Air Jordan 6 “Cactus”.png");

		Product djKhaledJ3 = createProduct("DJ Khaled x Air Jordan 3 “Grateful”",
				"Celebre per il suo colore rosso acceso e il logo 'We The Best', questa scarpa è un pezzo da collezione per pochi.",
				new BigDecimal("12750.00"), LocalDateTime.now().plusDays(8), midTop,
				"DJ Khaled x Air Jordan 3 “Grateful”.png");

		Product pigeonDunk = createProduct("Nike Dunk SB Low “Pigeon”",
				"La sneaker che ha causato code e rivolte a NYC. Un pezzo di storia che ha cambiato per sempre la cultura delle sneaker.",
				new BigDecimal("18230.00"), LocalDateTime.now().plusDays(12), lowTop, "Nike Dunk SB Low “Pigeon”.png");

		Product travisPlaystation = createProduct("Travis Scott x PlayStation x Nike Dunk Low",
				"Una collaborazione ultra-limitata, distribuita solo a pochissimi fortunati in occasione del lancio della PS5.",
				new BigDecimal("13500.00"), LocalDateTime.now().plusSeconds(5), lowTop,
				"Travis Scott x PlayStation x Nike Dunk Low.png");

		Product eminemCarharttJ4 = createProduct("Eminem x Carhartt x Air Jordan 4",
				"Creata per una raccolta fondi, questa sneaker unisce tre icone americane. Un vero e proprio 'graal'.",
				new BigDecimal("23200.00"), LocalDateTime.now().plusDays(5), midTop,
				"Eminem x Carhartt x Air Jordan 4.png");

		Product moonShoe = createProduct("Nike 1972 Waffle Racing Flat \"Moon Shoe\"",
				"Un pezzo di storia Nike, una delle prime scarpe da corsa fatte a mano da Bill Bowerman. Ne esistono pochissimi esemplari.",
				new BigDecimal("122000.00"), LocalDateTime.now().plusDays(20), lowTop,
				"nike-moon-shoe-sothebys-0006-lat-l.jpg");

		// Salva i prodotti prima di creare le offerte
		productRepository.saveAll(
				List.of(jordan1, macklemoreJ6, djKhaledJ3, pigeonDunk, travisPlaystation, eminemCarharttJ4, moonShoe));

		// 4. CREA OFFERTE per simulare un'asta attiva
		createBid(jordan1, lauraBianchi, new BigDecimal("350.00"), LocalDateTime.now().minusHours(10));
		createBid(jordan1, marioRossi, new BigDecimal("500.00"), LocalDateTime.now().minusHours(5));
		createBid(jordan1, lauraBianchi, new BigDecimal("1100.00"), LocalDateTime.now().minusHours(3));
		createBid(jordan1, lucaVerdi, new BigDecimal("2200.00"), LocalDateTime.now().minusHours(2));
		createBid(jordan1, marioRossi, new BigDecimal("3400.00"), LocalDateTime.now().minusHours(1));

		createBid(eminemCarharttJ4, lucaVerdi, new BigDecimal("23500.00"), LocalDateTime.now().minusDays(1));
		createBid(eminemCarharttJ4, marioRossi, new BigDecimal("24100.00"), LocalDateTime.now().minusHours(2));

		createBid(moonShoe, lauraBianchi, new BigDecimal("125000.00"), LocalDateTime.now().minusHours(5));

		createBid(pigeonDunk, lucaVerdi, new BigDecimal("18500.00"), LocalDateTime.now().minusMinutes(30));

		createBid(travisPlaystation, marioRossi, new BigDecimal("13700.00"), LocalDateTime.now());

		// Salva di nuovo i prodotti per persistere il cambio del miglior offerente
		// (highestBidder)
		productRepository.saveAll(List.of(jordan1, eminemCarharttJ4, moonShoe, pigeonDunk));

		// 5. CREA COMMENTI
		createComment("Scarpa Leggendaria!", "Un vero capolavoro, non può mancare in una collezione seria.", marioRossi,
				jordan1);

		createComment("Incredibile!", "Non pensavo di vederla mai in vendita. Pezzo da museo.", lucaVerdi,
				eminemCarharttJ4);
		createComment("Un sogno", "Spero di riuscire a prenderla, è il mio graal da sempre.", marioRossi,
				eminemCarharttJ4);

		createComment("Un pezzo di storia", "Vale ogni singolo centesimo. Una scarpa che ha definito un'epoca.",
				lauraBianchi, moonShoe);

		createComment("La Dunk per eccellenza", "La scarpa che ha cambiato il gioco per tutti. Assurda.", marioRossi,
				pigeonDunk);

		createComment("Pazzesca", "Questa è la definizione di 'esclusiva'. In bocca al lupo al vincitore!", lucaVerdi,
				travisPlaystation);

		// Rendi persistenti i commenti attraverso i prodotti
		productRepository.saveAll(List.of(jordan1, eminemCarharttJ4, moonShoe, pigeonDunk, travisPlaystation));

		System.out.println("✅ Dati di esempio per l'asta caricati con successo.");
	}

	// --- METODI HELPER ---

	private User createUser(String name, String surname, String email, String password, String role,
			BigDecimal credit) {
		User user = new User();
		user.setName(name);
		user.setSurname(surname);
		user.setAvailableCredit(credit);
		Credentials credentials = new Credentials();
		credentials.setEmail(email);
		credentials.setPassword(passwordEncoder.encode(password));
		credentials.setRole(role);
		credentials.setUser(user);
		credentialsRepository.save(credentials);
		return user;
	}

	private Type createType(String name) {
		Type type = new Type();
		type.setName(name);
		return typeRepository.save(type);
	}

	private Product createProduct(String name, String description, BigDecimal startingPrice, LocalDateTime endDate,
			Type type, String imagePath) {
		Product product = new Product();
		product.setName(name);
		product.setDescription(description);
		product.setStartingPrice(startingPrice);
		product.setAuctionEndDate(endDate);
		product.setType(type);
		product.setImagePath(imagePath);
		// ✨ LA RIGA AGGIUNTA ✨
		// Imposta il campo persistente 'currentBid' al prezzo di partenza
		product.setCurrentBid(startingPrice);
		return product;
	}

	private void createComment(String title, String text, User author, Product product) {
		Comment comment = new Comment();
		comment.setTitle(title);
		comment.setAuthor(author);
		comment.setProduct(product);
		product.getComments().add(comment);
	}

	private void createBid(Product product, User bidder, BigDecimal amount, LocalDateTime timestamp) {
		Bid bid = new Bid();
		bid.setProduct(product);
		bid.setBidder(bidder);
		bid.setAmount(amount);
		bid.setTimestamp(timestamp);
		bidRepository.save(bid);

		// Aggiorna la lista di offerte del prodotto
		product.getBids().add(bid);

		// ✨ LA RIGA AGGIUNTA/CORRETTA ✨
		// Aggiorna il campo persistente 'currentBid' e il 'highestBidder'
		// Dato che il tuo codice di `BidService` valida già l'offerta, qui possiamo
		// semplicemente
		// impostarla, ma un controllo di sicurezza in più non guasta.
		if (amount.compareTo(product.getCurrentBid()) > 0) {
			product.setHighestBidder(bidder);
			product.setCurrentBid(amount);
		}
	}
}