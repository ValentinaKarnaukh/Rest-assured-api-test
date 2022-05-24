package ru.ValentinaKarnaukh.tests;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.ValentinaKarnaukh.dao.CreateTokenRequest;
import ru.ValentinaKarnaukh.dao.CreateTokenResponse;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class CreateTokenTests {
    private static final String PROPERTIES_FILE_PATH = "src/test/resources/application.properties";
    private static CreateTokenRequest request;
    private static CreateTokenResponse response;
    static Properties properties = new Properties();


    @BeforeAll
    static void beforeAll() throws IOException {
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
        properties.load(new FileInputStream(PROPERTIES_FILE_PATH));
        RestAssured.baseURI = properties.getProperty("base.url");

        request = CreateTokenRequest.builder()
                .username("admin")
                .password("password123")
                .build();
    }

    @Test
    void createTokenPositiveTest() {
        CreateTokenResponse response = given()
                .log()
                .method()
                .log()
                .uri()
                .log()
                .body()
                .header("Content-Type","application/json")
                .body(request)
                .expect()
                .statusCode(200)
                .when()
                .post("auth")
                .prettyPeek()
                .then()
                .extract()
                .as(CreateTokenResponse.class);
        assertThat(response, is(not(nullValue())));
        assertThat(response.getToken().length(), equalTo(15));
    }

    @Test
    void createTokenWithAWrongPasswordNegativeTest() {
        given()
                .log()
                .method()
                .log()
                .uri()
                .log()
                .body()
                .header("Content-Type","application/json")
                .body(request.withPassword("password"))
                .when()
                .post("auth")
                .prettyPeek()
                .then()
                .statusCode(200)
        .body("reason", CoreMatchers.equalTo("Bad credentials"));


    }

    @Test
    void createTokenWithAWrongUsernameNegativeTest() {
        Response response = given()
                .log()
                .method()
                .log()
                .uri()
                .log()
                .body()
                .header("Content-Type", "application/json")
                .body(request.withUsername("admin123"))
                .when()
                .post("auth")
                .prettyPeek();
        assertThat(response.statusCode(), equalTo(200));
        assertThat(response.body().jsonPath().get("reason"), containsStringIgnoringCase("Bad credentials"));
    }
}
