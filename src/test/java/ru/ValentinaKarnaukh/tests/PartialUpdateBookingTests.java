package ru.ValentinaKarnaukh.tests;

import io.qameta.allure.*;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.*;
import ru.ValentinaKarnaukh.dao.Bookingdates;
import ru.ValentinaKarnaukh.dao.CreateBookingRequest;
import ru.ValentinaKarnaukh.dao.CreateTokenRequest;

import static io.restassured.RestAssured.baseURI;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
@Severity(SeverityLevel.BLOCKER)
@DisplayName("Updating information booking")
@Story("Updating information booking")
@Feature("Tests for update information booking")
public class PartialUpdateBookingTests extends BaseTest{
    @BeforeAll
    static void beforeSuite() {
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
    @DisplayName("Update firstname in booking, authorization - cookie (P)")
    @Description("Positive test - update firstname in booking, authorization - cookie")
    @Step("Update firstname in booking, authorization - cookie")
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
    @DisplayName("Update lastname in booking, authorization - cookie (P)")
    @Description("Positive test - update lastname in booking, authorization - cookie")
    @Step("Update lastname in booking, authorization - cookie")
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
    @DisplayName("Update checkin in booking, authorization - cookie (P)")
    @Description("Positive test - update checkin in booking, authorization - cookie")
    @Step("Update checkin in booking, authorization - cookie")
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
    @DisplayName("Update all booking, authorization - token (P)")
    @Description("Positive test - update all booking, authorization - token")
    @Step("Update all booking, authorization - token")
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
    @DisplayName("Update booking dates in booking, authorization - cookie (N)")
    @Description("Negative test - update booking dates in booking, authorization - cookie")
    @Step("Update booking dates in booking, authorization - cookie")
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
    @DisplayName("Update totalprice in booking, authorization - token (N)")
    @Description("Negative test - update totalprice in booking, authorization - token")
    @Step("Update totalprice in booking, authorization - token")
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
    @DisplayName("Update firstname in booking without authorization (N)")
    @Description("Negative test - update firstname in booking without authorization")
    @Step("Update firstname in booking without authorization")
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