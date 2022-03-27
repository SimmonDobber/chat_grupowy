
import java.io.*;
import java.net.Socket;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class ClientHandler extends Communicative implements Runnable
{
    private static List<ClientHandler> clientHandlers = new LinkedList<>();

    public ClientHandler(Socket socket, Date creationTimestamp) throws IOException, ClassNotFoundException {
        super(socket);
        sendCreationTimestampToClient(creationTimestamp);
        username = readUsername();
        clientHandlers.add(this);
    }

    @Override
    public void run() {
        try {
            broadcastMessage(getWelcomingMessage());
            while (socket.isConnected()) {
                Message message = (Message)reader.readObject();
                broadcastMessage(message);
            }
        } catch (IOException | ClassNotFoundException e) {
            closeSession();
        }
    }

    private void broadcastMessage(Message message) throws IOException {
        if(message == null)
            throw new IOException("Client has disconnected.");
        sendMessagesToClients(message);
    }

    private void sendMessagesToClients(Message message) throws IOException {
        for (ClientHandler client : clientHandlers) {
            if (!client.username.equals(username))
                sendMessageToClient(message, client);
        }
    }

    private void sendMessageToClient(Message message, ClientHandler client) throws IOException {
        Message messageToSend = new Message(message);
        client.writer.writeObject(messageToSend);
    }

    private void closeSession(){
        broadcastChatLeaveMessage();
        clientHandlers.remove(this);
        closeConnection();
    }

    private void broadcastChatLeaveMessage(){
        try {
            broadcastMessage(getLeavingMessage());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Message getWelcomingMessage(){
        return new Message(new Date(), "SERVER: " + username + " has entered the chat!");
    }

    private Message getLeavingMessage(){
        return new Message(new Date(), "SERVER: " + username + " has left the chat!");
    }

    private String readUsername() throws IOException, ClassNotFoundException {
        Message usernameMessage = (Message)reader.readObject();
        return usernameMessage.content;
    }

    private void sendCreationTimestampToClient(Date creationTimestamp) throws IOException {
        Message message = new Message(creationTimestamp, null);
        writer.writeObject(message);
    }
}
