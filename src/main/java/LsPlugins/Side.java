package LsPlugins;

import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Side {
    private final UUID ownerId;
    private final Trade parentTrade;

    private boolean ready = false;
    private boolean confirmed = false;
    private boolean paused = false;

    private final List<ItemStack> offeredItems = new ArrayList<>();
    private final List<Pokemon> offeredPokemons = new ArrayList<>();
    private double offeredMoney = 0.0;

    public Side(Trade parentTrade, UUID ownerId) {
        this.parentTrade = parentTrade;
        this.ownerId = ownerId;
    }

    public Player getPlayer() {
        return Bukkit.getPlayer(ownerId);
    }

    public UUID getOwnerId() {
        return ownerId;
    }

    public Trade getParentTrade() {
        return parentTrade;
    }

    public boolean isReady() {
        return ready;
    }

    public void setReady(boolean ready) {
        this.ready = ready;
    }

    public boolean isConfirmed() {
        return confirmed;
    }

    public void setConfirmed(boolean confirmed) {
        this.confirmed = confirmed;
    }

    public boolean isPaused() {
        return paused;
    }

    public void setPaused(boolean paused) {
        this.paused = paused;
    }

    public List<ItemStack> getOfferedItems() {
        return offeredItems;
    }

    public List<Pokemon> getOfferedPokemons() {
        return offeredPokemons;
    }

    public double getOfferedMoney() {
        return offeredMoney;
    }

    public void setOfferedMoney(double money) {
        this.offeredMoney = money;
    }

    public void sendMessage(String message) {
        Player p = getPlayer();
        if (p != null) {
            p.sendMessage("§b[SafeTrade] §r" + message);
        }
    }
}