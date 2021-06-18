package net.tuuka.ecommerce.controller.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import net.tuuka.ecommerce.model.user.AppUser;
import org.springframework.security.core.GrantedAuthority;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

@AllArgsConstructor
@NoArgsConstructor
public class AppUserRepresentation {

    @JsonIgnore
    private AppUser appUser = new AppUser();

    public AppUser getAppUser() {
        return appUser;
    }

    public void setAppUser(AppUser appUser) {
        this.appUser = appUser;
    }

    public Long getId() {
        return appUser.getId();
    }

    public void setId(Long id) {
        appUser.setId(id);
    }

    public String getFirstName() {
        return appUser.getFirstName();
    }

    public void setFirstName(String firstName) {
        appUser.setFirstName(firstName);
    }

    public String getLastName() {
        return appUser.getLastName();
    }

    public void setLastName(String lastName) {
        appUser.setLastName(lastName);
    }

    public String getEmail() {
        return appUser.getEmail();
    }

    public void setEmail(String email) {
        appUser.setEmail(email);
    }

    public void setPassword(String password) { appUser.setPassword(password); }

    public String getPassword() { return appUser.getPassword(); }

    public Boolean getEnabled() {
        return appUser.getEnabled();
    }

    public void setEnabled(Boolean enabled) {
        appUser.setEnabled(enabled);
    }

    public Boolean getLocked() {
        return appUser.getLocked();
    }

    public void setLocked(Boolean locked) {
        appUser.setLocked(locked);
    }

    public Set<String> getRoles() {
        return appUser.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toSet());
    }

    public void setRoles(String[] roles) {
        appUser.setAuthorities(Arrays.asList(roles));
    }
}
