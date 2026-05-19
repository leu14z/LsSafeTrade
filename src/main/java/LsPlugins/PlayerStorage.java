package LsPlugins;

import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import org.bukkit.inventory.ItemStack;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PlayerStorage {
    private final UUID ownerId;
    private final List<ItemStack> items = new ArrayList<>();
    private final List<Pokemon> pokemons = new ArrayList<>();
    private double money = 0.0;

    public PlayerStorage(UUID ownerId) {
        this.ownerId = ownerId;
    }

    public UUID getOwnerId() {
        return ownerId;
    }

    public void addItem(ItemStack item) {
        items.add(item);
    }

    public void addPokemon(Pokemon pokemon) {
        pokemons.add(pokemon);
    }

    public void addMoney(double amount) {
        this.money += amount;
    }

    public List<ItemStack> getItems() {
        return items;
    }

    public List<Pokemon> getPokemons() {
        return pokemons;
    }

    public double getMoney() {
        return money;
    }

    public void clearAll() {
        items.clear();
        pokemons.clear();
        money = 0;
    }
}