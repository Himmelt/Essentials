package com.earth2me.essentials.settings;

import com.earth2me.essentials.storage.MapValueType;
import com.earth2me.essentials.storage.StorageObject;
import org.bukkit.Location;

import java.util.HashMap;
import java.util.Map;

public class Jails implements StorageObject {

    @MapValueType(Location.class)
    private Map<String, Location> jails = new HashMap<>();

    public Map<String, Location> getJails() {
        return jails;
    }

    public void setJails(Map<String, Location> jails) {
        this.jails = jails;
    }

}
