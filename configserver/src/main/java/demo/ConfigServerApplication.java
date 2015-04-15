package demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.config.server.EnableConfigServer;
import org.springframework.context.annotation.Configuration;


/**
 * The Config Server runs best as a standalone application,
 * but if you need to you can embed it in another application. Just use the @EnableConfigServer annotation
 *
 * Building Config Server is very simple.
 *   1. Add dependency on org.springframework.cloud:spring-cloud-config-server
 *   2. put EnableConfigServer to the entry point class.
 *   3. Describe the configuration where to fetch the configuration in bootstrap
 *
 *   By accessing http://localhost:8888/{name}/{env}/{label}, you can get the configuration for each environment(profile) of each application.
 *
 *  By accessing http://localhost:8888/foo/default ,you can get the configuration of foo.properties.
 *  By accessing http://localhost:8888/foo/development ,you can get the configuration foo.properties overwritten by foo-development.properties.
 *
 *
 */

//@EnableDiscoveryClient
@Configuration
@EnableAutoConfiguration
@EnableConfigServer
public class ConfigServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(ConfigServerApplication.class, args);
    }

}
