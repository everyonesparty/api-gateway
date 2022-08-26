package everyonesparty.apigateway.util;

import everyonesparty.apigateway.util.jwt.JwtUtil;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = JwtUtil.class)
class JwtUtilTest {
    
    @Autowired
    private JwtUtil jwtUtil;

    private static String testJwtToken;

    @BeforeAll
    static void beforeAll() {
        testJwtToken = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIyMzk5ODI4OTA4Iiwicm9sZXMiOlsiS0FLQU9fVVNFUiJdLCJpYXQiOjE2NjE0ODM5MTQsImV4cCI6MTY2MTQ4NzUxNH0.NvredrPsG-MbXQv41qnlmprycjqxkrJrPozXBEnO1dw";

    }

    @Test
    void IsValidJwt() {

        // when
        boolean isValid = jwtUtil.IsValidJwt(testJwtToken);

        // then
        assertTrue(isValid);
    }

    @Test
    void hasKaKaoUserRole() {

        // when
        boolean hasKaKaoUserRole = jwtUtil.hasKaKaoUserRole(testJwtToken);

        // then
        assertTrue(hasKaKaoUserRole);
    }
}