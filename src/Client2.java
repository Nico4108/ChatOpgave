
import java.io.*;
import java.net.*;
import java.util.Scanner;

public class Client2 {

    //Serverport som serveren kører på.
    final static int serverPort = 3030;

    public static void main(String[] args) throws UnknownHostException, IOException {

        Scanner scn = new Scanner(System.in);

        //Finder vores localhost ip
        InetAddress ip = InetAddress.getByName("localhost");

        //Vi laver connection til server
        Socket s = new Socket(ip, serverPort);

        //Data in- og outputStream oprettes og tager en socket til at "lytte" efter trafik/input
        DataInputStream dis = new DataInputStream(s.getInputStream());
        DataOutputStream dos = new DataOutputStream(s.getOutputStream());

        //send besked tråd
        Thread sendMessage = new Thread(new Runnable() {

            @Override
            public void run() {

                while (true){

                    //skriv besked
                    String msg = scn.nextLine();

                    try {
                        //send skrevet besked
                        dos.writeUTF(msg);

                    }catch (IOException e){
                        e.printStackTrace();
                    }
                }
            }
        });

        //læs modtaget besked tråd
        Thread readMessage = new Thread(new Runnable() {

            @Override
            public void run() {

                while (true){

                    try {
                        //Læs den modtaget besked fra en klient
                        String msg = dis.readUTF();
                        System.out.println(msg);

                    }catch (IOException e){
                        e.printStackTrace();
                    }
                }
            }
        });

        //Starter tråde
        sendMessage.start();
        readMessage.start();
    }
}
