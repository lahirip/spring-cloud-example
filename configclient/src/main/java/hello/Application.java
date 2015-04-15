package hello;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 *
 * Client is a Spring Boot application with the dependency on org.springframework.cloud:spring-cloud-config-client,
 * which enables to connect Config Server automatically and use properties via Config Server.
 * Also add the dependency on org.springframework.boot:spring-boot-starter-actuator.
 *
 * As long as Spring Boot Actuator and Spring Config Client are on the classpath
 * any Spring Boot application will try to contact a config server on http://localhost:8888 (the default value of spring.cloud.config.uri):
 *
 *  1. Application name of the client can be defined as spring.application.name key in bootstrap
 *  2. This class which is the entry point for Client
 *  3. We set up the implement a simple controller uses bar property
 *  4. You can find properties are obtained from Config Server by listing PropertiesSource using the feature of Spring Boot Actuator (http://localhost:8080/env)
 *  5. You can also see a single property. http://localhost:8080/env/bar
 *  6.When the config value for "bar" is changed in GIT  the client does not see it until REFRESH is called http://localhost:8080/refresh  (Check this)
 *  7.
 *
 */

@SpringBootApplication
public class Application {



    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}