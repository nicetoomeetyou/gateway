//package com.laicr.filter;
//
//import org.springframework.cloud.gateway.filter.GatewayFilter;
//import org.springframework.cloud.gateway.filter.GatewayFilterChain;
//import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
//import org.springframework.stereotype.Component;
//import org.springframework.web.server.ServerWebExchange;
//import reactor.core.publisher.Mono;
//
//
//@Component
//public class IgnoreReactiveLoadBalancerClientFilterFactory extends AbstractGatewayFilterFactory<Object> {
//
//
//    @Override
//    public GatewayFilter apply(Object config) {
//        return this::filter;
//    }
//
//    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
//        exchange.getAttributes().put(IgnoreReactiveLoadBalancerClientFilter.ATTRIBUTE_IGNORE_TEST_GLOBAL_FILTER, true);
//        return chain.filter(exchange);
//    }
//
//    @Override
//    public String name() {
//        return "IgnoreReactiveLoadBalancerClientFilter";
//    }
//}
