package com.refdata.grpc;

import static io.grpc.MethodDescriptor.generateFullMethodName;

/**
 */
@javax.annotation.Generated(
    value = "by gRPC proto compiler (version 1.60.0)",
    comments = "Source: refdata.proto")
@io.grpc.stub.annotations.GrpcGenerated
public final class RefDataServiceGrpc {

  private RefDataServiceGrpc() {}

  public static final java.lang.String SERVICE_NAME = "refdata.RefDataService";

  // Static method descriptors that strictly reflect the proto.
  private static volatile io.grpc.MethodDescriptor<com.refdata.grpc.Empty,
      com.refdata.grpc.LockList> getGetAllLocksMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "GetAllLocks",
      requestType = com.refdata.grpc.Empty.class,
      responseType = com.refdata.grpc.LockList.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.refdata.grpc.Empty,
      com.refdata.grpc.LockList> getGetAllLocksMethod() {
    io.grpc.MethodDescriptor<com.refdata.grpc.Empty, com.refdata.grpc.LockList> getGetAllLocksMethod;
    if ((getGetAllLocksMethod = RefDataServiceGrpc.getGetAllLocksMethod) == null) {
      synchronized (RefDataServiceGrpc.class) {
        if ((getGetAllLocksMethod = RefDataServiceGrpc.getGetAllLocksMethod) == null) {
          RefDataServiceGrpc.getGetAllLocksMethod = getGetAllLocksMethod =
              io.grpc.MethodDescriptor.<com.refdata.grpc.Empty, com.refdata.grpc.LockList>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "GetAllLocks"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.refdata.grpc.Empty.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.refdata.grpc.LockList.getDefaultInstance()))
              .setSchemaDescriptor(new RefDataServiceMethodDescriptorSupplier("GetAllLocks"))
              .build();
        }
      }
    }
    return getGetAllLocksMethod;
  }

  private static volatile io.grpc.MethodDescriptor<com.refdata.grpc.Empty,
      com.refdata.grpc.PortList> getGetAllPortsMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "GetAllPorts",
      requestType = com.refdata.grpc.Empty.class,
      responseType = com.refdata.grpc.PortList.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.refdata.grpc.Empty,
      com.refdata.grpc.PortList> getGetAllPortsMethod() {
    io.grpc.MethodDescriptor<com.refdata.grpc.Empty, com.refdata.grpc.PortList> getGetAllPortsMethod;
    if ((getGetAllPortsMethod = RefDataServiceGrpc.getGetAllPortsMethod) == null) {
      synchronized (RefDataServiceGrpc.class) {
        if ((getGetAllPortsMethod = RefDataServiceGrpc.getGetAllPortsMethod) == null) {
          RefDataServiceGrpc.getGetAllPortsMethod = getGetAllPortsMethod =
              io.grpc.MethodDescriptor.<com.refdata.grpc.Empty, com.refdata.grpc.PortList>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "GetAllPorts"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.refdata.grpc.Empty.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.refdata.grpc.PortList.getDefaultInstance()))
              .setSchemaDescriptor(new RefDataServiceMethodDescriptorSupplier("GetAllPorts"))
              .build();
        }
      }
    }
    return getGetAllPortsMethod;
  }

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static RefDataServiceStub newStub(io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<RefDataServiceStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<RefDataServiceStub>() {
        @java.lang.Override
        public RefDataServiceStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new RefDataServiceStub(channel, callOptions);
        }
      };
    return RefDataServiceStub.newStub(factory, channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static RefDataServiceBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<RefDataServiceBlockingStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<RefDataServiceBlockingStub>() {
        @java.lang.Override
        public RefDataServiceBlockingStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new RefDataServiceBlockingStub(channel, callOptions);
        }
      };
    return RefDataServiceBlockingStub.newStub(factory, channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static RefDataServiceFutureStub newFutureStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<RefDataServiceFutureStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<RefDataServiceFutureStub>() {
        @java.lang.Override
        public RefDataServiceFutureStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new RefDataServiceFutureStub(channel, callOptions);
        }
      };
    return RefDataServiceFutureStub.newStub(factory, channel);
  }

  /**
   */
  public interface AsyncService {

    /**
     */
    default void getAllLocks(com.refdata.grpc.Empty request,
        io.grpc.stub.StreamObserver<com.refdata.grpc.LockList> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getGetAllLocksMethod(), responseObserver);
    }

    /**
     */
    default void getAllPorts(com.refdata.grpc.Empty request,
        io.grpc.stub.StreamObserver<com.refdata.grpc.PortList> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getGetAllPortsMethod(), responseObserver);
    }
  }

  /**
   * Base class for the server implementation of the service RefDataService.
   */
  public static abstract class RefDataServiceImplBase
      implements io.grpc.BindableService, AsyncService {

    @java.lang.Override public final io.grpc.ServerServiceDefinition bindService() {
      return RefDataServiceGrpc.bindService(this);
    }
  }

  /**
   * A stub to allow clients to do asynchronous rpc calls to service RefDataService.
   */
  public static final class RefDataServiceStub
      extends io.grpc.stub.AbstractAsyncStub<RefDataServiceStub> {
    private RefDataServiceStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected RefDataServiceStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new RefDataServiceStub(channel, callOptions);
    }

    /**
     */
    public void getAllLocks(com.refdata.grpc.Empty request,
        io.grpc.stub.StreamObserver<com.refdata.grpc.LockList> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getGetAllLocksMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void getAllPorts(com.refdata.grpc.Empty request,
        io.grpc.stub.StreamObserver<com.refdata.grpc.PortList> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getGetAllPortsMethod(), getCallOptions()), request, responseObserver);
    }
  }

  /**
   * A stub to allow clients to do synchronous rpc calls to service RefDataService.
   */
  public static final class RefDataServiceBlockingStub
      extends io.grpc.stub.AbstractBlockingStub<RefDataServiceBlockingStub> {
    private RefDataServiceBlockingStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected RefDataServiceBlockingStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new RefDataServiceBlockingStub(channel, callOptions);
    }

    /**
     */
    public com.refdata.grpc.LockList getAllLocks(com.refdata.grpc.Empty request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getGetAllLocksMethod(), getCallOptions(), request);
    }

    /**
     */
    public com.refdata.grpc.PortList getAllPorts(com.refdata.grpc.Empty request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getGetAllPortsMethod(), getCallOptions(), request);
    }
  }

  /**
   * A stub to allow clients to do ListenableFuture-style rpc calls to service RefDataService.
   */
  public static final class RefDataServiceFutureStub
      extends io.grpc.stub.AbstractFutureStub<RefDataServiceFutureStub> {
    private RefDataServiceFutureStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected RefDataServiceFutureStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new RefDataServiceFutureStub(channel, callOptions);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<com.refdata.grpc.LockList> getAllLocks(
        com.refdata.grpc.Empty request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getGetAllLocksMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<com.refdata.grpc.PortList> getAllPorts(
        com.refdata.grpc.Empty request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getGetAllPortsMethod(), getCallOptions()), request);
    }
  }

  private static final int METHODID_GET_ALL_LOCKS = 0;
  private static final int METHODID_GET_ALL_PORTS = 1;

  private static final class MethodHandlers<Req, Resp> implements
      io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ServerStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ClientStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.BidiStreamingMethod<Req, Resp> {
    private final AsyncService serviceImpl;
    private final int methodId;

    MethodHandlers(AsyncService serviceImpl, int methodId) {
      this.serviceImpl = serviceImpl;
      this.methodId = methodId;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public void invoke(Req request, io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_GET_ALL_LOCKS:
          serviceImpl.getAllLocks((com.refdata.grpc.Empty) request,
              (io.grpc.stub.StreamObserver<com.refdata.grpc.LockList>) responseObserver);
          break;
        case METHODID_GET_ALL_PORTS:
          serviceImpl.getAllPorts((com.refdata.grpc.Empty) request,
              (io.grpc.stub.StreamObserver<com.refdata.grpc.PortList>) responseObserver);
          break;
        default:
          throw new AssertionError();
      }
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public io.grpc.stub.StreamObserver<Req> invoke(
        io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        default:
          throw new AssertionError();
      }
    }
  }

  public static final io.grpc.ServerServiceDefinition bindService(AsyncService service) {
    return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
        .addMethod(
          getGetAllLocksMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
              com.refdata.grpc.Empty,
              com.refdata.grpc.LockList>(
                service, METHODID_GET_ALL_LOCKS)))
        .addMethod(
          getGetAllPortsMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
              com.refdata.grpc.Empty,
              com.refdata.grpc.PortList>(
                service, METHODID_GET_ALL_PORTS)))
        .build();
  }

  private static abstract class RefDataServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoFileDescriptorSupplier, io.grpc.protobuf.ProtoServiceDescriptorSupplier {
    RefDataServiceBaseDescriptorSupplier() {}

    @java.lang.Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return com.refdata.grpc.RefDataProto.getDescriptor();
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.ServiceDescriptor getServiceDescriptor() {
      return getFileDescriptor().findServiceByName("RefDataService");
    }
  }

  private static final class RefDataServiceFileDescriptorSupplier
      extends RefDataServiceBaseDescriptorSupplier {
    RefDataServiceFileDescriptorSupplier() {}
  }

  private static final class RefDataServiceMethodDescriptorSupplier
      extends RefDataServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoMethodDescriptorSupplier {
    private final java.lang.String methodName;

    RefDataServiceMethodDescriptorSupplier(java.lang.String methodName) {
      this.methodName = methodName;
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.MethodDescriptor getMethodDescriptor() {
      return getServiceDescriptor().findMethodByName(methodName);
    }
  }

  private static volatile io.grpc.ServiceDescriptor serviceDescriptor;

  public static io.grpc.ServiceDescriptor getServiceDescriptor() {
    io.grpc.ServiceDescriptor result = serviceDescriptor;
    if (result == null) {
      synchronized (RefDataServiceGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new RefDataServiceFileDescriptorSupplier())
              .addMethod(getGetAllLocksMethod())
              .addMethod(getGetAllPortsMethod())
              .build();
        }
      }
    }
    return result;
  }
}
