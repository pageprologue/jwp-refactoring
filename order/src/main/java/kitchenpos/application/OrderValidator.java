package kitchenpos.application;

import kitchenpos.application.exception.InvalidOrderState;
import kitchenpos.application.exception.InvalidTableState;
import kitchenpos.application.exception.TableNotFoundException;
import kitchenpos.domain.*;
import org.springframework.stereotype.Component;

@Component
public class OrderValidator {
    private final TableRepository tableRepository;

    public OrderValidator(final TableRepository tableRepository) {
        this.tableRepository = tableRepository;
    }

    public void validate(Order order) {
        OrderTable orderTable = getOrderTable(order.getTableId());
        validateTableState(orderTable);
    }

    private void validateTableState(OrderTable orderTable) {
        TableState tableState = orderTable.getTableState();

        if (tableState.isEmpty()) {
            throw new InvalidTableState("빈 테이블에 주문을 받을 수 없습니다.");
        }
    }

    public void validateOrderStatus(OrderStatus orderStatus) {
        if (orderStatus.isCompleted()) {
            throw new InvalidOrderState("이미 계산을 완료하였습니다.");
        }
    }

    private OrderTable getOrderTable(Long tableId) {
        return tableRepository.findById(tableId)
                .orElseThrow(TableNotFoundException::new);
    }
}
