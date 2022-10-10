package io.je.project.config;

import com.auth0.jwk.GuavaCachedJwkProvider;
import com.auth0.jwk.Jwk;
import com.auth0.jwk.JwkProvider;
import com.auth0.jwk.UrlJwkProvider;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import utils.log.LoggerUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.net.URL;
import java.security.interfaces.RSAPublicKey;


/*
 * OAuth 2.0 authentication flow interceptor
 * */
public class AuthenticationInterceptor implements HandlerInterceptor {

    private static String jwksUrl;

    private static String issuer;

    public static void init(String issueUrl) {
        jwksUrl = (issueUrl + "/.well-known/openid-configuration/jwks");
        issuer = issueUrl;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }

        HandlerMethod handlerMethod = (HandlerMethod) handler;
        Method method = handlerMethod.getMethod();
        String originToken = request.getHeader("Authorization");
        String uri = request.getRequestURI();
        if (uri.contains("/jeproject/updateRunner")) return true;
        if (uri.contains("/workflow/updateStatus")) return true;

        if (originToken == null || originToken.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return false;
        }

        try {
            String token = getToken(originToken);
            DecodedJWT jwt = JWT.decode(token);
            JwkProvider http = new UrlJwkProvider(new URL(jwksUrl));
            JwkProvider provider = new GuavaCachedJwkProvider(http);
            Jwk jwk = provider.get(jwt.getId());
            Algorithm algorithm = Algorithm.RSA256((RSAPublicKey) jwk.getPublicKey(), null);
            JWTVerifier verifier = JWT.require(algorithm)
                    .withIssuer(issuer)
                    .withArrayClaim("scope", "je.write", "je.read")
                    .build();
            verifier.verify(token);
        } catch (JWTVerificationException exception) {
            LoggerUtils.logException(exception);
            PrintWriter writer = response.getWriter();
            writer.write(exception.getMessage());
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return false;
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

    }

    private String getToken(String originToken) {
        String[] arr = originToken.split(" ");
        return arr[1];
    }

}
