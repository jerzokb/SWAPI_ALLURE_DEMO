package SWAPI;

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

public class PlanetsTest extends BaseTest {

    @DisplayName("Read all planets list")
    @Test
    public void getAllPlanetsList() {

        Response response = given()
                .spec(reqSpec)
                .when()
                .get(BASE_URL + "/" + PLANETS)
                .then()
                .statusCode(HttpStatus.SC_OK)
                .extract()
                .response();

        JsonPath json = response.jsonPath();
        Assertions.assertThat(json.getString("count")).isEqualTo("61");
    }

    @DisplayName("Read planet with id=13")
    @Test
    public void getPlanetById() {

        Response response = given()
                .spec(reqSpec)
                .when()
                .get(BASE_URL + "/" + PLANETS + "/13")
                .then()
                .statusCode(HttpStatus.SC_OK)
                .extract()
                .response();

        JsonPath json = response.jsonPath();
        Assertions.assertThat(json.getString("name")).isEqualTo("Mustafar");
        Assertions.assertThat(json.getString("climate")).isEqualTo("hot");
        Assertions.assertThat(json.getString("terrain")).isEqualTo("volcanoes, lava rivers, mountains, caves");
    }

    @DisplayName("Read planet with id=27 using path param")
    @Test
    public void getPlanetWithPathVariable() {

        Response response = given()
                .spec(reqSpec)
                .pathParam("planetId", 27)
                .when()
                .get(BASE_URL + "/" + PLANETS + "/{planetId}")
                .then()
                .statusCode(HttpStatus.SC_OK)
                .extract()
                .response();

        JsonPath json = response.jsonPath();
        Assertions.assertThat(json.getString("name")).isEqualTo("Ord Mantell");
        Assertions.assertThat(json.getList("films"))
                .hasSize(1)
                .containsExactly("https://swapi.py4e.com/api/films/2/");
    }

    @DisplayName("Read planet using name search")
    @ParameterizedTest(name = "Name: {0}")
    @MethodSource("readPlanetNameData")
    public void getPlanetWithQueryParam(String name) {

        Response response = given()
                .spec(reqSpec)
                .queryParam("search", name)
                .when()
                .get(BASE_URL + "/" + PLANETS)
                .then()
                .statusCode(HttpStatus.SC_OK)
                .extract()
                .response();

        JsonPath json = response.jsonPath();
        Assertions.assertThat(json.getInt("count")).isEqualTo(1);
        Assertions.assertThat(json.getString("results.name")).contains(name);
    }

    private static Stream<Arguments> readPlanetNameData() {
        return Stream.of(
                Arguments.of("Dagobah"),
                Arguments.of("Dantooine"),
                Arguments.of("Champala"));
    }
}
