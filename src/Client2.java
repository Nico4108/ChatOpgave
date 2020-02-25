
import java.io.*;
import java.net.*;
import java.util.Scanner;

public class Client2 {

    final static int serverPort = 3030;

    public static void main(String[] args) throws UnknownHostException, IOException {

        Scanner scn = new Scanner(System.in);

        InetAddress ip = InetAddress.getByName("localhost");

        Socket s = new Socket(ip, serverPort);

        DataInputStream dis = new DataInputStream(s.getInputStream());
        DataOutputStream dos = new DataOutputStream(s.getOutputStream());

        Thread sendMessage = new Thread(new Runnable() {

            @Override
            public void run() {

                while (true){

                    String msg = scn.nextLine();

                    try {
                        dos.writeUTF(msg);

                    }catch (IOException e){
                        e.printStackTrace();
                    }
                }
            }
        });

        Thread readMessage = new Thread(new Runnable() {

            @Override
            public void run() {

                while (true){

                    try {

                        String msg = dis.readUTF();
                        System.out.println(msg);

                    }catch (IOException e){
                        e.printStackTrace();
                    }
                }
            }
        });

        sendMessage.start();
        readMessage.start();
    }

}
