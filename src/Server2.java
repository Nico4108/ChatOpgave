/*import java.util.*;
import java.io.*;
import java.net.*;

public class Server2 {

    static Vector<ClientHandler> ar = new Vector<>();

    private static int PORT = 3030;

    static int i = 0;
    static String name;

    public static String getName() {
        return name;
    }

    public static void setName(String name) {
        Server.name = name;
    }

    public static void main(String[] args) throws IOException{

        ServerSocket ss = new ServerSocket(PORT);
        Scanner scanner = new Scanner(System.in);
        String AC = "ok";

        Socket s;

        System.out.println("**** Starting server on PORT: "+ PORT + " ****\n");
        System.out.println("Waiting for clients to connect....\n");

        while (true) {

            s = ss.accept();

            DataInputStream dis = new DataInputStream(s.getInputStream());
            DataOutputStream dos = new DataOutputStream(s.getOutputStream());

            //Skriver til klient om brugernavn
            dos.writeUTF("Type Join: and then your user name: ");
            dos.flush();

            //For brugernavn fra klient
            String brugerNavn = dis.readUTF();

            String[] parts = brugerNavn.split(":");
            if (parts.length > 0) {
                String part1 = parts[0];
                setName(parts[1]);

                System.out.println("** Client: " + i + " waiting to be accepted **");

                if (scanner.next().equals(AC)) {

                    System.out.println("New client request received : " + s);

                    System.out.println("Creating a new handler for this client...");

                    ClientHandler mtch = new ClientHandler(s, "client " + i, dis, dos);

                    Thread t = new Thread(mtch);

                    System.out.println("Adding client " + i + " to active client list");

                    ar.add(mtch);

                    t.start();

                    i++;

                } else {
                    System.out.println("You were not accepted.... godbye!");
                    return;
                }
            }
        }

    }

}

class ClientHandler implements Runnable{


    Scanner scn = new Scanner(System.in);
    private String name;
    final DataInputStream dis;
    final DataOutputStream dos;
    Socket s;
    boolean isloggedin;

    public ClientHandler(Socket s, String name, DataInputStream dis, DataOutputStream dos) {

        this.dis = dis;
        this.dos = dos;
        this.name = name;
        this.s = s;
        this.isloggedin = true;
    }

    @Override
    public void run() {

        String received;
        while (true){

            try {
                received = dis.readUTF();

                System.out.println(received);

                if (received.equals("Logout")){
                    this.isloggedin=false;
                    this.s.close();
                    break;
                }

                StringTokenizer st = new StringTokenizer(received, "#");
                String MsgToSend = st.nextToken();
                String recipient = st.nextToken();

                for (ClientHandler mc : Server.ar){
                    if (mc.name.equals(recipient) && mc.isloggedin == true){
                        mc.dos.writeUTF(this.name + " : " + MsgToSend);
                        break;
                    }
                }

            }catch (IOException e){
                e.printStackTrace();
            }
        }
        try {
            this.dis.close();
            this.dos.close();
        }catch (IOException e){
            e.printStackTrace();
        }

    }
}
*/