package project;

import org.springframework.web.bind.annotation.*; 
import org.springframework.http.*;
import org.springframework.security.crypto.bcrypt.BCrypt;

import javax.servlet.http.*;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.http.*;

import javax.servlet.http.*;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import java.util.*;
import java.nio.charset.Charset;

@RestController
public class UserController {
	
	@RequestMapping(value = "/register", method = RequestMethod.POST) // <-- setup the endpoint URL at /hello with the HTTP POST method
	public ResponseEntity<String> register(@RequestBody String body, HttpServletRequest request) {
		JSONObject bodyObj = new JSONObject(body);
		String username = bodyObj.getString("username"); //Grabbing name and age parameters from URL
		String password = bodyObj.getString("password");
        Connection conn = null;
        String hashedpw = "";
        
		/*Creating http headers object to place into response entity the server will return.
		This is what allows us to set the content-type to application/json or any other content-type
		we would want to return */
		HttpHeaders responseHeaders = new HttpHeaders(); 
    	responseHeaders.set("Content-Type", "application/json");
		
        //Initializing a MessageDigest object which will allow us to digest a String with SHA-256 
		MessageDigest digest = null;
		String hashedKey = null;
		try {
			//digest = MessageDigest.getInstance("SHA-256"); //digest algorithm set to SHA-256
            //Converts the password to SHA-256 bytes. Then the bytes are converted to hexadecimal with the helper method written below
			//hashedKey = bytesToHex(digest.digest(password.getBytes("UTF-8"))); 
            String salt = BCrypt.gensalt(12);
            hashedKey = BCrypt.hashpw(password, salt);
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/HospiceProject", "root", "Not3important9");
         
		}catch(Exception e) {
		}
		
        //Checking if the hashmap contains the username trying to register and returns a BAD_REQUEST if username is taken
    	if (!MyServer.users.containsKey(username)) {
			MyServer.users.put(username, hashedKey); //Stores the username and hashed password into the HashMap
			 try {
			 // insert new user into MySQL Users table
            String query = "INSERT INTO Users (username, hashedpw) VALUES (?, ?)";    
                                              
            PreparedStatement stmt = null;  //important for safety reasons

            hashedpw = hashedKey;
            stmt = conn.prepareStatement(query);
            stmt.setString(1, username);
            stmt.setString(2, hashedpw);

            stmt.executeUpdate();
            System.out.println("inserted user into MySQL Users table");

	        } catch (SQLException e ) {
	            return new ResponseEntity(e.toString(), responseHeaders, HttpStatus.OK);
	        } finally {
	            try {
	                if (conn != null) { conn.close(); }
	            }catch(SQLException se) {
	            }  
	        } 
		}

		else {
			JSONObject responseObj = new JSONObject();
			responseObj.put("message", "username taken");
			return new ResponseEntity(responseObj.toString(), responseHeaders, HttpStatus.BAD_REQUEST);
			//return new ResponseEntity("{\"message\":\"username taken\"}", responseHeaders, HttpStatus.BAD_REQUEST);
		}
		//Returns the response with a String, headers, and HTTP status
    	JSONObject responseObj = new JSONObject();
		responseObj.put("username", username);
		responseObj.put("message", "User is registered; terrific job!");
		return new ResponseEntity(responseObj.toString(), responseHeaders, HttpStatus.OK);
	}
	
	@RequestMapping(value = "/login", method = RequestMethod.POST) // <-- setup the endpoint URL at /hello with the HTTP POST method
	public ResponseEntity<String> login(@RequestBody String body, HttpServletRequest request) {	//requesting to the server
		JSONObject bodyObj = new JSONObject(body);
		String username = bodyObj.getString("username"); //Grabbing name and age parameters from URL
		String password = bodyObj.getString("password");

		/*Creating http headers object to place into response entity the server will return.
		This is what allows us to set the content-type to application/json or any other content-type
		we would want to return */
		HttpHeaders responseHeaders = new HttpHeaders(); 
    	responseHeaders.set("Content-Type", "application/json");
		
        // put all usernames from database into users HashMap (these are users who registered in previous server sessions)
		MessageDigest digest = null;
		String hashedKey = null;
        Connection conn = null;

		try {
			digest = MessageDigest.getInstance("SHA-256");
            String salt = BCrypt.gensalt(12);
            hashedKey = BCrypt.hashpw(password, salt);
            String pastUsername ="";

            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/HospiceProject", "root", "Not3important9");
            String query = "SELECT * FROM Users WHERE username = ?";
            PreparedStatement stmt = null;  //important for safety reasons
            
            stmt = conn.prepareStatement(query);
            stmt.setString(1, username);

            ResultSet rs = stmt.executeQuery(); 
            while (rs.next()) { //while there's something else next in the resultset
                pastUsername = rs.getString("username");
                MyServer.users.put(username, hashedKey);
            }

		}catch(Exception e) {
		}
        
        //Check if the hashmap contains the username trying to login
    	if (!MyServer.users.containsKey(username)) {
			return new ResponseEntity("{\"message\":\"username not registered\"}", responseHeaders, HttpStatus.BAD_REQUEST);
		}

		else {		// user is in users HashMap
            
			String storedHashedKey = MyServer.users.get(username);
            //Retrieves the stored hashkey for the username logging in
			//String storedHashedKey = MyServer.users.get(username);
            //Compare the stored hashed key with the input hashedKey generated from the password parameter to validate the login
			//if (storedHashedKey.equals(hashedKey)) {			
				//Returns the response with a String, headers, and HTTP status

			if (BCrypt.checkpw(password, storedHashedKey)) {
				String token = generateRandomString(10);

				User user = new User(username, token);

                // store current Username and current token for session to use for token validation
                MyServer.currentUserName = username;
                MyServer.currentToken = token;

				if (MyServer.tokensArrayList.size() == 100) {
					MyServer.tokensArrayList.remove(99);
					MyServer.tokenHashmap.remove(username);
				}
				MyServer.tokensArrayList.add(0, user);
				MyServer.tokenHashmap.put(username, user);

		    	JSONObject responseObj = new JSONObject();
		        responseObj.put("token", token);
				responseObj.put("username", username);
				responseObj.put("message", "AMAZING; YOU HAVE LOGGED IN! Token received.");
				return new ResponseEntity(responseObj.toString(), responseHeaders, HttpStatus.OK);
				
			}

			else {
				return new ResponseEntity("{\"message\":\"username/password combination is incorrect\"}", responseHeaders, HttpStatus.BAD_REQUEST);
			}
		}
	}

    @RequestMapping(value = "/database", method = RequestMethod.GET) // <-- setup the endpoint URL at /hello with the HTTP POST method
    public ResponseEntity<String> database() {
        HttpHeaders responseHeaders = new HttpHeaders(); 
        JSONArray nameArray = new JSONArray();


// ADDED THIS FOR TOKEN VALIDATION

       String username = MyServer.currentUserName;
       String token = MyServer.currentToken;
        User user = MyServer.tokenHashmap.get(username);
        if (user.token.equals(token)) {
            //continue with code...

        responseHeaders.set("Content-Type", "application/json");
        Connection conn = null;
        int facility_id = 0;
        String facility_name = "";
        String location = "";
        String phone_number = "";
        int rooms_available = 0;

        try {
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/HospiceProject", "root", "Not3important9");
            String query = "SELECT * FROM Facilities WHERE facility_id>0";
            PreparedStatement stmt = null;  //important for safety reasons
            
            stmt = conn.prepareStatement(query);
            ResultSet rs = stmt.executeQuery(); 
            while (rs.next()) { //while there's something else next in the resultset
                facility_id = rs.getInt("facility_id");
                facility_name = rs.getString("facility_name");
                location = rs.getString("location");
                phone_number = rs.getString("phone_number");
                rooms_available = rs.getInt("rooms_available");
                
                JSONObject obj = new JSONObject();
                obj.put("facility_id", facility_id);
                obj.put("facility_name", facility_name);
                obj.put("location", location);
                obj.put("phone_number", phone_number);
                obj.put("rooms_available", rooms_available);
                
                nameArray.put(obj);
                    }


        } catch (SQLException e ) {
            return new ResponseEntity(e.toString(), responseHeaders, HttpStatus.OK);
        } finally {
            try {
                if (conn != null) { conn.close(); }
            }catch(SQLException se) {
            }  
        }
}
        else {
            return new ResponseEntity("{\"message\":\"Bad Token\"}", responseHeaders, HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity(nameArray.toString(), responseHeaders, HttpStatus.OK);
      
    }
    
    @RequestMapping(value = "/patients", method = RequestMethod.GET) // <-- setup the endpoint URL at /hello with the HTTP POST method
    public ResponseEntity<String> patients() {
        //String nameToPull = request.getParameter("firstname");
        HttpHeaders responseHeaders = new HttpHeaders(); 
        responseHeaders.set("Content-Type", "application/json");
        Connection conn = null;
        JSONArray nameArray = new JSONArray();
        int patient_id = 0;
        String patient_name = "";
        int reserved_facility = 0;
        int reserved_room = 0;
        String illness = "";
        String notes = "";

        //ADDED THIS FOR TOKEN VALIDATION


        String username = MyServer.currentUserName;
        String token = MyServer.currentToken;
         User user = MyServer.tokenHashmap.get(username);
         if (user.token.equals(token)) {
             //continue with code...
        try {
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/HospiceProject", "root", "Not3important9");
            String query = "SELECT * FROM Patients WHERE reserved_facility>0";
            PreparedStatement stmt = null;	//important for safety reasons
            
            stmt = conn.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();	
            while (rs.next()) {	//while there's something else next in the resultset
            	patient_id = rs.getInt("patient_id");
            	patient_name = rs.getString("patient_name");
            	reserved_facility = rs.getInt("reserved_facility");
            	reserved_room = rs.getInt("reserved_room");
            	illness = rs.getString("illness");
            	notes = rs.getString("notes");
            	
            	JSONObject obj = new JSONObject();
            	obj.put("patient_id", patient_id);
            	obj.put("patient_name", patient_name);
            	obj.put("reserved_facility", reserved_facility);
            	obj.put("reserved_room", reserved_room);
            	obj.put("illness", illness);
            	obj.put("notes", notes);
            	
            	nameArray.put(obj);
                    }
        } catch (SQLException e ) {
            return new ResponseEntity(e.toString(), responseHeaders, HttpStatus.OK);
        } finally {
            try {
                if (conn != null) { conn.close(); }
            }catch(SQLException se) {
            }  
        }
         }
         else {
             return new ResponseEntity("{\"message\":\"Bad Token\"}", responseHeaders, HttpStatus.BAD_REQUEST);
         }
        return new ResponseEntity(nameArray.toString(), responseHeaders, HttpStatus.OK);
      
    }
    
    @RequestMapping(value = "/search", method = RequestMethod.POST) // <-- setup the endpoint URL at /hello with the HTTP POST method
	public ResponseEntity<String> search(@RequestBody String body, HttpServletRequest request) {	//requesting to the server
		JSONObject bodyObj = new JSONObject(body);
		String searchbar = bodyObj.getString("searchbar"); //Grabbing facility name inputted into the search bar

		/*Creating http headers object to place into response entity the server will return.
		This is what allows us to set the content-type to application/json or any other content-type
		we would want to return */
		HttpHeaders responseHeaders = new HttpHeaders(); 
        responseHeaders.set("Content-Type", "application/json");
        Connection conn = null;
        JSONArray nameArray = new JSONArray();
        int facility_id = 0;
        int searchbar_id = 0;
        String facility_name = "";
        int room_no = 0;
        String amenities = "";
        String available = "";
        // ADDED THIS FOR TOKEN VALIDATION


       String username = MyServer.currentUserName;
       String token = MyServer.currentToken;
        User user = MyServer.tokenHashmap.get(username);
        if (user.token.equals(token)) {
            //continue with code...

        try {
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/HospiceProject", "root", "Not3important9");
           
            
            String query = "SELECT * FROM Facilities WHERE facility_id>0";	//looking at all facilities
            PreparedStatement stmt = null;	//important for safety reasons
            
            stmt = conn.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();	
            while (rs.next()) {	//while there's something else next in the resultset
            	facility_id = rs.getInt("facility_id");
            	facility_name = rs.getString("facility_name");

            	if (searchbar.equalsIgnoreCase(facility_name)) {//check if searchbar input match facilityname
            		searchbar_id = facility_id;					//if so, assign id
	            }
            }
        
            String query1 = "SELECT * FROM Rooms WHERE facility_id=?"; 	

            
            PreparedStatement stmt1 = null;	//important for safety reasons

            stmt1 = conn.prepareStatement(query1);
            stmt1.setInt(1, searchbar_id);
            ResultSet rs1 = stmt1.executeQuery();	
            while (rs1.next()) {	//while there's something else next in the resultset
            	room_no = rs1.getInt("room_no");
            	facility_id = rs1.getInt("facility_id");
            	amenities = rs1.getString("amenities");
            	available = rs1.getString("available");
            	
            	JSONObject obj = new JSONObject();
            	obj.put("room_no", room_no);
            	obj.put("facility_id", facility_id);
            	obj.put("amenities", amenities);
            	obj.put("available", available);
            	
            	nameArray.put(obj);
                    }
        } catch (SQLException e ) {
            return new ResponseEntity(e.toString(), responseHeaders, HttpStatus.OK);
        } finally {
            try {
                if (conn != null) { conn.close(); }
            }catch(SQLException se) {
            }  
        }   
    }
        else {
            return new ResponseEntity("{\"message\":\"Bad Token\"}", responseHeaders, HttpStatus.BAD_REQUEST);
        }    
        return new ResponseEntity(nameArray.toString(), responseHeaders, HttpStatus.OK);
	}

    //  ADDED THIS CALL BELOW 4:53
    //  1. first grab query from user's search (JSON object)
    //  2. in this file, update the availability to "occupied" for the specific room from received JSON 
    /** 
        get JSON values from HTML for fac_id and room_no  ->    RUN QUERY THAT SELECTS ALL ROOMS W/ THIS FAC_ID AND THIS ROOM_NO -> CHECK IF AVAILABLE

        IF NOT AVAILABLE: return repsonsentity that says taken already
        IF AVAILABLE: continue in /reserve method and update that room to "occupied" with another query
    **/

    @RequestMapping(value = "/reserve", method = RequestMethod.POST) // <-- setup the endpoint URL at /hello with the HTTP POST method
    public ResponseEntity<String> reserve(@RequestBody String body, HttpServletRequest request) {    //requesting to the server
        JSONObject bodyObj = new JSONObject(body);
        String searchbarFacID = bodyObj.getString("facid"); //Grabbing facility name inputted into the search bar
        String searchbarRoomNo = bodyObj.getString("roomno"); //Grabbing facility name inputted into the search bar

        String patID = bodyObj.getString("patid");
        String patName = bodyObj.getString("patname");
        String illness = bodyObj.getString("illness");
        String notes = bodyObj.getString("notes");

        HttpHeaders responseHeaders = new HttpHeaders(); 
        responseHeaders.set("Content-Type", "application/json");
        int facility_id = 0;
        int room_no = 0;
        int rooms_avail = 0;
        String availability = "";
        Connection conn = null;

        String reserveMessage = "room taken already";
        String username = MyServer.currentUserName;
        String token = MyServer.currentToken;
        User user = MyServer.tokenHashmap.get(username);
        if (user.token.equals(token)) {
            //continue with code...

        try {
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/HospiceProject", "root", "Not3important9");
            // Make query to get room from DB that matches ID
            // Check if room is available
            String query1 = "SELECT * FROM Rooms WHERE facility_id =? AND room_no = ?";  
            //Looking for specific facility's room that is empty            
            PreparedStatement stmt1 = null;  //important for safety reasons
           stmt1 = conn.prepareStatement(query1);
           stmt1.setInt(1, Integer.parseInt(searchbarFacID));
           stmt1.setInt(2, Integer.parseInt(searchbarRoomNo));

           // stmt.setString(1, nameToPull);
            ResultSet rs1 = stmt1.executeQuery(); 
            while (rs1.next()) { //while there's something else next in the resultset
                facility_id = rs1.getInt("facility_id");
                room_no = rs1.getInt("room_no");
                availability = rs1.getString("available");
            }
            
            if (availability.equals("occupied")) {
            	reserveMessage = "error: need to pick an available room";
            	return new ResponseEntity(reserveMessage, responseHeaders, HttpStatus.OK);
            }
            else {
            	reserveMessage = "room is available";
            	//continue.
            }
            
            String query2 = "UPDATE Rooms SET available = 'occupied' WHERE facility_id=? and room_no = ?";    
            //at the specific facility's room, make the availability occupied     
            PreparedStatement stmt2 = null;  //important for safety reasons
            stmt2 = conn.prepareStatement(query2);
            stmt2.setInt(1, Integer.parseInt(searchbarFacID));
            stmt2.setInt(2, Integer.parseInt(searchbarRoomNo));
            stmt2.executeUpdate();

            String query3 = "SELECT * FROM Facilities WHERE facility_id =?";  
            //Looking for number of available rooms in a facility
            
            PreparedStatement stmt3 = null;  //important for safety reasons
            stmt3 = conn.prepareStatement(query3);
            stmt3.setInt(1, Integer.parseInt(searchbarFacID));

            ResultSet rs3 = stmt3.executeQuery(); 
            while (rs3.next()) { //while there's something else next in the resultset
                //get rooms_available
            	rooms_avail = rs3.getInt("rooms_available");
            	rooms_avail--;	//subtract one
            }   
            
            String query4 = "UPDATE Facilities SET rooms_available = ? WHERE facility_id=?";    
            
            PreparedStatement stmt4 = null;  //important for safety reasons

            stmt4 = conn.prepareStatement(query4);
            stmt4.setInt(1, rooms_avail);
            stmt4.setInt(2, Integer.parseInt(searchbarFacID));

            stmt4.executeUpdate();          
   
            String query5 = "INSERT INTO Patients (patient_id, patient_name, reserved_facility, reserved_room, illness, notes) VALUES (?, ?, ?, ?, ?, ?)";
            PreparedStatement stmt5 = null;  //important for safety reasons
            stmt5 = conn.prepareStatement(query5);
            stmt5.setInt(1, Integer.parseInt(patID));
            stmt5.setString(2, patName);
            stmt5.setInt(3, Integer.parseInt(searchbarFacID));
            stmt5.setInt(4, Integer.parseInt(searchbarRoomNo));
            stmt5.setString(5, illness);
            stmt5.setString(6, notes);
            
            stmt5.executeUpdate();  
            
   
        } catch (SQLException e ) {
            return new ResponseEntity(e.toString(), responseHeaders, HttpStatus.OK);
        } finally {
            try {
                if (conn != null) { conn.close(); }
            }catch(SQLException se) {
            }  
        }
    }
     else {
            return new ResponseEntity("{\"message\":\"Bad Token\"}", responseHeaders, HttpStatus.BAD_REQUEST);
        }
        
        JSONObject responseObj = new JSONObject();
		responseObj.put("message", "Reserve method is completed");

        return new ResponseEntity(responseObj.toString(), responseHeaders, HttpStatus.OK);
    }

@RequestMapping(value = "/cancel", method = RequestMethod.POST) // <-- setup the endpoint URL at /hello with the HTTP POST method
    public ResponseEntity<String> cancel(@RequestBody String body, HttpServletRequest request) {    //requesting to the server
        JSONObject bodyObj = new JSONObject(body);
        String searchbarFacID = bodyObj.getString("facid"); //Grabbing facility name inputted into the search bar
        String searchbarRoomNo = bodyObj.getString("roomno"); //Grabbing facility name inputted into the search bar

        HttpHeaders responseHeaders = new HttpHeaders(); 
        responseHeaders.set("Content-Type", "application/json");
        int facility_id = 0;
        int room_no = 0;
        int rooms_avail = 0;
        String availability = "";
        Connection conn = null;

        String reserveMessage = "room taken already";
                  String username = MyServer.currentUserName;
                  String token = MyServer.currentToken;
                  User user = MyServer.tokenHashmap.get(username);
        if (user.token.equals(token)) {
            //continue with code...

        try {
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/HospiceProject", "root", "Not3important9");
            // Make query to get room from DB that matches ID
            // Check if room is available
            String query1 = "SELECT * FROM Rooms WHERE facility_id =? AND room_no = ?";  
            //Looking for specific facility's room that is empty            
            PreparedStatement stmt1 = null;  //important for safety reasons
           stmt1 = conn.prepareStatement(query1);
           stmt1.setInt(1, Integer.parseInt(searchbarFacID));
           stmt1.setInt(2, Integer.parseInt(searchbarRoomNo));

           // stmt.setString(1, nameToPull);
            ResultSet rs1 = stmt1.executeQuery(); 
            while (rs1.next()) { //while there's something else next in the resultset
                facility_id = rs1.getInt("facility_id");
                room_no = rs1.getInt("room_no");
                availability = rs1.getString("available");
            }
            if (availability.equals("empty")) {
            	reserveMessage = "error: need to pick a reserved room";
            	return new ResponseEntity(reserveMessage, responseHeaders, HttpStatus.OK);
            }
            else {
            	reserveMessage = "can cancel this room";
            	//continue.
            }
            
            String query2 = "UPDATE Rooms SET available = 'empty' WHERE facility_id=? and room_no = ?";    
            //at the specific facility's room, make the availability occupied     
            PreparedStatement stmt2 = null;  //important for safety reasons
            stmt2 = conn.prepareStatement(query2);
            stmt2.setInt(1, Integer.parseInt(searchbarFacID));
            stmt2.setInt(2, Integer.parseInt(searchbarRoomNo));
            stmt2.executeUpdate();

            String query3 = "SELECT * FROM Facilities WHERE facility_id =?";  
            //Looking for number of available rooms in a facility
            
            PreparedStatement stmt3 = null;  //important for safety reasons
           stmt3 = conn.prepareStatement(query3);
           stmt3.setInt(1, Integer.parseInt(searchbarFacID));

            ResultSet rs3 = stmt3.executeQuery(); 
            while (rs3.next()) { //while there's something else next in the resultset
                //get rooms_available
                rooms_avail = rs3.getInt("rooms_available");
                rooms_avail++;  //subtract one
            }   
            
             String query4 = "UPDATE Facilities SET rooms_available = ? WHERE facility_id=?";    
            
            PreparedStatement stmt4 = null;  //important for safety reasons

            stmt4 = conn.prepareStatement(query4);
            stmt4.setInt(1, rooms_avail);
            stmt4.setInt(2, Integer.parseInt(searchbarFacID));

            stmt4.executeUpdate();     
            
            String query5 = "DELETE FROM Patients WHERE reserved_facility = ? AND reserved_room = ?";
            PreparedStatement stmt5 = null;  //important for safety reasons

            stmt5 = conn.prepareStatement(query5);
            stmt5.setInt(1, Integer.parseInt(searchbarFacID));
            stmt5.setInt(2, Integer.parseInt(searchbarRoomNo));

            stmt5.executeUpdate();          
   
        } catch (SQLException e ) {
            return new ResponseEntity(e.toString(), responseHeaders, HttpStatus.OK);
        } finally {
            try {
                if (conn != null) { conn.close(); }
            }catch(SQLException se) {
            }  
        }
    }
     else {
            return new ResponseEntity("{\"message\":\"Bad Token\"}", responseHeaders, HttpStatus.BAD_REQUEST);
        }

        JSONObject responseObj = new JSONObject();
		responseObj.put("message", "Cancel method is completed");
        return new ResponseEntity(responseObj.toString(), responseHeaders, HttpStatus.OK);
    }




	
     public String generateRandomString(int length) {
         byte[] array = new byte[length]; 
         new Random().nextBytes(array);
         String generatedString = new String(array, Charset.forName("UTF-8"));

         return generatedString;
     }
     
    //Helper method to convert bytes into hexadecimal
	public static String bytesToHex(byte[] in) {
		StringBuilder builder = new StringBuilder();
		for(byte b: in) {
			builder.append(String.format("%02x", b));
		}
		return builder.toString();
	}
}