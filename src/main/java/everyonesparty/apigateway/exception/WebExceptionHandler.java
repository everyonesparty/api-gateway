package everyonesparty.apigateway.exception;

import everyonesparty.apigateway.exception.error.CommonError;
import everyonesparty.apigateway.exception.error.RestError;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;


@Slf4j
public class WebExceptionHandler implements ErrorWebExceptionHandler {

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
        log.warn("in GATEWAY exception handler : " + ex);

        RestError restError = getRestError(ex);

        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(restError.getHttpStatus());
        response.getHeaders().add("Content-Type", "application/json");

        byte[] bytes = errorCodeMaker(restError.getErrorCodeName(), restError.getErrorMsg())
                .getBytes(StandardCharsets.UTF_8);
        DataBuffer buffer = response.bufferFactory().wrap(bytes);
        return response.writeWith(Flux.just(buffer));
    }

    private String errorCodeMaker(String errorCode, String errorMessage) {
        return "{\"errorCode\":\"" + errorCode +"\", \"errorMessage\":\"" + errorMessage + "\"}";
    }

    private RestError getRestError(Throwable ex) {
        if(ex instanceof LogicalRuntimeException){
            LogicalRuntimeException logicalRuntimeException = (LogicalRuntimeException) ex;
            return logicalRuntimeException.getRestError();
        }
        else{
            return CommonError.INTERNAL_SERVER_ERROR;
        }
    }
}