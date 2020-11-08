import org.json.JSONObject;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

class ClientThread extends Thread {
    private Socket socket = null;
    private KMServer server = null;

    public ClientThread(Socket socket, KMServer server) {
        this.socket = socket;
        this.server = server;
    }

    public void run() {
        try {
            Crypto crypto = new Crypto();

            BufferedReader in = new BufferedReader(
                    new InputStreamReader(socket.getInputStream()));

            String numeClient = in.readLine();

            if (!numeClient.equalsIgnoreCase("Client A") && !numeClient.equalsIgnoreCase("Client B")) {
                System.out.println("S a conectat un client");
                if (numeClient.equalsIgnoreCase("trimitereA")) {
                    server.trimitereA = true;
                } else if (numeClient.equalsIgnoreCase("trimitereB")) {
                    server.trimitereB = true;
                }
                //System.out.println(server.trimitereA);
               // System.out.println(server.trimitereB);
                while (this.server.trimitereA.equals(false) || this.server.trimitereB.equals(false)) {
                    sleep(5000);
                }
                PrintWriter out = new PrintWriter(socket.getOutputStream());
                out.println("S-a terminat schimbul de mesaje");
                out.flush();
                server.done=true;

            } else {
                System.out.println("S-a conectat  " + numeClient);

                PrintWriter out = new PrintWriter(socket.getOutputStream());

                String alegere = "Care este modul de operare dorit?";

                out.println(alegere);

                out.flush();

                String modOperare = in.readLine();
                System.out.println(numeClient + " : Modul de operare dorit este: " + modOperare);

                if (numeClient.equals("Client A")) {
                    this.server.setAlegereA(modOperare);
                } else {
                    this.server.setAlegereB(modOperare);
                }
                while (this.server.alegereA.equals("neales") || this.server.alegereB.equals("neales")) {
                    sleep(5000);
                }


                if ((server.alegereA.equalsIgnoreCase(server.alegereB) && server.alegereA.equalsIgnoreCase("ofb")) || ((!server.alegereA.equalsIgnoreCase(server.alegereB) && server.choice == 1))) {

                    JSONObject obj = new JSONObject();
                    String cheie = new String(this.server.k2, "ISO-8859-1");
                    String iv = new String(this.server.iv, "ISO-8859-1");
                    obj.put("cheie", cheie);
                    obj.put("mod", "OFB");
                    obj.put("iv", iv);

                    String jsonString = obj.toString();
                    byte[] encryptedMessage = crypto.encryptECB(jsonString.getBytes("ISO-8859-1"), this.server.k3);
                    for (byte b : encryptedMessage) {
                        System.out.print(b + " ");
                    }
                    String encryptedString = new String(encryptedMessage, "ISO-8859-1");
                    //System.out.println("mesaj decriptat: " + new String(crypto.decryptECB(encryptedString.getBytes("ISO-8859-1"), server.k3)));
                    for (byte b : encryptedString.getBytes("ISO-8859-1")) {
                        System.out.print(b + "  ");
                    }

                    out.println(encryptedString);
                    out.flush();
                    String raspuns = in.readLine();
                    String confirmare = new String(crypto.encryptOFB(raspuns.getBytes("ISO-8859-1"), server.k2, server.iv), "ISO-8859-1");
                    System.out.println("am primit de la " + numeClient + " " + confirmare);
                    if (confirmare.equalsIgnoreCase("confirm         ")) {
                        if (numeClient.equals("Client A")) {
                            //System.out.println("confirmare a");
                            server.confirmareA = true;
                        } else {
                            //System.out.println("confirmare a");
                            server.confirmareB = true;
                        }
                    }
                    sleep(2000);
                    if (server.confirmareA && server.confirmareB) {
                        out.println("Incepeti comunicarea intre clienti");
                        out.flush();
                        server.confirmare = true;
                    }

                } else if ((server.alegereA.equalsIgnoreCase(server.alegereB) && server.alegereA.equalsIgnoreCase("ecb")) || ((!server.alegereA.equalsIgnoreCase(server.alegereB) && server.choice == 2))) {

                    JSONObject obj = new JSONObject();
                    String cheie = new String(this.server.k1, "ISO-8859-1");
                    obj.put("cheie", cheie);
                    obj.put("mod", "ECB");
                    obj.put("iv", "");
                    String jsonString = obj.toString();
                    byte[] encryptedMessage = crypto.encryptECB(jsonString.getBytes("ISO-8859-1"), this.server.k3);

                    String encryptedString = new String(encryptedMessage, "ISO-8859-1");

                    out.println(encryptedString);
                    out.flush();
                    String raspuns = in.readLine();
                    String confirmare = new String(crypto.decryptECB(raspuns.getBytes("ISO-8859-1"), server.k1), "ISO-8859-1");
                    System.out.println("am primit de la " + numeClient + " " + confirmare);
                    if (confirmare.equalsIgnoreCase("confirm         ")) {
                        if (numeClient.equalsIgnoreCase("Client A")) {
                            System.out.println("client a confirmare");
                            server.confirmareA = true;
                        } else {
                            System.out.println("client b confirmare");
                            server.confirmareB = true;

                        }
                    }
                    sleep(1000);
                    if (server.confirmareA && server.confirmareB) {
                        System.out.println("amandoi au confirmat");
                        out.println("Incepeti comunicarea intre clienti");
                        out.flush();
                        server.confirmare = true;
                    }
                }
            }

        } catch (IOException | InterruptedException e) {
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