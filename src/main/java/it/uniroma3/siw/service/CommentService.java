package it.uniroma3.siw.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.uniroma3.siw.model.Comment;
import it.uniroma3.siw.model.User;
import it.uniroma3.siw.repository.CommentRepository;

@Service
public class CommentService {

	@Autowired
	private CommentRepository commentRepository;

	@Transactional
	public Comment save(Comment comment) {
		return commentRepository.save(comment);
	}

	/**
	 * Trova un commento tramite il suo ID. Cruciale per la sicurezza e per il form
	 * di modifica.
	 */
	public Optional<Comment> findById(Long id) {
		return commentRepository.findById(id);
	}

	public List<Comment> findByAuthor(User author) {
		return commentRepository.findByAuthor(author);
	}

	public List<Comment> findAll() {
	    List<Comment> comments = new ArrayList<>();
	    this.commentRepository.findAll().forEach(comments::add);
	    return comments;
	}

	/**
	 * Aggiorna un commento esistente. Trova il commento, aggiorna i campi e lo
	 * salva.
	 */
	@Transactional
	public Comment update(Long id, Comment updatedComment) {
		Comment comment = this.findById(id).orElse(null);
		if (comment != null) {
			comment.setTitle(updatedComment.getTitle());
			return this.commentRepository.save(comment);
		}
		return null; // O lancia un'eccezione se il commento non viene trovato
	}

	/**
	 * Cancella un commento tramite il suo ID.
	 */
	@Transactional
	public void deleteById(Long id) {
		commentRepository.deleteById(id);
	}
}