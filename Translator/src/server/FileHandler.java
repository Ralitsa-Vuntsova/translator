package server;

import org.json.simple.parser.ParseException;

import java.io.*;
import java.util.LinkedHashMap;
import java.util.Map;

public class FileHandler {
    DataInputStream dataInputStream;
    DataOutputStream dataOutputStream;

    public FileHandler() {
        this.dataInputStream = null;
        this.dataOutputStream = null;
    }

    public FileHandler(DataInputStream dataInputStream, DataOutputStream dataOutputStream) {
        this.dataInputStream = dataInputStream;
        this.dataOutputStream = dataOutputStream;
    }

    public void translateFile() throws IOException, ParseException {
        String languageFrom = dataInputStream.readUTF();
        String languageTo = dataInputStream.readUTF();

        String tmpFileDirectoryPath = System.getProperty("java.io.tmpdir") + "file.txt";

        receiveFile(tmpFileDirectoryPath);

        Map<String, String> content = convertFileContentToMap(tmpFileDirectoryPath, languageFrom, languageTo);

        createFile(content);

        sendFile(System.getProperty("java.io.tmpdir") + "translated_file.txt");
    }

    private void sendFile(String filePath) throws IOException {
        File file = new File(filePath);
        FileInputStream fileInputStream = new FileInputStream(file);

        dataOutputStream.writeLong(file.length());

        int bytes = 0;
        byte[] buffer = new byte[4 * 1024];

        while ((bytes = fileInputStream.read(buffer)) != -1){
            dataOutputStream.write(buffer,0,bytes);
            dataOutputStream.flush();
        }

        fileInputStream.close();
    }

    private void receiveFile(String fileName) throws IOException {
        FileOutputStream fileOutputStream = new FileOutputStream(fileName);

        int bytes = 0;
        long size = dataInputStream.readLong();
        byte[] buffer = new byte[4 * 1024];

        while (size > 0 && (bytes = dataInputStream.read(buffer, 0, (int)Math.min(buffer.length, size))) != -1) {
            fileOutputStream.write(buffer,0,bytes);
            size -= bytes;
        }

        fileOutputStream.close();
    }

    private void createFile(Map<String, String> content) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(System.getProperty("java.io.tmpdir") + "translated_file.txt"));

        for (Map.Entry<String, String> entry : content.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();

            writer.write(key + "=" + value);
            writer.newLine();
        }

        writer.close();
    }

    private String translateWord(String word, String languageTo, String languageFrom) throws IOException, ParseException {
        ApertiumTranslator translator = new ApertiumTranslator();
        String translatedWord = translator.translateWord(word, languageTo, languageFrom);

        return translatedWord;
    }

    private Map<String, String> convertFileContentToMap(String filePath, String languageFrom, String languageTo) throws IOException, ParseException {
        FileInputStream fileInputStream = new FileInputStream(filePath);
        DataInputStream in = new DataInputStream(fileInputStream);
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(in));

        String stringLine;
        Map<String, String> content = new LinkedHashMap<>();
        while ((stringLine = bufferedReader.readLine()) != null) {
            String[] parts = stringLine.split("=");

            if (parts.length == 1) {
                content.put(parts[0], "");
            } else {
                content.put(parts[0], parts[1]);
            }
        }

        in.close();

        return translateMap(content, languageFrom, languageTo);
    }

    private Map<String, String> translateMap(Map<String, String> content, String languageFrom, String languageTo) throws IOException, ParseException {
        for (Map.Entry<String, String> entry : content.entrySet()) {
            String key = entry.getKey();

            if(entry.getValue() == "") {
                String translatedWord = translateWord(entry.getKey(), languageFrom, languageTo);
                content.put(key, translatedWord);
            }
        }

        return content;
    }
}
