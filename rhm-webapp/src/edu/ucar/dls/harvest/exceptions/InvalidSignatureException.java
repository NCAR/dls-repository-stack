package edu.ucar.dls.harvest.exceptions;

/**
 * Custom exception class used when harvesting from the LR
 * @author dfinke
 *
 */

public class InvalidSignatureException extends Exception {
	public InvalidSignatureException(String message) {
        super(message);
    }
	
}
