package com.example.identity.user;

import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Instant;
import java.util.UUID;

@Configuration
public class DemoUserConfig {

  @Bean
  ApplicationRunner seedDemoUser(UserRepository users, PasswordEncoder encoder) {
    return args -> {
      String email = "demo@demo.com";
      if (users.findByEmail(email).isPresent()) return;

      UserEntity u = new UserEntity(
          UUID.randomUUID(),
          email,
          encoder.encode("demo12345"),
          Instant.now()
      );
      users.save(u);
    };
  }
}
