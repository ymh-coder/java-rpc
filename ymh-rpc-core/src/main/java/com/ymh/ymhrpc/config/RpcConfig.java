package com.ymh.ymhrpc.config;

import com.ymh.ymhrpc.fault.retry.RetryStrategyKeys;
import com.ymh.ymhrpc.fault.tolerant.TolerantStrategyKeys;
import com.ymh.ymhrpc.loadbalancer.LoadBalancerKeys;
import com.ymh.ymhrpc.serializer.SerializerKeys;
import lombok.Data;

/**
 * RPC 框架全局配置
 */
@Data
public class RpcConfig {

    /**
     * 模拟调用
     */
    private boolean mock=false;

    /**
     * 名称
     */
    private String name="ymh-rpc";

    /**
     * 版本号
     */
    private String version="1.0";

    /**
     * 服务器主机名
     */
    private String serverHost="localhost";

    /**
     * 服务器端口号
     */
    private Integer serverPort=8080;

    /**
     * 序列化器
     */
    private String serializer= SerializerKeys.JDK;

    /**
     * 注册中心配置
     */
    private RegistryConfig registryConfig=new RegistryConfig();

    /**
     * 负载均衡器
     */
    private String loadBalancer= LoadBalancerKeys.ROUND_ROBIN;

    /**
     * 充实策略
     */
    private String retryStrategy= RetryStrategyKeys.NO;

    /**
     * 容错策略
     */
    private String tolerantStrategy= TolerantStrategyKeys.FAIL_FAST;

}
