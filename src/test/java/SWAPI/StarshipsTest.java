package SWAPI;

import SWAPI.model.Starships;
import base.BaseTest;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.apache.http.HttpStatus;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static io.restassured.RestAssured.given;

public class StarshipsTest extends BaseTest {

    @DisplayName("Read all starships list")
    @Test
    public void getAllStarshipsList() {

        Response response = given()
                .spec(reqSpec)
                .when()
                .get(BASE_URL + "/" + STARSHIPS)
                .then()
                .statusCode(HttpStatus.SC_OK)
                .extract()
                .response();

        JsonPath json = response.jsonPath();
        Assertions.assertThat(json.getString("count")).isEqualTo("37");
    }

    @DisplayName("Read starship with id=9")
    @Test
    public void getStarshipById() {

        Response response = given()
                .spec(reqSpec)
                .when()
                .get(BASE_URL + "/" + STARSHIPS + "/9")
                .then()
                .statusCode(HttpStatus.SC_OK)
                .extract()
                .response();

        JsonPath json = response.jsonPath();
        Assertions.assertThat(json.getString("name")).isEqualTo("Death Star");
        Assertions.assertThat(json.getString("model")).isEqualTo("DS-1 Orbital Battle Station");
    }

    @DisplayName("Read starship with id=17 using path param")
    @Test
    public void getFilmWithPathVariable() {

        Response response = given()
                .spec(reqSpec)
                .pathParam("starshipId", 17)
                .when()
                .get(BASE_URL + "/" + STARSHIPS + "/{starshipId}")
                .then()
                .statusCode(HttpStatus.SC_OK)
                .extract()
                .response();

        JsonPath json = response.jsonPath();
        Assertions.assertThat(json.getString("name")).isEqualTo("Rebel transport");
        Assertions.assertThat(json.getList("films"))
                .hasSize(2)
                .containsExactly("https://swapi.py4e.com/api/films/2/",
                        "https://swapi.py4e.com/api/films/3/");
    }

    @DisplayName("Read starship using name search")
    @ParameterizedTest(name = "Name: {0}")
    @MethodSource("readStarshipsData")
    public void getStarshipWithQueryParam(String name) {

        Response response = given()
                .spec(reqSpec)
                .queryParam("search", name)
                .when()
                .get(BASE_URL + "/" + STARSHIPS)
                .then()
                .statusCode(HttpStatus.SC_OK)
                .extract()
                .response();

        JsonPath json = response.jsonPath();
        Assertions.assertThat(json.getInt("count")).isEqualTo(1);
        Assertions.assertThat(json.getString("results.name")).contains(name);
    }

    private static Stream<Arguments> readStarshipsData() {
        return Stream.of(
                Arguments.of("Death Star"),
                Arguments.of("Calamari Cruiser"),
                Arguments.of("Slave 1"));
    }
}
