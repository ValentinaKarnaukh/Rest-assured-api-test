package ru.ValentinaKarnaukh.tests;

import com.github.javafaker.Faker;
import io.restassured.RestAssured;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.ValentinaKarnaukh.dao.Bookingdates;
import ru.ValentinaKarnaukh.dao.CreateBookingRequest;
import ru.ValentinaKarnaukh.dao.CreateTokenRequest;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;


public class DeleteBookingTests {
    private static final String PROPERTIES_FILE_PATH = "src/test/resources/application.properties";
    private static CreateTokenRequest request;
    private static Bookingdates requestBookingDates;
    private static CreateBookingRequest requestCreateBooking;
    static Properties properties = new Properties();
    static String token;
    String id;
    static Faker faker = new Faker();

    @BeforeAll

    static void beforeAll() throws IOException {
        properties.load(new FileInputStream(PROPERTIES_FILE_PATH));
        RestAssured.baseURI = properties.getProperty("base.url");
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
        requestBookingDates = Bookingdates.builder()
                .checkin(properties.getProperty("checkin"))
                .checkout(properties.getProperty("checkout"))
                .build();
        request = CreateTokenRequest.builder()
                .username(properties.getProperty("username"))
                .password(properties.getProperty("password"))
                .build();
        requestCreateBooking = CreateBookingRequest.builder()
                .firstname(faker.name().firstName())
                .lastname(faker.name().lastName())
                .totalprice(faker.hashCode())
                .depositpaid(faker.bool().bool())
                .bookingdates(requestBookingDates)
                .additionalneeds(faker.chuckNorris().fact())
                .build();
        token = given()
                .log()
                .all()
                .header("Content-Type", "application/json")
                .body(request)
                .expect()
                .statusCode(200)
                .body("token", is(Matchers.not(nullValue())))
                .when()
                .post("auth")
                .prettyPeek()
                .body()
                .jsonPath()
                .get("token")
                .toString();
    }

    @BeforeEach
    void setUp() {
        id = String.valueOf(given()
                .log()
                .all()
                .header("Content-Type", "application/json")
                .body(requestCreateBooking)
                .expect()
                .statusCode(200)
                .when()
                .post("booking")
                .prettyPeek()
                .body()
                .jsonPath()
                .get("bookingid")
                .toString());
    }



    @Test
    void deleteBookingPositiveTest() {
        given()
                .log()
                .all()
                .header("Cookie","token=" +token)
                .when()
                .delete("booking/"+ id)
                .prettyPeek()
                .then()
                .statusCode(201);
    }

    @Test
    void deleteBookingWithAuthorizationPositiveTest() {
        given()
                .log()
                .all()
                .header("Authorization","Basic YWRtaW46cGFzc3dvcmQxMjM=")
                .when()
                .delete("booking/"+ id)
                .prettyPeek()
                .then()
                .statusCode(201);
    }

    @Test
    void deleteBookingWithoutAuthorizationNegativeTest() {
        given()
                .log()
                .all()
                .header("","")
                .when()
                .delete("booking/"+ id)
                .prettyPeek()
                .then()
                .statusCode(403);
    }
}
