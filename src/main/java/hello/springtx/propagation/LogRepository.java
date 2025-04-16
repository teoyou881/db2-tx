package hello.springtx.propagation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager; // 수정됨
import java.util.Optional;

@Slf4j
@Repository
@RequiredArgsConstructor
public class LogRepository {

    private final EntityManager em;

    @Transactional
    public void save(Log logMessage) {
        log.info("Saving log");
        em.persist(logMessage);

        if (logMessage.getMessage().contains("LogException")) {
            log.info("Exception occurred while saving log");
            throw new RuntimeException("Exception occurred");
        }
    }

    public Optional<Log> find(String message) {
        return em.createQuery("select l from Log l where l.message = :message", Log.class)
                 .setParameter("message", message)
                 .getResultList()
                 .stream()
                 .findAny();
    }
}