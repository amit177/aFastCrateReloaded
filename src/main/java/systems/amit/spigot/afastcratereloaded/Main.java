package systems.amit.spigot.afastcratereloaded;

import com.hazebyte.crate.api.CrateAPI;
import com.hazebyte.crate.api.CratePlugin;
import com.hazebyte.crate.api.crate.Crate;
import com.hazebyte.crate.api.crate.reward.Reward;
import javafx.util.Pair;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import systems.amit.spigot.afastcratereloaded.events.Event_CrateInteract;
import systems.amit.spigot.afastcratereloaded.events.Event_PlayerQuit;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Main extends JavaPlugin implements Listener {

    private static Main instance;
    public CratePlugin crateAPI;
    public PlayerData playerData;

    public String recursivePermission = "";
    public String keyNameFormat = "";
    public boolean showHelpCredits = true;
    public Material keyMaterial = null;
    public int minimumInventorySlots = 2;


    public static Main getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        instance = this;
        crateAPI = CrateAPI.getInstance();

        if (!loadConfig()) {
            getLogger().severe("Could not load config, disabling.");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        playerData = new PlayerData();
        if (!playerData.load()) {
            getLogger().severe("Could not load playerdata, disabling.");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        getServer().getPluginManager().registerEvents(new Event_PlayerQuit(), this);
        getServer().getPluginManager().registerEvents(new Event_CrateInteract(), this);
        getCommand("afastcratereloaded").setExecutor(new Cmd_afastcratereloaded());

        new Metrics(this, 8364);
        getLogger().info("The plugin has been enabled.");
    }

    @Override
    public void onDisable() {
        instance = null;
        crateAPI = null;
        playerData = null;
        getLogger().info("The plugin has been disabled.");
    }

    /**
     * The function loads the configuration file
     *
     * @return true if successful, false otherwise
     */
    private boolean loadConfig() {
        saveDefaultConfig();
        if (!(getConfig().contains("prefix")
                || getConfig().contains("recursive-permission")
                || getConfig().contains("key-material")
                || getConfig().contains("key-name-format")
                || getConfig().contains("show-help-credits")
                || getConfig().contains("required-inventory-slots")
        )) {
            getLogger().severe("The config is missing a required section, not proceeding");
            return false;
        }

        // build the lang section in the file
        if (getConfig().getConfigurationSection("lang") == null) {
            for (Lang entry : Lang.values()) {
                getConfig().set("lang." + entry.getPath(), entry.getDefaultValue());
            }
            saveConfig();
        }

        recursivePermission = getConfig().getString("recursive-permission");
        keyNameFormat = getConfig().getString("key-name-format");
        showHelpCredits = getConfig().getBoolean("show-help-credits");
        minimumInventorySlots = getConfig().getInt("minimum-inventory-slots");

        try {
            keyMaterial = Material.valueOf(getConfig().getString("key-material"));
        } catch (IllegalArgumentException e) {
            getLogger().severe("Invalid key material: '" + getConfig().getString("key-material") + "'");
            return false;
        }

        return true;
    }

    /**
     * The function handles opening crates.
     *
     * @param p         The requested player
     * @param recursive Whether to open keys that are received or not
     * @return A map with the amount of keys that were opened of each crate
     */
    public HashMap<String, Integer> processKeys(Player p, boolean recursive, String crateName, int keyAmount) {
        Pair<HashMap<String, Integer>, List<String>> result = openCrateKeys(p, crateName, keyAmount, recursive);

        for (String cmd : result.getValue()) {
            Bukkit.dispatchCommand(getServer().getConsoleSender(), cmd);
        }

        return result.getKey();
    }

    /**
     * The function recursively opens the keys that are received from opening a crate.
     *
     * @param p         the player that is opening the crate
     * @param crateName the name of the crate
     * @param amount    the amount of keys
     * @return Pair:
     * HashMap<String, Integer> - a map of crate names and the amount of them that was opened
     * List<String> - the list of commands that will grant the final rewards to the player
     */
    private Pair<HashMap<String, Integer>, List<String>> openCrateKeys(Player p, String crateName, int amount, boolean recursive) {
        crateName = crateName.toLowerCase();
        HashMap<String, Integer> crateCounter = new HashMap<>();
        List<String> finalRewards = new ArrayList<>();

        for (int i = 0; i < amount; i++) {
            Crate c = crateAPI.getCrateRegistrar().getCrate(crateName);
            List<Reward> generatedRewards = c.generatePrizes(p);

            for (Reward reward : generatedRewards) {
                for (String cmd : reward.getCommands()) {
                    // add the crate that was opened to the list
                    addValueToMap(crateCounter, crateName, 1);
                    // if a crate key was received as a reward
                    if (recursive && cmd.startsWith("crate give to")) {
                        // split the key name and the amount
                        String[] keyReward = cmd.toLowerCase().replace("crate give to {player} ", "").split(" ");
                        if (keyReward.length != 2) {
                            getLogger().severe("Error parsing crate command: '" + cmd + "'");
                            continue;
                        }
                        if (playerData.isCrateInSkipList(p.getUniqueId(), keyReward[0])) {
                            // add the command of the reward
                            finalRewards.add(cmd.replace("{player}", p.getName()));
                        } else {
                            Pair<HashMap<String, Integer>, List<String>> result = openCrateKeys(p, keyReward[0], Integer.parseInt(keyReward[1]), true);
                            // add the crates that were opened
                            for (Map.Entry<String, Integer> entry : result.getKey().entrySet()) {
                                addValueToMap(crateCounter, entry.getKey(), entry.getValue());
                            }
                            // add all of the commands
                            finalRewards.addAll(result.getValue());
                        }
                    } else {
                        // add the command of the reward
                        finalRewards.add(cmd.replace("{player}", p.getName()));
                    }
                }
            }
        }
        return new Pair<>(crateCounter, finalRewards);
    }

    /**
     * The function adds a key and value to a map:
     * If the key doesn't exist, it will be created and the value will be set.
     * If the key already exists, the new value will be combined with the old value.
     *
     * @param map   The requested map
     * @param key   The requested key
     * @param value The value to be added
     */
    private void addValueToMap(HashMap<String, Integer> map, String key, Integer value) {
        if (map.containsKey(key)) {
            map.put(key, map.get(key) + value);
        } else {
            map.put(key, value);
        }
    }

    /**
     * The function cleans the display name of the key
     *
     * @param name The display name of the key from the player's inventory
     * @return The name of the crate
     */
    public String sanitizeKeyName(String name) { return ChatColor.stripColor(name).replace(keyNameFormat, ""); }

    /**
     * The function generates the final crate open message
     *
     * @param crates The amount of crates opened of each type
     * @return The generated message
     */
    public String makeOpenMessage(HashMap<String, Integer> crates) {
        StringBuilder crateMessageBuilder = new StringBuilder();
        for (Map.Entry<String, Integer> entry : crates.entrySet()) {
            // format the string
            crateMessageBuilder.append(" ")
                    .append(formatCratePlaceholder(Lang.OPEN_MESSAGE_CRATE_FORMAT.get().replace("%amount%", entry.getValue().toString()), entry.getKey()));
        }
        String crateMessage = crateMessageBuilder.toString();
        // remove the last ',' from the text if using the default config
        // which separates the keys in the message using ','
        if (crateMessage.substring(crateMessage.length() - 1).equals(",")) {
            crateMessage = crateMessage.substring(0, crateMessage.length() - 1);
        }
        return Lang.OPEN_MESSAGE_FULL.get()
                .replace(" %result%", crateMessage);
    }

    /**
     * The function translates the crate placeholders.
     *
     * @param message   The message with the placeholders
     * @param crateName The name of the crate
     * @return A message with the crate placeholders replaced
     */
    public String formatCratePlaceholder(String message, String crateName) {
        return message.replace("%crate%", crateName.substring(0, 1).toUpperCase() + crateName.substring(1))
                .replace("%crate_lower%", crateName)
                .replace("%crate_upper%", crateName.toUpperCase());
    }

    /**
     * The function counts the amount of empty slots in the player's inventory.
     *
     * @param p The requested player
     * @return The amount of empty slots
     */
    public int getEmptySlotCount(Player p) {
        int i = 0;
        for (ItemStack item : p.getInventory()) {
            if (item != null && item.getType() != Material.AIR) {
                i++;
            }
        }
        return 36 - i;
    }
}
