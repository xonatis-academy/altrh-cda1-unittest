package fr.educentre.demo.controllers;

import fr.educentre.demo.domain.Role;
import fr.educentre.demo.dto.RoleDto;
import fr.educentre.demo.exceptions.RoleNotFoundException;
import fr.educentre.demo.exceptions.UserNotFoundException;
import fr.educentre.demo.services.RoleService;
import fr.educentre.demo.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;

@RestController
@RequestMapping("/api/v1")
public class RoleController {

    @Autowired
    private RoleService roleService;

    @Autowired
    private UserService userService;

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/roles")
    public ResponseEntity<Iterable<Role>> list() {
        return ResponseEntity.ok(roleService.list()); // Sending a 200 HTTP status code
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/roles")
    public ResponseEntity<Role> create(@RequestBody RoleDto dto) throws URISyntaxException {
        Role role = roleService.create(dto.getName());
        URI uri = new URI("/roles/" + role.getId());
        return ResponseEntity.created(uri).body(role);
    }


    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/roles/{id}")
    public ResponseEntity<Role> get(@PathVariable int id) throws RoleNotFoundException {
        Role role = roleService.get(id);
        if (role == null) {
            throw new RoleNotFoundException();
        }
        return ResponseEntity.ok(role);
    }


    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/roles/{roleId}/users/{userId}/attach")
    public ResponseEntity<UserDetails> attach(@PathVariable int roleId, @PathVariable int userId) throws URISyntaxException, UserNotFoundException {
        UserDetails user = userService.get(userId);
        if (user == null) {
            throw new UserNotFoundException();
        }
        roleService.attach(user, roleId);
        return ResponseEntity.ok(user);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/roles/{roleId}/users/{userId}/detach")
    public ResponseEntity<UserDetails> detach(@PathVariable int roleId, @PathVariable int userId) throws URISyntaxException, UserNotFoundException {
        UserDetails user = userService.get(userId);
        if (user == null) {
            throw new UserNotFoundException();
        }
        roleService.detach(user, roleId);
        return ResponseEntity.ok(user);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping("/roles/{id}")
    public ResponseEntity<?> delete(@PathVariable int id) {
        roleService.remove(id);
        return ResponseEntity.noContent().build();
    }

}
