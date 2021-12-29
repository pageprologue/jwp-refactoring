package kitchenpos.application.exception;

import kitchenpos.exeption.NotFoundException;

public class OrderNotFoundException extends NotFoundException {
    public OrderNotFoundException() {
        super("주문을 찾을 수 없습니다.");
    }

    public OrderNotFoundException(String message) {
        super(message);
    }
}
