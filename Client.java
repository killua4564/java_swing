
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class Client {

    private final int port;
    private final String serverName;
    private final Socket socket;
    private final ObjectInputStream input;
    private final ObjectOutputStream output;

    public Client(String serverName, int port) throws UnknownHostException, IOException {
        this.port = port;
        this.serverName = serverName;
        this.socket = new Socket(InetAddress.getByName(this.serverName), this.port);
        this.output = new ObjectOutputStream(this.socket.getOutputStream());
        this.input = new ObjectInputStream(this.socket.getInputStream());
        this.output.flush();
    }

    public void exec() throws IOException, ClassNotFoundException {
        String receive = null;
        Scanner scanner = new Scanner(System.in);
        while (true) {
            receive = (String) this.input.readObject();
            if (receive.equals("Connection close\n")) {
                break;
            } else {
                System.out.print(receive);
            }
            this.output.writeObject(scanner.nextLine());
            this.output.flush();
            receive = (String) this.input.readObject();
            if (receive.equals("Connection close\n")) {
                break;
            } else {
                System.out.print(receive);
            }
        }
    }

    public static void main(String args[]) throws UnknownHostException, IOException, ClassNotFoundException {
        Client client = new Client("localhost", 7777);
        client.exec();
    }
}
