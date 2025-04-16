package hello.springtx.propagation;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertTrue;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.UnexpectedRollbackException;

@Slf4j
@SpringBootTest
class MemberServiceTest {

  @Autowired
  MemberService memberService;
  @Autowired
  MemberRepository memberRepository;
  @Autowired
  LogRepository logRepository;


  /**
   * MemberService @Transactional:OFF
   * MemberRepository @Transactional:ON
   * LogRepository @Transactional:ON
   */
  @Test
  void outerTxOff_success() {
    // given
    String username = "outerTxOff_success";

    // when
    memberService.joinV1(username);

    // then: All data is storing
    assertTrue(memberRepository.find(username)
                               .isPresent());
    assertTrue(logRepository.find(username)
                            .isPresent());
  }

  /**
   * MemberService @Transactional:OFF
   * MemberRepository @Transactional:ON
   * LogRepository @Transactional:ON Exception
   */
  @Test
  void outerTxOff_fail() {
    // given
    String username = "LogException";

    // when
    assertThatThrownBy(() -> memberService.joinV1(username)).isInstanceOf(RuntimeException.class);

    // then: member data was saved, but log data was not.
    assertTrue(memberRepository.find(username)
                               .isPresent());
    assertTrue(logRepository.find(username)
                            .isEmpty());
  }

  /**
   * MemberService @Transactional:ON
   * MemberRepository @Transactional:OFF
   * LogRepository @Transactional:OFF
   */
  @Test
  void singleTx() {
    // given
    String username = "singleTx";
    // when
    memberService.joinV1(username);
    // then
    assertTrue(memberRepository.find(username)
                               .isPresent());
    assertTrue(logRepository.find(username)
                            .isPresent());
  }

  /**
   * MemberService @Transactional:ON
   * MemberRepository @Transactional:ON
   * LogRepository @Transactional:ON
   */
  @Test
  void outerTxOn_success() {
    // given
    String username = "outerTxOn_success";
    // when
    memberService.joinV1(username);
    // then
    assertTrue(memberRepository.find(username)
                               .isPresent());
    assertTrue(logRepository.find(username)
                            .isPresent());
  }

  /**
   * MemberService @Transactional:ON
   * MemberRepository @Transactional:ON
   * LogRepository @Transactional:ON Exception
   */
  @Test
  void outerTxOn_fail() {
    // given
    String username = "LogException";
    // when
    assertThatThrownBy(() -> memberService.joinV1(username)).isInstanceOf(RuntimeException.class);
    // then: all data should be rolled back
    assertTrue(memberRepository.find(username)
                               .isEmpty());
    assertTrue(logRepository.find(username)
                            .isEmpty());
  }

  /**
   * MemberService @Transactional:ON
   * MemberRepository @Transactional:ON
   * LogRepository @Transactional:ON Exception
   */
  @Test
  void recoverException_fail() {
    // given
    String username = "LogException";
    // when
    assertThatThrownBy(() -> memberService.joinV2(username)).isInstanceOf(UnexpectedRollbackException.class);
    // then:
    assertTrue(memberRepository.find(username)
                               .isEmpty());
    assertTrue(logRepository.find(username)
                            .isEmpty());
  }

}