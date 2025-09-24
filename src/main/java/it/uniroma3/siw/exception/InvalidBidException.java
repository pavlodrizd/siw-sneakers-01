package it.uniroma3.siw.exception;

/**
 * A custom exception thrown for various reasons when a bid is not valid, for
 * example: the bid amount is too low, or the auction has already ended.
 */
public class InvalidBidException extends Exception {

	public InvalidBidException(String message) {
		super(message);
	}
}
