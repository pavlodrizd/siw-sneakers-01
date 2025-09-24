package it.uniroma3.siw.exception;

/**
 * A custom exception thrown when a user attempts to place a bid but does not
 * have enough available credit in their wallet.
 */
public class InsufficientCreditException extends Exception {

	public InsufficientCreditException(String message) {
		super(message);
	}
}
