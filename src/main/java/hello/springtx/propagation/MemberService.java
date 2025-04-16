package hello.springtx.propagation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberService {

  private final MemberRepository memberRepository;
  private final LogRepository logRepository;

  @Transactional
  public void joinV1(String username) {
    Member member = new Member(username);
    Log logMessage = new Log(username);

    log.info("== Starting memberRepository call ==");
    memberRepository.save(member);
    log.info("== Finished memberRepository call ==");

    log.info("== Starting logRepository call ==");
    logRepository.save(logMessage);
    log.info("== Finished logRepository call ==");
  }

  public void joinV2(String username) {
    Member member = new Member(username);
    Log logMessage = new Log(username);

    log.info("== Starting memberRepository call ==");
    memberRepository.save(member);
    log.info("== Finished memberRepository call ==");

    log.info("== Starting logRepository call ==");

    // if error is about log, should handle
    try {
      logRepository.save(logMessage);
    } catch (RuntimeException e) {
      log.info("Failed to save the log. logMessage={}", logMessage.getMessage());
      log.info("Converting to normal flow");
    }
    log.info("== Finished logRepository call ==");
  }
}