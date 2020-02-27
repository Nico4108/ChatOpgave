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
        super("Server Connection Tråd");
        this.socket = socket;
        this.server = server;
        this.username = username;
    }

    //Metode som skriver til alle klienter der er connected til serveren
    public void writeToAllInChat(String[] fragments){
        String message = fragments[1];

        for (int a = 0; a < server.getConnections().size(); a++){
            sConnection = server.getConnections().get(a);

            try {
                sConnection.dos.writeUTF("Besked fra " + username + ": " + message);
                //flush ryder OutputStream så den er tom
                sConnection.dos.flush();

            }catch (IOException e){
                e.printStackTrace();
            }
        }
    }

    ////Metode som skriver til en specifik klient der er connected til serveren
    public void writeToOneClient(String[] fragments) {

        String writeTo = fragments[1];
        String text = fragments[2];

        //ArrayListen bliver kørt igennem for at finde den rigtige klient
        for (int a = 0; a < server.getConnections().size(); a++) {

            sConnection = server.getConnections().get(a);

            System.out.println(server.getConnections().get(a).getUsername() + " chatter med " + username);

            //Findes brugernavnet med det ønsket i arraylisten
            if (writeTo.equalsIgnoreCase(server.getConnections().get(a).getUsername())){

                try {
                    //Vi bruger 'sConnection' til at sikre og vi kun skriver til 1 klient (den ønsket)
                    sConnection.dos.writeUTF("Fra " + username + ": " + text);
                    //flush ryder OutputStream så den er tom
                    sConnection.dos.flush();
                    dos.writeUTF("Besked sent fra " + username);
                    break;

                }catch (IOException e){
                    e.printStackTrace();
                }
            }
        }
    }

    //metode til at vise alle aktive klienter på serveren/listen
    public void clientList(){

        //Her bliver vores 'getCoonections' arrayliste loopet igennem og printer alle på listen
        for (int a = 0; a < server.getConnections().size(); a++) {
            try {

                //Her bliver der kun brugt 'dos' da beskede skal til klienten selv
                dos.writeUTF("Connected " + a + ": " + server.getConnections().get(a).getUsername());
                //flush ryder OutputStream så den er tom
                dos.flush();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void helpME() throws IOException {
        dos.writeUTF("***** HELP ME GUIDE *****\n");
        dos.writeUTF("MOne = skrive med 1 client: (MOne: brugernavn på klient du vil skrive til: besked ---> MOne: Nic: Hey du!");
        dos.writeUTF("MAll = skriv til alle klienter: (MAll: dit brugernavn: besekd) ---> MAll: besked");
        dos.writeUTF("List = viser en liste over klienter: (list: dit brugernavn:) ---> list: nic");
    }

    @Override
    //Thread run
    public void run() {
        try {

            //Data in- og outputStream oprettes og tager en socket til at "lytte" efter trafik/input
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

                //Her bliver 'messageIn' sat til den værdi der bliver opfanget fra DataInputStream
                String messageIn = dis.readUTF();

                //Meddelse bliver delt op med ':' da der kan være flere valg og funktioner i en
                //meddelse fra clienten.
                String[] fragments = messageIn.split(":");
                if (fragments.length > 0) {

                    String control = fragments[0];

                    // Switch-Cases til valgmuligheder af funktioner for client og server.
                    switch (control) {

                        case "MAll"://Broadcast
                            writeToAllInChat(fragments);
                            break;

                        case "MOne"://Data
                            writeToOneClient(fragments);
                            break;

                        case "list"://LIST
                            clientList();
                            break;
                        case "HELPME":
                            helpME();
                            break;

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
