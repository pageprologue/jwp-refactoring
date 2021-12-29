package kitchenpos.application;

import kitchenpos.application.exception.InvalidOrderState;
import kitchenpos.application.exception.InvalidTableState;
import kitchenpos.application.exception.TableNotFoundException;
import kitchenpos.domain.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.Optional;

import static kitchenpos.domain.OrderStatus.COMPLETION;
import static kitchenpos.domain.OrderStatus.COOKING;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@DisplayName("주문 조건 테스트")
@ExtendWith(MockitoExtension.class)
class OrderValidatorTest {
    @Mock
    private TableRepository tableRepository;
    @InjectMocks
    private OrderValidator orderValidator;

    private OrderTable 빈테이블;
    private OrderLineItem 주문항목;
    private Order 주문;

    @BeforeEach
    void setUp() {
        빈테이블 = OrderTable.of(0, new TableState(true));
        주문항목 = OrderLineItem.of(1L, 1L);
        주문 = Order.of(1L, COOKING, Collections.singletonList(주문항목));
    }

    @Test
    @DisplayName("주문 항목의 목록이 비어있는 경우 예외가 발생한다.")
    void validateOrderLineItemsEmpty() {
        Order 주문 = Order.of(1L, COOKING, Collections.emptyList());

        assertThatThrownBy(() -> orderValidator.validate(주문))
                .isInstanceOf(TableNotFoundException.class);
    }

    @Test
    @DisplayName("주문 테이블이 빈 테이블인 경우 예외가 발생한다.")
    void validateOrderTableEmpty() {
        when(tableRepository.findById(anyLong())).thenReturn(Optional.of(빈테이블));

        assertThatThrownBy(() -> orderValidator.validate(주문))
                .isInstanceOf(InvalidTableState.class);
        verify(tableRepository, times(1)).findById(anyLong());
    }

    @Test
    @DisplayName("주문 상태가 완료인 경우 예외가 발생한다.")
    void validateOrderStatus() {
        assertThatThrownBy(() -> orderValidator.validateOrderStatus(COMPLETION))
                .isInstanceOf(InvalidOrderState.class);
    }
}
