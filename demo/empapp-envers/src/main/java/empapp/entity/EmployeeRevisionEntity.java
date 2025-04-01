package empapp.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.envers.RevisionEntity;
import org.hibernate.envers.RevisionNumber;
import org.hibernate.envers.RevisionTimestamp;

import java.time.LocalDateTime;

@Entity
@RevisionEntity(StubUsernameListener.class)
@Getter
@Setter
public class EmployeeRevisionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @RevisionNumber
    private Long id;

    @RevisionTimestamp
    private LocalDateTime revisionDate;

    private String username;
}
