package LsPlugins;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

import java.io.*;
import java.util.UUID;

public class DataManager {
    private static File dataDir;
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public static void init() {
        dataDir = new File(LsSafeTrade.getInstance().getDataFolder(), "data");
        if (!dataDir.exists()) {
            dataDir.mkdirs();
        }
    }

    public static void saveStorage(PlayerStorage storage) {
        if (storage.getItems().isEmpty() && storage.getPokemons().isEmpty() && storage.getMoney() <= 0) {
            return;
        }

        File file = new File(dataDir, storage.getOwnerId().toString() + ".json");
        JsonObject json = new JsonObject();

        json.addProperty("money", storage.getMoney());

        JsonArray itemsArray = new JsonArray();
        for (ItemStack item : storage.getItems()) {
            String encoded = itemToBase64(item);
            if (!encoded.isEmpty()) {
                itemsArray.add(encoded);
            }
        }
        json.add("items", itemsArray);

        JsonArray pokesArray = new JsonArray();
        for (Pokemon pokemon : storage.getPokemons()) {
            try {
                Class<?> nbtClass = Class.forName("net.minecraft.nbt.CompoundNBT");
                Object nbt = nbtClass.getDeclaredConstructor().newInstance();
                Pokemon.class.getMethod("writeToNBT", nbtClass).invoke(pokemon, nbt);
                pokesArray.add(nbt.toString());
            } catch (Exception ignored) {}
        }
        json.add("pokemons", pokesArray);

        try (FileWriter writer = new FileWriter(file)) {
            gson.toJson(json, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static PlayerStorage loadStorage(UUID uuid) {
        File file = new File(dataDir, uuid.toString() + ".json");
        PlayerStorage storage = new PlayerStorage(uuid);

        if (!file.exists()) return storage;

        try (FileReader reader = new FileReader(file)) {
            JsonObject json = gson.fromJson(reader, JsonObject.class);

            if (json.has("money")) {
                storage.addMoney(json.get("money").getAsDouble());
            }

            if (json.has("items")) {
                JsonArray itemsArray = json.getAsJsonArray("items");
                for (int i = 0; i < itemsArray.size(); i++) {
                    ItemStack item = itemFromBase64(itemsArray.get(i).getAsString());
                    if (item != null) storage.addItem(item);
                }
            }

            if (json.has("pokemons")) {
                JsonArray pokesArray = json.getAsJsonArray("pokemons");
                for (int i = 0; i < pokesArray.size(); i++) {
                    try {
                        Class<?> jsonToNBTClass = Class.forName("net.minecraft.nbt.JsonToNBT");
                        Object nbt = jsonToNBTClass.getMethod("getTagFromJson", String.class).invoke(null, pokesArray.get(i).getAsString());

                        Class<?> factoryClass = Class.forName("com.pixelmonmod.pixelmon.api.pokemon.factory.PokemonFactory");
                        Class<?> nbtCompoundClass = Class.forName("net.minecraft.nbt.CompoundNBT");

                        Pokemon pokemon = (Pokemon) factoryClass.getMethod("create", nbtCompoundClass).invoke(null, nbt);
                        if (pokemon != null) storage.addPokemon(pokemon);
                    } catch (Exception ignored) {}
                }
            }

            file.delete();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return storage;
    }

    private static String itemToBase64(ItemStack item) {
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream);
            dataOutput.writeObject(item);
            dataOutput.close();
            return Base64Coder.encodeLines(outputStream.toByteArray());
        } catch (Exception e) {
            return "";
        }
    }

    private static ItemStack itemFromBase64(String data) {
        try {
            ByteArrayInputStream inputStream = new ByteArrayInputStream(Base64Coder.decodeLines(data));
            BukkitObjectInputStream dataInput = new BukkitObjectInputStream(inputStream);
            ItemStack item = (ItemStack) dataInput.readObject();
            dataInput.close();
            return item;
        } catch (Exception e) {
            return null;
        }
    }
}