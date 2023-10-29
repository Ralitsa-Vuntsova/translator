package server;

import org.json.simple.parser.ParseException;

import java.io.*;
import java.net.*;
import java.util.*;

public class ConnectionHandler implements Runnable {
    protected Socket connection;
    protected static List pool = new LinkedList();

    public ConnectionHandler() {}

    public void handleConnection() {
        try {
            DataOutputStream streamWriter = new DataOutputStream(connection.getOutputStream());
            DataInputStream streamReader =  new DataInputStream(connection.getInputStream());

            if(streamReader.available() > 0) {
                String isWord = streamReader.readUTF();

                if(isWord.equals("word")) {
                    WordHandler wordHandler = new WordHandler(streamReader, streamWriter);
                    wordHandler.translateWord();
                } else {
                    FileHandler fileHandler = new FileHandler(streamReader, streamWriter);
                    fileHandler.translateFile();
                }
            }

            streamWriter.close();
            streamReader.close();
        } catch (FileNotFoundException e) {
            System.out.println("Could not find requested file on the server.");
        } catch (IOException e) {
            System.out.println("Error handling a client: " + e);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public static void processRequest(Socket requestToHandle) {
        synchronized (pool) {
            pool.add(pool.size(), requestToHandle);
            pool.notifyAll();
        }
    }

    public void run() {
        while (true) {
            synchronized (pool) {
                while (pool.isEmpty()) {
                    try {
                        pool.wait();
                    } catch (InterruptedException e) {
                        return;
                    }
                }

                connection = (Socket) pool.remove(0);
            }

            handleConnection();
        }
    }
}
