package client;

import java.io.*;
import java.net.*;

public class FileClient {
    protected DataInputStream socketReader;
    protected DataOutputStream socketWriter;
    protected String hostIp;
    protected int hostPort;

    public FileClient(String aHostIp, int aHostPort) {
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

    public void getTranslatedFile(String filePath, String languageTo, String languageFrom) throws IOException {
        socketWriter.writeUTF("file");
        socketWriter.writeUTF(languageFrom);
        socketWriter.writeUTF(languageTo);

        sendFile(filePath);
        socketWriter.flush();

        String[] filePathParts = filePath.split("\\.");
        String newFilePath = filePathParts[0] + "_new." + filePathParts[1];

        receiveFile(newFilePath);
    }

    private void sendFile(String filePath) throws IOException {
        File file = new File(filePath);
        FileInputStream fileInputStream = new FileInputStream(file);

        socketWriter.writeLong(file.length());

        int bytes = 0;
        byte[] buffer = new byte[4 * 1024];

        while ((bytes = fileInputStream.read(buffer)) != -1){
            socketWriter.write(buffer,0,bytes);
            socketWriter.flush();
        }

        fileInputStream.close();
    }

    private void receiveFile(String fileName) throws IOException {
        FileOutputStream fileOutputStream = new FileOutputStream(fileName);

        int bytes = 0;
        long size = socketReader.readLong();
        byte[] buffer = new byte[4 * 1024];

        while (size > 0 && (bytes = socketReader.read(buffer, 0, (int)Math.min(buffer.length, size))) != -1) {
            fileOutputStream.write(buffer,0,bytes);
            size -= bytes;
        }

        fileOutputStream.close();
    }
}
