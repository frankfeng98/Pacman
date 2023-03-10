package edu.rice.comp504.controller;

import com.google.gson.Gson;
import edu.rice.comp504.adapter.DispatchAdapter;

import java.util.concurrent.ConcurrentHashMap;

import static spark.Spark.*;
import static spark.Spark.post;

public class PacmanController {
    /**
     * Main entry point into the program.
     * @param args Program arguments normally passed to main on the command line
     */
    public static void main(String[] args) {
        staticFiles.location("/public");
        port(getHerokuAssignedPort());
        Gson gson = new Gson();
        ConcurrentHashMap<String, DispatchAdapter> map = new ConcurrentHashMap<>();

        get("/initialize", (request, response) -> {
            String uuid = request.queryParams("uuid");
            System.out.println(uuid);
            if (uuid == null) {
                return "Init first";
            }
            DispatchAdapter da;
            if (map.containsKey(uuid)) {
                da = map.get(uuid);
            } else {
                da = new DispatchAdapter();
                map.put(uuid, da);
            }
            return gson.toJson(da.initializeGame(Integer.parseInt(request.queryParams("level")), Integer.parseInt(request.queryParams("numberOfGhosts")), Integer.parseInt(request.queryParams("lives"))));
        });

        post("/update", (request, response) -> {
            String uuid = request.queryParams("uuid");
            if (uuid == null) {
                return "Init first";
            }
            DispatchAdapter da;
            if (map.containsKey(uuid)) {
                da = map.get(uuid);
            } else {
                return null;
            }
            return gson.toJson(da.updateStore(Integer.parseInt(request.queryParams("direction"))));
        });
    }

    /**
     * Get the Heroku assigned port number.
     * @return  Heroku assigned port number
     */
    static int getHerokuAssignedPort() {
        ProcessBuilder processBuilder = new ProcessBuilder();
        if (processBuilder.environment().get("PORT") != null) {
            return Integer.parseInt(processBuilder.environment().get("PORT"));
        }
        return 4567; //return default port if heroku-port isn't set (i.e. on localhost)
    }
}
