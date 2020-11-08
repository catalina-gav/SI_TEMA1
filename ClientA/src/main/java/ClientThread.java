import java.io.*;
import java.net.Socket;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Scanner;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

class ClientThread extends Thread {
    private Socket socket = null;
    public byte[] k1;
    public byte[] k2;
    public byte[] iv;
    private String modOperare;


    public ClientThread(Socket socket, String modOperare, byte[] iv, byte[] k1, byte[] k2) {
        this.socket = socket;
        this.modOperare = modOperare;
        this.iv = iv;
        this.k1 = k1;
        this.k2 = k2;
    }

    public void run() {
        try {
            Crypto crypto = new Crypto();
            StringBuilder stringBuilder = new StringBuilder(1000);

            PrintWriter out =
                    new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(socket.getInputStream()));

            String raspuns = in.readLine();
            System.out.println("S-a conectat " + raspuns);

            File file =
                    new File("E:\\Catalina\\tema.txt");
            Scanner sc = new Scanner(file);

            while (sc.hasNextLine()) {
                stringBuilder.append(sc.nextLine());

            }
            sc.close();

            if (modOperare.equalsIgnoreCase("ecb")) {
                String encrText = new String(crypto.encryptECB(stringBuilder.toString().getBytes("ISO-8859-1"), k1), "ISO-8859-1");
//                    String decrText = new String(crypto.decryptECB(encrText.getBytes("ISO-8859-1"), k1), "ISO-8859-1");
//                    System.out.println("decriptare" + decrText);
                out.println(encrText);
                out.flush();
            } else if (modOperare.equalsIgnoreCase("ofb")) {
                String encrText = new String(crypto.encryptOFB(stringBuilder.toString().getBytes("ISO-8859-1"), k2, iv), "ISO-8859-1");
//                    String decrText = new String(crypto.encryptOFB(encrText.toString().getBytes("ISO-8859-1"), k2, iv), "ISO-8859-1");
//                    System.out.println("decriptare" + decrText);
                out.println(encrText);
                out.flush();
            }
            System.out.println("Am trimis textul criptat catre B");

            socket.close();

        } catch (IOException e) {
            System.err.println("Communication error... " + e);
        } catch (NoSuchAlgorithmException | IllegalBlockSizeException | BadPaddingException | NoSuchPaddingException | InvalidKeyException e) {
            e.printStackTrace();
        } finally {
            try {
                socket.close(); // or use try-with-resources
            } catch (IOException e) {
                System.err.println(e);
            }
        }
    }
}