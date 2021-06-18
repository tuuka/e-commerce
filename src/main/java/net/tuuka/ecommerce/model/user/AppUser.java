package net.tuuka.ecommerce.model.user;

import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
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
@NoArgsConstructor
@ToString
@EqualsAndHashCode
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

    @ManyToMany(fetch = FetchType.EAGER, cascade = {CascadeType.MERGE})
    @JoinTable(name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<AppUserAuthority> authorities = new HashSet<>(
            Collections.singletonList(new AppUserAuthority(AppUserRole.values()[0]))
    );

    public AppUser(String firstName, String lastName, String email, String password) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public Boolean getLocked() {
        return locked;
    }

    public void setLocked(Boolean locked) {
        this.locked = locked;
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
        return authorities.stream()
                .map(auth ->
                        new SimpleGrantedAuthority(auth.getRole().name()))
                .collect(Collectors.toList());
    }

    public void setAuthorities(Collection<String> userRoles) {
        Set<String> allowedRoles = Arrays.stream(AppUserRole.values())
                .map(Enum::name).collect(Collectors.toSet());

        this.authorities = userRoles.stream()
                .map(r -> r.contains("ROLE_") ? r : "ROLE_" + r)
                .filter(allowedRoles::contains)
                .map(r -> new AppUserAuthority(AppUserRole.valueOf(r)))
                .collect(Collectors.toSet());
    }

    public void setAuthorities(Set<AppUserAuthority> authorities) {
        this.authorities = authorities;
    }

}
