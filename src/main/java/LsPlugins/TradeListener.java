package LsPlugins;

import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

public class TradeListener implements Listener {

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        if (!(e.getWhoClicked() instanceof Player)) return;
        Player p = (Player) e.getWhoClicked();
        Trade trade = Tracker.getActiveTrade(p);
        if (trade == null) return;

        String title = e.getView().getTitle();

        if (title.startsWith("§8Troca:")) {
            e.setCancelled(true);
            Side side = trade.getSide(p);
            if (side == null) return;

            if (e.getClickedInventory() == e.getView().getBottomInventory()) {
                if (e.getCurrentItem() != null && e.getCurrentItem().getType() != Material.AIR) {
                    if (side.getOfferedItems().size() + side.getOfferedPokemons().size() < 12) {
                        side.getOfferedItems().add(e.getCurrentItem().clone());
                        e.getClickedInventory().setItem(e.getSlot(), null);
                        side.setReady(false);
                        trade.getOther(side).setReady(false);
                        trade.updateVisuals();
                    } else {
                        p.sendMessage("§b[SafeTrade] §cLimite de 12 slots atingido.");
                    }
                }
                return;
            }

            int slot = e.getRawSlot();

            if (e.getClickedInventory() == e.getView().getTopInventory()) {
                int[] p1Slots = {10,11,12, 19,20,21, 28,29,30, 37,38,39};
                int[] p2Slots = {14,15,16, 23,24,25, 32,33,34, 41,42,43};
                int[] mySlots = (side == trade.getSide1()) ? p1Slots : p2Slots;

                for (int i = 0; i < mySlots.length; i++) {
                    if (slot == mySlots[i]) {
                        int pokesSize = side.getOfferedPokemons().size();
                        int itemsSize = side.getOfferedItems().size();

                        if (i < pokesSize) {
                            Pokemon removed = side.getOfferedPokemons().remove(i);
                            Object party = PixelmonUtils.getParty(p.getUniqueId());
                            Object pc = PixelmonUtils.getPC(p.getUniqueId());

                            if (party != null && PixelmonUtils.hasSpace(party)) {
                                PixelmonUtils.addPokemon(party, removed);
                            } else if (pc != null && PixelmonUtils.addPokemon(pc, removed)) {
                                p.sendMessage("§b[SafeTrade] §aPokémon removido e enviado para o seu PC.");
                            } else {
                                Tracker.getOrCreateStorage(p.getUniqueId()).addPokemon(removed);
                            }
                        }
                        else if (i < pokesSize + itemsSize) {
                            ItemStack removed = side.getOfferedItems().remove(i - pokesSize);
                            if (p.getInventory().firstEmpty() != -1) p.getInventory().addItem(removed);
                            else p.getWorld().dropItem(p.getLocation(), removed);
                        }

                        side.setReady(false);
                        trade.getOther(side).setReady(false);
                        trade.updateVisuals();
                        return;
                    }
                }
            }

            if (slot == 45 || slot == 53) {
                if ((slot == 45 && side == trade.getSide1()) || (slot == 53 && side == trade.getSide2())) {
                    side.setReady(!side.isReady());
                    trade.updateVisuals();
                }
            } else if (slot == 46 || slot == 52) {
                if ((slot == 46 && side == trade.getSide1()) || (slot == 52 && side == trade.getSide2())) {
                    cancelTrade(trade, "§b[SafeTrade] §cTroca cancelada pelo jogador.");
                }
            } else if (slot == 18 || slot == 26) {
                if ((slot == 18 && side == trade.getSide1()) || (slot == 26 && side == trade.getSide2())) {
                    ChatListener.waitingMoney.put(p.getUniqueId(), trade);
                    p.closeInventory();
                    p.sendMessage("");
                    p.sendMessage("§b[SafeTrade] §eDigite no chat a quantia de dinheiro para oferecer:");
                    p.sendMessage("");
                }
            } else if (slot == 9 || slot == 17) {
                if ((slot == 9 && side == trade.getSide1()) || (slot == 17 && side == trade.getSide2())) {
                    PartyMenu.openParty(p, trade);
                }
            }
        }
        else if (title.equals("§8Sua Equipe - Selecione")) {
            e.setCancelled(true);
            Side side = trade.getSide(p);

            Material pokeBallMat = PixelmonUtils.getPokeBallMaterial();
            if (e.getCurrentItem() != null && e.getCurrentItem().getType() == pokeBallMat) {
                if (e.getCurrentItem().getItemMeta().getLore() != null) {
                    for (String line : e.getCurrentItem().getItemMeta().getLore()) {
                        if (line.startsWith("§0SLOT:")) {
                            int slotIndex = Integer.parseInt(line.replace("§0SLOT:", ""));
                            Object party = PixelmonUtils.getParty(p.getUniqueId());
                            Pokemon selected = PixelmonUtils.getPokemon(party, slotIndex);

                            if (selected != null) {
                                if (side.getOfferedItems().size() + side.getOfferedPokemons().size() < 12) {
                                    PixelmonUtils.setPokemon(party, slotIndex, null);
                                    side.getOfferedPokemons().add(selected);
                                    side.setReady(false);
                                    trade.getOther(side).setReady(false);

                                    Bukkit.getScheduler().runTask(LsSafeTrade.getInstance(), () -> {
                                        trade.open(p);
                                        trade.updateVisuals();
                                    });
                                } else {
                                    p.sendMessage("§b[SafeTrade] §cLimite de 12 slots atingido.");
                                }
                            }
                            break;
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void onClose(InventoryCloseEvent e) {
        if (!(e.getPlayer() instanceof Player)) return;
        Player p = (Player) e.getPlayer();
        Trade trade = Tracker.getActiveTrade(p);

        if (trade != null && trade.getState() != TradeState.ENDED) {
            String title = e.getView().getTitle();
            if (title.startsWith("§8Troca:")) {
                Bukkit.getScheduler().runTaskLater(LsSafeTrade.getInstance(), () -> {
                    if (Tracker.getActiveTrade(p) != null && trade.getState() != TradeState.ENDED) {
                        if (!ChatListener.waitingMoney.containsKey(p.getUniqueId()) &&
                                !p.getOpenInventory().getTitle().equals("§8Sua Equipe - Selecione")) {

                            cancelTrade(trade, "§b[SafeTrade] §cTroca encerrada: um jogador fechou o menu.");
                        }
                    }
                }, 3L);
            }
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        Player p = e.getPlayer();

        if (ChatListener.waitingMoney.containsKey(p.getUniqueId())) {
            Trade t = ChatListener.waitingMoney.remove(p.getUniqueId());
            if (t != null) cancelTrade(t, "§b[SafeTrade] §cTroca cancelada: um jogador desconectou.");
        }

        Trade trade = Tracker.getActiveTrade(p);
        if (trade != null && trade.getState() != TradeState.ENDED) {
            cancelTrade(trade, "§b[SafeTrade] §cTroca cancelada: um jogador desconectou.");
        }
    }

    private void cancelTrade(Trade trade, String reason) {
        if (trade.getState() == TradeState.ENDED) return;

        trade.setState(TradeState.ENDED);
        Tracker.removeActiveTrade(trade);

        ChatListener.waitingMoney.remove(trade.getSide1().getOwnerId());
        ChatListener.waitingMoney.remove(trade.getSide2().getOwnerId());

        trade.getSide1().sendMessage(reason);
        trade.getSide2().sendMessage(reason);

        devolverItens(trade.getSide1());
        devolverItens(trade.getSide2());

        trade.forceEnd();
    }

    private void devolverItens(Side side) {
        Player p = side.getPlayer();
        PlayerStorage storage = Tracker.getOrCreateStorage(side.getOwnerId());

        if (side.getOfferedMoney() > 0 && p != null) {
            VaultEconomy.deposit(p, side.getOfferedMoney());
        }

        for (ItemStack item : side.getOfferedItems()) {
            if (p != null && p.getInventory().firstEmpty() != -1) {
                p.getInventory().addItem(item);
            } else {
                storage.addItem(item);
            }
        }

        for (Pokemon pokemon : side.getOfferedPokemons()) {
            Object party = PixelmonUtils.getParty(side.getOwnerId());
            Object pc = PixelmonUtils.getPC(side.getOwnerId());

            if (party != null && PixelmonUtils.hasSpace(party)) {
                PixelmonUtils.addPokemon(party, pokemon);
            } else if (pc != null && PixelmonUtils.addPokemon(pc, pokemon)) {
            } else {
                storage.addPokemon(pokemon);
            }
        }
        DataManager.saveStorage(storage);
    }
}