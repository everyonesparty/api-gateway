package everyonesparty.apigateway.util;

import everyonesparty.apigateway.util.jwt.JwtUtil;
import everyonesparty.apigateway.util.jwt.UserRole;
import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.autoconfigure.RefreshAutoConfiguration;
import org.springframework.test.context.ActiveProfiles;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@ImportAutoConfiguration(RefreshAutoConfiguration.class)
@ActiveProfiles("codetest") // test profile 로 설정해서 {}-codetest.yml 을 바라보게 만듦
@SpringBootTest(classes = JwtUtil.class)
class JwtUtilTest {
    
    @Autowired
    private JwtUtil jwtUtil;

    @Test
    void IsValidJwt_성공() {

        // given
        String testJwtToken = jwtUtil.createToken("0123456789", new HashSet<UserRole>(Arrays.asList(UserRole.KAKAO_USER)));
        Optional<Claims> claims = jwtUtil.parseJwt(testJwtToken);
        // when
        boolean isValid = jwtUtil.IsValidJwt(claims);

        // then
        assertTrue(isValid);
    }

//    @Test
//    void IsValidJwt_시크릿키가_달라질때() throws NoSuchFieldException, IllegalAccessException {
//
//        // given
//        String testJwtToken = jwtUtil.createToken("0123456789", new HashSet<UserRole>(Arrays.asList(UserRole.KAKAO_USER)));
//
//        // when
//        Class aClass = JwtUtil.class;
//        Field field = aClass.getDeclaredField("secretKey");
//        field.setAccessible(true);
//        field.set(jwtUtil, "dummyvalue");
//
//        boolean isValid = jwtUtil.IsValidJwt(testJwtToken);
//
//        // then
//        assertTrue(!isValid);
//    }

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