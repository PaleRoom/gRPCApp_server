package org.example;

import io.grpc.Server;
import io.grpc.ServerBuilder;

import java.io.IOException;


public class App {
    public static void main(String[] args) throws IOException, InterruptedException {

//        Server server = ServerBuilder.forPort(1232).addService(new GreetingServiceImpl())
//      Server server = ServerBuilder.forPort(1232).addService(new FlowControlGreetingServiceImpl())

        Server server = ServerBuilder.forPort(1232).addService(new DownloadServiceImpl())
                .build();
        server.start();
        System.out.println("Server is listening for client...");
        server.awaitTermination();
    }
}
