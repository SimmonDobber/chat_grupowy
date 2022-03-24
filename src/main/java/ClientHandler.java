
import java.io.*;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;

public class ClientHandler extends Communicative implements Runnable {
    private static List<ClientHandler> clientHandlers = new LinkedList<>();

    public ClientHandler(Socket socket) throws IOException {
        super(socket);
        this.username = reader.readLine();
        clientHandlers.add(this);
        broadcastMessage("SERVER: " + username + " has entered the chat!");
    }

    @Override
    public void run() {
        while (socket.isConnected()) {
            try {
                broadcastMessage(reader.readLine());
            } catch (IOException e) {
                clientHandlers.remove(this);
                closeConnection();
            }
        }
    }

    private void broadcastMessage(String message) throws IOException {
        if(message == null)
            throw new IOException("Client has disconnected.");
        sendMessageToClients(message);
    }

    private void sendMessageToClients(String message) throws IOException {
        for (ClientHandler client : clientHandlers) {
            if (!client.username.equals(username))
                sendMessageToClient(message, client);
        }
    }

    private void sendMessageToClient(String message, ClientHandler client) throws IOException {
        client.writer.write(message);
        client.writer.newLine();
        client.writer.flush();
    }


}
