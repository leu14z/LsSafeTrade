package LsPlugins;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

public class VaultEconomy {
    private static Economy econ = null;

    public static boolean setupEconomy() {
        if (Bukkit.getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = Bukkit.getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        econ = rsp.getProvider();
        return econ != null;
    }

    public static boolean has(Player player, double amount) {
        if (econ == null) return false;
        return econ.has(player, amount);
    }

    public static void withdraw(Player player, double amount) {
        if (econ != null) {
            econ.withdrawPlayer(player, amount);
        }
    }

    public static void deposit(Player player, double amount) {
        if (econ != null) {
            econ.depositPlayer(player, amount);
        }
    }

    public static double getBalance(Player player) {
        if (econ == null) return 0.0;
        return econ.getBalance(player);
    }
}
