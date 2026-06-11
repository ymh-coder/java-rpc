package com.ymh.ymhrpc.fault.retry;

import com.ymh.ymhrpc.loadbalancer.LoadBalancer;
import com.ymh.ymhrpc.loadbalancer.RoundRobinLoadBalancer;
import com.ymh.ymhrpc.spi.SpiLoader;

/**
 * 重试策略工厂
 */
public class RetryStrategyFactory {
    static {
        SpiLoader.load(RetryStrategy.class);
    }

    private static final RetryStrategy DEFAULT_RETRY_STRATEGY=new NoRetryStrategy();

    public static RetryStrategy getInstance(String key){
        return SpiLoader.getInstance(RetryStrategy.class,key);
    }
}
