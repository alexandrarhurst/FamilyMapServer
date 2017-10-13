package DataTransfer.Response;

/**
 * Class used to transfer the data for a Response request
 */
public class RegisterResponse {
    private String message;
    private String authToken;
    private String userName;
    private String personID;

    public RegisterResponse(String message,
                            String authToken,
                            String userName,
                            String personID) {
        this.message = message;
        this.authToken = authToken;
        this.userName = userName;
        this.personID = personID;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getAuthToken() {
        return authToken;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPersonID() {
        return personID;
    }

    public void setPersonID(String personID) {
        this.personID = personID;
    }
}
