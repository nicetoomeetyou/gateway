package com.laicr.filter;

import com.laicr.util.CheckTailNoUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.support.ServerWebExchangeUtils;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class AddGrayLabelGateWayFilter implements GatewayFilter, Ordered {

    private static final Log log = LogFactory.getLog(AddGrayLabelGateWayFilter.class);

    private static final String TAIL_NO="1,2";

    private static final String COURIER_STATION_NO="111,222";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        String courierStationNo=exchange.getRequest().getQueryParams().getFirst("courierStationNo");


        if (StringUtils.isBlank(courierStationNo)){
            log.warn("param courierStationNo is null");
            throw new NullPointerException("param courierStationNo is null");
        }

        Set<String> tailNoSet= new HashSet<>(Arrays.asList(TAIL_NO.split(",")));
        Set<String> courierStationNoSet= new HashSet<>(Arrays.asList(COURIER_STATION_NO.split(",")));
        if(courierStationNoSet.contains(courierStationNo) || CheckTailNoUtil.checkTailNo(courierStationNo, tailNoSet)){
            URI url = (URI)exchange.getAttribute(ServerWebExchangeUtils.GATEWAY_REQUEST_URL_ATTR);
            ServerHttpRequest request= exchange.getRequest().mutate().path("newPath").build();
            exchange.getAttributes().put(ServerWebExchangeUtils.GATEWAY_REQUEST_URL_ATTR,request.getURI());
            return chain.filter(exchange.mutate().request(request).build());
        }
        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return 1;
    }


}
