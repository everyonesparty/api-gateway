package everyonesparty.apigateway.filter;

import everyonesparty.apigateway.exception.LogicalRuntimeException;
import everyonesparty.apigateway.exception.error.JwtError;
import everyonesparty.apigateway.util.jwt.JwtUtil;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import java.util.Optional;

@Component
@Slf4j
public class BasicAuthorizationHeaderFilter extends AbstractGatewayFilterFactory<BasicAuthorizationHeaderFilter.Config> {

    @Autowired
    private JwtUtil jwtUtil;

    public BasicAuthorizationHeaderFilter(Environment env){
        super(Config.class);
    }

    public static class Config{

    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            String jwtString = getJwtString(exchange.getRequest());

            Optional<Claims> parsedJwt = parseClaims(jwtString);

            if(!jwtUtil.IsValidJwt(parsedJwt)){
                throw new LogicalRuntimeException(JwtError.INVALID_JWT_TOKEN);
            }

            ServerHttpRequest mutateRequest = addHeader(exchange, parsedJwt);
            return chain.filter(exchange.mutate().request(mutateRequest).build());
        };
    }

    private String getJwtString(ServerHttpRequest request) {
        if(!isContainsAuthorizationHeader(request)){
            throw new LogicalRuntimeException(JwtError.NOT_EXIST_JWT_TOKEN);
        }
        return request.getHeaders().get(HttpHeaders.AUTHORIZATION).get(0);
    }

    private Optional<Claims> parseClaims(String authorizationHeader) {
        String jwt = authorizationHeader.replace("Bearer","");
        return jwtUtil.parseJwt(jwt);
    }

    private boolean isContainsAuthorizationHeader(ServerHttpRequest request) {
        return request.getHeaders().containsKey(HttpHeaders.AUTHORIZATION);
    }

    /***
     * > jwt 속에 들어있는 정보를 까서 뒷단의 서버로 전달
     * @param exchange
     * @param parsedJwt
     * @return
     */
    private ServerHttpRequest addHeader(ServerWebExchange exchange, Optional<Claims> parsedJwt) {
        ServerHttpRequest request = exchange.getRequest();
        return parsedJwt
                .flatMap(ele -> jwtUtil.getSubject(ele))
                .map(subject -> exchange.getRequest().mutate()
                        .header("kakaId", subject)
                        .build()
                ).orElse(request);
    }
}
