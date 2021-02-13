package com.jayden.example.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.jayden.example.grpc.GreeterServiceImpl;
import com.linecorp.armeria.common.grpc.GrpcMeterIdPrefixFunction;
import com.linecorp.armeria.common.grpc.GrpcSerializationFormats;
import com.linecorp.armeria.server.TransientHttpService;
import com.linecorp.armeria.server.docs.DocService;
import com.linecorp.armeria.server.grpc.GrpcService;
import com.linecorp.armeria.server.healthcheck.HealthCheckService;
import com.linecorp.armeria.server.logging.AccessLogWriter;
import com.linecorp.armeria.server.logging.LoggingService;
import com.linecorp.armeria.server.metric.MetricCollectingService;
import com.linecorp.armeria.spring.ArmeriaServerConfigurator;
import io.grpc.protobuf.services.ProtoReflectionService;
import io.micrometer.core.instrument.MeterRegistry;

@Configuration
public class ArmeriaConfig {

  private MeterRegistry meterRegistry;

  public ArmeriaConfig(MeterRegistry meterRegistry) {
    this.meterRegistry = meterRegistry;
  }

  @Bean
  public ArmeriaServerConfigurator armeriaServerConfigurator() {
    return builder -> {
      builder.service("/health", HealthCheckService.of().decorate(TransientHttpService.newDecorator()));
      builder.serviceUnder("/docs", new DocService());

      builder.decorator(LoggingService.newDecorator());
      builder.decorator(MetricCollectingService.newDecorator(GrpcMeterIdPrefixFunction.of("grpc.service")));

      builder.accessLogWriter(AccessLogWriter.combined(), false);

      builder.service(
          GrpcService.builder()
              .addService(new GreeterServiceImpl())
              .addService(ProtoReflectionService.newInstance())
              .supportedSerializationFormats(GrpcSerializationFormats.values())
              .enableUnframedRequests(true)
              .useBlockingTaskExecutor(true)
              .build());

      builder.meterRegistry(meterRegistry);
    };
  }
}
