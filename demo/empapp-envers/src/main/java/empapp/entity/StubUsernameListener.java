package empapp.entity;

import org.hibernate.envers.RevisionListener;

public class StubUsernameListener implements RevisionListener {

    @Override
    public void newRevision(Object revisionEntity) {
        if (revisionEntity instanceof EmployeeRevisionEntity employeeRevisionEntity) {
            employeeRevisionEntity.setUsername("admin");
        }
    }
}
