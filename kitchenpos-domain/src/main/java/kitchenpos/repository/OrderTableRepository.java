package kitchenpos.repository;


import kitchenpos.domain.order.OrderTable;
import kitchenpos.domain.order.TableGroup;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderTableRepository extends JpaRepository<OrderTable, Long> {
	List<OrderTable> findAllByIdIn(List<Long> orderTableIds);

	List<OrderTable> findAllByTableGroup(TableGroup tableGroup);

}