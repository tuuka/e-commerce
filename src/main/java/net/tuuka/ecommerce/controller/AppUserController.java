package net.tuuka.ecommerce.controller;

import lombok.RequiredArgsConstructor;
import net.tuuka.ecommerce.controller.dto.AppUserRepresentation;
import net.tuuka.ecommerce.service.AppUserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/api/users")
public class AppUserController {

    private final AppUserService appUserService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    ResponseEntity<List<AppUserRepresentation>> getAllAppUsers() {
        return ResponseEntity.ok(appUserService.findAll().stream()
                .map(AppUserRepresentation::new).collect(Collectors.toList()));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    ResponseEntity<AppUserRepresentation> getAppUserById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(new AppUserRepresentation(appUserService.getAppUserById(id)));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    ResponseEntity<AppUserRepresentation> updateAppUser(@RequestBody AppUserRepresentation appUserRepresentation,
                                          @PathVariable("id") Long id) {

        appUserRepresentation.setId(id);

        return ResponseEntity.ok().location(linkTo(methodOn(this.getClass())
                .getAppUserById(id)).toUri()).body(new AppUserRepresentation(
                        appUserService.updateAppUser(appUserRepresentation.getAppUser())));
    }

    @PostMapping()
    @PreAuthorize("hasRole('ADMIN')")
    ResponseEntity<AppUserRepresentation> updateAppUser(@RequestBody AppUserRepresentation appUserRequest) {

        return ResponseEntity.ok(new AppUserRepresentation(
                appUserService.saveAppUser(appUserRequest.getAppUser())));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    void deleteAppUser(@PathVariable("id") Long id) {

        appUserService.deleteAppUserById(id);

    }

}
