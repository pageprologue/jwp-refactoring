package kitchenpos.application.exception;

import kitchenpos.exeption.InvalidException;

public class InvalidOrderState extends InvalidException {
    public InvalidOrderState() {
        super();
    }

    public InvalidOrderState(String message) {
        super(message);
    }
}
