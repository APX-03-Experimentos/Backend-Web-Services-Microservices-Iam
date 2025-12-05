package apx.inc.iam_services.iam.domain.model.aggregates;

import apx.inc.iam_services.iam.domain.model.commands.UpdateUserCommand;
import apx.inc.iam_services.iam.domain.model.entities.Role;
import apx.inc.iam_services.shared.domain.model.aggregates.AuditableAbstractAggregateRoot;
import jakarta.persistence.*;
import lombok.Getter;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Entity
public class User extends AuditableAbstractAggregateRoot<User> {

    private String username;
    private String password;

    @ManyToMany(fetch = FetchType.EAGER,cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "user_roles",
            joinColumns = @JoinColumn(name="user_id"),
            inverseJoinColumns = @JoinColumn(name="role_id")
    )
    private Set<Role> userRoles;

    @ElementCollection
    @CollectionTable(name = "student_courses", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "course_id")
    private Set<Long> studentInCourseIds; // ✅ Solo IDs

    protected User() {
        super();
        this.userRoles = new HashSet<>();
        this.studentInCourseIds = new HashSet<>();
    }

    public User(String username, String password) {
        this.username = username;
        this.password = password;
        this.userRoles = new HashSet<>();
        this.studentInCourseIds = new HashSet<>();
    }

    public User(String username, String password, List<Role> roles) {
        this(username, password);
        addRoles(roles);
    }

    public User updateUserDetails(UpdateUserCommand updateUserCommand) {
        this.username = updateUserCommand.username();
        this.password = updateUserCommand.password();
        return this;
    }

    // ✅ SOLUCIÓN: Manejar solo IDs
    public void assignToCourse(Long courseId) {
        this.studentInCourseIds.add(courseId);
    }

    public void removeFromCourse(Long courseId) {
        this.studentInCourseIds.remove(courseId);
    }

    public boolean isEnrolledInCourse(Long courseId) {
        return this.studentInCourseIds.contains(courseId);
    }

    public void addRoles(List<Role> roles) {
        var validatedRoleSet = Role.validateRoleSet(roles);
        this.userRoles.addAll(validatedRoleSet);
    }

}
