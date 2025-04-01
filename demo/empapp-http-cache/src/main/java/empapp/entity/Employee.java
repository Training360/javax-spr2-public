package empapp.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter @Setter
@EntityListeners(AuditingEntityListener.class)
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @LastModifiedDate
    private LocalDateTime lastModifiedAt;

    @Version
    private int version;

    @OneToMany(mappedBy = "employee", cascade = CascadeType.REMOVE)
    private List<Address> addresses;

}
