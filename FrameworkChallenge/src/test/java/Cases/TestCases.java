package Cases;

import Data.ProvideData;
import io.restassured.http.ContentType;
import org.json.simple.JSONObject;
import org.testng.annotations.AfterGroups;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;

import java.io.*;
import java.util.ArrayList;
import java.util.Properties;

import static io.restassured.RestAssured.*;


public class TestCases {
    String key,username,password,session_id;
    Properties properties = new Properties();
    Methods methods = new Methods();
    ArrayList listCreatedName = new ArrayList();
    ArrayList listCreatedID = new ArrayList();

    @BeforeSuite
    public void Setup() throws IOException {
        String request_token;
        baseURI = "https://api.themoviedb.org/3";
        InputStream inCred = new FileInputStream("Credentials.properties");

        properties.load(inCred);
        key=properties.getProperty("Key");
        username=properties.getProperty("Username");
        password=properties.getProperty("Password");

       if(properties.getProperty("Session_id")== null || methods.compare(properties.getProperty("Expires_at"))){

           System.out.println("Request token");

           request_token = given().params("api_key",key)
                   .when().get("/authentication/token/new")
                   .then().statusCode(200).and().extract().path("request_token");

           System.out.println("Validate token");

           JSONObject body = new JSONObject();
           body.put("username",username);
           body.put("password",password);
           body.put("request_token",request_token);
           properties.setProperty("Expires_at",given().contentType(ContentType.JSON).accept(ContentType.JSON).body(body.toJSONString())
                   .when().post("/authentication/token/validate_with_login?api_key="+key)
                   .then().statusCode(200).and().extract().path("expires_at"));

           System.out.println("Create session");

           body = new JSONObject();
           body.put("request_token",request_token);
           session_id= given().contentType(ContentType.JSON).accept(ContentType.JSON).body(body.toJSONString())
                   .when().post("/authentication/session/new?api_key="+key)
                   .then().statusCode(200).and().extract().path("session_id");

           properties.setProperty("Session_id",session_id);
           OutputStream outCred = new FileOutputStream("Credentials.properties");
           properties.store(outCred,null);

       }else{
           session_id= properties.getProperty("Session_id");
           System.out.println("Sesion existente valida");
       }
    }

    @AfterGroups("CreatingList")
    public void AfterCreateList() throws IOException {
        methods.AddList(listCreatedName,listCreatedID);
        listCreatedName = new ArrayList<>();
        listCreatedID = new ArrayList<>();
    }

    @Test(groups = {"CreatingList"},
            dataProvider = "ListNames",
            dataProviderClass = ProvideData.class)
    public void CreateList(String genre, String description){
        System.out.println("Creating list: "+genre);
        JSONObject body = new JSONObject();
        body.put("name",genre);
        body.put("description",description);
        body.put("language","en");
        int id = given().contentType(ContentType.JSON).accept(ContentType.JSON).body(body.toJSONString())
                .when().post("/list?api_key="+key+"&session_id="+session_id)
                .then().statusCode(201).and().extract().path("list_id");
        listCreatedID.add(id);
        listCreatedName.add(genre);
        System.out.println(id);
    }

    @Test(dataProvider = "Movies",
            dataProviderClass = ProvideData.class)
    public void AddMovieToList(String listName, String listID, String movieName,String movieID){
        System.out.println("Adding movie "+movieName+ " to list "+ listName);
        JSONObject body = new JSONObject();
        body.put("media_id",Integer.parseInt(movieID.trim()));
        given().contentType(ContentType.JSON).accept(ContentType.JSON).body(body.toJSONString())
                .when().post("/list/"+Integer.parseInt(listID.trim())+"/add_item?api_key="+key+"&session_id="+session_id)
                .then().statusCode(201).and().log().body();
    }

    @Test(dataProvider = "ListToClear",
            dataProviderClass = ProvideData.class)
    public void ClearList(int list){
        System.out.println("Clearing list "+ list);
        given()
                .when().post("/list/"+list+"/clear?api_key="+key+"&session_id="+session_id+"&confirm="+true)
                .then().statusCode(201).and().log().body();
    }

    @Test(dataProvider = "ListToDelete",
            dataProviderClass = ProvideData.class)
    public void DeleteList(String listName, String listID){
        System.out.println("Deleting list "+ listName);
        given()
                .when().delete("/list/"+listID.trim()+"?api_key="+key+"&session_id="+session_id)
                .then().statusCode(201).and().log().body();
    }

    @Test(dataProvider = "ListDetails",
            dataProviderClass = ProvideData.class)
    public void GetDetailsList(String listName, String listID ){
        System.out.println("Detail of list "+ listName);
        given()
                .when().get("/list/"+listID.trim()+"?api_key="+key)
                .then().statusCode(200).and().log().body();
    }

    @Test(dataProvider = "MovieDetails",
            dataProviderClass = ProvideData.class)
    public void GetDetailsMovie(String movieName, String movieID){
        System.out.println("Detail of movie "+ movieName);
        given()
                .when().get("/movie/"+Integer.parseInt(movieID.trim())+"?api_key="+key)
                .then().statusCode(200).and().log().body();
    }

    @Test(dataProvider = "MovieRate",
            dataProviderClass = ProvideData.class)
    public void RateMovie(int movie, double rate){
        System.out.println("Rating movie "+movie);
        JSONObject body = new JSONObject();
        body.put("value",rate);
        given().contentType(ContentType.JSON).accept(ContentType.JSON).body(body.toJSONString())
                .when().post("/movie/"+movie+"/rating?api_key="+key+"&session_id="+session_id)
                .then().statusCode(201).and().log().body();
    }

}
