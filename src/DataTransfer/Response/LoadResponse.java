package DataTransfer.Response;

/**
 * Class used to transfer the data from load endpoint, back to the client
 */
public class LoadResponse {
    private String message;

    public LoadResponse(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
