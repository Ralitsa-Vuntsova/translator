package server;

import org.json.simple.parser.ParseException;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class WordHandler {
    DataInputStream dataInputStream;
    DataOutputStream dataOutputStream;

    public WordHandler() {
        dataInputStream = null;
        dataOutputStream = null;
    }

    public WordHandler(DataInputStream dataInputStream, DataOutputStream dataOutputStream) {
        this.dataInputStream = dataInputStream;
        this.dataOutputStream = dataOutputStream;
    }

    public void translateWord() throws IOException, ParseException {
        String word = dataInputStream.readUTF();
        String languageFrom = dataInputStream.readUTF();
        String languageTo = dataInputStream.readUTF();

        ApertiumTranslator translator = new ApertiumTranslator();
        String translatedWord = translator.translateWord(word, languageFrom, languageTo);

        dataOutputStream.writeUTF(translatedWord);
    }
}
