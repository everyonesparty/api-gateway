package everyonesparty.apigateway.util.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.*;

@Slf4j
@Component
@RefreshScope
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.token-validity-in-seconds}")
    private long tokenValidMilisecond; // 1시간만 토큰 유효

    @PostConstruct
    protected void init() {
        secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes());
    }

    /***
     * jwt token 생성을 front-server 로 위임
     */
    @Deprecated
    public String createToken(String userPk, Set<UserRole> roles) {
        Claims claims = Jwts.claims().setSubject(userPk);
        claims.put("roles", new ArrayList<>(roles));
        Date now = new Date();
        return Jwts.builder()
                .setClaims(claims) // 데이터
                .setIssuedAt(now) // 토큰 발행일자
                .setExpiration(new Date(now.getTime() + tokenValidMilisecond)) // 토큰만료일자
                .signWith(SignatureAlgorithm.HS256, secretKey) // 암호화 알고리즘, secret값 세팅
                .compact();
    }

    /***
     * > 디비 안거치는 단순 jwt 검증 -> secretKey 로 정상적으로 까지기만 하면 통과되는 인증 필터
     * > TODO: 필요 시 디비에 저장된 사용자 아이디와 토큰에 포함된 사용자 아이디 검증하는 별도 기능 추가
     */
    public boolean IsValidJwt(String jwt) {
        return parseJwt(jwt)
                .flatMap(this::getSubject)
                .filter(subject -> !subject.isEmpty())
                .map(subject -> {
                    log.info("extracted subject is {}", subject);
                    return true;
                }).orElse(false);
    }

    public boolean hasKaKaoUserRole(String jwt){
        return this.hasValidRole(jwt, UserRole.KAKAO_USER);
    }

    /***
     * > jwt 에 userRole 이 있는지?
     */
    private boolean hasValidRole(String jwt, UserRole userRole) {
        return parseJwt(jwt)
                .flatMap(this::getUserRoles)
                .filter(userRoles -> !userRoles.isEmpty())
                .filter(userRoles -> userRoles.contains(userRole.name()))
                .map(userRoles -> {
                    log.info("extracted userRoles is {}", userRoles.toString());
                    return true;
                })
                .orElse(false);
    }

    private Optional<Claims> parseJwt(String jwt) {
        try {
            return Optional.ofNullable(Jwts.parser()
                    .setSigningKey(secretKey)
                    .parseClaimsJws(jwt).getBody());
        } catch (Exception ex) {
            log.error("jwt parsing error. {}", ex.getMessage(), ex);
            return Optional.empty();
        }
    }

    private Optional<String> getSubject(Claims parsedJwt) {
        return Optional.ofNullable(parsedJwt.getSubject());
    }

    private Optional<ArrayList<String>> getUserRoles(Claims parsedJwt) {
        return Optional.ofNullable((ArrayList<String>) parsedJwt.get("roles"));
    }
}
