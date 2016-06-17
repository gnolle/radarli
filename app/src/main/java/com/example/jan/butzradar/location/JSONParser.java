package com.example.jan.butzradar.location;

import com.example.jan.butzradar.location.LocationEntry;
import com.google.gson.Gson;

public class JSONParser {

    private Gson gson;
    private String jsonToParse;

    public LocationEntry[] entries;

    public JSONParser(String jsonToParse) {
        this.jsonToParse = jsonToParse;

        gson = new Gson();
        buildJSON();
    }

    private void buildJSON() {
        entries = gson.fromJson(jsonToParse, LocationEntry[].class);
    }

}
