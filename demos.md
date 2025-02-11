# Bevezetés

## Alkalmazás indítása

* Meglévő alkalmazottakat nyilvántartó alkalmazást bővítek
* Projekt megnyitás, IntelliJ IDEA Ultimate
* Adatbázis indítás

```shell
docker run -d -e POSTGRES_DB=employees -e POSTGRES_USER=employees  -e POSTGRES_PASSWORD=employees  -p 5432:5432  --name employees-postgres postgres
```

* Swagger: http://localhost:8080/swagger-ui.html
* `src/test/http/employees.http`: List, Create, List

## Adatbázis kapcsolat felvétele

## Alkalmazás felépítése

* Háromrétegű Spring Boot alkalmazás, `pom.xml`
* `application.properties`, adatbázis séma generálás
* `Employee` entitás
* `EmployeeRepository` Spring Data JPA, Dynamic projection
* `EmployeeService`, MapStruct, `EmployeeDto` DTO
* `EmployeeController`

# SOAP webszolgáltatások CXF-fel

## Egyszerű webszolgáltatás implementálása

`empapp-cxf` könyvtárba másolás

`pom.xml`

```xml
<dependency>
    <groupId>org.apache.cxf</groupId>
    <artifactId>cxf-spring-boot-starter-jaxws</artifactId>
    <version>4.1.0</version>
</dependency>
```

`empapp.wdto.EmployeeWdto`

```java
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeWdto {

    private Long id;

    private String name;
}
```

``empapp.webservice.EmployeeEndpoint`

```java
@WebService
@Service
@RequiredArgsConstructor
public class EmployeeEndpoint {

    private final EmployeeService employeeService;

    public List<EmployeeWdto> findAll() {
        return employeeService.findEmployeeWdtos();
    }
}
```

`EmployeeService`

```java
public List<EmployeeWdto> findEmployeeWdtos() {
    return employeeRepository.findAllBy(EmployeeWdto.class);
}
```

```java
import jakarta.xml.ws.Endpoint;
import org.apache.cxf.Bus;
import org.apache.cxf.jaxws.EndpointImpl;

@Configuration(proxyBeanMethods = false)
public class CxfConfiguration {

    @Bean
    public Endpoint employeeWebServiceEndpoint(Bus bus, EmployeeEndpoint employeeEndpoint) {
        Endpoint endpoint = new EndpointImpl(bus, employeeEndpoint);
        endpoint.publish("/employees");
        return endpoint;
    }
}
```

`http://localhost:8080/services`

SoapUI a WSDL alapján

SOAP Fault

```
@AllArgsConstructor(onConstructor_ = @PersistenceCreator)
```

## Névterek használata

`EmployeeEndpoint`

```java
public static final String EMPLOYEES_NAMESPACE = "https://training.com/employees";
```

```java
@WebService(targetNamespace = EmployeeEndpoint.EMPLOYEES_NAMESPACE)
```

```java
@WebResult(name = "employee", targetNamespace = EMPLOYEES_NAMESPACE)
```

`package-info.java`

```java
@XmlSchema(namespace = EMPLOYEES_NAMESPACE, elementFormDefault = XmlNsForm.QUALIFIED)
package empapp.webservice;

import jakarta.xml.bind.annotation.XmlNsForm;
import jakarta.xml.bind.annotation.XmlSchema;

import static empapp.webservice.EmployeeEndpoint.EMPLOYEES_NAMESPACE;
```

```xml
<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" xmlns:emp="https://training.com/employees">
   <soapenv:Header/>
   <soapenv:Body>
      <emp:findAll/>
   </soapenv:Body>
</soapenv:Envelope>
```

## Webszolgáltatás személyre szabása

```java
@Data
@AllArgsConstructor
@XmlAccessorType(XmlAccessType.FIELD)
public class EmployeeWdto {

    @XmlAttribute
    private long id;

    private String name;
}
```

## CXF naplózás

`pom.xml`

```xml
<dependency>
    <groupId>org.apache.cxf</groupId>
    <artifactId>cxf-rt-features-logging</artifactId>
    <version>4.1.0</version>
</dependency>
```

```java
@Configuration(proxyBeanMethods = false)
public class CxfConfiguration {

    @Bean
    public LoggingFeature loggingFeature() {
        LoggingFeature loggingFeature = new LoggingFeature();
        loggingFeature.setPrettyLogging(true);
        return loggingFeature;
    }

    @Bean
    public Endpoint employeeWebServiceEndpoint(Bus bus, EmployeeEndpoint employeeEndpoint, LoggingFeature loggingFeature) {
        EndpointImpl endpoint = new EndpointImpl(bus, employeeEndpoint);
        endpoint.setFeatures(List.of(loggingFeature));
        endpoint.publish("/employees");        
        return endpoint;
    }
}
```

## Webszolgáltatás kliens

Starter: `empapp-cxf-client`, groupId: `spring.training`, package: `empapp` Lombok

```xml
<dependency>
    <groupId>org.apache.cxf</groupId>
    <artifactId>cxf-spring-boot-starter-jaxws</artifactId>
    <version>${cxf.version}</version>
</dependency>
```

```xml
<plugin>
    <groupId>org.apache.cxf</groupId>
    <artifactId>cxf-codegen-plugin</artifactId>
    <version>${cxf.version}</version>
    <executions>
        <execution>
            <id>generate-sources</id>
            <phase>generate-sources</phase>
            <configuration>
                <wsdlOptions>
                    <wsdlOption>
                        <wsdl>${basedir}/src/main/resources/employees.wsdl</wsdl>
                    </wsdlOption>
                </wsdlOptions>
            </configuration>
            <goals>
                <goal>wsdl2java</goal>
            </goals>
        </execution>
    </executions>
</plugin>
```

`application.properties`

```conf
spring.main.web-application-type=none
```

```java
@SpringBootApplication
@Slf4j
public class EmpappCxfClientApplication implements CommandLineRunner {

	public static void main(String[] args) {
		SpringApplication.run(EmpappWsClientApplication.class, args);
	}

	@Override
	public void run(String... args) {
		var service = new EmployeeEndpointService();
		var port = service.getEmployeeEndpointPort();
		var response = port.findAll();
		log.info("Employees: {}", response.stream().map(EmployeeWdto::getName).toList());
	}
}
```

`EmployeeEndpointService` konstruktornak megadható az URL, amúgy a generált kódba a WSDL-ből kerül.

# Aszinkron üzenetkezelés JMS-sel

## ActiveMQ Artemis indítása

```shell
docker run -d -p 61616:61616 -p 8161:8161 --name artemis apache/activemq-artemis:latest-alpine
```

Adminisztrációs felület: http://localhost:8161/

Felhasználónév/jelszó: `artemis` / `artemis`


## Üzenet küldése

```xml
<dependency>
	<groupId>org.springframework.boot</groupId>
	<artifactId>spring-boot-starter-artemis</artifactId>
</dependency>
```

`application.properties`

Alapértelmezetten a `localhost`-hoz, default porton (`61616`) kapcsolódik,
de ez felülbírálható a `spring.artemis.host` és `spring.artemis.port` paraméterekkel

```conf
spring.artemis.user=artemis
spring.artemis.password=artemis
```

`EmployeeService`

```java
private final JmsTemplate jmsTemplate;
```

```java
jmsTemplate.send("employees-events",session -> session.createTextMessage("Employee has been created: {}", employeeDto));
```

Első paraméter név vagy `Destination`, mely el is hagyható, ekkor a `spring.jms.template.default-destination` konfigurációs paraméter alapján

Browse queue

## Architektúra

```java
public record EmployeeHasBeenCreatedEvent(long id, String name) {
}
```

```java
private ApplicationEventPublisher eventPublisher;
```

```java
eventPublisher.publishEvent(new EmployeeHasBeenCreatedEvent(employee.getId(), employee.getName()));
```

`gateway` package

```java
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Component
public @interface Gateway {
}
```

```java
@Gateway
@RequiredArgsConstructor
public class JmsGateway {

    private final JmsTemplate jmsTemplate;

    @EventListener
    public void sendMessage(EmployeeHasBeenCreatedEvent event) {
        jmsTemplate.send("employees-events",session -> session.createTextMessage(event.toString()));
    }

}
```

## Üzenet fogadása

```java
public static final String EMPLOYEES_EVENTS_QUEUE = "employees-events";
```

```java
@JmsListener(destination = EMPLOYEES_EVENTS_QUEUE)
public void receiveMessage(Message message) {
    if (message instanceof TextMessage textMessage) {
        try {
            log.info("Received text message: {}", textMessage.getText());
        }
        catch (JMSException ex) {
            throw new RuntimeException("Error reading message", ex);
        }
    }
    else {
        throw new IllegalArgumentException("Message must be of type TextMessage");
    }
}
```

## JSON formátumú üzenet küldése

* Alapesetben a `SimpleMessageConverter` aktív
    * `String` -> `TextMessage`
    * `byte[]` -> `BytesMessage`
    * `Map` -> `MapMessage`
    * `Serializable` -> `ObjectMessage`


```java
@Configuration(proxyBeanMethods = false)
public class JmsConfig {

    @Bean
    public MessageConverter messageConverter(ObjectMapper objectMapper){
        MappingJackson2MessageConverter converter = new MappingJackson2MessageConverter();
        converter.setTypeIdPropertyName("_typeId");
        return converter;
    }

}
```

XML esetén `MarshallingMessageConverter` (JAXB)

```java
// @JmsListener(destination = EMPLOYEES_EVENTS_QUEUE)
```

`_typeId` fejléc

## A typeId header konfigurálása

```java
converter.setTypeIdMappings(Map.of("EmployeeHasBeenCreated", EmployeeHasBeenCreatedEvent.class));
```

## JSON formátumú üzenet fogadása

```java
@JmsListener(destination = EMPLOYEES_EVENTS_QUEUE)
public void receiveMessage(EmployeeHasBeenCreatedEvent event) {
    log.info("Received text message: {}", event);
}
```

## Üzenet fejléc írása és olvasása

* `MessagePostProcessor` alkalmazásával

```java
jmsTemplate.convertAndSend(EMPLOYEES_EVENTS_QUEUE, event,
                message -> {
                    message.setStringProperty("odd", Boolean.toString(event.id() % 2 == 1));
                    return message;
                }
            );
```

```java
@JmsListener(destination = EMPLOYEES_EVENTS_QUEUE)
public void receiveMessage(EmployeeHasBeenCreatedEvent event, @Header String odd) {
    log.info("Received text message: {}, {}", event, odd);
}
```

Szabványos fejléc olvasása:

```java
@Header(name = JmsHeaders.MESSAGE_ID) String messageId
```

Összes fejléc olvasása:

```java
@Headers Map<String, Object> headers
```

## Message selector használata


```java
@JmsListener(destination = EMPLOYEES_EVENTS_QUEUE, selector = "odd = 'true'")
```

__Majd kikapcsolása, hogy a következő demókat ne befolyásolja__

## Mérgezett üzenetek

```java
throw new IllegalStateException("Can not read message");
```

Alapértelmezett: 10

`JMSRedelivered` és `JMSXDeliveryCount` fejléc

DLQ: `_AMQ_ORIG` propery-k

__Kivétel dobás megjegyzésbe!__

## ErrorHandler

Alapesetben kiírja a stacktrace-t

```java
@Bean
public DefaultJmsListenerContainerFactory jmsListenerContainerFactory(
        DefaultJmsListenerContainerFactoryConfigurer configurer,
        ConnectionFactory connectionFactory) {
    DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
    configurer.configure(factory, connectionFactory);
    factory.setErrorHandler(t -> log.error("Error: " + t.getMessage()));
    return factory;
}
```

__Majd kapcsoljuk ki, mert azt a factory-t akarjuk konfigurálni `application.properties` fájlból__

Ha az üzenetfeldolgozás során keletkező kivételeket szeretnéd kezelni, használd a `setErrorHandler` metódust.
Ha a JMS kapcsolat szintjén jelentkező problémákra kell reagálnod, a `setExceptionListener` a megfelelő választás.

## Delivery Delay

`Duration` típusú

```conf
spring.jms.template.delivery-delay=5s
```

```java
jmsTemplate.setDeliveryDelay(Duration.ofSeconds(10).toMillis());
```

## Quality of Service

Erőforrás használatának vezérlése, erőforrás megtakarítás

* Delivery mode: message persistence
    * Típusai
        * `DeliveryMode.PERSISTENT`: default, a JMS provider leállása után is megmaradnak
        * `DeliveryMode.NON_PERSISTENT`: vissza nem állítható üzenetek: gyorsabb, kevesebb tárhelyet foglalnak
    * `JmsTemplate.setDeliveryMode()`
    * `JMSDeliveryMode` fejléc
* Priority
    * Skála 0-9-ig (legalacsonyabb - legmagasabb)
    * Default: 4
    * `JmsTemplate.setPriority()`
    * `JMSPriority` fejléc
* Time to live

__Fogadás kikapcsolása__

```conf
spring.jms.template.qos-enabled=true
spring.jms.template.priority=5
spring.jms.template.delivery-mode=non_persistent
spring.jms.template.time-to-live=10s
```

```java
jmsTemplate.setExplicitQosEnabled(true);
jmsTemplate.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
jmsTemplate.setPriority(5);
jmsTemplate.setTimeToLive(Duration.ofSeconds(10).toMillis());
```

* Időszinkronizálás a szerverek között

__Ezek kikapcsolása, fogadás bekapcsolása__

## Tranzakciókezelés

```conf
spring.jms.template.session.transacted=true
```

`sendMessage()` metódusban

* `@Transactional` annotáció
* Több üzenet elküldése
* `throw new IllegalStateException("Test exception");`

__Ciklus, kivételdobás megjegyzésbe!__

## Kérés-válasz kommunikáció

```java
public record FindEmployeeByIdRequest(Long id) {
}
```

```java
converter.setTypeIdMappings
```

```java
@JmsListener(destination = "employees-request")
@SendTo("employees-response")
public EmployeeDto findEmployeeById(FindEmployeeByIdRequest findEmployeeByIdRequest) {
    log.info("Received employee request: {}", findEmployeeByIdRequest);
    return employeeService.findEmployeeById(findEmployeeByIdRequest.id());
}
```

`JmsConfiguration`-be bekerül az `EmployeeDto`

```java
converter.setTypeIdMappings(Map.of("EmployeeHasBeenCreated", EmployeeHasBeenCreatedEvent.class,
                "FindEmployeeById", FindEmployeeByIdRequest.class,
                "Employee", EmployeeDto.class));
```

* Üzenet küldése felületen

```plain
_typeId=FindEmployeeByIdRequest
```

```json
{"id": 40}
```

* Üzenet browse felületen

```shell
bin/artemis producer --user artemis --password=artemis --message='{"id": 2}' --message-count=1 --destination employees-request --properties='[{"type":"string","key":"_typeId","value": "FindEmployeeById"}]'
```

## Kérés-válasz kommunikáció temporary queue használatával

Parancssori kliens létrehozása: Starter, `empapp-jms-client`, Lombok, Artemis

`application.properties`

```conf
spring.artemis.user=artemis
spring.artemis.password=artemis
```

```java
@SpringBootApplication
@RequiredArgsConstructor
@Slf4j
public class EmpappJmsClientApplication implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(EmpappJmsClientApplication.class, args);
    }

    private final JmsTemplate jmsTemplate;

    @Override
	public void run(String... args) throws Exception {
		// language=json
		String json = """
				{"id": 40}
				""";
		Message response = jmsTemplate.sendAndReceive("employees-request",
				session -> {
					TextMessage request = session.createTextMessage(json);
					request.setStringProperty("_type", "FindEmployeeById");
					return request;
				});

		log.info("Response: {}, destination: {}", ((TextMessage) response).getText(),
				response.getJMSDestination());
	}
}
```

Temporary queue:

* Csak a létrehozó tud fogadni, bárki tud küldeni

Fejlécek:

* `JMSDestination` melyik sorba került az üzenet
* `JMSReplyTo` melyik sorba várja a küldő az üzenetet

```java
converter.setTargetType(MessageType.TEXT);
```


## Kérés-válasz kommunikáció JMSCorrelationID használatával szerver oldalon

`JmsConfiguration`-be bekerül az `EmployeeDto`

```java
converter.setTypeIdMappings(Map.of("EmployeeHasBeenCreated", EmployeeHasBeenCreatedEvent.class,
                "FindEmployeeById", FindEmployeeByIdRequest.class,
                "Employee", EmployeeDto.class));
```

```java
@JmsListener(destination = "employees-request")
public void findEmployeeById(FindEmployeeByIdRequest request) {
    log.info("Received employee request: {}", request);
    EmployeeDto employeeDto = employeeService.findEmployeeById(request.id());
    jmsTemplate.convertAndSend("employees-response", employeeDto, message -> {
        message.setJMSCorrelationID(Long.toString(request.id()));
        return message;
    });
}
```

Browse

## Kérés-válasz kommunikáció JMSCorrelationID használatával kliens oldalon

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-json</artifactId>
</dependency>
```

`FindEmployeeById`, `EmployeeDto` másolása

`JmsConfiguration`, bele kell tenni az `EmployeeDto`-t is

* Nem állítható be, és körülményesen kiolvasható üzenetazonosító

```java
jmsTemplate.convertAndSend("employees-request", new FindEmployeeByIdRequest(40));
EmployeeDto employeeDto = (EmployeeDto) jmsTemplate.receiveSelectedAndConvert("employees-response",
        "JMSCorrelationID = '%d'".formatted(40));
log.info("Employee: {}", employeeDto.name());
```

Amennyiben nem jön válasz, végtelen ideig vár.

```java
jmsTemplate.setReceiveTimeout(Duration.ofSeconds(2).toMillis());
```

Ekkor `null` értéket kapunk

## Publish and subscribe

```conf
spring.jms.pub-sub-domain=true
```

Ez a beállítás beállítja a `JmsListenerContainerFactory` és a `JmsTemplate` konfigurációját is (ha nem írjuk felül `@Configuration` annotációval ellátott osztályban bármelyiket)

```java
@Bean
public DefaultJmsListenerContainerFactory jmsListenerContainerFactory(
        DefaultJmsListenerContainerFactoryConfigurer configurer,
        ConnectionFactory connectionFactory) {
    DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
    configurer.configure(factory, connectionFactory);
    factory.setPubSubDomain(true);
    return factory;
}
```

```java
jmsTemplate.setPubSubDomain(true);
```

## Durable subscription

```conf
spring.jms.subscription-durable=true
spring.jms.client-id=empapp1
```

`JmsGateway`

```java
@JmsListener(destination = EMPLOYEES_EVENTS_QUEUE, subscription = "events")
```

__Másik `@JmsListener` törlése, mert azzal összeakad__

Run configuration:

```plain
-Dspring.jms.client-id=empapp2
```

Artemis: employees-events/queues/multicast/empapp1.events-handler

Másik példány leállítása, üzenetek küldése

Másik példány indítása, üzeneteket feldolgozza

## Spring Boot JMS architektúra

* Interfészek, osztályok, annotációk
* Alkalmazás architektúra

## Point to point és Publish and subscribe egy alkalmazáson belül

`application.properties`

```conf
#spring.jms.pub-sub-domain=true
#spring.jms.subscription-durable=true
#spring.jms.client-id=empapp1
```

`JmsConfiguration`

```java
@Bean
public DefaultJmsListenerContainerFactory jmsListenerContainerFactory(
        DefaultJmsListenerContainerFactoryConfigurer configurer,
        ConnectionFactory connectionFactory
) {
    DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
    configurer.configure(factory, connectionFactory);
    return factory;
}

@Bean
public DefaultJmsListenerContainerFactory pubsubJmsListenerContainerFactory(
        DefaultJmsListenerContainerFactoryConfigurer configurer,
        ConnectionFactory connectionFactory
) {
    DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
    configurer.configure(factory, connectionFactory);
    factory.setPubSubDomain(true);
    factory.setSubscriptionDurable(true);
    return factory;
}

@Bean
public JmsTemplate jmsTemplate(
        ConnectionFactory connectionFactory,
        MessageConverter messageConverter) {
    JmsTemplate jmsTemplate = new JmsTemplate(connectionFactory);
    jmsTemplate.setMessageConverter(messageConverter);
    return jmsTemplate;
}

@Bean
@SneakyThrows
public JmsTemplate pubsubJmsTemplate(
        ConnectionFactory connectionFactory, MessageConverter messageConverter) {
    JmsTemplate jmsTemplate = new JmsTemplate(connectionFactory);
    jmsTemplate.setPubSubDomain(true);
    jmsTemplate.setMessageConverter(messageConverter);
    return jmsTemplate;
}

@Bean
public BeanPostProcessor beanPostProcessor(PubSubJmsProperties jmsProperties) {
    return new BeanPostProcessor() {
        @Override
        public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
            if (bean instanceof CachingConnectionFactory cachingConnectionFactory) {
                cachingConnectionFactory.setClientId(jmsProperties.getClientId());
            }
            return bean;
        }
    };
}
```

`JmsGateway`

```java
private final JmsTemplate pubsubJmsTemplate;
```

```java
@JmsListener(destination = EMPLOYEES_EVENTS_TOPIC, subscription = "events",
    containerFactory = "pubsubJmsListenerContainerFactory")
```

```java
pubsubJmsTemplate.convertAndSend(EMPLOYEES_EVENTS_QUEUE,
```

Visszatehető:

```java
@JmsListener(destination = "employees-request")
```

* Megy mindkét típusú üzenet

## Második példány indítása


```java
@ConfigurationProperties(prefix = "pub-sub.jms")
@Data
public class PubSubJmsProperties {

    private String clientId;
}
```

```java
@EnableConfigurationProperties(PubSubJmsProperties.class)
```

```java
connectionFactory.setClientId(pubSubJmsProperties.getClientId());
```

`application.properties`:

```
pub-sub.jms.client=empapp1
```

Run configuration:

```
-Dpub-sub.jms.client-id=empapp2
```

* publish and subscribe esetén mind a két alkalmazás példány megkapja
* point to point esetén hol az egyik, hol a másik példány kapja meg

## Browsing

```java
@Gateway
@RequiredArgsConstructor
@Slf4j
public class DlqGateway {

    private final JmsTemplate jmsTemplate;

    @PostConstruct
    public void listDlq() {
        List<String> messagesInDlq = jmsTemplate.browse("DLQ", (session, browser) -> {
            List<String> messages = new ArrayList<>();
            Enumeration enumeration = browser.getEnumeration();
            while (enumeration.hasMoreElements()) {
                Message message = (Message) enumeration.nextElement();
                String id = message.getJMSMessageID();
                String source = message.getStringProperty("_AMQ_ORIG_QUEUE");
                messages.add(id + ": " + source);
            }
            return messages;
        });
        log.info("Messages in DLQ: {}", messagesInDlq);
    }
}
```

# Elosztott tranzakciókezelés

## Elosztott tranzakció adatbázis és message broker között

# CXF over JMS

```xml
<dependency>
    <groupId>org.apache.cxf</groupId>
    <artifactId>cxf-spring-boot-starter-jaxws</artifactId>
    <version>${cxf.version}</version>
</dependency>


<dependency>
    <groupId>org.apache.cxf</groupId>
    <artifactId>cxf-rt-transports-jms</artifactId>
    <version>${cxf.version}</version>
</dependency>
```

```xml
<plugin>
    <groupId>org.apache.cxf</groupId>
    <artifactId>cxf-java2ws-plugin</artifactId>
    <version>${cxf.version}</version>
    <dependencies>
        <dependency>
            <groupId>org.apache.cxf</groupId>
            <artifactId>cxf-rt-frontend-jaxws</artifactId>
            <version>${cxf.version}</version>
        </dependency>
        <dependency>
            <groupId>org.apache.cxf</groupId>
            <artifactId>cxf-rt-frontend-simple</artifactId>
            <version>${cxf.version}</version>
        </dependency>
    </dependencies>

    <executions>
        <execution>
            <id>process-classes</id>
            <phase>process-classes</phase>
            <configuration>
                <className>empapp.webservice.EmployeeEndpoint</className>
                <genWsdl>true</genWsdl>
                <verbose>true</verbose>
            </configuration>
            <goals>
                <goal>java2ws</goal>
            </goals>
        </execution>
    </executions>
</plugin>
```

`empapp.webservice.EmployeeWdto`

* JAXB miatt paraméter nélküli konstruktor
* Spring Data JPA miatt `@AllArgsConstructor`, de mivel több konstruktor van, ezért `@PersistenceCreator` annotációval

```java
@Data
@AllArgsConstructor(onConstructor_ = @PersistenceCreator)
@NoArgsConstructor
@XmlAccessorType(XmlAccessType.FIELD)
public class EmployeeWdto {

    @XmlAttribute
    private long id;

    private String name;

}
```

`EmployeeService`

```java
public List<EmployeeDto> findEmployeeWdtos() {
    return employeeRepository.findAllBy(EmployeeWdto.class);
}
```

`EmployeeEndpoint`

```java
@WebService(targetNamespace = EmployeeEndpoint.EMPLOYEES_NAMESPACE)
@Service
@RequiredArgsConstructor
public class EmployeeEndpoint {

    public static final String EMPLOYEES_NAMESPACE = "https://training.com/employees";

    private final EmployeeService employeeService;

    @WebResult(name = "employee", targetNamespace = EMPLOYEES_NAMESPACE)
    public List<EmployeeWdto> findAll() {
        return employeeService.listEmployeeWdtos();
    }
}
```

`package-info.java`

```java
@XmlSchema(namespace = EMPLOYEES_NAMESPACE, elementFormDefault = XmlNsForm.QUALIFIED)
package empapp.webservice;

import jakarta.xml.bind.annotation.XmlNsForm;
import jakarta.xml.bind.annotation.XmlSchema;

import static empapp.webservice.EmployeeEndpoint.EMPLOYEES_NAMESPACE;
```

## CXF over JMS szerver alkalmazás konfiguráció

`WebServiceConfiguration`

```java
@Configuration(proxyBeanMethods = false)
public class WebServiceConfiguration {

    @Bean
    public Endpoint jmsEndpoint(SpringBus bus, JMSConfigFeature jmsConfigFeature, EmployeeEndpoint employeeEndpoint) {
        EndpointImpl endpoint = new EndpointImpl(bus, employeeEndpoint);
        endpoint.getFeatures().add(jmsConfigFeature);
        endpoint.publish("jms:queue:employees-ws-request");
        return endpoint;
    }

    @Bean
    public JMSConfigFeature jmsConfigFeature(ConnectionFactory mqConnectionFactory){
        JMSConfigFeature feature = new JMSConfigFeature();

        JMSConfiguration jmsConfiguration = new JMSConfiguration();
        jmsConfiguration.setConnectionFactory(mqConnectionFactory);
        jmsConfiguration.setTargetDestination("employees-ws-request");
        jmsConfiguration.setMessageType("text");

        feature.setJmsConfig(jmsConfiguration);
        return feature;
    }
}
```

`mvnw package`, hogy előálljon a WSDL fájl

## JMS kliens alkalmazás

Starter, `empapp-cxf-jms-client`, Lombok, Artemis

`application.properties`

```conf
spring.artemis.user=artemis
spring.artemis.password=artemis
```

```java
@SpringBootApplication
@RequiredArgsConstructor
@Slf4j
public class EmpappJmsClientApplication implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(EmpappJmsClientApplication.class, args);
    }

    private final JmsTemplate jmsTemplate;

    @Override
    public void run(String... args) throws Exception {
        // language=xml
        String xml = """
                <soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" xmlns:emp="https://training.com/employees">
                   <soapenv:Header/>
                   <soapenv:Body>
                      <emp:findAll/>
                   </soapenv:Body>
                </soapenv:Envelope>
                """;

        Message response = jmsTemplate.sendAndReceive("employees-ws-request", session -> {
            TextMessage message = session.createTextMessage(xml);
            message.setStringProperty("SOAPJMS_contentType", "application/soap+xml");
            message.setStringProperty("SOAPJMS_requestURI", "jms:queue:employees-ws-request");
            return message;
        });

        if (response instanceof TextMessage textMessage) {
            log.info("Response: {}", textMessage.getText());
        }
    }
}
```

## CXF over JMS kliens alkalmazás

```xml
<dependency>
    <groupId>org.apache.cxf</groupId>
    <artifactId>cxf-spring-boot-starter-jaxws</artifactId>
    <version>${cxf.version}</version>
</dependency>


<dependency>
    <groupId>org.apache.cxf</groupId>
    <artifactId>cxf-rt-transports-jms</artifactId>
    <version>${cxf.version}</version>
</dependency>
```

* WSDL átmásolása

```xml
<plugin>
    <groupId>org.apache.cxf</groupId>
    <artifactId>cxf-codegen-plugin</artifactId>
    <version>${cxf.version}</version>
    <executions>
        <execution>
            <id>generate-sources</id>
            <phase>generate-sources</phase>
            <configuration>
                <wsdlOptions>
                    <wsdlOption>
                        <wsdl>${basedir}/src/main/resources/EmployeeEndpoint.wsdl</wsdl>
                    </wsdlOption>
                </wsdlOptions>
            </configuration>
            <goals>
                <goal>wsdl2java</goal>
            </goals>
        </execution>
    </executions>
</plugin>
```

`mvnw package`

```java
@SpringBootApplication
@RequiredArgsConstructor
@Slf4j
public class EmpappJmsClientApplication implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(EmpappJmsClientApplication.class, args);
    }

    private final ConnectionFactory connectionFactory;

    @Override
    public void run(String... args) throws Exception {
        JaxWsProxyFactoryBean factoryBean = new JaxWsProxyFactoryBean();
        factoryBean.setServiceClass(EmployeeEndpoint.class);
        factoryBean.setAddress("jms:queue:employees-ws-request");

        JMSConfigFeature jmsConfigFeature = new JMSConfigFeature();
        JMSConfiguration jmsConfiguration = new JMSConfiguration();
        jmsConfiguration.setConnectionFactory(connectionFactory);
        jmsConfiguration.setTargetDestination("employees-ws-request");
        jmsConfiguration.setMessageType("text");
        jmsConfiguration.setRequestURI("jms:queue:employees-ws-request");
        jmsConfigFeature.setJmsConfig(jmsConfiguration);

        factoryBean.setFeatures(List.of(jmsConfigFeature));
        EmployeeEndpoint endpoint = (EmployeeEndpoint) factoryBean.create();

        List<EmployeeWdto> response = endpoint.findAll();
        log.info("Employees: {}", response.stream().map(EmployeeWdto::getName).toList());
    }
}
```

