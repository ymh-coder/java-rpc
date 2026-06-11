package com.ymh.ymhrpc.loadbalancer;

import com.ymh.ymhrpc.spi.SpiLoader;

/**
 * 负载均衡器工厂 （工厂模式，用于获取负载均衡器对象）
 */
public class LoadBalanceFactory {
    static {
        SpiLoader.load(LoadBalancer.class);
    }

    private static final LoadBalancer DEFAULT_LOAD_BALANCE=new RoundRobinLoadBalancer();

    public static LoadBalancer getInstance(String key){
        return SpiLoader.getInstance(LoadBalancer.class,key);
    }
}
