package SWAPI;

import SWAPI.model.People;
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

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static io.restassured.RestAssured.given;

public class PeopleTest extends BaseTest {

    private People person;

    @DisplayName("Read all people list")
    @Test
    public void getPeopleList() {

        Response response = given()
                .spec(reqSpec)
                .when()
                .get(BASE_URL + "/" + PEOPLE)
                .then()
                .statusCode(HttpStatus.SC_OK)
                .extract()
                .response();

        JsonPath json = response.jsonPath();
        Assertions.assertThat(json.getString("count")).isEqualTo("87");
    }

    @DisplayName("Read person with id=1")
    @Test
    public void getOnePerson() {

        Response response = given()
                .spec(reqSpec)
                .when()
                .get(BASE_URL + "/" + PEOPLE + "/1")
                .then()
                .statusCode(HttpStatus.SC_OK)
                .extract()
                .response();

        JsonPath json = response.jsonPath();
        Assertions.assertThat(json.getString("name")).isEqualTo("Luke Skywalker");
        List<String> films = json.getList("films");
        Assertions.assertThat(films).hasSize(5);
    }

    @DisplayName("Read person with id=2 using path param")
    @Test
    public void getPersonWithPathVariable() {

        Response response = given()
                .spec(reqSpec)
                .pathParam("personId", 2)
                .when()
                .get(BASE_URL + "/" + PEOPLE + "/{personId}")
                .then()
                .statusCode(HttpStatus.SC_OK)
                .extract()
                .response();

        JsonPath json = response.jsonPath();
        Assertions.assertThat(json.getString("name")).isEqualTo("C-3PO");
    }

    @DisplayName("Read person using full name search")
    @ParameterizedTest(name = "Name: {0}")
    @MethodSource("readPersonFullNameData")
    public void getPersonWithQueryParam(String name) {

        Response response = given()
                .spec(reqSpec)
                .queryParam("search", name)
                .when()
                .get(BASE_URL + "/" + PEOPLE)
                .then()
                .statusCode(HttpStatus.SC_OK)
                .extract()
                .response();

        JsonPath json = response.jsonPath();
        Assertions.assertThat(json.getInt("count")).isEqualTo(1);
        Assertions.assertThat(json.getString("results.name")).contains(name);
    }

    // SWAPI doesn't allow to create new records
    @DisplayName("Create person")
    @Test
    public void createPerson() {

        List<String> films = new ArrayList<>();
        films.add("Spectre");
        films.add("Skyfall");

        List<String> species = new ArrayList<>();
        species.add("Human");

        List<String> starships = new ArrayList<>();
        starships.add("Walther PPK");
        starships.add("Omega");

        List<String> vehicles = new ArrayList<>();
        vehicles.add("Aston Martin DB5");
        vehicles.add("JaguarXJ");
        vehicles.add("Mercedes-Benz W221");

        person = new People();
        person.setName("James Bond");
        person.setBirth_year("1968");
        person.setEye_color("blue");
        person.setGender("male");
        person.setHair_color("blond");
        person.setHeight("178");
        person.setMass("88");
        person.setSkin_color("pale");
        person.setHomeworld("https://swapi.py4e.com/api/planets/1/");
        person.setFilms(films);
        person.setSpecies(species);
        person.setStarships(starships);
        person.setVehicles(vehicles);
        person.setUrl("https://jamesbond.com.pl/");
        person.setCreated("2012-10-23T01:01:01.644000Z");
        person.setEdited("2012-10-26T01:01:01.644000Z");

        Response response = given()
                .spec(reqSpec)
                .body(person)
                .when()
                .post(BASE_URL + "/" + PEOPLE)
                .then()
                .statusCode(HttpStatus.SC_INTERNAL_SERVER_ERROR)
                .extract()
                .response();
    }

    private static Stream<Arguments> readPersonFullNameData() {
        return Stream.of(
                Arguments.of("R2-D2"),
                Arguments.of("Darth Vader"),
                Arguments.of("Obi-Wan Kenobi"));
    }
}
