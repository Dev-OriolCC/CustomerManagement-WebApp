package com.oriol.customermagnet.filter;

import com.oriol.customermagnet.provider.TokenProvider;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import static com.oriol.customermagnet.utils.ExceptionUtils.processError;
import static java.util.Arrays.asList;
import static org.apache.logging.log4j.util.Strings.EMPTY;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@RequiredArgsConstructor
@Slf4j
@Component
public class CustomAuthorizationFilter extends OncePerRequestFilter {
    private static final String TOKEN_PREFIX = "Bearer ";
    private static final String[] PUBLIC_ROUTES = {"/api/v1/user/login","/api/v1/user/register", "/api/v1/user", "/api/v1/user/verify/code/"
    , "/api/v1/user/refresh/token","/api/v1/user/image" };
    private final TokenProvider tokenProvider;
    public static final String EMAIL_KEY = "email";
    public static final String TOKEN_KEY = "token";
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {

            String token = getToken(request);
            Long userId = getUserId(request);
            if (tokenProvider.isTokenValid(userId, token)) {
                List<GrantedAuthority> grantedAuthorities = tokenProvider.grantedAuthorities(token);
                Authentication authentication = tokenProvider.getAuthentication(userId, grantedAuthorities, request);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } else {
                SecurityContextHolder.clearContext();
            }
            filterChain.doFilter(request, response);
        } catch (Exception exception) {
            log.error(exception.getMessage());
            processError(request, response, exception);
        }
    }

    private Long getUserId(HttpServletRequest request) {
        return tokenProvider.getSubject(getToken(request), request);

    }
    private String getToken(HttpServletRequest request) {
        String header = request.getHeader(AUTHORIZATION);
        if (header != null && header.startsWith(TOKEN_PREFIX)) {
            System.out.println("Header: "+header.replace(TOKEN_PREFIX, EMPTY));
            return header.replace(TOKEN_PREFIX, EMPTY);
        } else {
            return null;
        }
//        return ofNullable(request.getHeader(AUTHORIZATION)
//                .filter(header -> header.startsWith(TOKEN_PREFIX))
//                .map(token -> token.replace(TOKEN_PREFIX, EMPTY)).get());
    }


    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        return request.getHeader(AUTHORIZATION) == null || !request.getHeader(AUTHORIZATION).startsWith(TOKEN_PREFIX) ||
                request.getMethod().equalsIgnoreCase("OPTIONS") || asList(PUBLIC_ROUTES).contains(request.getRequestURI());
    }
}
