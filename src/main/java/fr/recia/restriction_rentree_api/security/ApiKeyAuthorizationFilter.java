package fr.recia.restriction_rentree_api.security;

import fr.recia.restriction_rentree_api.configuration.RestrictionProperties;
import fr.recia.restriction_rentree_api.configuration.SecurityProperty;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.util.matcher.IpAddressMatcher;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@Component
@Slf4j
public class ApiKeyAuthorizationFilter extends OncePerRequestFilter {

    private final RestrictionProperties restrictionProperties;
    private final AntPathMatcher matcher;

    public ApiKeyAuthorizationFilter(RestrictionProperties restrictionProperties) {
        this.restrictionProperties = restrictionProperties;
        this.matcher = new AntPathMatcher();
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain chain) throws ServletException, IOException {

        String apiKey = request.getHeader("X-API-KEY");
        String path = request.getRequestURI();
        String ip = request.getRemoteHost();

        if (apiKey == null) {
            log.debug("No key specified : continuing filter chain");
            chain.doFilter(request, response);
            return;
        }

        SecurityProperty config = restrictionProperties.getSecurity().get(apiKey);
        if (config == null) {
            log.warn("Key {} unknown : request unauthorized", apiKey);
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        if (config.getAllowedIps().stream().noneMatch(allowed -> new IpAddressMatcher(allowed).matches(ip))) {
            log.warn("IP {} is not valid for key {} : request unauthorized", ip, apiKey);
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        if (config.getAllowedPaths().stream().noneMatch(p -> matcher.match(p, path))) {
            log.warn("Path {} is not valid for key {} : request unauthorized", path, apiKey);
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        Authentication auth = new UsernamePasswordAuthenticationToken(apiKey, null, List.of());
        SecurityContextHolder.getContext().setAuthentication(auth);
        log.debug("Client authenticated with key {}", apiKey);

        chain.doFilter(request, response);
    }
}