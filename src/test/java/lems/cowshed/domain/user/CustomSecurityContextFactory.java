package lems.cowshed.domain.user;

import lems.cowshed.service.CustomUserDetails;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

import java.util.Collections;

public class CustomSecurityContextFactory implements WithSecurityContextFactory<WithMockCustomUser> {
    @Override
    public SecurityContext createSecurityContext(WithMockCustomUser annotation) {
        SecurityContext context = SecurityContextHolder.createEmptyContext();

        User user = User.builder()
                .id(annotation.id())
                .username(annotation.username())
                .password(annotation.password())
                .role("ROLE_USER")
                .build();

        CustomUserDetails principal = new CustomUserDetails(user);
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(principal, annotation.password(), principal.getAuthorities());
        context.setAuthentication(authenticationToken);
        return context;
    }
}
