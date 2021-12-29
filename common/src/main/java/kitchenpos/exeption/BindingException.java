package kitchenpos.exeption;

public class BindingException extends RuntimeException {
    public BindingException() {
    }

    public BindingException(String message) {
        super(message);
    }
}
