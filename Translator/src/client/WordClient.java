package client;

import java.io.*;
import java.net.*;

public class WordClient {
    protected DataInputStream socketReader;
    protected DataOutputStream socketWriter;
    protected String hostIp;
    protected int hostPort;

    public WordClient(String aHostIp, int aHostPort) {
        hostIp = aHostIp;
        hostPort = aHostPort;
    }

    public void setUpConnection() {
        try {
            Socket client = new Socket(hostIp, hostPort);

            socketReader = new DataInputStream(client.getInputStream());
            socketWriter = new DataOutputStream(client.getOutputStream());
        } catch (UnknownHostException e) {
            System.out.println("Error setting up socket connection: unknown host at " + e);
        }
        catch (IOException e) {
            System.out.println("Error setting up socket connection: " + e);
        }
    }

    public void tearDownConnection() {
        try {
            socketWriter.close();
            socketReader.close();
        } catch (IOException e) {
            System.out.println("Error tearing down socket connection: " + e);
        }
    }

    public String getTranslatedWord(String word, String languageFrom, String languageTo) throws IOException {
        socketWriter.writeUTF("word");
        socketWriter.writeUTF(word);
        socketWriter.writeUTF(languageFrom);
        socketWriter.writeUTF(languageTo);
        socketWriter.flush();

        String translatedWord = socketReader.readUTF();

        return translatedWord;
    }
}