package com.ymh.ymhrpc.proxy;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.ymh.ymhrpc.constant.RpcConstant;
import com.ymh.ymhrpc.fault.retry.RetryStrategy;
import com.ymh.ymhrpc.fault.retry.RetryStrategyFactory;
import com.ymh.ymhrpc.fault.tolerant.TolerantStrategy;
import com.ymh.ymhrpc.fault.tolerant.TolerantStrategyFactory;
import com.ymh.ymhrpc.loadbalancer.LoadBalanceFactory;
import com.ymh.ymhrpc.loadbalancer.LoadBalancer;
import com.ymh.ymhrpc.RpcApplication;
import com.ymh.ymhrpc.config.RpcConfig;
import com.ymh.ymhrpc.model.RpcRequest;
import com.ymh.ymhrpc.model.RpcResponse;
import com.ymh.ymhrpc.model.ServiceMetaInfo;
import com.ymh.ymhrpc.registry.Registry;
import com.ymh.ymhrpc.registry.RegistryFactory;
import com.ymh.ymhrpc.serializer.Serializer;
import com.ymh.ymhrpc.serializer.SerializerFactory;
import com.ymh.ymhrpc.server.tcp.VertxTcpClient;

import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 服务代理（JDK 动态代理）
 */
public class ServiceProxy implements InvocationHandler {

    /**
     * 调用代理
     *
     * @return
     * @throws Throwable
     */
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        String serviceName = method.getDeclaringClass().getName();
        //发请求
        RpcRequest rpcRequest = RpcRequest.builder()
                .serviceName(serviceName)
                .methodName(method.getName())
                .parameterTypes(method.getParameterTypes())
                .args(args)
                .build();

        // 从注册中心获取服务提供者请求地址
        RpcConfig rpcConfig = RpcApplication.getRpcConfig();
        Registry registry = RegistryFactory.getInstance(rpcConfig.getRegistryConfig().getRegistry());
        ServiceMetaInfo serviceMetaInfo = new ServiceMetaInfo();
        serviceMetaInfo.setServiceName(serviceName);
        serviceMetaInfo.setServiceVersion(RpcConstant.DEFAULT_SERVICE_VERSION);
        List<ServiceMetaInfo> serviceMetaInfoList = registry.serviceDiscovery(serviceMetaInfo.getServiceKey());
        if (CollUtil.isEmpty(serviceMetaInfoList)) {
            throw new RuntimeException("暂无服务地址");
        }
//        ServiceMetaInfo selectedServiceMetaInfo = serviceMetaInfoList.get(0);


        // 负载均衡
        LoadBalancer loadBalancer = LoadBalanceFactory.getInstance(rpcConfig.getLoadBalancer());
        Map<String,Object> requestParams=new HashMap<>();
        requestParams.put("methodName",rpcRequest.getMethodName());
        ServiceMetaInfo selectedServiceMetaInfo = loadBalancer.select(requestParams, serviceMetaInfoList);

        System.out.println("这次请求的端口是："+selectedServiceMetaInfo.getServicePort());

        // 使用容错机制
        RpcResponse rpcResponse;
        try {
            // 重试机制
            RetryStrategy retryStrategy = RetryStrategyFactory.getInstance(rpcConfig.getRetryStrategy());
            rpcResponse = retryStrategy.doRetry(() ->
                    VertxTcpClient.doRequest(rpcRequest, selectedServiceMetaInfo)
            );
        } catch (Exception e) {
            // 容错机制
            TolerantStrategy tolerantStrategy = TolerantStrategyFactory.getInstance(rpcConfig.getTolerantStrategy());
            rpcResponse=tolerantStrategy.doTolerant(null,e);
        }

        //发送TCP 请求
        return rpcResponse.getData();
    }

    /**
     * http 请求
     * @param selectedServiceMetaInfo
     * @param bodyBytes
     * @return
     * @throws IOException
     */
    private static RpcResponse doHttpRequest(ServiceMetaInfo selectedServiceMetaInfo, byte[] bodyBytes) throws IOException {
        final Serializer serializer = SerializerFactory.getInstance(RpcApplication.getRpcConfig().getSerializer());
        // 发送HTTP请求
        try (HttpResponse httpResponse = HttpRequest.post(selectedServiceMetaInfo.getServiceAddress())
                .body(bodyBytes)
                .execute()) {
            byte[] result = httpResponse.bodyBytes();
            RpcResponse rpcResponse = serializer.deserialize(result, RpcResponse.class);
            return rpcResponse;
        }
    }
}
