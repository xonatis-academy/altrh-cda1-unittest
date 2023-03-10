package fr.educentre.demo.services.impl;

import fr.educentre.demo.domain.Owner;
import fr.educentre.demo.exceptions.AccountExistsException;
import fr.educentre.demo.repositories.OwnerRepository;
import fr.educentre.demo.services.JwtUserService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class JwtUserServiceImpl implements JwtUserService {

    private final String signingKey;

    public JwtUserServiceImpl() {
        this.signingKey = "motdepassebien";
    }

    @Autowired
    private AuthenticationConfiguration authenticationConfiguration;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private OwnerRepository ownerRepository;

    @Override
    public String generateJwtForUser(UserDetails user) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + 3600 * 1000);
        String username = user.getUsername();

        String jwt = Jwts.builder().
                setSubject(username).setIssuedAt(now).setExpiration(expiryDate)
                .signWith(SignatureAlgorithm.HS512, signingKey).compact();

        return jwt;
    }

    @Override
    public UserDetails getUserFromJwt(String jwt) {
        Claims claims = Jwts.parser().setSigningKey(signingKey).parseClaimsJws(jwt).getBody();
        String username = claims.getSubject();
        UserDetails user = loadUserByUsername(username);
        return user;
    }

    @Override
    public Authentication authenticate(String username, String password) throws Exception {
        Authentication authentication = new UsernamePasswordAuthenticationToken(username, password);
        authentication = authenticationConfiguration.getAuthenticationManager().authenticate(authentication);
        return authentication;
    }

    @Override
    public UserDetails save(String username, String password) throws AccountExistsException {
        UserDetails user = ownerRepository.findByLogin(username);
        if (user != null) {
            throw new AccountExistsException();
        }

        Owner owner = new Owner();
        owner.setLogin(username);
        owner.setPassword(passwordEncoder.encode(password));
        ownerRepository.save(owner);
        return owner;
    }

    @Override
    public UserDetails get(int id) {
        return ownerRepository.findById(id).orElse(null);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserDetails user = ownerRepository.findByLogin(username);
        if (user == null) {
            throw new UsernameNotFoundException("The user cannot be found");
        }
        return user;
    }
}
