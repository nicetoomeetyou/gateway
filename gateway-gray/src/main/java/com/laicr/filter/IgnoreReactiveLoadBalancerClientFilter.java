//package com.laicr.filter;
//
//import org.springframework.cloud.gateway.config.LoadBalancerProperties;
//import org.springframework.cloud.gateway.filter.GatewayFilterChain;
//import org.springframework.cloud.gateway.filter.ReactiveLoadBalancerClientFilter;
//import org.springframework.cloud.loadbalancer.support.LoadBalancerClientFactory;
//import org.springframework.web.server.ServerWebExchange;
//import reactor.core.publisher.Mono;
//
//public class IgnoreReactiveLoadBalancerClientFilter extends ReactiveLoadBalancerClientFilter {
//    public final static String ATTRIBUTE_IGNORE_TEST_GLOBAL_FILTER = "@ignoreReactiveLoadBalancerClientFilter";
//
//    public IgnoreReactiveLoadBalancerClientFilter(LoadBalancerClientFactory clientFactory, LoadBalancerProperties properties) {
//        super(clientFactory, properties);
//    }
//
//    @Override
//    public int getOrder() {
//        //在GatewayFilter之后执行
//        return 10;
//    }
//
//    @Override
//    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
//        if (exchange.getAttribute(ATTRIBUTE_IGNORE_TEST_GLOBAL_FILTER) != null) {
//            return chain.filter(exchange);
//        }else {
//            return super.filter(exchange,chain);
//        }
//    }
//
//}
