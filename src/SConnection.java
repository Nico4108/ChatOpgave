import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class SConnection extends Thread{

    private Socket socket;
    private Server server;
    private DataInputStream dis;
    private DataOutputStream dos;
    private boolean running = true;
    private SConnection sConnection;
    private String username;

    public String getUsername() {
        return username;
    }

    public SConnection(Socket socket, Server server, String username) {
        super("Server Connection Thread");
        this.socket = socket;
        this.server = server;
        this.username = username;
    }

    public void writeToAllInChat(String[] parts){
        String message = parts[1];

        for (int j = 0; j < server.getConnections().size(); j++){
            sConnection = server.getConnections().get(j);

            try {
                sConnection.dos.writeUTF("Besked fra " + username + ": " + message);
                sConnection.dos.flush();
                //System.out.println(username + " ");

            }catch (IOException e){
                e.printStackTrace();
            }
        }
    }

    public void writeToOneClient(String[] parts) {

        String writeTo = parts[1];
        String text = parts[2];

        for (int j = 0; j < server.getConnections().size(); j++) {
            sConnection = server.getConnections().get(j);

            System.out.println(server.getConnections().get(j).getUsername() + " chatter med " + username);

            if (writeTo.equalsIgnoreCase(server.getConnections().get(j).getUsername())){

                try {
                    sConnection.dos.writeUTF("Fra " + username + ": " + text);
                    sConnection.dos.flush();
                    dos.writeUTF("Besked sent fra " + username);
                    break;

                }catch (IOException e){
                    e.printStackTrace();
                }
            }
        }
    }

    public void clientList(){

        for (int j = 0; j < server.getConnections().size(); j++) {
            try {

                dos.writeUTF("Connected " + j + ": " + server.getConnections().get(j).getUsername());
                dos.flush();

            } catch (IOException e) {
                e.printStackTrace();
            }

        }

    }


    @Override
    public void run() {
        try {

            dis = new DataInputStream(socket.getInputStream());
            dos = new DataOutputStream(socket.getOutputStream());

            while (running){

                //Thread til at sove så den ikke er aktiv hele tiden.
                while (dis.available() == 0){
                    try {

                        Thread.sleep(1);

                    }catch (InterruptedException ie){
                        ie.printStackTrace();
                    }
                }

                //Her bliver 'messageIn' sat til den værdi er bliver opfanget fra DataInputStream
                String messageIn = dis.readUTF();

                String[] parts = messageIn.split(":");
                if (parts.length > 0) {
                    // Part1 er kommandoen
                    String part1 = parts[0];

                    // Switch-Cases til valgmuligheder af funktioner for client og server.
                    switch (part1) {

                        case "broadcast":
                            writeToAllInChat(parts);
                            break;

                        case "data":
                            writeToOneClient(parts);
                            break;

                        case "list":
                            clientList();
                            break;

                        /*case "IMAV":
                            imavTimerUpdate();
                            break;*/

                        default:
                            dos.writeUTF("ERROR - Command not understood");
                            dos.flush();
                            break;
                    }
                }

            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }
}
