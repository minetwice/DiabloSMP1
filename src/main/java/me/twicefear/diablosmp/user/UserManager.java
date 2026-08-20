package me.twicefear.diablosmp.user;

import me.twicefear.diablosmp.DiabloSMP;
import me.twicefear.diablosmp.stone.StoneType;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.Collection;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class UserManager {

    private final DiabloSMP plugin;
    private final Map<UUID, UserData> userMap = new ConcurrentHashMap<>();
    private final NamespacedKey absorbedStoneKey;
    private final NamespacedKey receivedFirstStoneKey;

    public UserManager(DiabloSMP plugin) {
        this.plugin = plugin;
        this.absorbedStoneKey = new NamespacedKey(plugin, "absorbed_stone");
        this.receivedFirstStoneKey = new NamespacedKey(plugin, "received_first_stone");
    }

    public UserData getUserData(UUID uuid) {
        return userMap.computeIfAbsent(uuid, UserData::new);
    }

    public void loadPlayerData(Player player) {
        UserData data = getUserData(player.getUniqueId());
        PersistentDataContainer pdc = player.getPersistentDataContainer();
        if (pdc.has(absorbedStoneKey, PersistentDataType.STRING)) {
            String stoneId = pdc.get(absorbedStoneKey, PersistentDataType.STRING);
            if (stoneId != null) {
                data.setAbsorbedStone(StoneType.fromId(stoneId));
            }
        }
        if (pdc.has(receivedFirstStoneKey, PersistentDataType.BYTE)) {
            Byte val = pdc.get(receivedFirstStoneKey, PersistentDataType.BYTE);
            if (val != null && val == (byte) 1) {
                data.setHasReceivedFirstJoinStone(true);
            }
        }
    }

    public void savePlayerData(Player player) {
        UserData data = getUserData(player.getUniqueId());
        PersistentDataContainer pdc = player.getPersistentDataContainer();
        if (data.hasAbsorbedStone()) {
            pdc.set(absorbedStoneKey, PersistentDataType.STRING, data.getAbsorbedStone().getId());
        } else {
            pdc.remove(absorbedStoneKey);
        }
        if (data.hasReceivedFirstJoinStone()) {
            pdc.set(receivedFirstStoneKey, PersistentDataType.BYTE, (byte) 1);
        }
    }

    public void removeUserData(UUID uuid) {
        userMap.remove(uuid);
    }

    public Collection<UserData> getAllUsers() {
        return userMap.values();
    }
}
