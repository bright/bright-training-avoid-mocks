package com.example.training;

import com.example.training.config.RedisConfig;
import com.redis.testcontainers.RedisContainer;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.ContextConfiguration;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import static com.redis.testcontainers.RedisContainer.DEFAULT_IMAGE_NAME;

@ContextConfiguration(initializers = WithRedis.ContextInitializer.class)
@Retention(RetentionPolicy.RUNTIME)
public @interface WithRedis {

  class ContextInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {
      var redisContainer = new RedisContainer(DEFAULT_IMAGE_NAME);
      // cleanup container on context close
      applicationContext.getBeanFactory().registerSingleton("redisContainer", redisContainer);

      redisContainer.start();

      RedisConfig.setRedisHost(redisContainer.getHost());
      RedisConfig.setRedisPort(redisContainer.getMappedPort(6379));

    }
  }
}
