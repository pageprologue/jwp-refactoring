package api.order.application.exception;

import common.error.NotExistException;

public class NotExistOrderTableGroupException extends NotExistException {
    public NotExistOrderTableGroupException() {
        super("해당하는 주문 그룹 테이블을 찾을 수 없습니다.");
    }

    public NotExistOrderTableGroupException(String message) {
        super(message);
    }
}