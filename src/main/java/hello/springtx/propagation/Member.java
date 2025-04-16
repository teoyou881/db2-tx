package hello.springtx.propagation;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Member {

  @Id
  @GeneratedValue
  private Long id;
  private String username;

  public Member() {
  }

  public Member(String username) {
    this.username = username;
  }
}