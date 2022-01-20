package guru.sfg.brewery.repositories;

import guru.sfg.brewery.domain.BeerOrder;
import guru.sfg.brewery.domain.Customer;
import guru.sfg.brewery.domain.OrderStatusEnum;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import javax.persistence.LockModeType;
import java.util.List;
import java.util.UUID;

/**
 * Created by jt on 2019-01-26.
 */
public interface BeerOrderRepository  extends JpaRepository<BeerOrder, UUID> {

    Page<BeerOrder> findAllByCustomer(Customer customer, Pageable pageable);

    List<BeerOrder> findAllByOrderStatus(OrderStatusEnum orderStatusEnum);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    BeerOrder findOneById(UUID id);

    @Query( "select o " +
            "from BeerOrder o " +
            "where o.id =?1 and " +
                "(true = :#{hasAuthority('order.read')} or " +
                "o.customer.id = ?#{principal?.customer?.id})")
    BeerOrder findOneByIdSecured(UUID orderId);
}
