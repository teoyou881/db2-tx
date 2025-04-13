package hello.springtx.apply;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.event.EventListener;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationManager;

/* order
* 1. Spring container starts due to @SpringBootTest
  2. Bean of Hello configuration class is created
  3. initV1() is automatically executed due to @PostConstruct
  4. When container initialization is complete, init2() is automatically executed due to ApplicationReadyEvent
  5. While the go() test method is empty, we can observe the logs from the above initialization process
* */

@SpringBootTest
public class InitTxTest {

  @Autowired
  Hello hello;


  @Test
  void go() {
    // Initialization logic is called by Spring at the initialization stage.
  }

  @TestConfiguration
  static class InitTxTestConfig {

    @Bean
    Hello hello() {
      return new Hello();
    }
  }

  @Slf4j
  static class Hello {

    @PostConstruct
    @Transactional
    public void initV1() {
      boolean isActive = TransactionSynchronizationManager.isActualTransactionActive();
      log.info("Hello init @PostConstruct tx active={}", isActive);
    }

    @EventListener(value = ApplicationReadyEvent.class)
    @Transactional
    public void init2() {
      boolean isActive = TransactionSynchronizationManager.isActualTransactionActive();
      log.info("Hello init ApplicationReadyEvent tx active={}", isActive);
    }
  }
}
