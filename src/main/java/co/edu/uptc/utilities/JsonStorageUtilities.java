package co.edu.uptc.utilities;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class JsonStorageUtilities {
    private Gson gson;

    private static final String FILEPATH = "src/main/java/persistence/";
    private static final String EXTENSION = ".json";

    public JsonStorageUtilities(){
        //El gson esta inicializado asi para que se escriba en cascada y no en una misma linea
        gson = new GsonBuilder().setPrettyPrinting().create();
    }

    public <T> void saveDataToFile(List<T> dataList, String fileName, Type type) {
        File file = new File(FILEPATH + fileName + EXTENSION);
        if (dataList == null) {
            dataList = new ArrayList<>();
        }
        try (FileWriter fileWriter = new FileWriter(file)) {
            gson.toJson(dataList, type, fileWriter);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public <T> List<T> readContentFromFile(String fileName, Type type) {
        List<T> dataList = new ArrayList<>();

        File file = new File( FILEPATH + fileName + EXTENSION);
        if (!file.exists()) {
            return null;
        }
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(file))) {
            dataList.clear();
            dataList = gson.fromJson(bufferedReader, type);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return dataList;
    }



}
