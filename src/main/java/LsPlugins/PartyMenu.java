package LsPlugins;

import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import java.util.ArrayList;
import java.util.List;

public class PartyMenu {
    public static void openParty(Player player, Trade trade) {
        Inventory inv = Bukkit.createInventory(null, 9, "§8Sua Equipe - Selecione");
        Object party = PixelmonUtils.getParty(player.getUniqueId());

        Material pokeMat = PixelmonUtils.getPokeBallMaterial();

        for (int i = 0; i < 6; i++) {
            Pokemon p = PixelmonUtils.getPokemon(party, i);
            if (p != null) {
                ItemStack icon = new ItemStack(pokeMat);
                ItemMeta meta = icon.getItemMeta();
                meta.setDisplayName("§b" + p.getLocalizedName() + (p.isShiny() ? " §e★" : ""));
                List<String> lore = new ArrayList<>();
                lore.add("§7Nível: §f" + p.getPokemonLevel());
                lore.add("§aClique para oferecer.");
                lore.add("§0SLOT:" + i);
                meta.setLore(lore);
                icon.setItemMeta(meta);
                inv.setItem(i + 1, icon);
            }
        }
        player.openInventory(inv);
    }
}