package com.earth2me.essentials.settings;

import com.earth2me.essentials.storage.MapValueType;
import com.earth2me.essentials.storage.StorageObject;
import org.bukkit.Location;

import java.util.HashMap;
import java.util.Map;

public class Spawns implements StorageObject {

    @MapValueType(Location.class)
    private Map<String, Location> spawns = new HashMap<>();

    public Map<String, Location> getSpawns() {
        return spawns;
    }

    public void setSpawns(HashMap<String, Location> spawns) {
        this.spawns = spawns;
    }

}
