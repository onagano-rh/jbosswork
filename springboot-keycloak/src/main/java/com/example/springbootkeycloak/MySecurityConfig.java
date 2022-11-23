package com.example.springbootkeycloak;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.user.OidcUserAuthority;
import org.springframework.security.web.SecurityFilterChain;

import com.nimbusds.jose.shaded.json.JSONArray;
import com.nimbusds.jose.shaded.json.JSONObject;

@EnableWebSecurity
public class MySecurityConfig {
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(authReq -> authReq
            .mvcMatchers("/protected/admin").hasRole("admin")
            .mvcMatchers("/protected").authenticated()
            .anyRequest().permitAll())
            .oauth2Login(
                oauthLogin -> oauthLogin.userInfoEndpoint(
                    userInfo -> userInfo.userAuthoritiesMapper(userAuthoritiesMapper()))
        );

        return http.build();
    }
    
    /*
     * Map realm_access.roles claim to GrantedAuthority.
     * These are from ID token, not access token.
     * You should enable "Add ID Token" flag on "Client Scopes > roles > Mappers > realm roles".
     * Taken from: https://kazuhira-r.hatenablog.com/entry/2022/09/08/015028
     * 
     * For resource severes, provably JwtGrantedAuthoritiesConverter is required as in:
     * https://www.baeldung.com/spring-security-map-authorities-jwt
     */
    GrantedAuthoritiesMapper userAuthoritiesMapper() {
        return authorities -> {
            Set<GrantedAuthority> mappedAuthorities = new HashSet<>(authorities);

            authorities.forEach(authority -> {
                if (authority instanceof OidcUserAuthority) {
                    OidcUserAuthority oidcUserAuthority = (OidcUserAuthority) authority;

                    OidcIdToken idToken = oidcUserAuthority.getIdToken();
                    Map<String, Object> claims = idToken.getClaims();

                    JSONObject realmAccess = (JSONObject) claims.get("realm_access");
                    if (realmAccess != null) {
                        JSONArray roles = (JSONArray) realmAccess.get("roles");
                        if (roles != null) {
                            roles.forEach(role -> {
                                String roleName = (String) role;
                                mappedAuthorities.add(new SimpleGrantedAuthority("ROLE_" + roleName));
                            });    
                        }
                    }
                }
            });

            return mappedAuthorities;
        };
    }
}
