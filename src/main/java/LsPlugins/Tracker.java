package LsPlugins;

import org.bukkit.entity.Player;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Tracker {
    private static final Map<UUID, Trade> activeTrades = new HashMap<>();
    private static final Map<UUID, PlayerStorage> storages = new HashMap<>();

    public static void addActiveTrade(Trade trade) {
        activeTrades.put(trade.getSide1().getOwnerId(), trade);
        activeTrades.put(trade.getSide2().getOwnerId(), trade);
    }

    public static void removeActiveTrade(Trade trade) {
        if (trade == null) return;
        activeTrades.remove(trade.getSide1().getOwnerId());
        activeTrades.remove(trade.getSide2().getOwnerId());
    }

    public static void forceClear(UUID uuid) {
        Trade trade = activeTrades.get(uuid);
        if (trade != null) {
            trade.forceEnd();
            removeActiveTrade(trade);
        }
        activeTrades.remove(uuid);
    }

    public static Trade getActiveTrade(Player player) {
        return activeTrades.get(player.getUniqueId());
    }

    public static Collection<Trade> getActiveTrades() {
        return activeTrades.values();
    }

    public static PlayerStorage getOrCreateStorage(UUID uuid) {
        return storages.computeIfAbsent(uuid, PlayerStorage::new);
    }

    public static void unloadStorage(UUID uuid) {
        storages.remove(uuid);
    }
}