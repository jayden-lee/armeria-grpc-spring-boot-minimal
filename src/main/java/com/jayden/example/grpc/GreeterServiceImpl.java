package com.jayden.example.grpc;

import com.jayden.helloworld.GreeterGrpc.GreeterImplBase;
import com.jayden.helloworld.HelloReply;
import com.jayden.helloworld.HelloRequest;
import io.grpc.stub.StreamObserver;

public class GreeterServiceImpl extends GreeterImplBase {

  @Override
  public void sayHello(HelloRequest request, StreamObserver<HelloReply> responseObserver) {
    HelloReply reply = HelloReply.newBuilder()
        .setMessage("hello")
        .build();
    responseObserver.onNext(reply);
    responseObserver.onCompleted();
  }
}
