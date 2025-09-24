package it.uniroma3.siw.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import it.uniroma3.siw.model.Comment;
import it.uniroma3.siw.model.Product;
import it.uniroma3.siw.model.User;

public interface CommentRepository extends CrudRepository<Comment, Long> {

	List<Comment> findByProduct(Product product);

	List<Comment> findByAuthor(User author);

	void deleteByProduct(Product product);

	void deleteByAuthor(User author);
}