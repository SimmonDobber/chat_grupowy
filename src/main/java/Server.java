import java.io.*;
import java.net.ServerSocket;
import java.util.Date;

public class Server
{
    public static final String HOST = "localhost";
    public static final int PORT = 2137;

    private ServerSocket socket;

    public static void main(String[] args) throws IOException {
        new Server(new ServerSocket(PORT));
    }

    private Server(ServerSocket socket) {
        this.socket = socket;
        initializeServer();
    }

    private void initializeServer() {
        try {
            while (!socket.isClosed())
                addClient();
        } catch (IOException | ClassNotFoundException e) {
            closeServerSocket();
        }
    }

    private void addClient() throws IOException, ClassNotFoundException {
        ClientHandler clientHandler = new ClientHandler(socket.accept(), new Date());
        Thread thread = new Thread(clientHandler);
        thread.start();
    }

    private void closeServerSocket() {
        try {
            if (socket != null)
                socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
