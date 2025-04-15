package hello.springtx.order;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.fail;

import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@Slf4j
@SpringBootTest
class OrderServiceTest {

  @Autowired
  OrderService orderService;

  @Autowired
  OrderRepository orderRepository;

  @Test
  void order() throws NotEnoughMoneyException {
    // given
    Order order = new Order();
    order.setUsername("normal");

    // when
    orderService.order(order);

    // then
    Order findOrder = orderRepository.findById(order.getId())
                                     .get();
    assertThat(findOrder.getPayStatus()).isEqualTo("completed");
  }

  @Test
  void runtimeException() {
    // given
    Order order = new Order();
    order.setUsername("exception");

    // when, then
    assertThatThrownBy(() -> orderService.order(order)).isInstanceOf(RuntimeException.class);

    // then: Since it was rolled back, there should be no data.
    Optional<Order> orderOptional = orderRepository.findById(order.getId());
    assertThat(orderOptional.isEmpty()).isTrue();
  }

  @Test
  void bizException() {
    // given
    Order order = new Order();
    order.setUsername("insufficient balance");

    // when
    try {
      orderService.order(order);
      fail("Insufficient balance exception should occur.");
    } catch (NotEnoughMoneyException e) {
      log.info("Notify the customer of insufficient balance and guide them to deposit into a separate account");
    }

    // then
    Order findOrder = orderRepository.findById(order.getId())
                                     .get();
    assertThat(findOrder.getPayStatus()).isEqualTo("pending");
  }
}