
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class Server extends Thread {

    private final int port;
    private final int maxConnections;
    private final String password;
    private final ServerSocket serverSocket;
    private final List<Connection> connections;

    public Server(int port, int maxConnections, String password) throws IOException {
        this.port = port;
        this.password = password;
        this.maxConnections = maxConnections;
        this.connections = Collections.synchronizedList(new LinkedList());
        this.serverSocket = new ServerSocket(this.port, this.maxConnections);
    }

    @Override
    public void run() {
        while (true) {
            try {
                Socket socket = this.serverSocket.accept();
                if (this.connections.size() < this.maxConnections) {
                    Connection connection = new Connection(socket);
                    this.connections.add(connection);
                    connection.start();
                } else {
                    System.out.println("connections overflow\n");
                }
            } catch (IOException ex) {
                System.out.println(ex.toString());
            }
        }
    }

    public static void main(String args[]) throws IOException {
        Server server = new Server(7777, 10, "killua4564");
        server.start();
    }

    private class Connection extends Thread {

        private final Socket socket;
        private final ObjectInputStream input;
        private final ObjectOutputStream output;

        public Connection(Socket socket) throws IOException {
            this.socket = socket;
            this.input = new ObjectInputStream(socket.getInputStream());
            this.output = new ObjectOutputStream(socket.getOutputStream());
            System.out.println("connection create");
        }

        private void interaction() throws IOException {
            while (true) {
                try {
                    this.output.flush();
                    this.output.writeObject("command> ");
                    this.output.flush();
                    String command = (String) this.input.readObject();
                    System.out.println(String.format("receive command: %s", command));
                    if (command.equals("quit")) {
                        this.close();
                        break;
                    } else {
                        Runtime runtime = Runtime.getRuntime();
                        Process process = runtime.exec(command);
                        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                        String line = null;
                        StringBuilder string = new StringBuilder();
                        while ((line = reader.readLine()) != null) {
                            string.append(line).append("\n");
                        }
                        this.output.writeObject(string.toString());
                    }
                } catch (IOException | ClassNotFoundException ex) {
                    this.output.writeObject(ex.toString() + "\n");
                }
            }
        }

        private void login() throws IOException, ClassNotFoundException {
            int errorCount = 0;
            while (true) {
                this.output.flush();
                this.output.writeObject("password: ");
                this.output.flush();
                String getPassword = (String) this.input.readObject();
                System.out.println(String.format("receive password: %s", getPassword));
                if (password.equals(getPassword)) {
                    this.output.writeObject("login success\n");
                    interaction();
                    break;
                } else {
                    this.output.writeObject("login fail\n");
                    if (++errorCount == 3) {
                        this.close();
                        break;
                    }
                }
            }
        }

        @Override
        public void run() {
            try {
                login();
            } catch (IOException | ClassNotFoundException ex) {
                System.out.println(ex.toString());
            }
        }

        public void close() throws IOException {
            System.out.println("Connection close");
            this.output.writeObject("Connection close\n");
            this.socket.close();
            this.input.close();
            this.output.close();
        }
    }
}
