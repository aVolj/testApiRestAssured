package ru.volodichev.testApiRestAssured.testNewsApi;

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
import static org.hamcrest.Matchers.hasItems;

/**
 * todo Document type NewApiTest
 */
public class NewsApiTest {
    public static final String API_KEY = "4aa771e751334d04a0137e2d30bf70d9";
    public static final String BASE_URI = "https://newsapi.org/v2/";
    public static final String endPointQA = "everything";
    public static RequestSpecification spec; //переменная для подготовки первоначального состояния запросов

    @BeforeAll
    static void setUp() {
        spec = new RequestSpecBuilder()
            .setBaseUri(BASE_URI)
            .log(LogDetail.ALL)
            .setAccept(ContentType.JSON)
            .addQueryParam("apikey", API_KEY)
            .build();
    }

    @DisplayName("Результат GET-запроса возвращается код 200")
    @ParameterizedTest
    @ValueSource(strings = {"QA", "QC", "Junit", "Java"})
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
            .queryParam("q", "QA")
            .when().get(endPointQA)
            .then().header("cache-control", "no-cache");
    }

    @ParameterizedTest
    @ValueSource(strings = {"Habr.com", "Tproger.ru"}) //Статей Tproger в данном ответе нет
    public void nameSourceContains(String s) {

        given().spec(spec)
            .queryParam("q", "QA")
            .when().get(endPointQA)
            .then()
            .assertThat().body("articles.source.name",hasItems(s));
    }


    @DisplayName("totalResults not Null ")
    @Test
    void totalResultsNotNull() {
        given().spec(spec)
            .queryParam("q", "QA")
            .when().get(endPointQA)
            .then().body("totalResults", Matchers.notNullValue());
    }
}


