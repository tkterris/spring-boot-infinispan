# Spring Boot Infinispan Example

This project is a demonstration of using Infinispan to drive Spring Boot caching. The application is based off of the existing "spring-boot-sample-data-rest" application, with changes made to configure caching, Infinispan, and clustering.

## Getting Started

You will need to have Maven and Java installed. Maven will need to have the Maven Central repository, which is where the project dependencies are available.

After downloading this project, run the following command:

```
mvn clean install
```

This will build the project. You should observe a successful build, with all tests passing.

Once the build is complete, run the following command to start the application:

```
java -Djava.net.preferIPv4Stack=true -jar target/spring-boot-sample-data-rest-1.4.3.RELEASE.jar --server.port=8080 
```

Change the value of the last argument to set the port of the application. Running this script multiple times in different terminal sessions, changing the port for each, will cluster the applications (assuming multicast is enabled for the machine they're running on).

## Testing

Once the application is running, you can access data through the following endpoints:

```
http://localhost:8080/api/cities/search/findByNameAndCountryAllIgnoringCase?name=Sydney&country=Australia
http://localhost:8080/api/cities/search/findByNameContainingAndCountryContainingAllIgnoringCase?name=&country=UK
```

Hibernate is configured to log when a query is executed. If the cache is functioning properly, if you hit the same endpoint twice, the query will only be executed once against the database. However, cache data will be removed after a timeout or if a maximum number of entries are stored in the cache - see infinispan.xml for details.

Try hitting the endpoint for one application process, then on another. The data should stay in the cache, so the second application process shouldn't have to hit the database for the data.

## Authorship and Acknowledgments

Original application by spring-boot team:

https://github.com/spring-projects/spring-boot/blob/master/spring-boot-samples/spring-boot-sample-data-rest/

Cache code and Infinispan configuration added by Trevor Xiao.

