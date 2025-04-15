package hello.springtx.order;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {

  private final OrderRepository orderRepository;
  // JPA reflects Order data to the database at the time of transaction commit.

  @Transactional
  public void order(Order order) throws NotEnoughMoneyException {
    log.info("order called");
    orderRepository.save(order);
    log.info("Entering payment process");

    if (order.getUsername()
             .equals("exception")) {
      log.info("System exception occurred");
      throw new RuntimeException("System exception");

      /*
      * Insufficient Balance:
        The payStatus is set to "pending", and a checked exception NotEnoughMoneyException("Insufficient balance") is thrown.
        Although an insufficient balance triggers a checked exception and sets payStatus to pending,
        it is expected that the order data should still be committed.
      * */
    } else if (order.getUsername()
                    .equals("insufficient balance")) {
      log.info("Insufficient balance business exception occurred");
      order.setPayStatus("pending");
      throw new NotEnoughMoneyException("Insufficient balance");

    } else {
      // Normal approval
      log.info("Normal approval");
      order.setPayStatus("completed");
    }

    log.info("Payment process completed");
  }
}