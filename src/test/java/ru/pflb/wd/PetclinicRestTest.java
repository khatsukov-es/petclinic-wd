package ru.pflb.wd;

import org.apache.commons.lang3.RandomStringUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Test;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static io.restassured.RestAssured.given;
import static org.apache.commons.lang3.StringUtils.capitalize;
import static org.hamcrest.Matchers.*;

/**
 * @author <a href="mailto:8445322@gmail.com">Ivan Bonkin</a>.
 */
public class PetclinicRestTest {

    private static final String BASE_URI = "http://localhost:9966/petclinic";

    /**
     * Должен возвращать JSON объект по фамилии уже существующего клиента.
     */
    public JSONObject findOwner(String lastName) {
// получение ответа от сервера
        String serverResponseBody = given()
                .contentType("application/json")
                .accept("application/json")
                .baseUri(BASE_URI)
                .when()
// GET /api/owners - все хозяева
                .get("/api/owners")
                .then()
// ожилаение 2xx кодов - успешных
                .statusCode(both(greaterThanOrEqualTo(200)).and(lessThan(300)))
// извлечение тела ответа сервера как JSONArray
                .extract().body().asString();
// преобразование ответа сервера к типу JSONArray
        JSONArray array = new JSONArray(serverResponseBody);


// see https://stackoverflow.com/a/7634559
        for(int n = 0; n < array.length(); n++) {
            JSONObject object = array.getJSONObject(n);
// нашли внутри JSONArray объект по фамилии lastName
            if (lastName.equals(object.getString("lastName"))) {
                return object;
            }
        }

        throw new IllegalArgumentException("Владелец \"" + lastName + "\" не был найден");
    }

    @Test
    public void shouldFindOwnerAndAddPet() {

// получение JSON описания хозяина с фамилией Franklin
        JSONObject user = findOwner("Franklin");

// генерация произвольного имени домашнего животного в формате Xxxxx.
        String name = capitalize(RandomStringUtils.randomAlphabetic(5).toLowerCase());

// составления тела запроса на добавление нового домашенго животного
        JSONObject petJsonObj = new JSONObject()
// id = null
                .put("id", JSONObject.NULL)
// name = сгенерированное имя
                .put("name", name)
// дату рождения выбираем равной текущий день минус 7 дней
                .put("birthDate", LocalDate.now().minusDays(7).format(DateTimeFormatter.ofPattern("yyyy/MM/dd")))
// в поле тип вводим - bird
                .put("type", new JSONObject().put("id", 5).put("name", "bird"))
// в поле хозяина вводим JSON описания хозяина
                .put("owner", user);

        Integer petId = given()
// Content-type для запроса - формат body запроса
                .contentType("application/json")
// тело запроса
                .body(petJsonObj.toString())
// URI REST сервера
                .baseUri(BASE_URI)
                .when()
// путь относительно REST сервера
                .post("/api/pets")
                .then()
// ожилаение 2xx кодов - успешных
                .statusCode(equalTo(201))
// проверка того, что в ответе сервера был создан тот же питомец
                .body("name", equalTo(name))
// возвращение id созданного питомца
                .extract().path("id");

        System.out.println("Добавлен питомец с id=\"" + petId + "\"");

// http://localhost:9966/petclinic/api/owners/*/lastname/Franklin

    }
    @Test
    public void addOwnerAndFindHim() {

// генерация аттрибутов
        String rndFirst = capitalize(RandomStringUtils.randomAlphabetic(5).toLowerCase());
        String rndLast = capitalize(RandomStringUtils.randomAlphabetic(5).toLowerCase());
        String rndAddress = capitalize(RandomStringUtils.randomAlphabetic(5).toLowerCase());
        String rndCity = capitalize(RandomStringUtils.randomAlphabetic(5).toLowerCase());
        String rndTelephone = RandomStringUtils.randomNumeric(5);

// составления тела запроса на добавление нового ownera
        JSONObject petJsonObj = new JSONObject()
// id = null
                .put("id", JSONObject.NULL)
                .put("firstName",rndFirst)
                .put("lastName",rndLast)
                .put("address",rndAddress)
                .put("city",rndCity)
                .put("telephone",rndTelephone);
// в поле хозяина вводим JSON описания хозяина
// .put("owner",user);

        Integer ownerId = given()
// Content-type для запроса - формат body запроса
                .contentType("application/json")
// тело запроса
                .body(petJsonObj.toString())
// URI REST сервера
                .baseUri(BASE_URI)
                .when()
// путь относительно REST сервера
                .post("/api/owners")
                .then()
// ожилаение 2xx кодов - успешных
                .statusCode(equalTo(201))
// проверка того, что в ответе сервера был создан тот же юзер
                .body("firstName", equalTo(rndFirst))
// возвращение id созданного юзера
                .extract().path("id");
        JSONObject owner = findOwner(rndLast);

        System.out.println("Добавлен owner с id=\"" + ownerId + "\"");

    }
}