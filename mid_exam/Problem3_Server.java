
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class Problem3_Server extends Thread {

    private final int port;
    private final ServerSocket serverSocket;
    private final List<Connection> connections;

    public Problem3_Server(int port) throws IOException {
        this.port = port;
        this.connections = Collections.synchronizedList(new LinkedList());
        this.serverSocket = new ServerSocket(this.port, 10);
    }

    @Override
    public void run() {
        while (true) {
            try {
                Socket socket = this.serverSocket.accept();
                Connection connection = new Connection(socket);
                this.connections.add(connection);
                connection.start();
            } catch (IOException ex) {
                System.out.println(ex.toString());
            }
        }
    }

    private class Connection extends Thread {

        private final Socket socket;
        private final ObjectInputStream input;
        private final ObjectOutputStream output;

        public Connection(Socket socket) throws IOException {
            this.socket = socket;
            this.input = new ObjectInputStream(socket.getInputStream());
            this.output = new ObjectOutputStream(socket.getOutputStream());
            System.out.println("create connection");
        }

        @Override
        public void run() {
            try {
                File dir = new File(".");
                while (true) {
                    this.output.flush();
                    this.output.writeObject("command> ");
                    String command = (String) this.input.readObject();
                    System.out.println(String.format("receive command: %s", command));
                    this.output.flush();
                    if (command.equals("quit")) {
                        this.close();
                        break;
                    } else if (command.equals("dir")) {
                        StringBuilder string = new StringBuilder();
                        for (File file : dir.listFiles()) {
                            string.append(file.getName()).append("\n");
                        }
                        this.output.writeObject(string.toString());
                    } else {
                        String cmd[] = command.split(" ");
                        if (cmd.length == 2) {
                            if (cmd[0].equals("cd")) {
                                if (cmd[1].equals("..")) {
                                    if (dir.getParentFile() == null) {
                                        this.output.writeObject("此目錄偵測不到上一層\n");
                                    } else {
                                        dir = dir.getParentFile();
                                        this.output.writeObject("已切換目錄至上一層\n");
                                    }
                                } else {
                                    boolean key = true;
                                    for (File file : dir.listFiles()) {
                                        if (file.getName().equals(cmd[1]) && file.isDirectory()) {
                                            key = false;
                                            dir = file;
                                            this.output.writeObject(String.format("已切換目錄至%s\n", cmd[1]));
                                            break;
                                        }
                                    }
                                    if (key) {
                                        this.output.writeObject(String.format("目錄%s不存在\n", cmd[1]));
                                    }
                                }
                            } else if (cmd[0].equals("del")) {
                                boolean key = true;
                                for (File file : dir.listFiles()) {
                                    if (file.getName().equals(cmd[1])) {
                                        key = false;
                                        if (file.delete()) {
                                            this.output.writeObject(String.format("File %s deleted\n", cmd[1]));
                                        } else {
                                            this.output.writeObject(String.format("File %s delete error\n", cmd[1]));
                                        }
                                        break;
                                    }
                                }
                                 if (key) {
                                        this.output.writeObject("File not found\n");
                                    }
                            } else {
                                this.output.writeObject("Command not found\n");
                            }
                        } else {
                            this.output.writeObject("Command not found\n");
                        }
                    }
                }
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

    public static void main(String args[]) throws IOException {
        Problem3_Server server = new Problem3_Server(7777);
        server.start();
    }
}
