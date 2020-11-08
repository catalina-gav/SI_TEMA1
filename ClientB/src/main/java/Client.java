import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Scanner;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

public class Client {
    public static String stringK3 = "thisisa128bitkey";
    public static byte[] k3;
    public static byte[] k1;
    public static byte[] k2;
    public static byte[] iv;
    private static String modOperare = "";

    static {
        try {
            k3 = stringK3.getBytes("ISO-8859-1");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        Crypto crypto = new Crypto();
        String serverAddress = "127.0.0.1"; // The server's IP address
        int PORT = 8100; // The server's port
        try (
                Socket socket = new Socket(serverAddress, PORT);

                PrintWriter out =
                        new PrintWriter(socket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(
                        new InputStreamReader(socket.getInputStream()))) {
            String request = "Client B";
            out.println(request);
            String response = in.readLine();
            System.out.println(response);
            String inputString = "";
            try {

                Scanner scanner = new Scanner(System.in);
                inputString = scanner.nextLine();
                while (!inputString.equalsIgnoreCase("ECB") && !inputString.equalsIgnoreCase("OFB")) {
                    System.out.println("Nu ati ales un mod corect, incercati din nou.Alegeti OFB/ofb sau ECB/ecb");
                    inputString = scanner.nextLine();
                }
                out.println(inputString);
                out.flush();
                String response2 = in.readLine();
                byte[] decryptedMessage = crypto.decryptECB(response2.getBytes("ISO-8859-1"), k3);
                String decr = new String(decryptedMessage, "ISO-8859-1");
//                System.out.println(decr);
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject = new JSONObject(decr);
                } catch (JSONException err) {
                    System.err.println("Ooops... " + err);
                    ;
                }
                System.out.println("modul este: " + jsonObject.getString("mod"));
                modOperare = jsonObject.getString("mod");
                byte[] cheie = jsonObject.getString("cheie").getBytes("ISO-8859-1");
                iv = jsonObject.getString("iv").getBytes("ISO-8859-1");
                if (jsonObject.getString("mod").equalsIgnoreCase("ecb")) {
                    k1 = cheie;
                    out.println(new String(crypto.encryptECB("confirm".getBytes("ISO-8859-1"), cheie), "ISO-8859-1"));
                    out.flush();
                } else if (jsonObject.getString("mod").equalsIgnoreCase("ofb")) {
                    k2 = cheie;
                    out.println(new String(crypto.encryptOFB("confirm".getBytes("ISO-8859-1"), cheie, iv), "ISO-8859-1"));
                    out.flush();
                }
                System.out.println(in.readLine());

            } catch (UnknownHostException | NoSuchPaddingException | NoSuchAlgorithmException | BadPaddingException | IllegalBlockSizeException | InvalidKeyException e) {
                System.err.println("No server listening... " + e);
            }
        }

        Thread.sleep(1000);
        try (

                Socket socket = new Socket(serverAddress, 8080);

                PrintWriter out =
                        new PrintWriter(socket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(
                        new InputStreamReader(socket.getInputStream()))) {
            String request = "Client B";
            out.println(request);
            out.flush();
            String response = in.readLine();
//            System.out.println(response);
            if (modOperare.equalsIgnoreCase("ecb")) {
                String decrText = new String(crypto.decryptECB(response.getBytes("ISO-8859-1"), k1), "ISO-8859-1");
                System.out.println("Textul primit de la A este: " + decrText);
            } else if (modOperare.equalsIgnoreCase("ofb")) {
                String decrText = new String(crypto.encryptOFB(response.toString().getBytes("ISO-8859-1"), k2, iv), "ISO-8859-1");
                System.out.println("Textul primit de la A este: " + decrText);
            }

        } catch (NoSuchAlgorithmException | IllegalBlockSizeException | BadPaddingException | NoSuchPaddingException | InvalidKeyException e) {
            e.printStackTrace();
        }
        Thread.sleep(6000);

        try (

                Socket socket = new Socket(serverAddress, PORT);

                PrintWriter out =
                        new PrintWriter(socket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(
                        new InputStreamReader(socket.getInputStream()))) {
            String request = "trimitereB";
            out.println(request);
            out.flush();
            String response = in.readLine();
            System.out.println(response);

        }


    }
}