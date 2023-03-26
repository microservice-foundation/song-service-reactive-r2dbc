package com.epam.training.microservicefoundation.songservice.base;

import com.epam.training.microservicefoundation.songservice.repository.DatasourceConfiguration;
import com.epam.training.microservicefoundation.songservice.repository.PostgresExtension;
import io.restassured.module.webtestclient.RestAssuredWebTestClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@ExtendWith(PostgresExtension.class)
@DirtiesContext
@ContextConfiguration(classes = DatasourceConfiguration.class)
//@MockitoSettings(strictness = Strictness.LENIENT) // https://stackoverflow.com/questions/42947613/how-to-resolve-unneccessary-stubbing-exception
@TestPropertySource(locations = "classpath:application.properties")
public abstract class RestBase {

  @BeforeEach
  public void setup(ApplicationContext context) {

    RestAssuredWebTestClient.applicationContextSetup(context);
  }
}
