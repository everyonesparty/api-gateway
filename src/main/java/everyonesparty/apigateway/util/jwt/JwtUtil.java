package everyonesparty.apigateway.util.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.Set;

@Slf4j
@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.token-validity-in-seconds}")
    private long tokenValidMilisecond; // 1시간만 토큰 유효

    @PostConstruct
    protected void init() {
        secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes());
    }

    public String createToken(String userPk, Set<UserRole> roles) {
        Claims claims = Jwts.claims().setSubject(userPk);
        claims.put("roles", new ArrayList<>(roles));
        Date now = new Date();
        return Jwts.builder()
                .setClaims(claims) // 데이터
                .setIssuedAt(now) // 토큰 발행일자
                .setExpiration(new Date(now.getTime() + 3600000)) // set Expire Time
                .signWith(SignatureAlgorithm.HS256, secretKey) // 암호화 알고리즘, secret값 세팅
                .compact();
    }

    public boolean IsValidJwt(String jwt) {
        boolean isValid = true;
        String subject = null;
        try{
            subject = Jwts.parser().setSigningKey(secretKey)
                    .parseClaimsJws(jwt).getBody()
                    .getSubject();
        }catch (Exception ex){
            log.error("jwt parsing error. {}", ex.getMessage());
            isValid = false;
        }

        if(subject == null || subject.isEmpty()){
            log.error("subject is invalid. subject: {}", subject);
            isValid = false;
        }

        log.info("extracted subject is {}", subject);
        return isValid;
    }


    public boolean hasKaKaoUserRole(String jwt){
        return this.hasValidRole(jwt, UserRole.KAKAO_USER);
    }



    /***
     * > jwt 에 userRole 이 있는지?
     * @param jwt
     * @param userRole
     * @return
     */
    private boolean hasValidRole(String jwt, UserRole userRole) {
        boolean isValid = true;
        ArrayList<String> userRoles = null;
        try{
            userRoles = (ArrayList<String>) Jwts.parser().setSigningKey(secretKey)
                    .parseClaimsJws(jwt).getBody()
                    .get("roles");
        }catch (Exception ex){
            log.error("jwt parsing error. {}", ex.getMessage());
            isValid = false;
        }

        if(userRoles == null || userRoles.isEmpty() || !userRoles.contains(userRole.name())){
            log.error("This jwt token does not have role {}.", userRole);
            isValid = false;
        }

        return isValid;
    }
}
