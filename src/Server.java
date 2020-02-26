import java.util.*;
import java.io.*;
import java.net.*;

public class Server {

    //static Vector<ClientHandler> ar = new Vector<>();
    private ArrayList<SConnection> connections = new ArrayList<>();

    //Serverens port nummer
    private static int PORT = 3030;

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

        //Laver en ny socket som venter på Clienter på PORT
        ServerSocket ss = new ServerSocket(PORT);

        Socket s;

        System.out.println("**** Starter server på PORT: "+ PORT + " ****\n");
        System.out.println("Venter for klienter til at connect.....\n");

        //Et while loop bliver lavet når en forbindelse fra en client bliver fundet
        while (true) {

            //accepterer clienten
            s = ss.accept();

            //DataInd- og OutputStream bliver brugt til at sende data fra klient til serveren og omvendt.
            dis = new DataInputStream(s.getInputStream());
            dos = new DataOutputStream(s.getOutputStream());

            //Serveren skriver til klienten og beder om et brugernavn
            dos.writeUTF("Type Join: and then your user name: ");
            //flush ryder OutputStream så den er tom
            dos.flush();

            //Serveren modtager indput fra klienten og sætten det til klientens brugernavn
            String brugerNavn = dis.readUTF();

            //Her bliver vores meddelse delt op med ':' da der kan være flere valg og funktioner i en
            //meddelse fra clienten.
            String[] fragments = brugerNavn.split(":");
            if (fragments.length > 0) {
                String part1 = fragments[0];
                setName(fragments[1]);

                //Før en klient kan joine serveren skal de skrive 'join:' og deres brugernavn
                //hvis dette ikke er opfyldt kaster den en fejl
                if (part1.equalsIgnoreCase("join")) {

                    while (true) {
                        userName(name);

                        if (valid){

                        SConnection sConnection = new SConnection(s, this, name);

                        //Starter tråden
                        sConnection.start();

                        //vores connection bliver her tilføjet til vores 'ArrayListe'
                        connections.add(sConnection);
                            System.out.println(name + " har joinet serveren\n");
                            break;

                    } else{
                        // Ved indtastet forkert brugernavn.
                        dos.writeUTF("Prøv igen: ");
                        dos.flush();

                        //nyt input fra klienten.
                        String newTextIn = dis.readUTF();
                            fragments = newTextIn.split(":");

                            if (fragments.length > 0) {
                                setName(fragments[1]);
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

    //metode til at checke om brugernavn er brugbart og ikke findes i forvejen
    public void userName(String inputUserName){

        //løber listen igennem for klienter
        for (int i = 0; i < connections.size(); i++){

            //hvis et brugernavn allerede findes bliver klienten afvist.
            if (connections.get(i).getUsername().equalsIgnoreCase(inputUserName)){
                setStop(true);
                break;
            }
        }

        if (stop){
            try {
                //Når klienten bliver afvist
                dos.writeUTF("Brugernavn eksistere allerede");
                dos.flush();
                valid = false;
                stop = false;

            }catch (IOException e){
                e.printStackTrace();
            }

        }
        //Brugernavnet findes ikke og bliver godkendt
        else {
            try {
                dos.writeUTF("Brugernavn OK");
                dos.writeUTF("Du kan nu chatte!");
                dos.writeUTF("Skriv 'HELPME' for en guide");
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