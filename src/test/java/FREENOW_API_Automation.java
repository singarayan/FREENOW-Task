import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;

import org.apache.commons.validator.routines.EmailValidator;
import org.json.simple.JSONObject;
import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import com.google.gson.Gson;
import com.task.model.Comments;
import com.task.model.Posts;
import com.task.model.User;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.http.Method;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

public class FREENOW_API_Automation {
	
	private int userFoundId;
	
	@BeforeTest
	  public void beforeTest() {
		RestAssured.baseURI="https://jsonplaceholder.typicode.com/";

	}
	@ Test (priority=1)
	void apiValidation()
	{
		
		RequestSpecification HttpRequest=RestAssured.given();
        Response response=HttpRequest.request(Method.GET,"/");
		String responseBody= response.getBody().asString();
		int statusCode= response.getStatusCode();
		Assert.assertEquals(statusCode, 200);
		
		
}


    @Test (priority=2)
	   void nameVerification() {
		boolean userFound=false;
        RequestSpecification HttpRequest=RestAssured.given();
		 
		Response response=HttpRequest.request(Method.GET,"/users");
		
		String responseBody= response.getBody().asString();
		Gson gson = new Gson();
		User users[] = gson.fromJson(responseBody, User[].class);
		for(int i=0;i<users.length;i++) {
			if(users[i].getUsername().equals("Samantha")) {
				userFound=true;
				userFoundId = users[i].getId();
			}
		}
		Assert.assertEquals(userFound, true);
		
		}
    
    @Test (priority=3)
	   void searchPostVerification()  {
		
        RequestSpecification HttpRequest=RestAssured.given();
		Response response=HttpRequest.request(Method.GET,"posts?userId="+userFoundId);
		
		String responseBody= response.getBody().asString();
		
		Gson gson = new Gson();
		Posts posts[] = gson.fromJson(responseBody, Posts[].class);
		for(int i=0;i<posts.length;i++) {
			Assert.assertEquals(posts[i].getUserId(), userFoundId);
		}
		}

    
    @Test (priority=4)
	   void emailVerification() {
		
		
	 	RequestSpecification HttpRequest=RestAssured.given();
	 
		Response response=HttpRequest.request(Method.GET,"/users/"+userFoundId+"/comments");
	
		String responseBody= response.getBody().asString();
		Gson gson = new Gson();
		Comments comments[] = gson.fromJson(responseBody, Comments[].class);
		EmailValidator emailValidator = EmailValidator.getInstance();
		for(Comments comment : comments) {
			if(emailValidator.isValid(comment.getEmail())) {
				Assert.assertTrue(emailValidator.isValid(comment.getEmail()));
			}
			
		}
    }
    public List<Posts> searchByWordInPost(
    		  String searchWord, List<Posts> posts) {
    		    Iterator<Posts> iterator = posts.iterator();
    		    List<Posts> searchResutls = new ArrayList<Posts>();
    		    while (iterator.hasNext()) {
    		    	Posts post = iterator.next();
    		        if (post.getBody().contains(searchWord)) {
    		        	searchResutls.add(post);
    		        }
    		    }
    		    return searchResutls;
    		}

    
    public List<Posts> searchPostByUserId(
  		 int userId, List<Posts> posts) {
  		    Iterator<Posts> iterator = posts.iterator();
  		    List<Posts> searchResutls = new ArrayList<Posts>();
  		    while (iterator.hasNext()) {
  		    	Posts post = iterator.next();
  		        if (post.getUserId()==userId) {
  		        	searchResutls.add(post);
  		        }
  		    }
  		    return searchResutls;
  		}
    @ Test (priority=5)
	void postPutValidation()
	{
		
    	RequestSpecification HttpRequest=RestAssured.given();
	    JSONObject requestParameters=new JSONObject();
		requestParameters.put("userId","10");
		requestParameters.put("title","testpostmethod");
		requestParameters.put("body","Body message created by RestAssuredPost");
		
		HttpRequest.header("Content-Type","application/json");
		HttpRequest.body(requestParameters.toJSONString());
		
		Response response=HttpRequest.request(Method.POST,"/posts");
		String responseBody= response.getBody().asString();
		int statusCode= response.getStatusCode();
		String responseTitle = response.jsonPath().getString("title");
  	    Assert.assertEquals(statusCode, 201);
		Assert.assertEquals("testpostmethod",responseTitle);
		
	}
      @ Test (priority=6)
      void putUserRequest()
      {
    	  
    	
    	  RequestSpecification request = RestAssured.given();
    	  
    	  JSONObject requestParams = new JSONObject();
    	  requestParams.put("userId", 1); 
    	  requestParams.put("id", 1);
    	  requestParams.put("title", "RestAssured Title");
    	  requestParams.put("body", "RestAssured ");
    	  
    	  request.body(requestParams.toJSONString());
    	  Response response = request.put("/posts/1");
    	  int statusCode = response.getStatusCode();
    	  Assert.assertEquals(statusCode, 200);
    	  
    	  

	}
      @Test (priority=7)
      void patchPostRequest()
      {
       	  
    	  RequestSpecification request = RestAssured.given();
    	  JSONObject requestParams = new JSONObject();
    	  requestParams.put("userId", 1); 
    	  requestParams.put("id", 10);
    	  requestParams.put("title", "RestAssured Title");
    	  requestParams.put("body", "RestAssured Patch ");
    	  request.body(requestParams.toJSONString());
    	  Response response = request.patch("/posts/1");
    	  int statusCode = response.getStatusCode();
    	  Assert.assertEquals(statusCode, 200); 

    	  }
      
	@ Test (priority=8)
      void deletePosts() {
    	  
          RequestSpecification request = RestAssured.given(); 
          
          request.header("Content-Type", "application/json"); 
          Response response = request.delete("/posts/1"); 
          
          int statusCode = response.getStatusCode();
          Assert.assertEquals(statusCode, 200);
          }
}