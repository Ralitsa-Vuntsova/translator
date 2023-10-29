package server;

import java.io.*;
import java.net.*;

public class TranslatorServer {
    protected int maxConnections;
    protected int listenPort;

    public TranslatorServer(int aListenPort, int maxConnections) {
        listenPort = aListenPort;
        this.maxConnections = maxConnections;
    }

    public void acceptConnections() {
        System.out.println("Server is starting to listen for requests!");

        try {
            ServerSocket server = new ServerSocket(listenPort, 5);

            Socket incomingConnection = null;
            while (true) {
                incomingConnection = server.accept();
                handleConnection(incomingConnection);
            }
        } catch (BindException e) {
            System.out.println("Unable to bind to port " + listenPort);
        } catch (IOException e) {
            System.out.println("Unable to instantiate a ServerSocket on port: " + listenPort);
        }
    }

    protected void handleConnection(Socket connectionToHandle) {
        ConnectionHandler.processRequest(connectionToHandle);
    }

    public void setUpHandlers() {
        for (int i = 0; i < maxConnections; i++) {
            ConnectionHandler currentHandler = new ConnectionHandler();
            new Thread(currentHandler, "Handler " + i).start();
        }
    }
}