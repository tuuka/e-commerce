package net.tuuka.ecommerce.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.util.*;
import java.util.stream.Collectors;

@Entity
@Table(name = "app_user",
        uniqueConstraints = {
                @UniqueConstraint(name = "app_user_email_unique",
                        columnNames = "email")})
@Getter
@Setter
@NoArgsConstructor
public class AppUser implements UserDetails {

    @Id
    @SequenceGenerator(name = "appUserSequence",
            sequenceName = "app_user_sequence",
            allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE,
            generator = "appUserSequence")
    private Long id;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "email")
    private String email;

    @Column(name = "password")
    private String password;

    @Column(name = "enabled", columnDefinition = "BOOLEAN")
    private Boolean enabled = false;

    @Column(name = "locked", columnDefinition = "BOOLEAN")
    private Boolean locked = false;

    @ManyToMany(fetch = FetchType.EAGER, cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.DETACH, CascadeType.REFRESH})
    @JoinTable(name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<AppUserAuthority> userAuthorities = new HashSet<>(Collections
            .singletonList(new AppUserAuthority(AppUserRole.values()[0])));

    public AppUser(String firstName, String lastName, String email, String password) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
    }

    //   ------- UserDetail Override ---------------
    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return !locked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return userAuthorities.stream()
                .map(role ->
                        new SimpleGrantedAuthority(role.getRole().name()))
                .collect(Collectors.toList());
    }

}
