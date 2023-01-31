package com.example;

import java.util.Collection;
import java.util.Map;

import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;

@EnableWebSecurity
public class MySecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(authReq -> authReq
            .mvcMatchers("/protected/admin").hasRole("admin")
            .mvcMatchers("/protected").authenticated()
            .anyRequest().permitAll())
            .oauth2ResourceServer(oauth2 ->
                oauth2.jwt(jwt ->
                    jwt.jwtAuthenticationConverter(jwtAuthenticationConverter())))
            ;
        return http.build();
    }
    
    private JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(jwt -> {
            JwtGrantedAuthoritiesConverter grantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
            Collection<GrantedAuthority> grantedAuthorities = grantedAuthoritiesConverter.convert(jwt);

            Map<String, Object> realmAccess = jwt.getClaimAsMap("realm_access");
            if (realmAccess != null) {
                @SuppressWarnings("unchecked")
                Collection<String> roles = (Collection<String>) realmAccess.get("roles");
                if (roles != null) {
                    roles.forEach(role -> {
                        grantedAuthorities.add(new SimpleGrantedAuthority("ROLE_" + role));
                    });
                }
            }

            return grantedAuthorities;
        });
        return jwtAuthenticationConverter;
    }
}
