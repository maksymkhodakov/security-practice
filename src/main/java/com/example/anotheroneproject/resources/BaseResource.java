package com.example.anotheroneproject.resources;

import com.example.anotheroneproject.exceptions.WrongCredentials;
import com.example.anotheroneproject.models.AuthenticationRequest;
import com.example.anotheroneproject.models.AuthenticationResponse;
import com.example.anotheroneproject.services.MyUserDetailsService;
import com.example.anotheroneproject.utils.JwtUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
public class BaseResource {
    private final AuthenticationManager authenticationManager;
    private final MyUserDetailsService userDetailsService;
    private final JwtUtil jwtTokenUtil;

    public BaseResource(AuthenticationManager authenticationManager,
                        MyUserDetailsService userDetailsService,
                        JwtUtil jwtTokenUtil) {
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
        this.jwtTokenUtil = jwtTokenUtil;
    }

    @PostMapping
    @RequestMapping(value = "/authenticate")
    public ResponseEntity<?> createAuthenticationToken(
            @RequestBody AuthenticationRequest authenticationRequest) {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                            authenticationRequest.getUsername(),
                            authenticationRequest.getPassword()));
        } catch (BadCredentialsException e) {
            throw new WrongCredentials("Incorrect username or password", e);
        }
        final UserDetails userDetails = userDetailsService
                .loadUserByUsername(authenticationRequest.getUsername());
        final String jwt = jwtTokenUtil.generateToken(userDetails);
        return ResponseEntity.ok(new AuthenticationResponse(jwt));
    }
}
