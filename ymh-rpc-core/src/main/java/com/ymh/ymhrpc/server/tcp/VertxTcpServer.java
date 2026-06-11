package com.ymh.ymhrpc.server.tcp;

import com.ymh.ymhrpc.server.HttpServer;
import io.vertx.core.Vertx;
import io.vertx.core.net.NetServer;
import lombok.extern.slf4j.Slf4j;

/**
 * Vertx TCP 服务器
 */
@Slf4j
public class VertxTcpServer implements HttpServer {
    @Override
    public void doStart(int port) {
        Vertx vertx = Vertx.vertx();

        // 创建TCP服务器
        NetServer server = vertx.createNetServer();
        server.connectHandler(new TcpServerHandler());

//        server.connectHandler(socket -> {
//            String testMessage="Hello,server! Hello,server! Hello,server!";
//            int messageLength = testMessage.getBytes().length;

//            RecordParser parser = RecordParser.newFixed(8);
//            parser.setOutput(new Handler<Buffer>() {
//                int size=-1;
//                Buffer resultBuffer=Buffer.buffer();
//
//                @Override
//                public void handle(Buffer buffer) {
//                    if(size==-1){
//                        size=buffer.getInt(4);
//                        parser.fixedSizeMode(size);
//                        resultBuffer.appendBuffer(buffer);
//                    }else{
//                        resultBuffer.appendBuffer(buffer);
//                        System.out.println(resultBuffer.toString());
//
//                        parser.fixedSizeMode(8);
//                        size=-1;
//                        resultBuffer=Buffer.buffer();
//
//                    }
//                }
//            });
//            socket.handler(buffer -> {


//                if(buffer.getBytes().length < messageLength) {
//                    System.out.println("半包，length="+buffer.getBytes().length);
//                    return;
//                }
//
//                if(buffer.getBytes().length > messageLength) {
//                    System.out.println("粘包，length="+buffer.getBytes().length);
//                    return;
//                }
//
//                String str = new String(buffer.getBytes(0, messageLength));
//                System.out.println(str);
//
//                if(testMessage.equals(str)) {
//                    System.out.println("Good.");
//                }
//            });
//            socket.handler(parser);
//        });

//        //处理请求
//        server.connectHandler(socket->{
//
//            //处理链接
//            socket.handler(buffer -> {
//                //处理接收到的字节数据
//                byte[] requestData = buffer.getBytes();
//
//                byte[] responseData=handleRequest(requestData);
//
//                //发送响应
//                socket.write(Buffer.buffer(responseData));
//            });
//        });

        //启动TCP服务器并监听指定端口
        server.listen(port,result->{
            if(result.succeeded()){
                log.info("TCP server started on port"+port);
            }else {
                log.info("Failed to start TCP server:"+result.cause());
            }
        });
    }

//    private byte[] handleRequest(byte[] requestData) {
//        return "Hello, Client!".getBytes();
//    }

//    public static void main(String[] args) {
//        new VertxTcpServer().doStart(8888);
//    }
}
