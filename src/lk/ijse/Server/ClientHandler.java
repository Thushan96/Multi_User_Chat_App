package lk.ijse.Server;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

public class ClientHandler extends Thread{
    private ArrayList<ClientHandler> clients;
    private Socket socket;
    private BufferedReader reader;
    private PrintWriter writer;

    ObjectOutputStream oos = null;
    ObjectInputStream ois = null;

    public ClientHandler(Socket socket, ArrayList<ClientHandler> clients) {
        try {
            this.socket = socket;
            this.clients = clients;
            this.reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));//get msg from input stream and add it to bufferedReader(to convert as string)
            this.writer = new PrintWriter(socket.getOutputStream(), true);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void run() {
        try {
            String msg;
            while ((msg = reader.readLine()) != null) {

                try{

                }catch (Exception e){
                    System.out.println("closed");
                }
                if (msg.equalsIgnoreCase( "exit")) {
                    return;
                }
                for (ClientHandler cl : clients) {
                    cl.writer.println(msg);
                }
            }
        } catch (Exception e) {

        }


        finally {
            try {
                System.out.println("Client has Disconnected!");
                reader.close();
                writer.close();
                socket.close();
            } catch (IOException e) {

            }
        }
    }

}
