package org.example;

import com.example.grpc.GreetingServiceGrpc;
import com.example.grpc.GreetingServiceOuterClass;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;

public class GreetingServiceImpl extends GreetingServiceGrpc.GreetingServiceImplBase {
    public void greeting(GreetingServiceOuterClass.HelloRequest request,
                         StreamObserver<GreetingServiceOuterClass.HelloResponse> responseObserver) {

        System.out.println(request);


        /**
         * цикл и try-catch используютлся для обеспечения потоковой передачи данных
         */
        for (long i = 0; i < 10; i++) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            /**
             * Создание экземпляра ответа от сервера огласно описанным в Hello Response полям
             */
            GreetingServiceOuterClass.HelloResponse response = GreetingServiceOuterClass.
                    HelloResponse.newBuilder()
                    .setGreeting("Hello from server, " + request.getName() + " [" + (i + 1) + "]")
                    .build();

            if (i == 5) {
                /**
                 * Задаем Тип исключения, Cтатус и его описание для отправки на клиент
                 */
                responseObserver.onError(new StatusRuntimeException(
                        Status.INVALID_ARGUMENT.withDescription("Illegal loop value: " + i)
                ));
                System.out.println("Error");
                break;
            } else {
                /**
                 * Пересылка клиенту ответа
                 */

                responseObserver.onNext(response);
                System.out.println("Success");
            }
        }
        /**
         * закрытие обсервера для передачи данных
         */
        responseObserver.onCompleted();
    }
}
