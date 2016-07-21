import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

    public Server(int port) throws IOException {
        ServerSocket service = new ServerSocket(port);
        try {
            while (true) {
                Socket s = service.accept();
                System.out.println("Accepted from " + s.getInetAddress());
                Handler handler = new Handler(s);
                handler.start();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            service.close();
        }
    }

    public static void main(String[] args) {
        try {
            String args0 = "8082";
            new Server(Integer.parseInt(args0));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
