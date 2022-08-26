package everyonesparty.apigateway.exception.error;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;


/***
 * 미리 정의해 둔 에러들
 */
@AllArgsConstructor
public enum CommonError implements RestError {

    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 오류 입니다.");

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
