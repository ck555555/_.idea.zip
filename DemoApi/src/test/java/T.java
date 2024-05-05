import com.jayway.jsonpath.DocumentContext;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.internal.common.assertion.Assertion;
import static org.hamcrest.Matchers.equalTo;
import io.qameta.allure.*;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.junit.jupiter.api.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import static io.restassured.RestAssured.given;
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)


public class T extends Metod {
    public String Token;
    public String Id;

    @Epic("Тестируем получение токена")
    @Feature("TokenGet")
    @Story("Получаем токен, кладем его в переменную для использования в следующих запросах")

    @Test
    @Order(1)
    public void TokenGet() throws IOException {
        JsonPath response = given()
                .filter(new AllureRestAssured())
                .log().all()
                .header("Authorization", "Bearer ")
                .contentType(ContentType.JSON)
                .when()

                .body("{\n" +
                        "    \"username\" : \"admin\",\n" +
                        "    \"password\" : \"password123\"\n" +
                        "}")
                .post("https://restful-booker.herokuapp.com/auth")
                .prettyPeek()
                .then()
                .statusCode(200)
                .extract().jsonPath();
        Token = response.getString("token");
        InputProp("src\\test\\resources\\properties.properties","Token",Token);
    }

    @Test
    @Epic("Позитивный кейс 1. Создание карточки")
    @Feature("Create")
    @Story("Создаем карточку, проверяем поля. Сохраняем ID в переменную для дальнейших тестов")
    @Order(2)
    public void Create() throws IOException {

        JsonPath response1 = given()
                .filter(new AllureRestAssured())
                .log().all()
                .header("Authorization", "Basic " + ReadProp ("src\\test\\resources\\properties.properties", "Token"))
                .contentType(ContentType.JSON)
                .when()

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
                .post("https://restful-booker.herokuapp.com/booking")
                .prettyPeek()
                .then()
                .statusCode(200)
                .extract().jsonPath();

        Id = response1.getString("bookingid");
        InputProp("src\\test\\resources\\properties.properties","bookingid",Id);

        Assertions.assertEquals(response1.getString("booking.firstname"),"Jim","FirstName не равно Jim");
        Assertions.assertEquals(response1.getString("booking.lastname"),"Brown","lastname не равно Brown");
        Assertions.assertEquals(response1.getInt("booking.totalprice"),111,"totalprice не равно 111");
        Assertions.assertEquals(response1.getBoolean("booking.depositpaid"),true,"depositpaid не равно true");
        Assertions.assertEquals(response1.getString("booking.additionalneeds"),"Breakfast","additionalneeds не равно Breakfast");
        Assertions.assertEquals(response1.getString("booking.bookingdates.checkin"), "2018-01-01", "checkin не соответствует искомому");
        Assertions.assertEquals(response1.getString("booking.bookingdates.checkout"), "2019-01-01", "checkout не соответствует искомомут");

    }


    @Test
    @Epic("Позитивный кейс 1. Поиск карточки по ID")
    @Feature("Find")
    @Story("Ищем карточку по ID, проверяем поля")
    @Order(3)
    public void Find() throws IOException {

        JsonPath response1 = given()
                .filter(new AllureRestAssured())
                .log().all()
                .header("Authorization", "Basic " + ReadProp ("src\\test\\resources\\properties.properties", "Token"))
                .contentType(ContentType.JSON)
                .when()
                .get(("https://restful-booker.herokuapp.com/booking/")+ReadProp ("src\\test\\resources\\properties.properties", "bookingid"))
                .prettyPeek()
                .then()
                .statusCode(200)
                .extract().jsonPath();
        Assertions.assertEquals(response1.getString("bookingid"),Id,"bookingid не равно ID");
        Assertions.assertEquals(response1.getString("firstname"),"Jim","FirstName не равно Jim");
        Assertions.assertEquals(response1.getString("lastname"),"Brown","lastname не равно Brown");
        Assertions.assertEquals(response1.getInt("totalprice"),111,"totalprice не равно 111");
        Assertions.assertEquals(response1.getBoolean("depositpaid"),true,"depositpaid не равно true");
        Assertions.assertEquals(response1.getString("additionalneeds"),"Breakfast","additionalneeds не равно Breakfast");
        Assertions.assertEquals(response1.getString("bookingdates.checkin"), "2018-01-01", "checkin не соответствует искомому");
        Assertions.assertEquals(response1.getString("bookingdates.checkout"), "2019-01-01", "checkout не соответствует искомомут");
}


    @Test
    @Epic("Позитивный кейс 1. Удаление карточки по ID")
    @Feature("Delete")
    @Story("Удаляем карточку с нужным ID")
    @Order(4)
    public void Delete() throws IOException {

        JsonPath response1 = given()
                .filter(new AllureRestAssured())
                .log().all()
                .header("Authorization", "Basic " + ReadProp ("src\\test\\resources\\properties.properties", "Token"))
                .contentType(ContentType.JSON)
                .cookie( "token", ReadProp ("src\\test\\resources\\properties.properties", "Token"))
                .when()
                .delete(("https://restful-booker.herokuapp.com/booking/")+ReadProp ("src\\test\\resources\\properties.properties", "bookingid"))
                .prettyPeek()
                .then()
                .statusCode(201)
                .extract().jsonPath();

    }

    @Test
    @Epic("Позитивный кейс 1. Проверяем удаление карточки по ID")
    @Feature("CheckDelete")
    @Story("Проверяем, что карточка, с нужным ID удалена и отсутствует в БД")
    @Order(5)
    public void CheckDelete() throws IOException {

        JsonPath response1 = given()
                .filter(new AllureRestAssured())
                .log().all()
                .header("Authorization", "Basic " + ReadProp ("src\\test\\resources\\properties.properties", "Token"))
                .contentType(ContentType.JSON)
                .when()
                .get(("https://restful-booker.herokuapp.com/booking/")+ReadProp ("src\\test\\resources\\properties.properties", "bookingid"))
                .prettyPeek()
                .then()
                .statusCode(404)
                .extract().jsonPath();


    }
}


