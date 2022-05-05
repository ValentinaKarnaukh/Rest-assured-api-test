package ru.ValentinaKarnaukh.tests;

import io.qameta.allure.*;
import io.restassured.response.Response;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.ValentinaKarnaukh.dao.CreateTokenRequest;
import ru.ValentinaKarnaukh.dao.CreateTokenResponse;

import static io.restassured.RestAssured.baseURI;
import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
@Severity(SeverityLevel.BLOCKER)
@DisplayName("Create token")
@Feature("Generate a token")
@Story("Generate a user token")
public class CreateTokenTests extends BaseTest{
    @BeforeAll
    static void beforeSuite() {
        request = CreateTokenRequest.builder()
                .username("admin")
                .password("password123")
                .build();
    }
    @Test
    @DisplayName("Creation token (P)")
    @Description("Positive test - Create token")
    @Step("Create token")
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
                .post(baseURI+"auth")
                .prettyPeek()
                .then()
                .extract()
                .as(CreateTokenResponse.class);
        assertThat(response, is(not(nullValue())));
        assertThat(response.getToken().length(), equalTo(15));
    }

    @Test
    @DisplayName("Creation token with wrong password (N)")
    @Description("Negative test - Create a token with a wrong password")
    @Step("Create token with wrong password")
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
                .post(baseURI+"auth")
                .prettyPeek()
                .then()
                .statusCode(200)
                .body("reason", CoreMatchers.equalTo("Bad credentials"));


    }

    @Test
    @DisplayName("Creation token with wrong username (N)")
    @Description("Negative test - Create a token with a wrong username")
    @Step("Create token with wrong username")
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
                .post(baseURI+"auth")
                .prettyPeek();
        assertThat(response.statusCode(), equalTo(200));
        assertThat(response.body().jsonPath().get("reason"), containsStringIgnoringCase("Bad credentials"));
    }
}