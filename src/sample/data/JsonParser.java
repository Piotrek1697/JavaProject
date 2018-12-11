package sample.data;

import com.google.gson.Gson;

import java.io.*;
import java.lang.reflect.Type;
import java.util.ArrayList;

public class JsonParser {

    public static <T> ArrayList<T> getEntityArrayList(String jsonPath, Type type) throws FileNotFoundException {
        
        ArrayList<T> arrayList;

        try (InputStream inputStream = new FileInputStream(jsonPath)) {
            Gson gson = new Gson();
            Reader reader = new InputStreamReader(inputStream);
            arrayList = gson.fromJson(reader, type);
        } catch (IOException e) {
            throw new FileNotFoundException("File not found");
        }

        return arrayList;
    }
}
