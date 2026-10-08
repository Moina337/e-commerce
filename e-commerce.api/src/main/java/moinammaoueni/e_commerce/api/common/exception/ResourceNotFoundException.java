package moinammaoueni.e_commerce.api.common.exception;

/**
 * Exception métier levée lorsqu'une ressource est introuvable.
 */
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }
}
