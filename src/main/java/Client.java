import java.io.*;
import java.net.Socket;
import java.util.*;


public class Client extends Communicative
{
    private static final int MESSAGE_HISTORY_SIZE = 10;
    private Scanner scanner;
    private TreeSet<Message> messageHistory;
    private Date connectionTimestampCorrespondingToServer;
    private Date connectionTimestampCorrespondingToClient;

    public static void main(String[] args) throws IOException {
        Client client = new Client(new Socket(Server.HOST, Server.PORT));
        client.listenForMessage();
        client.inputUsername();
        client.sendMessagesToServer();
    }

    private Client(Socket socket) throws IOException {
        super(socket);
        this.messageHistory = new TreeSet<>(Comparator.comparing((Message m) -> m.time));
        this.scanner = new Scanner(System.in);
    }

    public void inputUsername(){
        System.out.print("Enter your username for the group chat: ");
        username = scanner.nextLine();
    }

    private void listenForMessage() {
        new Thread(this::readMessages).start();
    }

    private void sendMessagesToServer() {
        try {
            sendMessageToServer(username);
            while (socket.isConnected())
                sendMessageToServer(username + ": " + scanner.nextLine());
        } catch (IOException e) {
            closeConnection();
        }
    }

    private void sendMessageToServer(String messageContent) throws IOException {
        Message message = new Message(getMessageSendingTime(), messageContent);
        //delay();
        writer.writeObject(message);
    }

    private void delay(){
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void readMessages(){
        initializeConnectionTimeStamps();
        while (socket.isConnected()){
            Message message = readMessage();
            parseMessage(message);
            displayMessages();
        }
    }

    private Message readMessage(){
        try {
            return (Message)reader.readObject();
        } catch (IOException | ClassNotFoundException e) {
            closeConnection();
        }
        return null;
    }

    private void parseMessage(Message message){
        messageHistory.add(message);
        if(messageHistory.size() > MESSAGE_HISTORY_SIZE)
            messageHistory.pollFirst();
    }

    private void displayMessages(){
        for(Message message : messageHistory)
            System.out.println("[" + message.time + "] " + message.content);
        System.out.println("|-------------------------------------------------------------------------|");
    }

    private void initializeConnectionTimeStamps(){
        Message message = readMessage();
        connectionTimestampCorrespondingToServer = message.time;
        connectionTimestampCorrespondingToClient = new Date();
    }

    private Date getMessageSendingTime(){
        long timeDifference = new Date().getTime() - connectionTimestampCorrespondingToClient.getTime();
        long currentTimestampCorrespondingToServer = timeDifference + connectionTimestampCorrespondingToServer.getTime();
        return new Date(currentTimestampCorrespondingToServer);
    }
}
