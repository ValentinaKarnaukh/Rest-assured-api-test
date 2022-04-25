package ru.ValentinaKarnaukh.tests;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class PartialUpdateBookingTests {
    static String token;
    static String id;
    @BeforeAll
    static void beforeAll() {
        token = given()
                .log()
                .all()
                .header("Content-Type", "application/json")
                .body("{\n" +
                        "    \"username\" : \"admin\",\n" +
                        "    \"password\" : \"password123\"\n" +
                        "}")
                .expect()
                .statusCode(200)
                .body("token", is(Matchers.not(nullValue())))
                .when()
                .post("https://restful-booker.herokuapp.com/auth")
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
                .body("{\n" +
                        "    \"firstname\" : \"Jim\",\n" +
                        "    \"lastname\" : \"Brown\",\n" +
                        "    \"totalprice\" : 111,\n" +
                        "    \"depositpaid\" : true,\n" +
                        "    \"bookingdates\" : {\n" +
                        "        \"checkin\" : \"2018-01-01\",\n" +
                        "        \"checkout\" : \"2019-01-01\"\n" +
                        "    },\n" +
                        "    \"additionalneeds\" : \"Breakfast\"\n" +
                        "}")
                .expect()
                .statusCode(200)
                .when()
                .post("https://restful-booker.herokuapp.com/booking")
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
                .delete("https://restful-booker.herokuapp.com/booking/" + id)
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
                .body("{\n" +
                        "    \"firstname\" : \"Jane\"\n" +
                        "}")
                .when()
                .patch("https://restful-booker.herokuapp.com/booking/" + id)
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
                .body("{\n" +
                        "    \"firstname\" : \"Jim\",\n" +
                        "    \"lastname\" : \"Carry\",\n" +
                        "    \"totalprice\" : 111,\n" +
                        "    \"depositpaid\" : true,\n" +
                        "    \"bookingdates\" : {\n" +
                        "        \"checkin\" : \"2018-01-01\",\n" +
                        "        \"checkout\" : \"2019-01-01\"\n" +
                        "    },\n" +
                        "    \"additionalneeds\" : \"Breakfast\"\n" +
                        "}")
                .when()
                .patch("https://restful-booker.herokuapp.com/booking/"+ id)
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
                .body("{\n" +
                        "    \"firstname\" : \"Jim\",\n" +
                        "    \"lastname\" : \"Brown\",\n" +
                        "    \"totalprice\" : 111,\n" +
                        "    \"depositpaid\" : true,\n" +
                        "    \"bookingdates\" : {\n" +
                        "        \"checkin\" : \"2018-12-29\",\n" +
                        "        \"checkout\" : \"2019-01-01\"\n" +
                        "    },\n" +
                        "    \"additionalneeds\" : \"Breakfast\"\n" +
                        "}")
                .when()
                .patch("https://restful-booker.herokuapp.com/booking/"+ id)
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
                .body("{\n" +
                        "    \"firstname\" : \"David\",\n" +
                        "    \"lastname\" : \"Bowie\",\n" +
                        "    \"totalprice\" : 546,\n" +
                        "    \"depositpaid\" : false,\n" +
                        "    \"bookingdates\" : {\n" +
                        "        \"checkin\" : \"2022-04-29\",\n" +
                        "        \"checkout\" : \"2022-05-11\"\n" +
                        "    },\n" +
                        "    \"additionalneeds\" : \"Breakfast, lunch, dinner\"\n" +
                        "}")
                .when()
                .patch("https://restful-booker.herokuapp.com/booking/"+ id)
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
                .body("{\n" +
                        "    \"bookingdates\" : {\n" +
                        "        \"checkin\" : \"2022-03-22\",\n" +
                        "        \"checkout\" : \"2022-02-22\"\n" +
                        "}")
                .when()
                .patch("https://restful-booker.herokuapp.com/booking/" + id)
                .prettyPeek()
                .then()
                .statusCode(400);
    }

    @Test
    void totalpriceUpdateBookingNegativeTest() {
        given()
                .log()
                .all()
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .header("Authorization","Basic YWRtaW46cGFzc3dvcmQxMjM=")
                .body("{\n" +
                        "    \"totalprice\" : -25,\n" +

                        "}")
                .when()
                .patch("https://restful-booker.herokuapp.com/booking/"+ id)
                .prettyPeek()
                .then()
                .statusCode(400);
    }

    @Test
    void firstnameUpdateBookingWithoutAuthorizationNegativeTest() {
        given()
                .log()
                .all()
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .header("","")
                .body("{\n" +
                        "    \"firstname\" : \"Tom\",\n" +
                        "    \"lastname\" : \"Brown\",\n" +
                        "    \"totalprice\" : 111,\n" +
                        "    \"depositpaid\" : true,\n" +
                        "    \"bookingdates\" : {\n" +
                        "        \"checkin\" : \"2018-01-01\",\n" +
                        "        \"checkout\" : \"2019-01-01\"\n" +
                        "    },\n" +
                        "    \"additionalneeds\" : \"Breakfast\"\n" +
                        "}")
                .when()
                .patch("https://restful-booker.herokuapp.com/booking/"+ id)
                .prettyPeek()
                .then()
                .statusCode(403);
    }
}
