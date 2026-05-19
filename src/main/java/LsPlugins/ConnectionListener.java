package LsPlugins;

import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;

public class ConnectionListener implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        PlayerStorage storage = DataManager.loadStorage(p.getUniqueId());

        boolean recovered = false;

        if (storage.getMoney() > 0) {
            VaultEconomy.deposit(p, storage.getMoney());
            recovered = true;
        }

        for (ItemStack item : storage.getItems()) {
            if (p.getInventory().firstEmpty() != -1) p.getInventory().addItem(item);
            else p.getWorld().dropItem(p.getLocation(), item);
            recovered = true;
        }

        if (!storage.getPokemons().isEmpty()) {
            Object party = PixelmonUtils.getParty(p.getUniqueId());
            if (party != null) {
                for (int i = storage.getPokemons().size() - 1; i >= 0; i--) {
                    if (PixelmonUtils.hasSpace(party)) {
                        PixelmonUtils.addPokemon(party, storage.getPokemons().get(i));
                        storage.getPokemons().remove(i);
                        recovered = true;
                    }
                }
            }
        }

        if (recovered) {
            p.sendMessage("§b[SafeTrade] §aSeus bens pendentes foram devolvidos!");
            DataManager.saveStorage(storage);
        }
    }
}