package me.simplq.dao;

import java.util.List;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "owner")
@Getter
@Setter
@NoArgsConstructor
public class Owner {
  @Id String id;
  @OneToMany List<Queue> queues;
  String companionDevice;

  public Owner(String id) {
    this.id = id;
  }

  public static Owner empty() {
    return new Owner(null);
  }
}
