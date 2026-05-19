package LsPlugins;

import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.List;

public class Config {

    public static boolean showEggStats = false;
    public static boolean showEggName = true;

    public static boolean logsEnabled = true;
    public static int logExpiryDays = 31;

    public static boolean asyncSaving = true;

    public static List<String> blacklistedItems = new ArrayList<>();
    public static List<String> blacklistedPokemons = new ArrayList<>();

    public static void load() {
        LsSafeTrade plugin = LsSafeTrade.getInstance();
        plugin.reloadConfig();
        FileConfiguration config = plugin.getConfig();

        showEggStats = config.getBoolean("Settings.ShowEggStats", false);
        showEggName = config.getBoolean("Settings.ShowEggName", true);

        logsEnabled = config.getBoolean("Settings.Logs.Enabled", true);
        logExpiryDays = config.getInt("Settings.Logs.ExpiryDays", 31);

        asyncSaving = config.getBoolean("Settings.Storage.AsyncSaving", true);

        blacklistedItems = config.getStringList("Blacklist.Items");
        blacklistedPokemons = config.getStringList("Blacklist.Pokemons");
    }

    public static boolean isItemBlacklisted(String materialName) {
        for (String item : blacklistedItems) {
            if (item.equalsIgnoreCase(materialName)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isPokemonBlacklisted(String speciesName) {
        for (String pokemon : blacklistedPokemons) {
            if (pokemon.equalsIgnoreCase(speciesName)) {
                return true;
            }
        }
        return false;
    }
}