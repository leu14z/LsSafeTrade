package LsPlugins;

import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import org.bukkit.inventory.ItemStack;

public class MenuAction {

    public static void executeTrade(Trade trade) {
        trade.setState(TradeState.WAITING_FOR_CONFIRMATION);

        Side s1 = trade.getSide1();
        Side s2 = trade.getSide2();

        VaultEconomy.deposit(s1.getPlayer(), s2.getOfferedMoney());
        VaultEconomy.deposit(s2.getPlayer(), s1.getOfferedMoney());

        transfer(s1, s2);
        transfer(s2, s1);

        TradeLogger.log(trade);
        trade.forceEnd();
        Tracker.removeActiveTrade(trade);

        s1.sendMessage("§aTroca finalizada com sucesso!");
        s2.sendMessage("§aTroca finalizada com sucesso!");
    }

    private static void transfer(Side from, Side to) {
        for (ItemStack item : from.getOfferedItems()) {
            if (to.getPlayer() != null && to.getPlayer().getInventory().firstEmpty() != -1) {
                to.getPlayer().getInventory().addItem(item);
            } else {
                Tracker.getOrCreateStorage(to.getOwnerId()).addItem(item);
            }
        }

        for (Pokemon p : from.getOfferedPokemons()) {
            Object party = PixelmonUtils.getParty(to.getOwnerId());
            Object pc = PixelmonUtils.getPC(to.getOwnerId());

            if (party != null && PixelmonUtils.hasSpace(party)) {
                PixelmonUtils.addPokemon(party, p);
            } else if (pc != null && PixelmonUtils.addPokemon(pc, p)) {
                if (to.getPlayer() != null) {
                    to.getPlayer().sendMessage("§aPokémon enviado para o seu PC.");
                }
            } else {
                Tracker.getOrCreateStorage(to.getOwnerId()).addPokemon(p);
            }
        }
    }
}