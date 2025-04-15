package hello.springtx.exception;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
public class RollbackTest {

  @Autowired
  RollbackService service;

  @Test
  void runtimeException() {
    assertThatThrownBy(() -> service.runtimeException()).isInstanceOf(RuntimeException.class);

  }

  @Test
  void checkedException() throws MyException {
    assertThatThrownBy(() -> service.checkedException()).isInstanceOf(MyException.class);
  }

  @Test
  void checkedException2() throws MyException {
    assertThatThrownBy(() -> service.checkedException2()).isInstanceOf(MyException.class);
  }

  @TestConfiguration
  static class RollbackTestConfig {

    @Bean
    RollbackService rollbackService() {
      return new RollbackService();
    }
  }

  @Slf4j
  static class RollbackService {

    // runtime error -> rollback
    @Transactional
    public void runtimeException() {
      log.info("call runtimeException");
      throw new RuntimeException();
    }

    // checked exception -> commit
    @Transactional
    public void checkedException() throws MyException {
      log.info("call checkedException");
      throw new MyException();
    }

    // checked exception rollbackFor -> rollback
    @Transactional(rollbackFor = MyException.class)
    public void checkedException2() throws MyException {
      log.info("call checkedException2");
      throw new MyException();
    }
  }

  static class MyException extends Exception {}
}
