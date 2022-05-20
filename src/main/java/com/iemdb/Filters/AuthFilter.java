//package com.iemdb.Filters;
//
//import java.io.IOException;
//
//import com.iemdb.Entity.User;
//import com.iemdb.controller.Authentication;
//import com.iemdb.model.IEMovieDataBase;
//import io.jsonwebtoken.Claims;
//import io.jsonwebtoken.Jws;
//import io.jsonwebtoken.JwtException;
//import io.jsonwebtoken.Jwts;
//import org.springframework.http.HttpHeaders;
//
//import java.nio.charset.StandardCharsets;
//import java.util.Date;
//import java.util.Enumeration;
//
//import javax.crypto.SecretKey;
//import javax.crypto.spec.SecretKeySpec;
//import javax.servlet.Filter;
//import javax.servlet.FilterChain;
//import javax.servlet.FilterConfig;
//import javax.servlet.ServletException;
//import javax.servlet.ServletRequest;
//import javax.servlet.ServletResponse;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//
//import org.springframework.stereotype.Component;
//import org.springframework.core.annotation.Order;
//
//@Component
////@Order(2)
//public class AuthFilter implements Filter {
//    @Override
//    public void destroy() {}
//
//    @Override
//    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterchain)
//            throws IOException, ServletException {
//        filterchain.doFilter(request, response);
//        HttpServletRequest httpRequest = (HttpServletRequest) request;
//        String token = httpRequest.getHeader(HttpHeaders.AUTHORIZATION);
//
//        if (token == null || token.split(" ").length < 2) {
//            ((HttpServletResponse) response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
//            response.getWriter().write("{\"error\": \"No JWT token was provided\"}");
//            ((HttpServletResponse) response).setHeader("Content-Type", "application/json;charset=UTF-8");
//            return;
//        }
//
//        String jwt = token.split(" ")[1];
//        SecretKey key = new SecretKeySpec(Authentication.KEY.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
//        Jws<Claims> jwsClaims;
//        try {
//            jwsClaims = Jwts.parserBuilder()
//                    .setSigningKey(key)
//                    .build()
//                    .parseClaimsJws(jwt);
//            if (jwsClaims.getBody().getExpiration().before(new Date()))
//                throw new JwtException("Token is expired");
//            String username = jwsClaims.getBody().get("user", String.class);
//            User user = IEMovieDataBase.getInstance().getUser(username);
//            request.setAttribute("user", user);
//        } catch (JwtException e) {
//            System.out.println(e.getMessage());
//            ((HttpServletResponse) response).setStatus(HttpServletResponse.SC_FORBIDDEN);
//            response.getWriter().write("{\"error\": \"BAD JWT\"}");
//            ((HttpServletResponse) response).setHeader("Content-Type", "application/json;charset=UTF-8");
//            return;
//        }
//
//        filterchain.doFilter(request, response);
//    }
//
//    @Override
//    public void init(FilterConfig filterconfig) throws ServletException {}
//}
