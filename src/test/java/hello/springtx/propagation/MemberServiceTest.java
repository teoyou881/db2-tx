package hello.springtx.propagation;

import static org.junit.jupiter.api.Assertions.assertTrue;

import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@Slf4j
@SpringBootTest
class MemberServiceTest {

  @Autowired
  MemberService memberService;
  @Autowired
  MemberRepository memberRepository;
  @Autowired
  LogRepository logRepository;

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

  @Test
  void outerTxOff_fail() {
    // given
    String username = "LogException";

    // when
    Assertions.assertThatThrownBy(() -> memberService.joinV1(username))
              .isInstanceOf(RuntimeException.class);

    // then: member data was saved, but log data was not.
    assertTrue(memberRepository.find(username)
                               .isPresent());
    assertTrue(logRepository.find(username)
                            .isEmpty());
  }
}