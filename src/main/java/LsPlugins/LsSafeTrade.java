package LsPlugins;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class LsSafeTrade extends JavaPlugin {

    private static LsSafeTrade instance;

    @Override
    public void onEnable() {
        instance = this;

        if (!VaultEconomy.setupEconomy()) {
            getLogger().severe("Vault nao encontrado! Desativando...");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        saveDefaultConfig();
        DataManager.init();
        TradeLogger.init();

        getCommand("safetrade").setExecutor(new TradeCommand());

        Bukkit.getPluginManager().registerEvents(new TradeListener(), this);
        Bukkit.getPluginManager().registerEvents(new ConnectionListener(), this);
        Bukkit.getPluginManager().registerEvents(new ChatListener(), this);

        getLogger().info("LsSafeTrade ativado! Estilo: LsPlugins.");
    }

    @Override
    public void onDisable() {
        for (Trade trade : Tracker.getActiveTrades()) {
            trade.forceEnd();
        }
    }

    public static LsSafeTrade getInstance() {
        return instance;
    }
}