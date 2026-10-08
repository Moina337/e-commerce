package moinammaoueni.e_commerce.api.common.exception;

/**
 * Exception métier levée lors d'une tentative de création d'une ressource déjà existante.
 */
public class DuplicateResourceException extends RuntimeException {

    public DuplicateResourceException(String message) {
        super(message);
    }
}
