package sample.data;

import com.google.gson.Gson;

import java.io.*;
import java.util.Map;

/**
 * Class created to easily parsing json file to some objects' class.
 */
public class JsonParser {

    /**
     * Method parse json to map
     * @param file
     * @return Map of json file
     * @throws IOException
     */
    public static Map getMap(File file) throws IOException {

        Map map;

        try (InputStream inputStream = new FileInputStream(file)) {
            Gson gson = new Gson();
            Reader reader = new InputStreamReader(inputStream);
            map = gson.fromJson(reader,Map.class);
        } catch (IOException e) {
            throw new IOException("Json loading failed");
        }

        return map;
    }
}
