import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Random;

public class KMServer {
    // Define the port on which the server is listening
    String alegereA = "neales";
    String alegereB = "neales";
    String stringK3 = "thisisa128bitkey";
    String stringK2 = "thisisaOFBbitkey";
    String stringK1 = "thisisaECBbitkey";
    String ivString = "7777777213271777";
    Boolean confirmare = false;
    Boolean confirmareA = false;
    Boolean confirmareB = false;
    Boolean trimitereA = false;
    Boolean trimitereB = false;
    Boolean done = false;
    Random random = new Random();
    int choice = random.nextInt(3 - 1) + 1;
    byte[] k1 = stringK1.getBytes("ISO-8859-1");
    byte[] k2 = stringK2.getBytes("ISO-8859-1");
    byte[] k3 = stringK3.getBytes("ISO-8859-1");
    byte[] iv = ivString.getBytes("ISO-8859-1");
    public static final int PORT = 8100;

    public void setAlegereA(String alegere) {
        this.alegereA = alegere;
    }

    public void setAlegereB(String alegere) {
        this.alegereB = alegere;
    }

    public KMServer() throws IOException {

        System.out.println("Vectorul de initializre: " + new String(iv, "ISO-8859-1"));
        System.out.println("Cheia 1: " + new String(k1, "ISO-8859-1"));
        System.out.println("Cheia 2: " + new String(k2, "ISO-8859-1"));
        System.out.println("Cheia 3:" + new String(k3, "ISO-8859-1"));
        ServerSocket serverSocket = null;
        try {

            serverSocket = new ServerSocket(PORT);
            while (!done) {
                //System.out.println("Waiting for a client ...");
                try {
                    serverSocket.setSoTimeout(5000);
                    Socket socket = serverSocket.accept();

                    new ClientThread(socket, this).start();
                } catch (IOException e) {
                    System.out.println("server socket timeout");
                }
            }
            System.out.println("S-a terminat executia programului.");
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            serverSocket.close();
        }
    }

    public static void main(String[] args) throws IOException {
        KMServer server = new KMServer();
    }

}
