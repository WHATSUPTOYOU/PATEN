package TokenAnalysis.TokenAna.Tools;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;
import java.io.*;
import java.util.ArrayList;

public class JsonRW {
    public static void write(String tpath, ArrayList<String> methodList) throws IOException {
//        System.out.println(jsonData);
//        JsonBuilderFactory jsonBuilderFactory = Json.createBuilderFactory(null);
//        JsonObjectBuilder builder = jsonBuilderFactory.createObjectBuilder();
//        StringReader reader = new StringReader(jsonData);
//        JsonReader jsonReader = Json.createReader(reader);
//        JsonObject jsonObject = jsonReader.readObject();
//        System.out.println(jsonObject.get("Method"));
//        System.out.println(1);

        // create json String
        String jsonString = "{\"Method\":[";
        for(String method: methodList){
            jsonString += "\"";
            jsonString += method;
            jsonString += "\",";
        }
        jsonString = jsonString.substring(0, jsonString.length() - 1);
        jsonString += "]}";
        BufferedWriter bf = new BufferedWriter(new FileWriter(tpath));
        bf.write(jsonString);
        bf.close();
    }

    public static String read(String jsonfile) throws IOException {
        String jsonString = "";
        BufferedReader bfr = new BufferedReader(new FileReader(jsonfile));
        jsonString += bfr.readLine();
        return parse(jsonString);
    }

    public static String parse(String jsonString) throws IOException {
        String content = "";
//        JsonObject jobj = Json.parseObject(s);
        StringReader reader = new StringReader(jsonString);
        JsonReader jsonReader = Json.createReader(reader);
        JsonObject jsonObject = jsonReader.readObject();
        for(String no: jsonObject.keySet()){
            JsonObject methodInfo = jsonObject.getJsonObject(no);
            JsonArray callList = methodInfo.getJsonArray("callGraph");
//            for(JsonValue method: callList.){
//                System.out.println(1);
////                ((JsonObjectBuilderImpl.JsonObjectImpl) method).getString("content");
//            }
            for(int i=0; i<callList.size(); i++){
                JsonObject callMethod = callList.getJsonObject(i);
                content += callMethod.getString("content");
            }
        }
        return content;
    }

    public static void main(String[] args) throws IOException {
        System.out.println(read("./jsonfile/callContent.json"));
    }

}
