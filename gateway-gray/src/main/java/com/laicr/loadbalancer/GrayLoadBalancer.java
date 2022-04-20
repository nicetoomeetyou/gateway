package com.laicr.loadbalancer;

import com.google.common.collect.Maps;
import com.laicr.weight.model.WeightMeta;
import com.laicr.weight.util.WeightRandomUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.reactive.DefaultResponse;
import org.springframework.cloud.client.loadbalancer.reactive.EmptyResponse;
import org.springframework.cloud.client.loadbalancer.reactive.Request;
import org.springframework.cloud.client.loadbalancer.reactive.Response;
import org.springframework.cloud.loadbalancer.core.*;
import org.springframework.http.HttpHeaders;
import org.springframework.util.CollectionUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.*;

public class GrayLoadBalancer implements ReactorServiceInstanceLoadBalancer {
    private static final Log log = LogFactory.getLog(GrayLoadBalancer.class);
    private ObjectProvider<ServiceInstanceListSupplier> serviceInstanceListSupplierProvider;
    private  String serviceId;




    public GrayLoadBalancer(ObjectProvider<ServiceInstanceListSupplier> serviceInstanceListSupplierProvider, String serviceId) {
        this.serviceId = serviceId;
        this.serviceInstanceListSupplierProvider = serviceInstanceListSupplierProvider;
    }


    @Override
    public Mono<Response<ServiceInstance>> choose(Request request) {
        HttpHeaders headers = (HttpHeaders) request.getContext();
        if (this.serviceInstanceListSupplierProvider != null) {
            ServiceInstanceListSupplier supplier = (ServiceInstanceListSupplier)this.serviceInstanceListSupplierProvider.getIfAvailable(NoopServiceInstanceListSupplier::new);
            return ((Flux)supplier.get()).next().map(list->getInstanceResponse((List<ServiceInstance>)list,headers));
        }

        return null;


    }



    private Response<ServiceInstance> getInstanceResponse(List<ServiceInstance> instances,HttpHeaders headers) {
        if (instances.isEmpty()) {
            return getServiceInstanceEmptyResponse();
        } else {
            return getServiceInstanceResponseByVersion(instances,headers);
        }
    }

    /**
     * 根据版本进行分发
     * @param instances
     * @param headers
     * @return
     */
    private Response<ServiceInstance> getServiceInstanceResponseByVersion(List<ServiceInstance> instances, HttpHeaders headers) {
        if (CollectionUtils.isEmpty(instances)){
            return getServiceInstanceEmptyResponse();
        }

        Map<ServiceInstance,Integer> weightMap = new HashMap<>();
        Map<ServiceInstance,Integer> grayWeightMap = new HashMap<>();
        String gray=headers.getFirst("gray");
        String stationNo=headers.getFirst("COURIER_STATION_NO");

        String maxVersion="";
//        for (ServiceInstance instance : instances) {
//            String version=instance.getMetadata().get("version");
//            if (StringUtils.compare(version,maxVersion)>0){
//                maxVersion=version;
//            }
//
//        }
        maxVersion=instances.stream().map(ins->ins.getMetadata().get("version")).max(String::compareTo).orElse("");

        for (ServiceInstance instance : instances) {
            String version=instance.getMetadata().get("version");
            if (StringUtils.compare(version,maxVersion)==0){
                grayWeightMap.put(instance,1);
            }else {
                weightMap.put(instance,1);
            }

        }

        if(ObjectUtils.isEmpty(weightMap) && ObjectUtils.isEmpty(grayWeightMap)){
            return getServiceInstanceEmptyResponse();
        }

        WeightMeta<ServiceInstance> weightMeta = WeightRandomUtils.buildWeightMeta(Boolean.parseBoolean(gray)&&Boolean.parseBoolean(stationNo)?grayWeightMap:weightMap);

        ServiceInstance serviceInstance = weightMeta.random();
        if(ObjectUtils.isEmpty(serviceInstance)){
            return getServiceInstanceEmptyResponse();
        }
        return new DefaultResponse(serviceInstance);
    }

//    private Response<ServiceInstance> getServiceInstanceResponseByVersion(List<ServiceInstance> instances, HttpHeaders headers) {
//        String versionNo = headers.getFirst("version");
//        System.out.println(versionNo);
//        Map<String,String> versionMap = new HashMap<>();
//        versionMap.put("version",versionNo);
//        final Set<Map.Entry<String,String>> attributes =
//                Collections.unmodifiableSet(versionMap.entrySet());
//        ServiceInstance serviceInstance = null;
//        for (ServiceInstance instance : instances) {
//            Map<String,String> metadata = instance.getMetadata();
//            if(metadata.entrySet().containsAll(attributes)){
//                serviceInstance = instance;
//                break;
//            }
//        }
//
//        if(ObjectUtils.isEmpty(serviceInstance)){
//            return getServiceInstanceEmptyResponse();
//        }
//        return new DefaultResponse(serviceInstance);
//    }

    /**
     *
     * 根据在nacos中配置的权重值，进行分发
     * @param instances
     *
     * @return
     */
    private Response<ServiceInstance> getServiceInstanceResponseWithWeight(List<ServiceInstance> instances,HttpHeaders headers) {
        Map<ServiceInstance,Integer> weightMap = new HashMap<>();
        for (ServiceInstance instance : instances) {
            Map<String,String> metadata = instance.getMetadata();
            System.out.println(metadata.get("version")+"-->weight:"+metadata.get("weight"));
            if(metadata.containsKey("weight")){
                weightMap.put(instance,Integer.valueOf(metadata.get("weight")));
            }
        }
        WeightMeta<ServiceInstance> weightMeta = WeightRandomUtils.buildWeightMeta(weightMap);
        if(ObjectUtils.isEmpty(weightMeta)){
            return getServiceInstanceEmptyResponse();
        }
        ServiceInstance serviceInstance = weightMeta.random();
        if(ObjectUtils.isEmpty(serviceInstance)){
            return getServiceInstanceEmptyResponse();
        }
        System.out.println(serviceInstance.getMetadata().get("version"));
        return new DefaultResponse(serviceInstance);
    }

    private Response<ServiceInstance> getServiceInstanceEmptyResponse() {
        log.warn("No servers available for service: " + this.serviceId);
        return new EmptyResponse();
    }
}