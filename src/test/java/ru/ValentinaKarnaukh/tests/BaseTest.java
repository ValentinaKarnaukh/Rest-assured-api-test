package ru.ValentinaKarnaukh.tests;

import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeAll;
import ru.ValentinaKarnaukh.dao.Bookingdates;
import ru.ValentinaKarnaukh.dao.CreateBookingRequest;
import ru.ValentinaKarnaukh.dao.CreateTokenRequest;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import static io.restassured.RestAssured.baseURI;

public abstract class BaseTest {
    protected static final String PROPERTIES_FILE_PATH = "src/test/resources/application.properties";
    protected static CreateTokenRequest request;
    protected static Bookingdates requestBookingDates;
    protected static CreateBookingRequest requestCreateBooking;
    protected static String token;
    String id;
    static Properties properties = new Properties();
    @BeforeAll
    static void beforeAll() throws IOException {
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
        RestAssured.filters(new AllureRestAssured());


        properties.load(new FileInputStream(PROPERTIES_FILE_PATH));
        baseURI = properties.getProperty("base.url");
    }
}
