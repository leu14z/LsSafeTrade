package LsPlugins;

import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import java.util.ArrayList;
import java.util.List;

public class Trade {
    private final Side side1;
    private final Side side2;
    private TradeState state;
    private final Inventory inventory;

    public Trade(Player p1, Player p2) {
        this.side1 = new Side(this, p1.getUniqueId());
        this.side2 = new Side(this, p2.getUniqueId());
        this.state = TradeState.TRADING;
        this.inventory = Bukkit.createInventory(null, 54, "§8Troca: " + p1.getName() + " & " + p2.getName());
        setupBaseLayout();
        updateVisuals();
    }

    private void setupBaseLayout() {
        ItemStack bg = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
        ItemMeta m = bg.getItemMeta(); m.setDisplayName("§r"); bg.setItemMeta(m);
        for (int i = 0; i < 54; i++) {
            if (i % 9 == 4 || i >= 45 || i < 9 || i % 9 == 0 || i % 9 == 8) inventory.setItem(i, bg);
        }
    }

    public void open(Player p) {
        if (p != null) p.openInventory(this.inventory);
    }

    public void updateVisuals() {
        renderHead(side1, 0);
        renderHead(side2, 8);
        renderSide(side1, new int[]{10,11,12, 19,20,21, 28,29,30, 37,38,39}, 45, 46, 18, 9);
        renderSide(side2, new int[]{14,15,16, 23,24,25, 32,33,34, 41,42,43}, 53, 52, 26, 17);
        if (side1.isReady() && side2.isReady()) MenuAction.executeTrade(this);
    }

    private void renderHead(Side side, int slot) {
        ItemStack head = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) head.getItemMeta();
        meta.setOwningPlayer(Bukkit.getOfflinePlayer(side.getOwnerId()));
        meta.setDisplayName("§bTreinador: §f" + Bukkit.getOfflinePlayer(side.getOwnerId()).getName());
        head.setItemMeta(meta);
        inventory.setItem(slot, head);
    }

    private void renderSide(Side side, int[] slots, int conf, int dec, int mon, int pok) {
        inventory.setItem(conf, createItem(side.isReady() ? Material.LIME_STAINED_GLASS_PANE : Material.LIME_DYE, side.isReady() ? "§a§lPRONTO" : "§7Clique para Confirmar"));
        inventory.setItem(dec, createItem(Material.RED_STAINED_GLASS_PANE, "§c§lCANCELAR"));
        inventory.setItem(mon, createItem(Material.GOLD_INGOT, "§eDinheiro:", " §f$" + side.getOfferedMoney(), "", "§7Clique para definir no chat."));

        Material pokeMat = PixelmonUtils.getPokeBallMaterial();
        inventory.setItem(pok, createItem(pokeMat, "§6Selecionar Pokémons", "§7Clique para ver sua equipe."));

        for (int s : slots) inventory.setItem(s, null);
        int idx = 0;
        for (Pokemon p : side.getOfferedPokemons()) {
            if (idx < slots.length) inventory.setItem(slots[idx++], createPokeItem(p));
        }
        for (ItemStack it : side.getOfferedItems()) {
            if (idx < slots.length) inventory.setItem(slots[idx++], it);
        }
    }

    private ItemStack createPokeItem(Pokemon p) {
        ItemStack it = new ItemStack(PixelmonUtils.getPokeBallMaterial());
        ItemMeta m = it.getItemMeta();
        m.setDisplayName("§e" + p.getLocalizedName() + (p.isShiny() ? " §6★" : ""));
        List<String> lore = new ArrayList<>();
        lore.add("§8§m-----------------------");
        lore.add(" §7Nível: §f" + p.getPokemonLevel());
        lore.add(" §7Natureza: §f" + PixelmonUtils.getNatureName(p));
        lore.add(" §7Habilidade: §f" + PixelmonUtils.getAbilityName(p));
        lore.add("");
        PixelmonUtils.addIVsToLore(p, lore);
        lore.add("§8§m-----------------------");
        lore.add("§aClique para remover da oferta.");
        m.setLore(lore);
        it.setItemMeta(m);
        return it;
    }

    private ItemStack createItem(Material mat, String name, String... lore) {
        ItemStack it = new ItemStack(mat);
        ItemMeta m = it.getItemMeta(); m.setDisplayName(name);
        if (lore.length > 0) {
            List<String> l = new ArrayList<>();
            for (String s : lore) l.add(s);
            m.setLore(l);
        }
        it.setItemMeta(m);
        return it;
    }

    public Inventory getInventory() { return inventory; }
    public Side getSide(Player p) { return side1.getOwnerId().equals(p.getUniqueId()) ? side1 : side2; }
    public Side getOther(Side s) { return s == side1 ? side2 : side1; }
    public Side getSide1() { return side1; }
    public Side getSide2() { return side2; }
    public TradeState getState() { return state; }
    public void setState(TradeState s) { this.state = s; }
    public void forceEnd() {
        this.state = TradeState.ENDED;
        if (side1.getPlayer() != null) side1.getPlayer().closeInventory();
        if (side2.getPlayer() != null) side2.getPlayer().closeInventory();
    }
}