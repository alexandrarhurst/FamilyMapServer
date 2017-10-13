package DataTransfer.Response;

/**
 * Class used to transfer the data from clear event, back to the client
 */
public class ClearResponse {
    private String message;

    public ClearResponse(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
