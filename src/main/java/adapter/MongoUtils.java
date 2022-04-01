package adapter;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.*;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

public class MongoUtils {
    public static final String DB_NAME ="data_craw";
    private static final String HOST = "localhost";
    private static final int PORT = 27017;

    //
    private static final String USERNAME = "root";
    private static final String PASSWORD = "Ijykqs8w@";

    // connect to MongoDB is not mandatory security.
    private static MongoClient getMongoClient_1() throws UnknownHostException {
        MongoClient mongoClient = new MongoClient(HOST, PORT);
        return mongoClient;
    }
    // connect to the DB MongoDB with security.
    private static MongoClient getMongoClient_2() throws UnknownHostException {
        MongoCredential credential = MongoCredential.createMongoCRCredential(
                USERNAME, DB_NAME, PASSWORD.toCharArray());

        MongoClient mongoClient = new MongoClient(
                new ServerAddress(HOST, PORT), Arrays.asList(credential));
        return mongoClient;
    }

    public static MongoClient getMongoClient() throws UnknownHostException {
        // Connect to MongoDB is not mandatory security.
        return getMongoClient_1();

        // You can replace by getMongoClient_2 ()
        // in case of connection to MongoDB with security.
    }

    public static void main(String[] args) throws Exception {
        MongoClient mongoClient = getMongoClient();
        System.out.println("List all DB:");

        // Get database names
        List<String> dbNames = mongoClient.getDatabaseNames();
        for (String dbName : dbNames) {
            System.out.println("+ DB Name: " + dbName);
        }
        DB db = mongoClient.getDB(DB_NAME);
        DBCollection companies = db.getCollection("companies");

        System.out.println("Collection: "+ companies);
        long rowCount = companies.count();
        System.out.println(" Document count: "+ rowCount);
        DBCursor cursor = companies.find();
        int i = 1;
        AdapterDB adapterDB = new AdapterDB("jdbc:mysql://localhost:3306/mongodb_sync_db");
        String date_input = java.time.LocalDate.now().toString();
        while (cursor.hasNext()) {
            ObjectMapper mapper = new ObjectMapper();
            String value = cursor.next().toString();
            JsonNode node = mapper.readTree(value);
//            System.out.println(value);
            String name = node.get("official_name").asText().replace("'","`");
            String date_of_license = node.get("date_of_license").asText().replace("'","`");
            String[] date = date_of_license.split("/");
            date_of_license = date[2] + "-" + date[1] + "-" + date[0];
            String email = node.get("email").asText().replace("'","`");
            String phone = node.get("phone").asText().replace("'","`");
            String address = node.get("address").asText().replace("'","`");
            String director = node.get("director").asText().replace("'","`");
            String business_code = node.get("business_code").asText().replace("'","`");
            String query = "insert into companies value ("
                    + "'" + name + "', "
                    + "'" + date_input + "', "
                    + "'" + date_of_license + "', "
                    + "'" + email + "', "
                    + "'" + phone + "', "
                    + "'" + address + "', "
                    + "'" + director + "', "
                    + "'" + business_code + "')";
//            System.out.println(query);
//            System.out.println(node.get("email").asText());
            AdapterDB.executeUpdate(query);
//            i++;
        }
        System.out.println("Done!");
    }
}
