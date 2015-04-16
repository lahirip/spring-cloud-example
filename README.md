# spring-cloud-example
This example show how we can use Srping Config Server and use Spring Cloud Bus to update all connected clients whenever there is an update made in config.  This example used Spring Boot to create Config Client Services.

#Quick Facts about Spring Cloud Bus
- The Spring Cloud Bus links all services through a RabbitMQ(AMQP) powered-bus
- broadcast state changes (e.g. configuration changes)
- By Default it uses exchange name = spring.cloud.bus
- Bus supports sending messages to all nodes/services or some specific service/node
- http endpoints are under the /bus/* actuator namespace
- "destination" parameter, e.g. "/bus/refresh?destination=customers:9000", where the destination is an `ApplicationContext` ID.
- For a Spring Boot application we can easily get the /bus/* endpoints active by adding dependency "spring-cloud-starter-bus-amqp"
- At start up the Bus enabled application (client) will contact spring.rabbitmq.addresses=localhost:5672 create the exchange .

#Prerequisite
 - make sure you have rbbitmq install and running in your machine. by default it listen in 5672
 - you can alternatively have a Rabbit container running by simply installing https://kitematic.com/  and then download and run rabbit container.   make sure you use the right ip and port in that case (no longer be 5672)
 
#To Run 
 - check all modules application and bootstap properties and make necessary chnages as necessary
 - simple change to respective module and run "mvn clean package" 
 
#Client Side 
- For every client do this:
 - set "spring.cloud.config.uri"  set to connect to Spring Config Server.
 - Add Spring Bus Dependency to all Services (have to figure out how non Spring Boot App will work)
 - Each Application/services should have a name:  set it using "spring.application.name"    (Read this to understand how it will be used:  https://github.com/spring-cloud/spring-cloud-bus/commit/ca7aa9a49c4199c58b0bce990f8cb38febd1454a)
 - At this stage we should have the service/config-clients up and running will  /bus/refresh and /bus/env endpoints up and running.  
 - curl -X POST http://localhost:8080/bus/refresh
 - curl -X POST http://localhost:8080/bus/refresh?destination=configclient:**

- Run multiple such clients:   
 - Create one more client following step above. 
 - Remember to change the "spring.application.name"   and   server.port
 - check that the new client is up and running
 - curl -X POST http://localhost:8085/bus/refresh
 - curl -X POST http://localhost:8085/bus/refresh?destination=configclient2:**

#Test to Prove Bus is Working:
 - When I submit to    curl -X POST http://localhost:8080/bus/refresh?destination=configclient:**    I see only configclient  Service is getting refreshed.  
 - When I I submit to    curl -X POST http://localhost:8080/bus/refresh?destination=configclient2:**    I see only configclient2 Service is getting refreshed.  
 - NOTICE:  it does not matter which service's    /bus/refresh     endpoint I am calling... thats what Spring Cloud bus does  consumes and forwards to all (or matched) service nodes.
 - What should be our next step then:  
 - Who will call the /bus/*  endpoint  and for what destination. 

 
# Environment Change Event Generator:
 - Today
  - There is nothing inbuilt in Config Server yet. But it will there soon. Read this: https://github.com/spring-cloud/spring-cloud-config/issues/86
  - We have to decide on a interim solution-  
   -  we can have Config Server join the Bus
   -  call its own /bus/refresh?....  endpoint   


