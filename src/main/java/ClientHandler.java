
import java.io.*;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;

public class ClientHandler extends Communicative implements Runnable {
    private static List<ClientHandler> clientHandlers = new LinkedList<>();

    public ClientHandler(Socket socket) throws IOException {
        super(socket);
        username = reader.readLine();
        clientHandlers.add(this);
        broadcastMessage("SERVER: " + username + " has entered the chat!");
    }

    @Override
    public void run() {
        try {
            while (socket.isConnected())
                broadcastMessage(reader.readLine());
        } catch (IOException e) {
            closeSession();
        }
    }

    private void broadcastMessage(String message) throws IOException {
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

    private void closeSession(){
        broadcastChatLeaveMessage();
        clientHandlers.remove(this);
        closeConnection();
    }

    private void broadcastChatLeaveMessage(){
        try {
            broadcastMessage("SERVER: " + username + " has left the chat!");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
