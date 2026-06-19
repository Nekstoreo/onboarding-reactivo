package co.com.bancolombia.onboarding.model.user;

public class ValidationException extends RuntimeException {
    public ValidationException(String message) {
        super(message);
    }
}
