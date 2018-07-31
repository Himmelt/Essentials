package com.earth2me.essentials;

import com.earth2me.essentials.utils.StringUtil;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.ConcurrentSkipListSet;

public class UserMap implements IConf {
    private final transient com.earth2me.essentials.IEssentials ess;
    private final transient ConcurrentHashMap<String, User> users = new ConcurrentHashMap<>();
    private final transient ConcurrentSkipListSet<UUID> keys = new ConcurrentSkipListSet<UUID>();
    private final transient ConcurrentSkipListMap<String, UUID> names = new ConcurrentSkipListMap<String, UUID>();
    private final transient ConcurrentSkipListMap<UUID, ArrayList<String>> history = new ConcurrentSkipListMap<UUID, ArrayList<String>>();
    private UUIDMap uuidMap;

    public UserMap(final com.earth2me.essentials.IEssentials ess) {
        super();
        this.ess = ess;
        uuidMap = new UUIDMap(ess);
        //RemovalListener<UUID, User> remListener = new UserMapRemovalListener();
        //users = CacheBuilder.newBuilder().maximumSize(ess.getSettings().getMaxUserCacheCount()).softValues().removalListener(remListener).build(this);
        //users = CacheBuilder.newBuilder().maximumSize(ess.getSettings().getMaxUserCacheCount()).softValues().build(this);
    }

    private void loadAllUsersAsync(final IEssentials ess) {
        ess.runTaskAsynchronously(() -> {
            synchronized (users) {
                final File userDir = new File(ess.getDataFolder(), "userdata");
                if (!userDir.exists()) {
                    return;
                }
                keys.clear();
                //users.invalidateAll();
                users.clear();
                String[] list = userDir.list();
                if (list != null && list.length >= 1) {
                    for (String string : list) {
                        if (!string.endsWith(".yml")) {
                            continue;
                        }
                        final String name = string.substring(0, string.length() - 4);
                        try {
                            keys.add(UUID.fromString(name));
                        } catch (IllegalArgumentException ex) {
                            //Ignore these users till they rejoin.
                        }
                    }
                }
                uuidMap.loadAllUsers(names, history);
            }
        });
    }

    public boolean userExists(final UUID uuid) {
        return keys.contains(uuid);
    }

    public User getUser(final String name) {
        final String sanitizedName = StringUtil.safeString(name);
        if (names.containsKey(sanitizedName)) {
            final UUID uuid = names.get(sanitizedName);
            return getUser(uuid);
        }

        final File userFile = getUserFileFromString(sanitizedName);
        if (userFile.exists()) {
            ess.getLogger().info("Importing user " + name + " to usermap.");
            User user = new User(new OfflinePlayer(sanitizedName, ess.getServer()), ess);
            trackUUID(user.getBase().getUniqueId(), user.getName(), true);
            return user;
        }
        return null;
    }

    public User getUser(final UUID uuid) {
        return users.get(uuid.toString());
    }

    public void trackUUID(final UUID uuid, final String name, boolean replace) {
        if (uuid != null) {
            keys.add(uuid);
            if (name != null && name.length() > 0) {
                final String keyName = StringUtil.safeString(name);
                if (!names.containsKey(keyName)) {
                    names.put(keyName, uuid);
                    uuidMap.writeUUIDMap();
                } else if (!names.get(keyName).equals(uuid)) {
                    if (replace) {
                        ess.getLogger().info("Found new UUID for " + name + ". Replacing " + names.get(keyName).toString() + " with " + uuid.toString());
                        names.put(keyName, uuid);
                        uuidMap.writeUUIDMap();
                    } else {
                        if (ess.getSettings().isDebug()) {
                            ess.getLogger().info("Found old UUID for " + name + " (" + uuid.toString() + "). Not adding to usermap.");
                        }
                    }
                }
            }
        }
    }

    public User load(final String stringUUID) throws Exception {
        UUID uuid = UUID.fromString(stringUUID);
        Player player = ess.getServer().getPlayer(uuid);
        if (player != null) {
            final User user = new User(player, ess);
            trackUUID(uuid, user.getName(), true);
            return user;
        }

        final File userFile = getUserFileFromID(uuid);

        if (userFile.exists()) {
            player = new OfflinePlayer(uuid, ess.getServer());
            final User user = new User(player, ess);
            ((OfflinePlayer) player).setName(user.getLastAccountName());
            trackUUID(uuid, user.getName(), false);
            return user;
        }

        throw new Exception("User not found!");
    }

    @Override
    public void reloadConfig() {
        getUUIDMap().forceWriteUUIDMap();
        loadAllUsersAsync(ess);
    }

    public void invalidateAll() {
        users.clear();
        //users.invalidateAll();
    }

    public void removeUser(final String name) {
        if (names == null) {
            ess.getLogger().warning("Name collection is null, cannot remove user.");
            return;
        }
        UUID uuid = names.get(name);
        if (uuid != null) {
            keys.remove(uuid);
            users.remove(uuid.toString());
            //users.invalidate(uuid);
        }
        names.remove(name);
        names.remove(StringUtil.safeString(name));
    }

    public Set<UUID> getAllUniqueUsers() {
        return Collections.unmodifiableSet(keys.clone());
    }

    public int getUniqueUsers() {
        return keys.size();
    }

    protected ConcurrentSkipListMap<String, UUID> getNames() {
        return names;
    }

    protected ConcurrentSkipListMap<UUID, ArrayList<String>> getHistory() {
        return history;
    }

    public List<String> getUserHistory(final UUID uuid) {
        return history.get(uuid);
    }

    public UUIDMap getUUIDMap() {
        return uuidMap;
    }

    private File getUserFileFromID(final UUID uuid) {
        final File userFolder = new File(ess.getDataFolder(), "userdata");
        return new File(userFolder, uuid.toString() + ".yml");
    }

    public File getUserFileFromString(final String name) {
        final File userFolder = new File(ess.getDataFolder(), "userdata");
        return new File(userFolder, StringUtil.sanitizeFileName(name) + ".yml");
    }

}
