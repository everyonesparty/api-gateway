package everyonesparty.apigateway.util;

import io.jsonwebtoken.Jwts;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.validation.constraints.Size;
import java.util.Base64;

@Slf4j
@Component
public class JwtUtil {
    @Value("${jwt.secret}")
    private String secretKey;


    @PostConstruct
    protected void init() {
        secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes());
    }


    public boolean IsValidJwt(String jwt) {
        boolean isValid = true;
        String subject = null;
        try{
            subject = Jwts.parser().setSigningKey(secretKey)
                    .parseClaimsJws(jwt).getBody()
                    .getSubject();
        }catch (Exception ex){
            isValid = false;
        }

        if(subject == null || subject.isEmpty())
            isValid = false;

        log.info("extracted subject is {}", subject);
        return isValid;
    }
}
