package kitchenpos.application.exception;

import kitchenpos.exeption.InvalidException;

public class InvalidPrice extends InvalidException {
    public InvalidPrice() {
        super();
    }

    public InvalidPrice(String message) {
        super(message);
    }
}
