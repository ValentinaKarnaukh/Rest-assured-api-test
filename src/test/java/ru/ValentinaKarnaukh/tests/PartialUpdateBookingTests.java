package ru.ValentinaKarnaukh.tests;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.ValentinaKarnaukh.dao.Bookingdates;
import ru.ValentinaKarnaukh.dao.CreateBookingRequest;
import ru.ValentinaKarnaukh.dao.CreateTokenRequest;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import static io.restassured.RestAssured.baseURI;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class PartialUpdateBookingTests {
    private static final String PROPERTIES_FILE_PATH = "src/test/resources/application.properties";
    private static CreateTokenRequest request;
    private static Bookingdates requestBookingDates;
    private static CreateBookingRequest requestCreateBooking;
    static Properties properties = new Properties();
    static String token;
    static String id;
    @BeforeAll
    static void beforeAll() throws IOException {
        properties.load(new FileInputStream(PROPERTIES_FILE_PATH));
        baseURI = properties.getProperty("base.url");
        requestBookingDates = Bookingdates.builder()
                .checkin("2018-01-01")
                .checkout("2019-01-01")
                .build();
        request = CreateTokenRequest.builder()
                .username("admin")
                .password("password123")
                .build();
        requestCreateBooking = CreateBookingRequest.builder()
                .firstname("Jim")
                .lastname("Brown")
                .totalprice(Integer.valueOf("111"))
                .depositpaid(Boolean.valueOf("true"))
                .bookingdates(requestBookingDates)
                .additionalneeds("Breakfast")
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
                .post(baseURI+"auth")
                .prettyPeek()
                .body()
                .jsonPath()
                .get("token")
                .toString();
    }

    @BeforeEach
    void setUp() {
        id = given()
                .log()
                .all()
                .header("Content-Type", "application/json")
                .body(requestCreateBooking)
                .expect()
                .statusCode(200)
                .when()
                .post(baseURI+"booking")
                .prettyPeek()
                .body()
                .jsonPath()
                .get("bookingid")
                .toString();
    }

    @AfterEach
    void tearDown() {
        given()
                .log()
                .method()
                .log()
                .uri()
                .log()
                .body()
                .header("Authorization", "Basic YWRtaW46cGFzc3dvcmQxMjM=")
                .when()
                .delete(baseURI+"booking/"+ id)
                .prettyPeek()
                .then()
                .statusCode(201);
    }


    @Test
    void firstnameUpdateBookingPositiveTest() {
        given()
                .log()
                .all()
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .header("Cookie", "token=" + token)
                .body(requestCreateBooking.withFirstname("Jane"))
                .when()
                .patch(baseURI+"booking/" + id)
                .prettyPeek()
                .then()
                .statusCode(200)
                .body("firstname", equalTo("Jane"));
    }

    @Test
    void lastnameUpdateBookingPositiveTest() {
        given()
                .log()
                .all()
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .header("Cookie","token=" + token)
                .body(requestCreateBooking.withLastname("Carry"))
                .when()
                .patch(baseURI+"booking/"+ id)
                .prettyPeek()
                .then()
                .statusCode(200)
                .body("firstname", equalTo("Jim"))
                .body("lastname", equalTo("Carry"))
                .body("totalprice", equalTo(Integer.valueOf("111")))
                .body("depositpaid", equalTo(Boolean.valueOf("true")))
                .body("bookingdates.checkin", equalTo("2018-01-01"))
                .body("bookingdates.checkout", equalTo("2019-01-01"))
                .body("additionalneeds", equalTo("Breakfast"));
    }

    @Test
    void checkInUpdateBookingPositiveTest() {
        given()
                .log()
                .all()
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .header("Cookie","token=" + token)
                .body(requestCreateBooking.withBookingdates(requestBookingDates.withCheckin("2018-12-29")))
                .when()
                .patch(baseURI+"booking/"+ id)
                .prettyPeek()
                .then()
                .statusCode(200)
                .body("bookingdates.checkin", equalTo("2018-12-29"));
    }

    @Test
    void allUpdateBookingPositiveTest() {
        given()
                .log()
                .all()
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .header("Authorization","Basic YWRtaW46cGFzc3dvcmQxMjM=")
                .body(requestCreateBooking.withFirstname("David")
                                .withLastname("Bowie")
                                .withTotalprice(Integer.valueOf("546"))
                                .withDepositpaid(Boolean.valueOf("false"))
                                .withBookingdates(requestBookingDates.withCheckin("2022-04-29").withCheckout("2022-05-11"))
                                .withAdditionalneeds("Breakfast, lunch, dinner"))
                .when()
                .patch(baseURI+"booking/"+ id)
                .prettyPeek()
                .then()
                .statusCode(200)
                .body("firstname", equalTo("David"))
                .body("lastname", equalTo("Bowie"))
                .body("totalprice", equalTo(Integer.valueOf("546")))
                .body("depositpaid", equalTo(Boolean.valueOf("false")))
                .body("bookingdates.checkin", equalTo("2022-04-29"))
                .body("bookingdates.checkout", equalTo("2022-05-11"))
                .body("additionalneeds", equalTo("Breakfast, lunch, dinner"));
    }

    @Test
    void bookingDatesUpdateBookingNegativeTest() {
        given()
                .log()
                .all()
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .header("Cookie", "token=" + token)
                .body(requestCreateBooking.withBookingdates(requestBookingDates.withCheckin("2022-03-22").withCheckout("2022-02-22")))
                .when()
                .patch(baseURI+"booking/" + id)
                .prettyPeek()
                .then()
                .statusCode(200);
    }

    @Test
    void totalpriceUpdateBookingNegativeTest() {
        given()
                .log()
                .all()
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .header("Authorization","Basic YWRtaW46cGFzc3dvcmQxMjM=")
                .body(requestCreateBooking.withTotalprice(Integer.valueOf("-25")))
                .when()
                .patch(baseURI+"booking/" + id)
                .prettyPeek()
                .then()
                .statusCode(200);
    }

    @Test
    void firstnameUpdateBookingWithoutAuthorizationNegativeTest() {
        given()
                .log()
                .all()
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .header("","")
                .body(requestCreateBooking.withFirstname("Tom"))
                .when()
                .patch(baseURI+"booking/" + id)
                .prettyPeek()
                .then()
                .statusCode(403);
    }
}
