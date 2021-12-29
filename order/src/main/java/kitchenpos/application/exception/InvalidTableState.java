package kitchenpos.application.exception;

import kitchenpos.exeption.InvalidException;

public class InvalidTableState extends InvalidException {
    public InvalidTableState() {
        super();
    }

    public InvalidTableState(String message) {
        super(message);
    }
}
