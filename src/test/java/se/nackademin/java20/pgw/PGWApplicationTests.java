package se.nackademin.java20.pgw;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.support.TestPropertySourceUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.containers.RabbitMQContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ExtendWith(SpringExtension.class)
@Testcontainers
@ContextConfiguration(initializers = PGWApplicationTests.Lab1ApplicationTestsContextInitializer.class)
@AutoConfigureMockMvc
class PGWApplicationTests {

    @Container
    private static final MySQLContainer db = new MySQLContainer("mysql:8.0.26").withPassword("password");

    @Container
    private static RabbitMQContainer rabbit = new RabbitMQContainer("rabbitmq:3.9.5");

    @Autowired
    RabbitAdmin rabbitAdmin;

    @Autowired
    RabbitTemplate rabbitTemplate;

    @Autowired
    MockMvc mockMvc;

    @BeforeEach
    public void before() {
    }

    @AfterEach
    public void after() {
        rabbitAdmin.purgeQueue("payments", true);
    }

    @Test
    void contextLoads() {
    }

/*
    @Test
    void shouldSendCreatedOnPaymentCreated() throws Exception {
        String json = "{\"reference\": \"dan\", \"balance\": 100}";
        mockMvc.perform(MockMvcRequestBuilders.post("/payment").content(json).contentType("application/json")).andExpect(status().is2xxSuccessful());

        String message = (String) rabbitTemplate.receiveAndConvert("payments");
        assertTrue(message.contains("dan"));
        assertTrue(message.contains("CREATED"));
    }

 */






    public static class Lab1ApplicationTestsContextInitializer
            implements ApplicationContextInitializer<ConfigurableApplicationContext> {

        @Override
        public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
            String host = db.getJdbcUrl();
            TestPropertySourceUtils.addInlinedPropertiesToEnvironment(
                    configurableApplicationContext,
                    "spring.datasource.url=" + host, "flyway.url=" + host,
                    "spring.rabbitmq.host=" + rabbit.getContainerIpAddress(), "spring.rabbitmq.port=" + rabbit.getMappedPort(5672));

        }
    }
}
