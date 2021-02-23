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

public class VehiclesTest extends BaseTest {

    @DisplayName("Read all vehicles list")
    @Test
    public void getAllVehiclesList() {

        Response response = given()
                .spec(reqSpec)
                .when()
                .get(BASE_URL + "/" + VEHICLES)
                .then()
                .statusCode(HttpStatus.SC_OK)
                .extract()
                .response();

        JsonPath json = response.jsonPath();
        Assertions.assertThat(json.getString("count")).isEqualTo("39");
    }

    @DisplayName("Read vahicle with id=4")
    @Test
    public void getVehicleById() {

        Response response = given()
                .spec(reqSpec)
                .when()
                .get(BASE_URL + "/" + VEHICLES + "/4")
                .then()
                .statusCode(HttpStatus.SC_OK)
                .extract()
                .response();

        JsonPath json = response.jsonPath();
        Assertions.assertThat(json.getString("name")).isEqualTo("Sand Crawler");
        Assertions.assertThat(json.getList("films"))
                .hasSize(2)
                .containsExactly("https://swapi.py4e.com/api/films/1/",
                        "https://swapi.py4e.com/api/films/5/");
    }

    @DisplayName("Read vehicle with id=6 using path param")
    @Test
    public void getVehicleWithPathVariable() {

        Response response = given()
                .spec(reqSpec)
                .pathParam("vehicleId", 6)
                .when()
                .get(BASE_URL + "/" + VEHICLES + "/{vehicleId}")
                .then()
                .statusCode(HttpStatus.SC_OK)
                .extract()
                .response();

        JsonPath json = response.jsonPath();
        Assertions.assertThat(json.getString("name")).isEqualTo("T-16 skyhopper");
    }

    @DisplayName("Read vehicle using name search")
    @ParameterizedTest(name = "Name: {0}")
    @MethodSource("readVehicleNameData")
    public void getFilmWithQueryParam(String name) {

        Response response = given()
                .spec(reqSpec)
                .queryParam("search", name)
                .when()
                .get(BASE_URL + "/" + VEHICLES)
                .then()
                .statusCode(HttpStatus.SC_OK)
                .extract()
                .response();

        JsonPath json = response.jsonPath();
        Assertions.assertThat(json.getInt("count")).isEqualTo(1);
        Assertions.assertThat(json.getString("results.name")).contains(name);
    }

    private static Stream<Arguments> readVehicleNameData() {
        return Stream.of(
                Arguments.of("X-34 landspeeder"),
                Arguments.of("TIE/LN starfighter"),
                Arguments.of("Imperial Speeder Bike"));
    }
}
