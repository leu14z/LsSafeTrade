package LsPlugins;

import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;
import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class TradeLogger {
    private static File logFile;

    public static void init() {
        File dir = new File(LsSafeTrade.getInstance().getDataFolder(), "logs");
        if (!dir.exists()) {
            dir.mkdirs();
        }
        logFile = new File(dir, "trades_finalizadas.txt");
    }

    public static void log(Trade trade) {
        if (trade.getState() != TradeState.ENDED) return;

        try (FileWriter writer = new FileWriter(logFile, true)) {
            String time = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));
            Side s1 = trade.getSide1();
            Side s2 = trade.getSide2();

            String p1Name = Bukkit.getOfflinePlayer(s1.getOwnerId()).getName();
            String p2Name = Bukkit.getOfflinePlayer(s2.getOwnerId()).getName();

            writer.write("\n[" + time + "] TROCA ENTRE: " + p1Name + " e " + p2Name + "\n");

            writer.write("-- Oferecido por " + p1Name + " --\n");
            writer.write("Dinheiro: " + s1.getOfferedMoney() + "\n");
            for (ItemStack item : s1.getOfferedItems()) {
                writer.write("Item: " + item.getType().name() + " x" + item.getAmount() + "\n");
            }
            for (Pokemon poke : s1.getOfferedPokemons()) {
                writer.write("Pokemon: " + poke.getSpecies().getName() + " (Nvl " + poke.getPokemonLevel() + ")\n");
            }

            writer.write("-- Oferecido por " + p2Name + " --\n");
            writer.write("Dinheiro: " + s2.getOfferedMoney() + "\n");
            for (ItemStack item : s2.getOfferedItems()) {
                writer.write("Item: " + item.getType().name() + " x" + item.getAmount() + "\n");
            }
            for (Pokemon poke : s2.getOfferedPokemons()) {
                writer.write("Pokemon: " + poke.getSpecies().getName() + " (Nvl " + poke.getPokemonLevel() + ")\n");
            }
            writer.write("--------------------------------------------------\n");

        } catch (IOException e) {
            LsSafeTrade.getInstance().getLogger().warning("Falha ao salvar log da troca!");
        }
    }
}