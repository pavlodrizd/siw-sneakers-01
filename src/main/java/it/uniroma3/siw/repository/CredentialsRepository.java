package it.uniroma3.siw.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import it.uniroma3.siw.model.Credentials;
import it.uniroma3.siw.model.User;

public interface CredentialsRepository extends CrudRepository<Credentials, Long> {

	public Optional<Credentials> findByEmail(String email);

	public List<Credentials> findAllByRole(String role);

	public boolean existsByEmail(String email);

	public Optional<Credentials> findByUser(User user);

}