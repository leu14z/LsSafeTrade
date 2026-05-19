package LsPlugins;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ChatListener implements Listener {

    public static final Map<UUID, Trade> waitingMoney = new HashMap<>();

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onChat(AsyncPlayerChatEvent e) {
        Player p = e.getPlayer();

        if (!waitingMoney.containsKey(p.getUniqueId())) return;

        e.setCancelled(true);
        Trade trade = waitingMoney.remove(p.getUniqueId());
        String message = e.getMessage();

        LsSafeTrade.getInstance().getServer().getScheduler().runTask(LsSafeTrade.getInstance(), () -> {
            if (trade.getState() == TradeState.ENDED) {
                p.sendMessage("§cEsta troca já foi encerrada.");
                return;
            }

            try {
                double amount = Double.parseDouble(message);

                if (amount < 0) {
                    p.sendMessage("§cO valor não pode ser negativo.");
                } else {
                    Side side = trade.getSide(p);

                    if (side.getOfferedMoney() > 0) {
                        VaultEconomy.deposit(p, side.getOfferedMoney());
                        side.setOfferedMoney(0);
                    }

                    if (VaultEconomy.has(p, amount)) {
                        VaultEconomy.withdraw(p, amount);
                        side.setOfferedMoney(amount);

                        side.setReady(false);
                        trade.getOther(side).setReady(false);

                        p.sendMessage("§aOferta alterada para: §f$" + amount);
                    } else {
                        p.sendMessage("§cVocê não tem saldo suficiente!");
                    }
                }
            } catch (NumberFormatException ex) {
                p.sendMessage("§cValor inválido. Digite apenas números.");
            }

            trade.open(p);
            trade.updateVisuals();
        });
    }
}