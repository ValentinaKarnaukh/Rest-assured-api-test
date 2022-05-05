package ru.ValentinaKarnaukh.tests;

import io.qameta.allure.*;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.ValentinaKarnaukh.dao.Bookingdates;
import ru.ValentinaKarnaukh.dao.CreateBookingRequest;
import ru.ValentinaKarnaukh.dao.CreateTokenRequest;

import static io.restassured.RestAssured.baseURI;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

@Severity(SeverityLevel.BLOCKER)
@DisplayName("Delete a booking")
@Story("Delete a booking")
@Feature("Tests for booking deletion")
public class DeleteBookingTests extends BaseTest {
    @BeforeAll
    static void beforeSuite() {
        request = CreateTokenRequest.builder()
                .username("admin")
                .password("password123")
                .build();
        requestBookingDates = Bookingdates.builder()
                .checkin("2018-01-01")
                .checkout("2019-01-01")
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
                .post(baseURI + "auth")
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
                .post(baseURI + "booking")
                .prettyPeek()
                .body()
                .jsonPath()
                .get("bookingid")
                .toString());
    }

    @Test
    @DisplayName("Delete a booking with authorization via cookie (P)")
    @Description("Positive test - delete a booking with authorization via cookie")
    @Step("Delete a booking with authorization via cookie")
    void deleteBookingPositiveTest() {
        given()
                .log()
                .all()
                .header("Cookie", "token=" + token)
                .when()
                .delete(baseURI + "booking/" + id)
                .prettyPeek()
                .then()
                .statusCode(201);
    }

    @Test
    @DisplayName("Delete a booking with authorization via token (P)")
    @Description("Positive test - delete a booking with authorization via token")
    @Step("Delete a booking with authorization via token")
    void deleteBookingWithAuthorizationPositiveTest() {
        given()
                .log()
                .all()
                .header("Authorization", "Basic YWRtaW46cGFzc3dvcmQxMjM=")
                .when()
                .delete(baseURI + "booking/" + id)
                .prettyPeek()
                .then()
                .statusCode(201);
    }

    @Test
    @DisplayName("Delete a booking without authorization (N)")
    @Description("Negative test - delete a booking without authorization")
    @Step("Delete a booking without authorization")
    void deleteBookingWithoutAuthorizationNegativeTest() {
        given()
                .log()
                .all()
                .header("", "")
                .when()
                .delete(baseURI + "booking/" + id)
                .prettyPeek()
                .then()
                .statusCode(403);
    }
}