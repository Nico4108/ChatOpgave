import java.util.*;
import java.io.*;
import java.net.*;

public class Server {

    //static Vector<ClientHandler> ar = new Vector<>();
    private ArrayList<SConnection> connections = new ArrayList<>();

    private static int PORT = 3030;

    static int i = 0;
    static String name;
    private boolean stop = false;
    private boolean valid = false;

    static private DataInputStream dis;
    static private DataOutputStream dos;

    public void setStop(boolean stop) {
        this.stop = stop;
    }

    public static String getName() {
        return name;
    }

    public static void setName(String name) {
        Server.name = name;
    }

    public List<SConnection> getConnections() {
        return connections;
    }

    public void serverM() throws IOException{

        ServerSocket ss = new ServerSocket(PORT);

        Socket s;

        System.out.println("**** Starting server on PORT: "+ PORT + " ****\n");
        System.out.println("Waiting for clients to connect....\n");

        while (true) {

            s = ss.accept();

            dis = new DataInputStream(s.getInputStream());
            dos = new DataOutputStream(s.getOutputStream());

            //Skriver til klient om brugernavn
            dos.writeUTF("Type Join: and then your user name: ");
            dos.flush();

            //For brugernavn fra klient
            String brugerNavn = dis.readUTF();

            String[] parts = brugerNavn.split(":");
            if (parts.length > 0) {
                String part1 = parts[0];
                setName(parts[1]);


                if (part1.equalsIgnoreCase("join")) {

                    while (true) {
                        userName(name);

                        if (valid){

                        SConnection sConnection = new SConnection(s, this, name);

                        sConnection.start();

                        connections.add(sConnection);
                            System.out.println(name + " har joinet serveren");
                            break;

                    } else{
                        // Ved forkert brugernavn.
                        dos.writeUTF("Try again: ");
                        dos.flush();

                        // Tages nyt input fra klienten.
                        String newTextIn = dis.readUTF();
                        parts = newTextIn.split(":");

                            if (parts.length > 0) {
                                setName(parts[1]);
                            }
                        }
                    }
                }else {

                    // Fejl: forkert format.
                    dos.writeUTF("Forkert format");
                    dos.flush();
                    break;

                }
            }else{
                // Fejl: forkert format.
                dos.writeUTF("Forkert format");
                dos.flush();
                break;
            }
        }

    }

    //metode til at checke om brugernavn
    public void userName(String inputUserName){

        //løber listen igennem for clients
        for (int i = 0; i < connections.size(); i++){

            if (connections.get(i).getUsername().equalsIgnoreCase(inputUserName)){
                setStop(true);
                break;
            }
        }

        if (stop){
            try {
                //Client afvist
                dos.writeUTF("Brugernavn eksistere allerede");
                dos.flush();
                valid = false;
                stop = false;

            }catch (IOException e){
                e.printStackTrace();
            }

        }
        else {
            try {
                dos.writeUTF("Brugernavn OK");
                dos.flush();

                valid = true;

            }catch (IOException E){
                E.printStackTrace();
            }
        }

    }

}

/*class ClientHandler implements Runnable{


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
}*/