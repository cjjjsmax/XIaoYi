import java.net.ServerSocket;

public class TestServer {
    public static void main(String[] args) {
        try {
            // Try to create a ServerSocket on port 8082
            ServerSocket serverSocket = new ServerSocket(8082);
            System.out.println("Successfully bound to port 8082");
            serverSocket.close();
        } catch (Exception e) {
            System.out.println("Failed to bind to port 8082: " + e.getMessage());
        }
    }
}