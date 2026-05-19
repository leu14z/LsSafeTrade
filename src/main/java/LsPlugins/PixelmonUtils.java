package LsPlugins;

import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import com.pixelmonmod.pixelmon.api.pokemon.stats.IVStore;
import org.bukkit.Material;

import java.util.UUID;
import java.util.List;

public class PixelmonUtils {

    public static Object getParty(UUID uuid) {
        try {
            Class<?> proxyClass = Class.forName("com.pixelmonmod.pixelmon.api.storage.StorageProxy");
            return proxyClass.getMethod("getParty", UUID.class).invoke(null, uuid);
        } catch (Exception e) { return null; }
    }

    public static Object getPC(UUID uuid) {
        try {
            Class<?> proxyClass = Class.forName("com.pixelmonmod.pixelmon.api.storage.StorageProxy");
            return proxyClass.getMethod("getPCForPlayer", UUID.class).invoke(null, uuid);
        } catch (Exception e) { return null; }
    }

    public static Pokemon getPokemon(Object party, int slot) {
        try {
            return (Pokemon) party.getClass().getMethod("get", int.class).invoke(party, slot);
        } catch (Exception e) { return null; }
    }

    public static void setPokemon(Object party, int slot, Pokemon pokemon) {
        try {
            party.getClass().getMethod("set", int.class, Pokemon.class).invoke(party, slot, pokemon);
        } catch (Exception e) { e.printStackTrace(); }
    }

    public static boolean addPokemon(Object storage, Pokemon pokemon) {
        try {
            storage.getClass().getMethod("add", Pokemon.class).invoke(storage, pokemon);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean hasSpace(Object party) {
        try {
            return (boolean) party.getClass().getMethod("hasSpace").invoke(party);
        } catch (Exception e) { return false; }
    }

    public static String getNatureName(Pokemon p) {
        try {
            Object nature = p.getClass().getMethod("getNature").invoke(p);
            return (String) nature.getClass().getMethod("getLocalizedName").invoke(nature);
        } catch (Exception e) { return "Desconhecida"; }
    }

    public static String getAbilityName(Pokemon p) {
        try {
            Object ability = p.getClass().getMethod("getAbility").invoke(p);
            return (String) ability.getClass().getMethod("getLocalizedName").invoke(ability);
        } catch (Exception e) { return "Desconhecida"; }
    }

    public static void addIVsToLore(Pokemon p, List<String> lore) {
        IVStore ivs = p.getIVs();
        int total = 0;
        int[] array = ivs.getArray();
        for (int i : array) total += i;
        double percentage = (total / 186.0) * 100.0;

        lore.add(" §bEstatísticas:");
        lore.add("  §fIVs Totais: §a" + total + "§7/§f186 §e(" + String.format("%.0f", percentage) + "%)");
    }

    public static Material getPokeBallMaterial() {
        try {
            Material m = Material.matchMaterial("pixelmon:poke_ball");
            if (m != null) return m;
            m = Material.matchMaterial("PIXELMON_POKE_BALL");
            if (m != null) return m;
        } catch (Exception ignored) {}
        return Material.SNOWBALL;
    }
}