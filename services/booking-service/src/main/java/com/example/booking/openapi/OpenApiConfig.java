package com.example.booking.openapi;

import io.swagger.v3.oas.models.parameters.Parameter;
import io.swagger.v3.oas.models.media.StringSchema;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class OpenApiConfig {

  @Bean
  public OpenApiCustomizer bookingCommonHeadersCustomizer() {
    return openApi -> {
      if (openApi.getPaths() == null) return;

      openApi.getPaths().forEach((path, pathItem) -> {
        pathItem.readOperations().forEach(op -> {
          if (op.getParameters() == null) op.setParameters(new ArrayList<>());

          // X-Trace-Id on all endpoints
          if (op.getParameters().stream().noneMatch(p -> "X-Trace-Id".equalsIgnoreCase(p.getName()))) {
            op.getParameters().add(header("X-Trace-Id", "Optional request trace id (propagates to logs and responses)."));
          }

          // X-Device-Id only for booking/payments endpoints (guest mode)
          if (path.startsWith("/v1/bookings") || path.startsWith("/v1/payments")) {
            if (op.getParameters().stream().noneMatch(p -> "X-Device-Id".equalsIgnoreCase(p.getName()))) {
              op.getParameters().add(header("X-Device-Id",
                  "Optional device id for guest mode. Provide either Authorization: Bearer <token> OR X-Device-Id."));
            }

            // Mark bearer auth as OPTIONAL (guest can use X-Device-Id).
            // In OpenAPI, optional security can be represented as: [ {}, {bearerAuth: []} ]
            op.setSecurity(List.of(
                new SecurityRequirement(), // no auth required
                new SecurityRequirement().addList("bearerAuth")
            ));
          }
        });
      });
    };
  }

  private static Parameter header(String name, String description) {
    Parameter p = new Parameter();
    p.setIn("header");
    p.setName(name);
    p.setRequired(false);
    p.setDescription(description);
    p.setSchema(new StringSchema());
    return p;
  }
}
