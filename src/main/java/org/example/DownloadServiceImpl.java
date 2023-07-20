package org.example;

import com.example.grpc.DownloadServiceGrpc;
import com.example.grpc.DownloadServiceOuterClass;
import com.google.protobuf.ByteString;
import io.grpc.Status;
import io.grpc.stub.ServerCallStreamObserver;
import io.grpc.stub.StreamObserver;

import java.io.BufferedInputStream;
import java.io.FileInputStream;

public class DownloadServiceImpl extends DownloadServiceGrpc.DownloadServiceImplBase {
    @Override
    public void downloadFile(DownloadServiceOuterClass.DownloadRequest request,
                             StreamObserver<DownloadServiceOuterClass.DownloadResponse> responseStreamObserver) {

        /**
         * Реализация FlowControl для избежания переполнения памяти при потоковой передаче
         * Часть 1 - приведение типа StreamObserver к ServerCallSreamObserver c методом isReady()
         */
        ServerCallStreamObserver<DownloadServiceOuterClass.DownloadResponse> callResponseStreamObserver
                = (ServerCallStreamObserver<DownloadServiceOuterClass.DownloadResponse>) responseStreamObserver;
        callResponseStreamObserver.disableAutoInboundFlowControl();
        /**
         *
         */

        try {
            System.out.println(request.getRequestPayload());

            FileInputStream fis = new FileInputStream("src/main/resources/TestFile.tmp");
            BufferedInputStream bis = new BufferedInputStream(fis);
            /**
             * Отправка исходного файла частями размером в  bufferSize
             */
            int bufferSize = 4000 * 1024;// 1K
            byte[] buffer = new byte[bufferSize];
            int length;
            while ((length = bis.read(buffer, 0, bufferSize)) != -1) {
                /**
                 * Часть 2 проверка на готовность к обработке следующего сообщения
                 *         и ожидание в случае неготовности
                 */
//                while (!callResponseStreamObserver.isReady()) {
//                    try {
//                        Thread.sleep(1);
////                        waitingCount++;
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                }
                /**
                 *
                 */

//                try {
//                    Thread.sleep(5000);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }

                callResponseStreamObserver
                        .onNext(DownloadServiceOuterClass.DownloadResponse.newBuilder()
                                .setFilePayload(ByteString.copyFrom(buffer, 0, length))
                                .setSize(bufferSize) //
                                .build());
            }
            fis.close();
            callResponseStreamObserver.onCompleted();
        } catch (Throwable e) {
            e.printStackTrace();
            callResponseStreamObserver.onError(Status.ABORTED
                    .withDescription("Unable to acquire the image " + request.getRequestPayload())
                    .withCause(e)
                    .asException());
        }
    }
}

