package kitchenpos.application;

import kitchenpos.dao.OrderDao;
import kitchenpos.domain.OrderTable;
import kitchenpos.domain.OrderTableRepository;
import kitchenpos.domain.TableGroup;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@DisplayName("테이블 서비스")
@ExtendWith(MockitoExtension.class)
class TableServiceTest {
    @Mock
    private OrderDao orderDao;
    @Mock
    private OrderTableRepository orderTableRepository;
    @InjectMocks
    private TableService tableService;

    private OrderTable 주문테이블;
    private OrderTable 빈테이블;

    @BeforeEach
    void setUp() {
        주문테이블 = new OrderTable(1L, 2, false);
        빈테이블 = new OrderTable(2L, 3, true);
    }

    @Test
    @DisplayName("테이블을 등록한다.")
    void create() {
        when(orderTableRepository.save(any())).thenReturn(빈테이블);

        OrderTable orderTable = tableService.create(빈테이블);

        verify(orderTableRepository, times(1)).save(any(OrderTable.class));
        assertThat(orderTable).extracting("id", "numberOfGuests", "empty")
                .containsExactly(빈테이블.getId(), 빈테이블.getNumberOfGuests(), 빈테이블.isEmpty());
    }

    @Test
    @DisplayName("테이블 목록을 조회한다.")
    void list() {
        when(orderTableRepository.findAll()).thenReturn(Arrays.asList(주문테이블, 빈테이블));

        List<OrderTable> orderTables = tableService.list();

        verify(orderTableRepository, times(1)).findAll();
        assertThat(orderTables).hasSize(2);
    }

    @Test
    @DisplayName("테이블을 빈 테이블로 변경한다.")
    void changeEmpty() {
        when(orderTableRepository.findById(1L)).thenReturn(Optional.of(주문테이블));
        when(orderDao.existsByOrderTableIdAndOrderStatusIn(anyLong(), anyList())).thenReturn(false);
        when(orderTableRepository.save(any())).thenReturn(빈테이블);

        OrderTable emptyOrderTable = tableService.changeEmpty(주문테이블.getId(), 빈테이블);

        verify(orderTableRepository, times(1)).findById(anyLong());
        verify(orderDao, times(1)).existsByOrderTableIdAndOrderStatusIn(anyLong(), anyList());
        verify(orderTableRepository, times(1)).save(any(OrderTable.class));
        assertThat(emptyOrderTable.isEmpty()).isTrue();
    }

    @Test
    @DisplayName("주문 테이블이 없는 경우 예외가 발생한다.")
    void validateOrderTable() {
        when(orderTableRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThatIllegalArgumentException()
                .isThrownBy(() -> tableService.changeEmpty(주문테이블.getId(), 빈테이블));
        verify(orderTableRepository, times(1)).findById(anyLong());
    }

    @Test
    @DisplayName("테이블 그룹으로 등록되어 있는 경우 예외가 발생한다.")
    void validateExistTableGroup() {
        주문테이블.setTableGroup(new TableGroup(LocalDateTime.now()));
        when(orderTableRepository.findById(1L)).thenReturn(Optional.of(주문테이블));

        assertThatIllegalArgumentException()
                .isThrownBy(() -> tableService.changeEmpty(주문테이블.getId(), 빈테이블));
        verify(orderTableRepository, times(1)).findById(anyLong());
    }

    @Test
    @DisplayName("테이블의 주문 상태가 완료가 아닌 경우 예외가 발생한다.")
    void validateOrderStatus() {
        when(orderTableRepository.findById(anyLong())).thenReturn(Optional.of(주문테이블));
        when(orderDao.existsByOrderTableIdAndOrderStatusIn(anyLong(), anyList())).thenReturn(true);

        assertThatIllegalArgumentException()
                .isThrownBy(() -> tableService.changeEmpty(주문테이블.getId(), 빈테이블));
        verify(orderTableRepository, times(1)).findById(anyLong());
        verify(orderDao, times(1)).existsByOrderTableIdAndOrderStatusIn(anyLong(), anyList());
    }

    @Test
    @DisplayName("테이블의 손님 수를 변경한다.")
    void changeNumberOfGuests() {
        int numberOfGuests = 5;
        OrderTable 손님수변경테이블 = new OrderTable(numberOfGuests, false);

        when(orderTableRepository.findById(anyLong())).thenReturn(Optional.of(주문테이블));
        when(orderTableRepository.save(any())).thenReturn(주문테이블);
        OrderTable changeGuestOrderTable = tableService.changeNumberOfGuests(주문테이블.getId(), 손님수변경테이블);

        verify(orderTableRepository, times(1)).findById(anyLong());
        verify(orderTableRepository, times(1)).save(any(OrderTable.class));
        assertThat(changeGuestOrderTable.getNumberOfGuests()).isEqualTo(numberOfGuests);
    }

    @Test
    @DisplayName("손님의 수가 0명 미만인 경우 예외가 발생한다.")
    void validateGuestNumbers() {
        int numberOfGuests = -1;
        OrderTable 손님수변경테이블 = new OrderTable(numberOfGuests, false);

        assertThatIllegalArgumentException()
                .isThrownBy(() -> tableService.changeNumberOfGuests(주문테이블.getId(), 손님수변경테이블));
    }

    @Test
    @DisplayName("주문 테이블이 없는 경우 예외가 발생한다.")
    void validateOrderTable2() {
        int numberOfGuests = 5;
        OrderTable 손님수변경테이블 = new OrderTable(numberOfGuests, false);
        when(orderTableRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThatIllegalArgumentException()
                .isThrownBy(() -> tableService.changeNumberOfGuests(주문테이블.getId(), 손님수변경테이블));
        verify(orderTableRepository, times(1)).findById(anyLong());
    }

    @Test
    @DisplayName("주문 테이블이 빈 테이블인 경우 예외가 발생한다.")
    void validateOrderTableEmpty() {
        when(orderTableRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThatIllegalArgumentException()
                .isThrownBy(() -> tableService.changeNumberOfGuests(주문테이블.getId(), 빈테이블));
        verify(orderTableRepository, times(1)).findById(anyLong());
    }
}