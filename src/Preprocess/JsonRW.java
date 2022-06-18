package Preprocess;

import javax.json.*;
import java.io.*;
import java.util.Map;

public class JsonRW {
    public static void write(Map<String, String> map, String jsonfile) throws IOException {
        JsonObject jsonObject = null;
        JsonBuilderFactory factory = null;
        factory = Json.createBuilderFactory(null);
        JsonObjectBuilder jsonObjectBuilder = factory.createObjectBuilder();
        for(String key:map.keySet()){
            jsonObjectBuilder.add(key, map.get(key));
        }
        jsonObject = jsonObjectBuilder.build();
        String jsonString = jsonObject.toString();
//        if(new File(jsonString).exists())
        BufferedWriter bf = new BufferedWriter(new FileWriter(jsonfile,true));
        bf.write(jsonString);
        bf.write("\n");
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
