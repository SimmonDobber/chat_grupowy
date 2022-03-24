import java.io.*;
import java.net.Socket;

public abstract class Communicative
{
    protected Socket socket;
    protected BufferedReader reader;
    protected BufferedWriter writer;
    protected String username;

    protected Communicative(Socket socket) throws IOException {
        this.socket = socket;
        this.reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
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
