package DataTransfer.Response;

/**
 * Class used to transfer the data from fill event, back to the client
 */
public class FillResponse {
    private String message;

    public FillResponse(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
