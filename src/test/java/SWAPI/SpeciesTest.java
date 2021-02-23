package SWAPI;

import SWAPI.model.Species;
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

public class SpeciesTest extends BaseTest {

    @DisplayName("Read all species list")
    @Test
    public void getAllSpeciesList() {

        Response response = given()
                .spec(reqSpec)
                .when()
                .get(BASE_URL + "/" + SPECIES)
                .then()
                .statusCode(HttpStatus.SC_OK)
                .extract()
                .response();

        JsonPath json = response.jsonPath();
        Assertions.assertThat(json.getString("count")).isEqualTo("37");
    }

    @DisplayName("Read specie with id=3")
    @Test
    public void getSpecieById() {

        Response response = given()
                .spec(reqSpec)
                .when()
                .get(BASE_URL + "/" + SPECIES + "/3")
                .then()
                .statusCode(HttpStatus.SC_OK)
                .extract()
                .response();

        JsonPath json = response.jsonPath();
        Assertions.assertThat(json.getString("name")).isEqualTo("Wookiee");
        Assertions.assertThat(json.getString("language")).isEqualTo("Shyriiwook");
    }

    @DisplayName("Read specie with id=7 using path param")
    @Test
    public void getSpecieWithPathVariable() {

        Response response = given()
                .spec(reqSpec)
                .pathParam("specieId", 7)
                .when()
                .get(BASE_URL + "/" + SPECIES + "/{specieId}")
                .then()
                .statusCode(HttpStatus.SC_OK)
                .extract()
                .response();

        JsonPath json = response.jsonPath();
        Assertions.assertThat(json.getString("name")).isEqualTo("Trandoshan");
    }

    @DisplayName("Read specie using name search")
    @ParameterizedTest(name = "Name: {0}")
    @MethodSource("readSpeciesNameData")
    public void getSpecieWithQueryParam(String name) {

        Response response = given()
                .spec(reqSpec)
                .queryParam("search", name)
                .when()
                .get(BASE_URL + "/" + SPECIES)
                .then()
                .statusCode(HttpStatus.SC_OK)
                .extract()
                .response();

        JsonPath json = response.jsonPath();
        Assertions.assertThat(json.getInt("count")).isEqualTo(1);
        Assertions.assertThat(json.getString("results.name")).contains(name);
    }

    private static Stream<Arguments> readSpeciesNameData() {
        return Stream.of(
                Arguments.of("Rodian"),
                Arguments.of("Gungan"),
                Arguments.of("Kel Dor"));
    }
}
