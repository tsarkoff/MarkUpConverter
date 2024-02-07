import com.opencsv.CSVReader;
import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import com.opencsv.bean.*;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.w3c.dom.*;

import javax.xml.parsers.*;
import java.io.*;
import java.util.*;

public class Main {
    public static void main(String[] args) {
        String[] columnMapping = {"id", "firstName", "lastName", "country", "age"};
        writeString(listToJson(parseCSV(columnMapping, "data.csv")), "data.json");
        writeString(listToJson(parseXML("data.xml")), "data2.json");
        jsonToList(readJsonString("data2.json")).forEach(System.out::println);
    }

    private static List<Employee> parseCSV(String[] columnMapping, String fileName) {
        try (CSVReader reader = new CSVReader(new FileReader(fileName))) {
            ColumnPositionMappingStrategy<Employee> strategy = new ColumnPositionMappingStrategy<>();
            strategy.setType(Employee.class);
            strategy.setColumnMapping(columnMapping);
            return new CsvToBeanBuilder<Employee>(reader)
                    .withMappingStrategy(strategy)
                    .build()
                    .parse();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    private static List<Employee> parseXML(String fileName) {
        List<Employee> employees = new ArrayList<Employee>();
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        try {
            NodeList nodeList = factory
                    .newDocumentBuilder()
                    .parse(fileName)
                    .getDocumentElement()
                    .getChildNodes();
            for (int i = 0; i < nodeList.getLength(); i++) {
                Node node = nodeList.item(i);
                if (Node.ELEMENT_NODE == node.getNodeType()) {
                    Element e = (Element) node;
                    employees.add(
                            new Employee(
                                    Long.parseLong(e.getElementsByTagName("id").item(0).getTextContent()),
                                    e.getElementsByTagName("firstName").item(0).getTextContent(),
                                    e.getElementsByTagName("lastName").item(0).getTextContent(),
                                    e.getElementsByTagName("country").item(0).getTextContent(),
                                    Integer.parseInt(e.getElementsByTagName("age").item(0).getTextContent())
                            )
                    );
                }
            }
            return employees;
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    private static String listToJson(List<Employee> list) {
        return new GsonBuilder()
                .setPrettyPrinting()
                .create()
                .toJson(list, new TypeToken<List<Employee>>() {}.getType());
    }

    private static void writeString(String json, String fileName) {
        try (FileWriter dataJson = new FileWriter(fileName)) {
            dataJson.write(json);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    private static String readJsonString(String fileName) {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String s;
            while ((s = br.readLine()) != null) {
                sb.append(s);
            }
            return sb.toString();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    private static List<Employee> jsonToList(String json) {
        List<Employee> employees = new ArrayList<>();
        Gson gson = new GsonBuilder().create();
        try {
            JSONArray jsonArray = (JSONArray) new JSONParser().parse(json);
            for (Object o : jsonArray) {
                employees.add(gson.fromJson(((JSONObject) o).toJSONString(), Employee.class));
            }
            return employees;
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return null;
    }
}
