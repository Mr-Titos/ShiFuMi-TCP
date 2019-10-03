import java.io.*;
import java.net.*;
import java.util.Scanner;

public class Client
{
    public static void main(String[] args) throws IOException
    {
        InetAddress ip = InetAddress.getByName("localhost");

        // connection a l'ip du dessus sur le port 5056
        Socket so = new Socket(ip, 5056);

        DataInputStream inp = new DataInputStream(so.getInputStream());
        DataOutputStream outp = new DataOutputStream(so.getOutputStream());

        Thread t = new Thread(new Logic(so,inp,outp));
        t.start();

    }
}

class Logic extends Thread {
    private Socket s;
    private DataInputStream dis;
    private DataOutputStream dos;

    Logic(Socket so, DataInputStream di, DataOutputStream dop) {
        s = so;
        dis = di;
        dos = dop;
        Start();
    }
    private void Start() {
        try
        {
            System.out.println("You're in Shi-Fu-Mi game !\n" +
            "Type Exit to terminate connection.");
        Scanner scn = new Scanner(System.in);

        ShutDownTask shutDownTask = new ShutDownTask(dos);
        Runtime.getRuntime().addShutdownHook(shutDownTask);

        // Logique application client
        while (true)
        {
            System.out.println(dis.readUTF());
            String tosend = scn.nextLine().trim();
            dos.writeUTF(tosend);

            if(tosend.toLowerCase().equals("exit"))
            {
                System.out.println("Closing this connection : " + s);
                s.close();
                System.out.println("Connection closed");
                break;
            }
            //Probleme sur la récéption du résultat pour celui qui a envoyé en premier
            // Il faut que celui-ci renvoie une donnée quelquonque pour récuperer le résultat
            String received = dis.readUTF();
            System.out.println(received);
        }

        // Liberation des ressources
        scn.close();
        dis.close();
        dos.close();
        }catch(Exception e){
            e.printStackTrace();
        }
    }

}

class ShutDownTask extends Thread {
    private DataOutputStream dos;

    ShutDownTask(DataOutputStream d) {
        dos = d;
    }

    @Override
    public void run() {
        System.out.println("Performing shutdown");
        try {
            dos.writeUTF("Exit");
        } catch (IOException e) { e.printStackTrace(); }
    }
}