package Model;

import DataAccess.AuthTokenDAO;
import DataAccess.EventDAO;
import java.time.*;

import java.util.Random;

/**
 * This class is used to create Authenticatino token objects to verify the validity of requests that are sent in.
 */
public class AuthToken {
    public static final int AUTH_TOKEN_LENGTH = 16;

    private String authToken;
    private LocalTime time;
    private String username;

    /**
     * Constructor for the AuthToken class
     */

    public AuthToken(String authToken, String username, LocalTime time){
        this.authToken = authToken;
        this.username = username;
        this.time = time;
    }

    public AuthToken(){
        // Default constructor
    }

    public void generate(String username){
        if(username != null) {
            this.authToken = randomAuthTokenGen();
            this.time = LocalTime.now();
            this.username = username;
        }
    }

    public static String randomAuthTokenGen(){
        String returnToken = "";
        String possibleChars = "abcdefghijklmnopqrstuvwxyz123456789";
        Random rand = new Random();

        for(int i = 0; i < AUTH_TOKEN_LENGTH; i++){
            returnToken += possibleChars.charAt(rand.nextInt(35));
        }

        //Check to make sure the ID doesn't already exist
        AuthTokenDAO dao = new AuthTokenDAO();
        if(dao.retrieveUsingAuthToken(returnToken) != null)
            return randomAuthTokenGen();
        else
            return returnToken;
    }

    public String getAuthToken() {
        return authToken;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

    public LocalTime getTime() {
        return time;
    }

    public void setTime(LocalTime time) {
        this.time = time;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
