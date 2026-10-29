package tn.esprit.autoloc.exception;

public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String resourceName, Long id) {
        super(String.format("%s introuvable avec l'identifiant : %d", resourceName, id));
    }

    public ResourceNotFoundException(String message) {
        super(message);
    }
}
