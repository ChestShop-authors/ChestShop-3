package com.Acrobot.Breeze.Utils.MojangAPI;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.JSONParser;

import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;
import java.util.concurrent.Callable;

/**
 * Class adapted from evilmidget38's UUIDFetcher
 * https://gist.github.com/evilmidget38/a5c971d2f2b2c3b3fb37
 */

public class UUIDFetcher implements Callable<Map<String, UUID>> {
    private static final int MAX_SEARCH = 100;

    private static final String PROFILE_URL = "https://api.mojang.com/profiles/page/";
    private static final String AGENT = "minecraft";

    private final JSONParser jsonParser = new JSONParser();
    private final List<String> names;

    public UUIDFetcher(String... names) {
        this.names = Arrays.asList(names);
    }

    public Map<String, UUID> call() throws Exception {
        Map<String, UUID> uuidMap = new HashMap<String, UUID>();
        String body = buildBody(names);

        for (int i = 1; i < MAX_SEARCH; i++) {
            HttpURLConnection connection = createConnection(i);

            writeBody(connection, body);

            JSONObject jsonObject = (JSONObject) jsonParser.parse(new InputStreamReader(connection.getInputStream()));
            JSONArray array = (JSONArray) jsonObject.get("profiles");
            Number count = (Number) jsonObject.get("size");

            if (count.intValue() == 0) {
                break;
            }

            for (Object profile : array) {
                JSONObject jsonProfile = (JSONObject) profile;

                String id = (String) jsonProfile.get("id");
                String name = (String) jsonProfile.get("name");

                UUID uuid = UUID.fromString(id.substring(0, 8) + "-" + id.substring(8, 12) + "-" + id.substring(12, 16) + "-" + id.substring(16, 20) + "-" + id.substring(20, 32));

                uuidMap.put(name, uuid);
            }
        }

        return uuidMap;
    }

    private static void writeBody(HttpURLConnection connection, String body) throws Exception {
        DataOutputStream writer = new DataOutputStream(connection.getOutputStream());

        writer.write(body.getBytes());
        writer.flush();
        writer.close();
    }

    private static HttpURLConnection createConnection(int page) throws Exception {
        URL url = new URL(PROFILE_URL + page);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setUseCaches(false);
        connection.setDoInput(true);
        connection.setDoOutput(true);

        return connection;
    }

    @SuppressWarnings("unchecked")
    private static String buildBody(List<String> names) {
        List<JSONObject> lookups = new ArrayList<JSONObject>();

        for (String name : names) {
            JSONObject jsonObject = new JSONObject();

            jsonObject.put("name", name);
            jsonObject.put("agent", AGENT);

            lookups.add(jsonObject);
        }
        return JSONValue.toJSONString(lookups);
    }
}
