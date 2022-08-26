package everyonesparty.apigateway.util;

import everyonesparty.apigateway.util.jwt.JwtUtil;
import everyonesparty.apigateway.util.jwt.UserRole;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = JwtUtil.class)
class JwtUtilTest {
    
    @Autowired
    private JwtUtil jwtUtil;

    @Test
    void IsValidJwt() {

        // given
        String testJwtToken = jwtUtil.createToken("0123456789", new HashSet<UserRole>(Arrays.asList(UserRole.KAKAO_USER)));

        // when
        boolean isValid = jwtUtil.IsValidJwt(testJwtToken);

        // then
        assertTrue(isValid);
    }

    @Test
    void hasKaKaoUserRole() {

        // given
        String testJwtToken = jwtUtil.createToken("0123456789", new HashSet<UserRole>(Arrays.asList(UserRole.KAKAO_USER)));

        // when
        boolean hasKaKaoUserRole = jwtUtil.hasKaKaoUserRole(testJwtToken);

        // then
        assertTrue(hasKaKaoUserRole);
    }
}