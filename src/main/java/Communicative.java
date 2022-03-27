import java.io.*;
import java.net.Socket;

public abstract class Communicative
{
    protected Socket socket;
    protected ObjectInputStream reader;
    protected ObjectOutputStream writer;
    protected String username;

    protected Communicative(Socket socket) throws IOException {
        this.socket = socket;
        this.writer = new ObjectOutputStream(socket.getOutputStream());
        this.reader = new ObjectInputStream(socket.getInputStream());
    }

    protected void closeConnection() {
        try {
            if (reader != null)
                reader.close();
            if (writer != null)
                writer.close();
            if (socket != null)
                socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
