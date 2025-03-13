//package com.example.reactive.services.security;
//
//import io.jsonwebtoken.*;
//import io.jsonwebtoken.security.Keys;
//import jakarta.servlet.http.HttpServletRequest;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.http.HttpHeaders;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.AuthenticationException;
//import org.springframework.security.core.GrantedAuthority;
//import org.springframework.security.core.authority.AuthorityUtils;
//import org.springframework.security.core.userdetails.User;
//import org.springframework.stereotype.Component;
//import org.springframework.util.StringUtils;
//
//import javax.crypto.SecretKey;
//import java.time.Duration;
//import java.util.Collection;
//import java.util.Date;
//import java.util.List;
//import java.util.UUID;
//import java.util.function.Function;
//
//import static java.util.stream.Collectors.joining;
//
//@Component
//public class JwtTokenProvider {
//
//    private static final Logger logger = LoggerFactory.getLogger(JwtTokenProvider.class);
//    private static final SecretKey SECRET_KEY = Keys.secretKeyFor(SignatureAlgorithm.HS256);
//    private static final Duration accessTokenValidityDuration = Duration.ofSeconds(10);
//    private static final String TOKEN_PREFIX = "Bearer ";
//    private static final String AUTHORITIES_KEY = "roles";
//
//
//    public String createToken(Authentication authentication) {
//
//        String email = authentication.getName();
//        ClaimsBuilder claimsBuilder = Jwts.claims().subject(email);
//
//        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
//        if (!authorities.isEmpty()) {
//            claimsBuilder.add(AUTHORITIES_KEY, authorities.stream().map(GrantedAuthority::getAuthority).collect(joining(",")));
//        }
//
//        Claims claims = claimsBuilder.build();
//        String id = UUID.randomUUID().toString().replace("-", "");
//        Date tokenCreateTime = new Date();
//        Date tokenValidity = Date.from(tokenCreateTime.toInstant().plus(accessTokenValidityDuration));
//
////        return Jwts.builder()
////                .claims(claims)
////                .issuedAt(tokenCreateTime)
////                .expiration(tokenValidity)
////                .signWith(SECRET_KEY, Jwts.SIG.HS256)
////                .compact();
//
//
//        return Jwts.builder()
//                .id(id)
//                .claims(claims)
////                .subject(email)
//                .issuedAt(tokenCreateTime)
//                .notBefore(tokenCreateTime)
//                .expiration(tokenValidity)
//                .signWith(SECRET_KEY, Jwts.SIG.HS256)
//                .compact();
//
//    }
//
//    public String createToken(String email) {
//        ClaimsBuilder claimsBuilder = Jwts.claims().subject(email);
//        Claims claims = claimsBuilder.build();
//        String id = UUID.randomUUID().toString().replace("-", "");
//        Date tokenCreateTime = new Date();
//        Date tokenValidity = Date.from(tokenCreateTime.toInstant().plus(accessTokenValidityDuration));
//        return Jwts.builder()
//                .id(id)
//                .claims(claims)
////                .subject(email)
//                .issuedAt(tokenCreateTime)
//                .notBefore(tokenCreateTime)
//                .expiration(tokenValidity)
//                .signWith(SECRET_KEY, Jwts.SIG.HS256)
//                .compact();
//
//    }
//
//
//    public String getUserName(String token) {
//        return Jwts.parser()
//                .verifyWith(SECRET_KEY)
//                .build()
////                .parseClaimsJws(token)
//                .parseSignedClaims(token)
//                .getPayload()
//                .getSubject();
//    }
//
//    public Authentication getAuthentication(String token) throws JwtException, IllegalArgumentException {
//        Claims claims = Jwts.parser()
//                .verifyWith(SECRET_KEY)
//                .build()
//                .parseSignedClaims(token) //parseClaimsJws will check expiration date
//                .getPayload();
//
//        String email = claims.getSubject(); //username
//        Object authoritiesClaim = claims.get(AUTHORITIES_KEY);
//
//        Collection<? extends GrantedAuthority> authorities = authoritiesClaim == null ? AuthorityUtils.NO_AUTHORITIES
//                : AuthorityUtils.commaSeparatedStringToAuthorityList(authoritiesClaim.toString());
//
//        User principal = new User(claims.getSubject(), "", authorities);
//
//        return new UsernamePasswordAuthenticationToken(principal, token, authorities);
//    }
//
//    public boolean validateToken(String token) {
//        try {
//            Jws<Claims> claims = Jwts.parser()
//                    .verifyWith(SECRET_KEY)
//                    .build()
//                    .parseSignedClaims(token);
//
//            logger.info("expiration date: {}", claims.getPayload().getExpiration());
//            return true;
//        } catch (JwtException | IllegalArgumentException e) {
//            logger.error("Invalid JWT token: {}", e.getMessage());
//        }
//        return false;
//    }
//
//    public String createToken(Employee employee) {
//        String id = UUID.randomUUID().toString().replace("-", "");
//        Date tokenCreateTime = new Date();
//        Date tokenValidity = Date.from(tokenCreateTime.toInstant().plus(accessTokenValidityDuration));
//
//        return Jwts.builder()
//                .id(id)
//                .subject(employee.getEmail())
//                .issuedAt(tokenCreateTime)
//                .notBefore(tokenCreateTime)
//                .expiration(tokenValidity)
//                .signWith(SECRET_KEY, Jwts.SIG.HS256)
//                .compact();
//
////        Claims claims = Jwts.claims()
////                .setSubject(employee.getEmail());
////                .add("first_name", employee.getFirstName())
////                .add("last_Name", employee.getLastName())
////                .build();
//
////        return Jwts.builder()
////                .setClaims(claims)
////                .setExpiration(tokenValidity)
////                .signWith(SignatureAlgorithm.HS256, SECRET.getBytes(StandardCharsets.UTF_8))
//////                .signWith(getSigningKey())
//////                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
////                .compact();
//    }
//
//    public void verifyToken(String token) throws ExpiredJwtException, UnsupportedJwtException, MalformedJwtException, SignatureException, IllegalArgumentException {
//        Claims claims = parseJwtClaims(token);
//        logger.info("----------------------------");
//        logger.info("ID: {}", claims.getId());
//        logger.info("Subject: {}", claims.getSubject());
//        logger.info("Issuer: {}", claims.getIssuer());
//        logger.info("Expiration: {}", claims.getExpiration());
//    }
//
////    private Key getSigningKey() {
////        byte[] keyBytes = SECRET.getBytes(StandardCharsets.UTF_8);
////        return Keys.hmacShaKeyFor(keyBytes);
////    }
//
//    private Claims parseJwtClaims(String token) {
//        return Jwts.parser()
//                .verifyWith(SECRET_KEY)
//                .build()
//                .parseSignedClaims(token)
//                .getPayload();
//    }
//
//    public Claims resolveClaims(HttpServletRequest req) {
//        try {
//            String token = resolveToken(req);
//            if (token != null) {
//                return parseJwtClaims(token);
//            }
//            return null;
//        } catch (ExpiredJwtException ex) {
//            req.setAttribute("expired", ex.getMessage());
//            throw ex;
//        } catch (Exception ex) {
//            req.setAttribute("invalid", ex.getMessage());
//            throw ex;
//        }
//    }
//
////    public String resolveToken(HttpServletRequest request) throws AuthenticationException {
////        final String bearerToken = request.getHeader(HttpHeaders.AUTHORIZATION);
////        if (bearerToken == null) {
////            throw new PreAuthenticatedCredentialsNotFoundException("missing " + HttpHeaders.AUTHORIZATION + " " + TOKEN_PREFIX);
////        }
////        if (!bearerToken.startsWith(TOKEN_PREFIX)) {
////            throw new InternalAuthenticationServiceException("auto header not start with " + TOKEN_PREFIX);
////        }
////        return bearerToken.substring(TOKEN_PREFIX.length());
////    }
//
//    public String resolveToken(HttpServletRequest request) {
//        final String bearerToken = request.getHeader(HttpHeaders.AUTHORIZATION);
//        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(TOKEN_PREFIX)) {
//            return bearerToken.substring(TOKEN_PREFIX.length());
//        }
//        return null;
//    }
//
//    public boolean validateClaims(Claims claims) throws AuthenticationException {
//        return claims.getExpiration().after(new Date());
//    }
//
//    public String getEmail(Claims claims) {
//        return claims.getSubject();
//    }
//
//    private List<String> getRoles(Claims claims) {
//        return (List<String>) claims.get("roles");
//    }
//
//    public String extractUsername(String token) {
//        return extractClaim(token, Claims::getSubject);
//    }
//
//    public Date extractExpiration(String token) {
//        return extractClaim(token, Claims::getExpiration);
//    }
//
//    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
//        final Claims claims = parseJwtClaims(token);
//        return claimsResolver.apply(claims);
//    }
//
//    private Boolean isTokenExpired(String token) {
//        return extractExpiration(token).before(new Date());
//    }
//
//    //
////    public String generateToken(UserDetails userDetails) {
////        Map<String, Object> claims = new HashMap<>();
////        claims.put("authorities",userDetails.getAuthorities());
////        return createToken(claims, userDetails.getUsername());
////    }
////
////    private String createToken(Map<String, Object> claims, String subject) {
////
////        return Jwts.builder().setClaims(claims).setSubject(subject).setIssuedAt(new Date(System.currentTimeMillis()))
////                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10))
////                .signWith(SignatureAlgorithm.HS256, SECRET_KEY).compact();
////    }
////
//    public boolean validateToken(String token, String email) {
//        final String username = extractUsername(token);
//        return username.equals(email) && !isTokenExpired(token);
//    }
//}
