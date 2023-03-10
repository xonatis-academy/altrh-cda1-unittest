package fr.educentre.demo.services;

import fr.educentre.demo.domain.Role;
import org.springframework.security.core.userdetails.UserDetails;

public interface RoleService {

    Role create(String name);

    void remove(int id);

    void attach(UserDetails user, int id);

    void detach(UserDetails user, int id);

    Iterable<Role> list();

    Role get(int id);
}

