import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class ImAlive extends Thread{

    private boolean IMAV = true;
    private DataInputStream dis;
    private DataOutputStream dos;


    public ImAlive() {
    }

    @Override
    public void run() {

        //Laver en evig loop som fortæller at serveren er i live hvert 20'ene sekund
        while (IMAV){
            try {

                Thread.sleep(20000);
                System.out.println("SERVER ER I LIVE");
                //dos.writeUTF("SERVER ER I LIVE");


            } catch (InterruptedException e) {
                e.printStackTrace();
                System.out.println("IMAV Fejl");
            }
        }
    }
}
