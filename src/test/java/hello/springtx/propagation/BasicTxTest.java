package hello.springtx.propagation;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import javax.sql.DataSource;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.UnexpectedRollbackException;
import org.springframework.transaction.interceptor.DefaultTransactionAttribute;

@Slf4j
@SpringBootTest
public class BasicTxTest {

  @Autowired
  PlatformTransactionManager txManager;

  @Test
  void commit() {
    log.info("Transaction started");
    TransactionStatus status = txManager.getTransaction(new DefaultTransactionAttribute());
    log.info("Transaction commit started");
    txManager.commit(status);
    log.info("Transaction commit completed");
  }

  @Test
  void rollback() {
    log.info("Transaction started");
    TransactionStatus status = txManager.getTransaction(new DefaultTransactionAttribute());
    log.info("Transaction rollback started");
    txManager.rollback(status);
    log.info("Transaction rollback completed");
  }

  @Test
  void double_commit() {
    log.info("Transaction 1 started");
    TransactionStatus tx1 = txManager.getTransaction(new DefaultTransactionAttribute());
    TransactionStatus tx2 = txManager.getTransaction(new DefaultTransactionAttribute());
    log.info("Transaction 1 commit");
    txManager.commit(tx1);

    log.info("Transaction 2 started");

    log.info("Transaction 2 commit");
    txManager.commit(tx2);
  }

  @Test
  void double_commit_rollback() {
    log.info("Transaction 1 started");
    TransactionStatus tx1 = txManager.getTransaction(new DefaultTransactionAttribute());
    log.info("Transaction 1 commit");
    txManager.commit(tx1);

    log.info("Transaction 2 started");
    TransactionStatus tx2 = txManager.getTransaction(new DefaultTransactionAttribute());
    log.info("Transaction 2 rollback");
    txManager.rollback(tx2);
  }

  @Test
  void inner_commit() {
    log.info("Outer transaction started");
    TransactionStatus outer = txManager.getTransaction(new DefaultTransactionAttribute());
    // outer.isNewTransaction() -> is it a new transaction which started?
    log.info("outer.isNewTransaction()={}", outer.isNewTransaction());

    log.info("Inner transaction started");
    TransactionStatus inner = txManager.getTransaction(new DefaultTransactionAttribute());
    // if isNewTransaction() is false, then it means this transaction is going to join
    log.info("inner.isNewTransaction()={}", inner.isNewTransaction());

    log.info("Inner transaction commit");
    // technically, inner transaction do not need to commit, because it has been participated in outer transaction
    txManager.commit(inner);

    log.info("Outer transaction commit");
    txManager.commit(outer);
  }

  @Test
  void outer_rollback() {
    log.info("Start outer transaction");
    TransactionStatus outer = txManager.getTransaction(new DefaultTransactionAttribute());
    log.info("Start inner transaction");
    TransactionStatus inner = txManager.getTransaction(new DefaultTransactionAttribute());
    log.info("Commit inner transaction");
    txManager.commit(inner);
    log.info("Rollback outer transaction");
    txManager.rollback(outer);
  }

  @Test
  void inner_rollback() {
    log.info("Start outer transaction");
    TransactionStatus outer = txManager.getTransaction(new DefaultTransactionAttribute());
    log.info("Start inner transaction");
    TransactionStatus inner = txManager.getTransaction(new DefaultTransactionAttribute());
    log.info("Rollback inner transaction");
    txManager.rollback(inner);
    log.info("Commit outer transaction");
    // system must inform the outer transaction that inner transaction is rolled back
    assertThatThrownBy(() -> txManager.commit(outer)).isInstanceOf(UnexpectedRollbackException.class);
    // txManager.commit(outer);
  }

  @Test
  void inner_rollback_requires_new() {
    log.info("Start outer transaction");
    TransactionStatus outer = txManager.getTransaction(new DefaultTransactionAttribute());
    log.info("outer.isNewTransaction()={}", outer.isNewTransaction());

    log.info("Start inner transaction");
    DefaultTransactionAttribute definition = new DefaultTransactionAttribute();
    definition.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
    TransactionStatus inner = txManager.getTransaction(definition);
    log.info("inner.isNewTransaction()={}", inner.isNewTransaction());

    log.info("Rollback inner transaction");
    txManager.rollback(inner); // rollback

    log.info("Commit outer transaction");
    txManager.commit(outer); // commit
  }

  @TestConfiguration
  static class Config {

    @Bean
    public PlatformTransactionManager transactionManager(DataSource dataSource) {
      return new DataSourceTransactionManager(dataSource);
    }
  }
}