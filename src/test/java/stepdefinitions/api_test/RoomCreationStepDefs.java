package stepdefinitions.api_test;


import com.github.javafaker.Faker;
import com.google.gson.Gson;
import io.cucumber.core.internal.com.fasterxml.jackson.core.JsonProcessingException;
import io.cucumber.core.internal.com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import pojos.RoomPojo;

import java.util.HashMap;
import java.util.Map;

import static base_urls.MedunnaBaseUrl.spec;
import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class RoomCreationStepDefs {
    Response response;
    int roomNumber;
    Map<String, Object> expectedDataMap;
    RoomPojo expectedDataPojo;

    @Given("send post request for creating room")
    public void send_post_request_for_creating_room() {
        //Set the url -->https://medunna.com/api/rooms
        spec.pathParams("first", "api", "second", "rooms");
        roomNumber = Faker.instance().number().numberBetween(100000, 1000000);

        //Set the expected data
        //1. Yol: String ile --> Tavsiye edilmez
        String expectedDataString = "{ \"description\": \"Created By API\", \"price\": 123, \"roomNumber\": " + roomNumber + ", \"roomType\": \"TWIN\", \"status\": true }";

        //2. Yol: Map ile
        expectedDataMap = new HashMap<>();
        expectedDataMap.put("description", "Created By API");
        expectedDataMap.put("price", 123);
        expectedDataMap.put("roomNumber", roomNumber);
        expectedDataMap.put("roomType", "TWIN");
        expectedDataMap.put("status", true);

        //3. Yol: Pojo class ile --> Tavsiye edilen
        expectedDataPojo = new RoomPojo(roomNumber, "TWIN", true, 123.0, "Created By API");

        //Send the request and get the response
        response = given(spec).body(expectedDataPojo).post("{first}/{second}");
        response.prettyPrint();

    }

    @Then("get the response and validate")
    public void get_the_response_and_validate() throws JsonProcessingException {
        //Do assertion
        //1. Yol: then() methodu + HamcrestMatcher
        response
                .then()
                .statusCode(201)
                .body("roomNumber", equalTo(roomNumber),
                        "roomType", equalTo("TWIN"),
                        "status", equalTo(true),
                        "price", equalTo(123.0F),
                        "description", equalTo("Created By API"));

        //2. Yol: JsonPath ile
        JsonPath jsonPath = response.jsonPath();

        assertEquals(201,response.statusCode());
        assertEquals(roomNumber, jsonPath.getInt("roomNumber"));
        assertEquals("TWIN", jsonPath.getString("roomType"));
        assertTrue(jsonPath.getBoolean("status"));
        assertEquals("123.0", jsonPath.getDouble("price")+"");
        assertEquals("Created By API", jsonPath.getString("description"));

        //3. Yol: Map ile
        Map<String,Object> actualDataMap = response.as(HashMap.class);

        assertEquals( expectedDataMap.get("roomNumber")+".0", actualDataMap.get("roomNumber")+"");
        assertEquals(expectedDataMap.get("roomType"), actualDataMap.get("roomType"));
        assertEquals(expectedDataMap.get("status"), actualDataMap.get("status"));
        assertEquals(expectedDataMap.get("price")+".0", actualDataMap.get("price")+"");
        assertEquals(expectedDataMap.get("description"), actualDataMap.get("description"));

        //4. Yol: Pojo class ile
        RoomPojo actualDataPojo = response.as(RoomPojo.class);

        assertEquals(expectedDataPojo.getRoomNumber(), actualDataPojo.getRoomNumber());
        assertEquals(expectedDataPojo.getRoomType(), actualDataPojo.getRoomType());
        assertEquals(expectedDataPojo.getStatus(), actualDataPojo.getStatus());
        assertEquals(expectedDataPojo.getPrice(), actualDataPojo.getPrice());
        assertEquals(expectedDataPojo.getDescription(), actualDataPojo.getDescription());

        //5. Yol: Pojo class + Object Mapper --> En çok tavsiye edilen
        RoomPojo actualDataPojoMapper = new ObjectMapper().readValue(response.asString(),RoomPojo.class);

        assertEquals(expectedDataPojo.getRoomNumber(), actualDataPojoMapper.getRoomNumber());
        assertEquals(expectedDataPojo.getRoomType(), actualDataPojoMapper.getRoomType());
        assertEquals(expectedDataPojo.getStatus(), actualDataPojoMapper.getStatus());
        assertEquals(expectedDataPojo.getPrice(), actualDataPojoMapper.getPrice());
        assertEquals(expectedDataPojo.getDescription(), actualDataPojoMapper.getDescription());

        //6. Yol: Pojo Class + Gson
        RoomPojo actualDataPojoGson = new Gson().fromJson(response.asString(), RoomPojo.class);

        assertEquals(expectedDataPojo.getRoomNumber(), actualDataPojoGson.getRoomNumber());
        assertEquals(expectedDataPojo.getRoomType(), actualDataPojoGson.getRoomType());
        assertEquals(expectedDataPojo.getStatus(), actualDataPojoGson.getStatus());
        assertEquals(expectedDataPojo.getPrice(), actualDataPojoGson.getPrice());
        assertEquals(expectedDataPojo.getDescription(), actualDataPojoGson.getDescription());



    }

}