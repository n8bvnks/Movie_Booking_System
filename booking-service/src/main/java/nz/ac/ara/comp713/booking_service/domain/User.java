package nz.ac.ara.comp713.booking_service.domain;

import jakarta.persistence.*;

@Entity
@Table(name = "users", uniqueConstraints = @UniqueConstraint(columnNames = "username"))
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String username;

   @Column(nullable = false)
private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    protected User() { }

    public String getPassword() { return password; }
    public Long getId() { return id; }
    public String getUsername() { return username; }
    public Role getRole() { return role; }
}