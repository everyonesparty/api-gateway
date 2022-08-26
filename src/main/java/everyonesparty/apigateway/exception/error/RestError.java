package everyonesparty.apigateway.exception.error;

import org.springframework.http.HttpStatus;

public interface RestError {

    public String getErrorCodeName();

    public HttpStatus getHttpStatus();

    public String getErrorMsg();

}
