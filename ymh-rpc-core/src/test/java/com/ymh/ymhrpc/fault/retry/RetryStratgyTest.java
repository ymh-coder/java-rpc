package com.ymh.ymhrpc.fault.retry;

import com.ymh.ymhrpc.model.RpcResponse;
import org.junit.Test;

public class RetryStratgyTest {
    RetryStrategy retryStrategy=new FixedIntervalRetryStrategy();

    @Test
    public void doRetry(){
        try {
            RpcResponse rpcResponse = retryStrategy.doRetry(() -> {
                System.out.println("测试重试");
                throw new RuntimeException("模拟重试失败");
            });
            System.out.println(rpcResponse);
        }catch (Exception e){
            System.out.println("重试多次失败");
            e.printStackTrace();
        }
    }
}
