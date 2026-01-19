package badWordScanner;

public class Response {
    public boolean isSave;
    public String message;

    public Response(boolean isSave, String message) {
        this.isSave = isSave;
        this.message = message;
    }
}
