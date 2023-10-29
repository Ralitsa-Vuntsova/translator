package server;

public class Main {
    public static void main(String[] args) {
        TranslatorServer server = new TranslatorServer(3000, 3);

        server.setUpHandlers();
        server.acceptConnections();
    }
}
