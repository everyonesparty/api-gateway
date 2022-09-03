package everyonesparty.apigateway.exception.error;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;


/***
 * 미리 정의해 둔 에러들
 */
@AllArgsConstructor
public enum JwtError implements RestError {

    FORBIDDEN_JWT_TOKEN(HttpStatus.FORBIDDEN, "JWT: 해당 리소스에 접근할 권한이 없습니다.(토큰은 유효하지만 권한 없음)"),
    NOT_EXIST_JWT_TOKEN(HttpStatus.UNAUTHORIZED, "JWT: 토큰 없는 요청 입니다."),
    INVALID_JWT_TOKEN(HttpStatus.UNAUTHORIZED, "JWT: 유효하지 않은 토큰 입니다.");

    private HttpStatus httpStatus;
    private String errorMsg;

    @Override
    public String toString(){
        return this.name();
    }

    @Override
    public String getErrorCodeName(){
        return this.name();
    }

    @Override
    public HttpStatus getHttpStatus() {
        return this.httpStatus;
    }

    @Override
    public String getErrorMsg() {
        return this.errorMsg;
    }
}
