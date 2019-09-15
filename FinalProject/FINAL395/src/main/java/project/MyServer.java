package project;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import java.util.HashMap;
import java.util.ArrayList;


@SpringBootApplication
public class MyServer {
    /*pretend database of users using a HashMap where key
    will be the username and the value will be the password or hashed password*/
	public static HashMap<String, String> users = new HashMap<String, String>(); 
	public static HashMap<String, User> tokenHashmap = new HashMap<String, User>();
	public static ArrayList<User> tokensArrayList = new ArrayList<User>();
	public static String currentUserName = "";
	public static String currentToken = "";




	public static void main(String[] args) {
		SpringApplication.run(MyServer.class, args);
	}
}



class User {
	String username;
	String token = "";
	User(String username, String token) {
		this.username = username;
		this.token = token;
	}
}