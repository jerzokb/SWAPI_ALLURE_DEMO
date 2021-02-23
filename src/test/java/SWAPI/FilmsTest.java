package SWAPI;

import SWAPI.model.Films;
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

public class FilmsTest  extends BaseTest {

    private Films film;

    @DisplayName("Read all films list")
    @Test
    public void getAllFilmsList() {

        Response response = given()
                .spec(reqSpec)
                .when()
                .get(BASE_URL + "/" + FILMS)
                .then()
                .statusCode(HttpStatus.SC_OK)
                .extract()
                .response();

        JsonPath json = response.jsonPath();
        Assertions.assertThat(json.getString("count")).isEqualTo("7");
    }

    @DisplayName("Read film with id=5")
    @Test
    public void getFilmById() {

        Response response = given()
                .spec(reqSpec)
                .when()
                .get(BASE_URL + "/" + FILMS + "/5")
                .then()
                .statusCode(HttpStatus.SC_OK)
                .extract()
                .response();

        JsonPath json = response.jsonPath();
        Assertions.assertThat(json.getString("title")).isEqualTo("Attack of the Clones");
        Assertions.assertThat(json.getString("director")).isEqualTo("George Lucas");
        Assertions.assertThat(json.getString("release_date")).isEqualTo("2002-05-16");
    }

    @DisplayName("Read film with id=3 using path param")
    @Test
    public void getFilmWithPathVariable() {

        Response response = given()
                .spec(reqSpec)
                .pathParam("filmId", 3)
                .when()
                .get(BASE_URL + "/" + FILMS + "/{filmId}")
                .then()
                .statusCode(HttpStatus.SC_OK)
                .extract()
                .response();

        JsonPath json = response.jsonPath();
        Assertions.assertThat(json.getString("title")).isEqualTo("Return of the Jedi");
        Assertions.assertThat(json.getList("planets"))
                .hasSize(5)
                .containsExactly("https://swapi.py4e.com/api/planets/1/",
                        "https://swapi.py4e.com/api/planets/5/",
                        "https://swapi.py4e.com/api/planets/7/",
                        "https://swapi.py4e.com/api/planets/8/",
                        "https://swapi.py4e.com/api/planets/9/");
    }

    @DisplayName("Read film using title search")
    @ParameterizedTest(name = "Title: {0}")
    @MethodSource("readFilmTitleData")
    public void getFilmWithQueryParam(String title) {

        Response response = given()
                .spec(reqSpec)
                .queryParam("search", title)
                .when()
                .get(BASE_URL + "/" + FILMS)
                .then()
                .statusCode(HttpStatus.SC_OK)
                .extract()
                .response();

        JsonPath json = response.jsonPath();
        Assertions.assertThat(json.getInt("count")).isEqualTo(1);
        Assertions.assertThat(json.getString("results.title")).contains(title);
    }

    // SWAPI doesn't allow to create new records
    @DisplayName("Create film")
    @Test
    public void createFilm() {

        List<String> species = new ArrayList<>();
        species.add("Human");

        List<String> starships = new ArrayList<>();
        starships.add("Walther PPK");
        starships.add("Omega");

        List<String> vehicles = new ArrayList<>();
        vehicles.add("Aston Martin DB5");
        vehicles.add("JaguarXJ");
        vehicles.add("Mercedes-Benz W221");

        List<String> characters = new ArrayList<>();
        characters.add("Daniel Craig");
        characters.add("Eva Green");
        characters.add("Mads Mikkelsen");
        characters.add("Judi Dench");

        List<String> planets = new ArrayList<>();
        planets.add("Earth");

        film = new Films();
        film.setTitle("Casino Royale");
        film.setEpisode_id(21);
        film.setOpening_crawl("You Know My Name");
        film.setDirector("Martin Campbell");
        film.setProducer("Paul Haggis");
        film.setRelease_date("2006-11-14");
        film.setSpecies(species);
        film.setStarships(starships);
        film.setVehicles(vehicles);
        film.setCharacters(characters);
        film.setPlanets(planets);
        film.setUrl("https://jamesbond.com.pl/casino-royale/");
        film.setCreated("2006-11-14");
        film.setEdited("2006-11-17");

        Response response = given()
                .spec(reqSpec)
                .body(film)
                .when()
                .post(BASE_URL + "/" + FILMS)
                .then()
                .statusCode(HttpStatus.SC_INTERNAL_SERVER_ERROR)
                .extract()
                .response();
    }

    private static Stream<Arguments> readFilmTitleData() {
        return Stream.of(
                Arguments.of("A New Hope"),
                Arguments.of("The Empire Strikes Back"),
                Arguments.of("The Phantom Menace"));
    }
}
