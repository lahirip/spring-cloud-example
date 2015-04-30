# spring-cloud-example (Using Config Server)
This example show how we can use Srping Config Server and use Spring Cloud Bus to update all connected clients whenever there is an update made in config.  This example used Spring Boot to create Config Client Services.

# Quick Facts about Config Server
 - It is Spring Boot Application created with `spring.config.name=configserver`
 - By default we do not want Config Server to be a config Cleint so Config Server comes with 'spring.cloud.config.enabled=false'
 - Default Storage is GIT. 
  -  Default strategy for locating a source is to clone a git repository reads the `spring.cloud.config.server.git.uri` at start up
 -  Environment is used to enumerate property sources
 -  Rest endpoints looks like:  
 -  The HTTP service has resources in the form:
    - `/{application}/{profile}[/{label}]`  
    - `/{application}-{profile}.yml`
    - `/{label}/{application}-{profile}.yml`
    - `/{application}-{profile}.properties`
    - `/{label}/{application}-{profile}.properties`
    - where the "application" is injected as the "spring.config.name" in the SpringApplication (i.e. what is normally "application" in a regular Spring Boot app), "profile" is an active profile (or comma-separated
list of properties), and "label" is an optional git label (defaults to "master".)
    - examples:
    - [http://localhost:8888/configclient/default](http://localhost:8888/configclient/default link)
    - [http://localhost:8888/foo/development](http://localhost:8888/foo/development)
    - [http://localhost:8888/foo/development,staging](http://localhost:8888/foo/development,staging)
    - [http://localhost:8888/domain1global,configclient/development](http://localhost:8888/domain1global,configclient/development)

- The Config Server itself is stateless, so you can spin up as many as these as you need and find them via eureka (this is what we should do when using other clients)
- If the config server can't reach git it will continue to serve what it has checked out even if it is stale.
broadcast the EnvironmentChangedEvent to all the instances instead of having them polling for changes (e.g. using the Spring Cloud Bus).
try 
 `http://localhost:8888/foo-development.properties` 
 `http://localhost:8888/foo/bar`
{"name":"foo","profiles":["bar"],"label":"master","propertySources":[{"name":"https://github.com/lahirip/config-repo/foo.properties","source":{"bar":"foo-default"}}]}

# Understanding how the property source precedence works
{  
   "name":"foo",
   "profiles":[  
      "development",
      "staging"
   ],
   "label":"master",
   "propertySources":[  
      {  
         "name":"https://github.com/lahirip/config-repo/foo-staging.properties",
         "source":{  
            "bar":"foo-statging"
         }
      },
      {  
         "name":"https://github.com/lahirip/config-repo/foo-development.properties",
         "source":{  
            "bar":"foo-development"
         }
      },
      {  
         "name":"https://github.com/lahirip/config-repo/foo.properties",
         "source":{  
            "bar":"foo-default"
         }
      }
   ]
}

# Quick Facts about Spring Cloud Bus
- The Spring Cloud Bus links all services through a RabbitMQ(AMQP) powered-bus
- broadcast state changes (e.g. configuration changes)
- By Default it uses exchange name = `spring.cloud.bus`
- Bus supports sending messages to all nodes/services or some specific service/node
- http endpoints are under the /bus/* actuator namespace
- `destination` parameter, e.g. /bus/refresh?destination=`<servicename>`, `/bus/refresh?destination=configclient2:8085`where the destination is an `ApplicationContext` ID.  If the ID is owned by an instance on the Bus then it will process the message and all other instances will ignore it. Spring Boot sets the ID for you in the ContextIdApplicationContextInitializer to a combination of the spring.application.name, active profiles and server.port by default.
- For a Spring Boot application we can easily get the /bus/* endpoints active by adding dependency "spring-cloud-starter-bus-amqp"
- At start up the Bus enabled application (client) will contact `spring.rabbitmq.addresses`=localhost:5672 create the exchange .

# Prerequisite
 - make sure you have rbbitmq install and running in your machine. by default it listen in 5672
 - you can alternatively have a Rabbit container running by simply installing https://kitematic.com/  and then download and run rabbit container.   make sure you use the right ip and port in that case (no longer be 5672)
 
# To Run 
 - check all modules application and bootstap properties and make necessary chnages as necessary
 - simple change to respective module and run "mvn clean package" 
 
# Client Side 
- For every client do this:
 - set `spring.cloud.config.uri`  set to connect to Spring Config Server.
 - Add Spring Bus Dependency to all Services (have to figure out how non Spring Boot App will work)
 - Each Application/services should have a name:  set it using `spring.application.name`    (Read this to understand how it will be used:  https://github.com/spring-cloud/spring-cloud-bus/commit/ca7aa9a49c4199c58b0bce990f8cb38febd1454a)
 - At this stage we should have the service/config-clients up and running will  `/bus/refresh` and `/bus/env` endpoints up and running.  
 - `curl -X POST http://localhost:8080/bus/refresh`
 - `curl -X POST http://localhost:8080/bus/refresh?destination=configclient:**`

- Run multiple such clients:   
 - Create one more client following step above. 
 - Remember to change the "spring.application.name"   and   server.port
 - check that the new client is up and running
 - curl -X POST http://localhost:8085/bus/refresh
 - curl -X POST http://localhost:8085/bus/refresh?destination=configclient2:**

#Test to Prove Bus is Working:
 - When I submit to    `curl -X POST http://localhost:8888/bus/refresh?destination=configclient:**`    I see only configclient  Service is getting refreshed.  
 - When I I submit to    `curl -X POST http://localhost:8888/bus/refresh?destination=configclient2:**`    I see only configclient2 Service is getting refreshed.  
 - NOTICE:  it does not matter which service's    /bus/refresh     endpoint I am calling... thats what Spring Cloud bus does  consumes and forwards to all (or matched) service nodes.
 - What should be our next step then:  
 - Who will call the /bus/*  endpoint  and for what destination. 

 
# Environment Change Event Generator:
 - Today
  - There is nothing inbuilt in Config Server yet. But it will there soon. Read this: https://github.com/spring-cloud/spring-cloud-config/issues/86
  - We have to decide on a interim solution-  
   -  we can have Config Server join the Bus
   -  call its own /bus/refresh?....  endpoint   


# How client specific properties are fetched:

-We already know

  - A Spring Config Client created with Spring Boot application with the dependency on spring-cloud-config-client,  connects to Config Server automatically and use properties via Config Server.
  - The client application name defined using "spring.application.name"  is used to fetch the properties 
  - The chosen profile gets precedence "spring.cloud.config.profile=production"

for example if the GIT Repository looks something like this:

    configclient.properties

    configclient-production.properties

    configclient-development.properties

    application.properties (this is which application's properties  - config server's? which is located in GIT repo.  May be we don;t care about this.)

Inspect configclient's env by hitting    `http://localhost:8080/env/` you will notice the production profile gets the precedence.

http://localhost:8080/env/C1.X   returns "Y-production"

- A Special Note about:  application.properties
  - its precedence is lower than the config client's properties
  - We can override a property of application.properties in config client properties. 
  - For example  http://localhost:8080/env/COMMON  will return "XYZ"  not "123-application-properties". Which is what we expect.
  - But, with application.properties we can not replicate the Global properties behavior, and the domain specific properties concept.
    - you can not change the name of it,
    - you can have only one application.properties in your repository (of course you can have different profile application-development etc). 
    - on top of this, if you think of a situation where you provisioned config server for each domain (say inventory services) then all common properties for that domain can be hosted in application.properties (in the central repo for that config server).  But you can not change the name and you can not host configs for another domain (say BI services) where set of services under D2 have different set of common properties. So application.properties are not super suitable for Global properties.


