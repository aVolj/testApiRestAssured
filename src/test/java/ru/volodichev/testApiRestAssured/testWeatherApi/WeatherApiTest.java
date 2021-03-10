package ru.volodichev.testApiRestAssured.testWeatherApi;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static io.restassured.RestAssured.given;

public class WeatherApiTest {

    public static final String API_KEY = "4e2171918825452593b105521211003";
    public static final String BASE_URI = "http://api.weatherapi.com/v1/";
    public static final String endPointQA = "astronomy.json";
    public static RequestSpecification spec; //переменная для подготовки первоначального состояния запросов

    @BeforeAll
    static void setUp() {
        spec = new RequestSpecBuilder()
            .setBaseUri(BASE_URI)
            .log(LogDetail.ALL)
            .setAccept(ContentType.JSON)
            .addQueryParam("key", API_KEY)
            .build();
    }

    @DisplayName("Результат GET-запроса возвращается код 200")
    @ParameterizedTest
    @ValueSource(strings = {"Moscow", "Orel", "Krasnodar", "St. Petersburg"})
    void apiReturnOk(String s) {
        given().spec(spec)
            .queryParam("q", s)
            .when().get(endPointQA)
            .then().statusCode(200); //проверяем код ответа от сервера
    }

    @DisplayName("Проверяем заголовок cache-control")
    @Test
    void cacheControl() {
        given().spec(spec)
            .queryParam("q", "Moscow")
            .when().get(endPointQA)
            .then().header("cache-control", "public, max-age=180");
    }

    @DisplayName("Проверка корректности страны для российских городов")
    @ParameterizedTest
    @ValueSource(strings = {"Moscow", "Orel", "Krasnodar", "St. Petersburg"})
    void correctCountryForCityOfRussia(String s) {
        given().spec(spec)
            .queryParam("q", s)
            .when().get(endPointQA)
            .then().body("location.country", Matchers.equalTo("Russia"));
    }
}
