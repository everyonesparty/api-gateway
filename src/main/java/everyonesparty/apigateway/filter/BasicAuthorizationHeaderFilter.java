package everyonesparty.apigateway.filter;

import everyonesparty.apigateway.exception.LogicalRuntimeException;
import everyonesparty.apigateway.exception.error.JwtError;
import everyonesparty.apigateway.util.jwt.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;

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
            ServerHttpRequest request =  exchange.getRequest();

            if(!request.getHeaders().containsKey(HttpHeaders.AUTHORIZATION)){
                throw new LogicalRuntimeException(JwtError.NOT_EXIST_JWT_TOKEN);
            }

            String authorizationHeader = request.getHeaders().get(HttpHeaders.AUTHORIZATION).get(0);
            String jwt = authorizationHeader.replace("Bearer","");
            if(!jwtUtil.IsValidJwt(jwt)){
                throw new LogicalRuntimeException(JwtError.INVALID_JWT_TOKEN);
            }
            return chain.filter(exchange);
        };
    }
}
