package empapp;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.history.RevisionMetadata;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class EmployeeRepositoryIT {

    @Autowired
    EmployeeRepository employeeRepository;

    @Test
    void findRevisions() {
        var revisions = employeeRepository.findRevisions(1L);

        assertEquals("John Doe", revisions.getContent().get(0).getEntity().getName());
        assertEquals("Jack Doe", revisions.getContent().get(1).getEntity().getName());
        assertEquals(RevisionMetadata.RevisionType.DELETE, revisions.getContent().get(2).getMetadata().getRevisionType());

        var revision = employeeRepository.findRevision(1L, 2L);
        assertEquals("Jack Doe", revision.get().getEntity().getName());
    }
}
