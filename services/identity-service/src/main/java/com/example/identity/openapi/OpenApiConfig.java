// package com.example.identity.openapi;

// import io.swagger.v3.oas.models.parameters.Parameter;
// import io.swagger.v3.oas.models.media.StringSchema;
// import org.springdoc.core.customizers.OpenApiCustomizer;
// import org.springframework.context.annotation.Bean;
// import org.springframework.context.annotation.Configuration;

// import java.util.ArrayList;

// @Configuration
// public class OpenApiConfig {

//   @Bean
//   public OpenApiCustomizer identityCommonHeadersCustomizer() {
//     return openApi -> {
//       if (openApi.getPaths() == null) return;

//       openApi.getPaths().values().forEach(pathItem ->
//           pathItem.readOperations().forEach(op -> {
//             if (op.getParameters() == null) op.setParameters(new ArrayList<>());

//             // Avoid duplicates if called multiple times
//             if (op.getParameters().stream().noneMatch(p -> "X-Trace-Id".equalsIgnoreCase(p.getName()))) {
//               op.getParameters().add(header("X-Trace-Id", "Optional request trace id (propagates to logs and responses)."));
//             }
//           })
//       );
//     };
//   }

//   private static Parameter header(String name, String description) {
//     Parameter p = new Parameter();
//     p.setIn("header");
//     p.setName(name);
//     p.setRequired(false);
//     p.setDescription(description);
//     p.setSchema(new StringSchema());
//     return p;
//   }
// }
