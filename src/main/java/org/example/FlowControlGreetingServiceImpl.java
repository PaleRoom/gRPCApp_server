package org.example;

import com.example.grpc.GreetingServiceGrpc;
import com.example.grpc.GreetingServiceOuterClass;

import io.grpc.stub.ServerCallStreamObserver;
import io.grpc.stub.StreamObserver;

public class FlowControlGreetingServiceImpl extends GreetingServiceGrpc.GreetingServiceImplBase {
    public void streaming(GreetingServiceOuterClass.HelloRequest request,
                          StreamObserver<GreetingServiceOuterClass.HelloResponse> responseStreamObserver) {
        /**
         * Реализация FlowControl для избежания переполнения памяти при потоковой передаче
         * Часть 1 - приведение типа StreamObserver к ServerCallSreamObserver c методом isReady()
         */
        ServerCallStreamObserver<GreetingServiceOuterClass.HelloResponse> callResponseStreamObserver
                = (ServerCallStreamObserver<GreetingServiceOuterClass.HelloResponse>) responseStreamObserver;
        callResponseStreamObserver.disableAutoInboundFlowControl();
        /**
         *
         */

        System.out.println(request);
        long waitingCount = 0;
        for (long i = 0; i < 1500000; i++) {
            GreetingServiceOuterClass.HelloResponse response = GreetingServiceOuterClass.
                    HelloResponse.newBuilder()
                    .setGreeting("Hello from server, " + request.getName() + " [" + (i + 1) + "]")
                    .build();
            /**
             * Часть 2 проверка на готовность к обработке следующего сообщения
             *         и ожидание в случае неготовности
              */
            while (!callResponseStreamObserver.isReady()) {
                try {
                    Thread.sleep(1);
                    waitingCount++;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            /**
             *
             */


            callResponseStreamObserver.onNext(response);
            System.out.println("Success " + i);

        }
        responseStreamObserver.onCompleted();
        System.out.println("Работа потока завершена штатно");
        System.out.println("Ожидание готовности сервера вызывалось " + waitingCount + "раз");
    }
}
