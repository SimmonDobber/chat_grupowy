import java.io.*;
import java.net.Socket;
import java.util.Scanner;


public class Client extends Communicative
{
    private Scanner scanner;

    public static void main(String[] args) throws IOException {
        Client client = new Client(new Socket(Server.HOST, Server.PORT));
        client.listenForMessage();
        client.sendMessagesToServer();
    }

    private Client(Socket socket) throws IOException {
        super(socket);
        this.scanner = new Scanner(System.in);
        this.username = inputUsername();
    }

    private String inputUsername(){
        System.out.print("Enter your username for the group chat: ");
        return scanner.nextLine();
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

    private void sendMessageToServer(String message) throws IOException {
        writer.write(message);
        writer.newLine();
        writer.flush();
    }

    private void readMessages(){
        while (socket.isConnected())
            readMessage();
    }

    private void readMessage(){
        try {
            System.out.println(reader.readLine());
        } catch (IOException e) {
            closeConnection();
        }
    }
}
