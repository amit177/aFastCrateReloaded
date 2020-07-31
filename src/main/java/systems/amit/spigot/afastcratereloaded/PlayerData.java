package systems.amit.spigot.afastcratereloaded;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class PlayerData {

    private final String CONFIG_RECURSIVE = "recursive";
    private final String CONFIG_SKIPPED_CRATES = "skipped-crates";
    public HashMap<UUID, List<String>> playerSkippedCrates = new HashMap<>();
    public HashMap<UUID, Boolean> playersRecursiveState = new HashMap<>();
    private File dataFile;
    private FileConfiguration dataConfig;

    /**
     * The function loads the playerdata file
     *
     * @return true if load was successful, false otherwise
     */
    public boolean load() {
        dataFile = new File(Main.getInstance().getDataFolder(), "playerdata.yml");
        if (!dataFile.exists()) {
            dataFile.getParentFile().mkdirs();
            Main.getInstance().saveResource("playerdata.yml", false);
        }

        dataConfig = new YamlConfiguration();
        try {
            dataConfig.load(dataFile);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * The function gets the playerdata file.
     *
     * @return The playerdata configuration file
     */
    private FileConfiguration getDataConfig() {
        return dataConfig;
    }

    /**
     * The function checks if the player has recursive key opening enabled
     *
     * @param uuid The UUID of the player
     * @return true if enabled, false otherwise
     */
    public boolean isRecursiveOpeningEnabled(UUID uuid) {
        if (playersRecursiveState.containsKey(uuid)) {
            return playersRecursiveState.get(uuid);
        }

        if (getDataConfig().contains(uuid + "." + CONFIG_RECURSIVE)) {
            playersRecursiveState.put(uuid, getDataConfig().getBoolean(uuid + "." + CONFIG_RECURSIVE));
        } else {
            // not adding it to the playerdata file to save some space
            // there's no point to add an empty section that only has default values
            playersRecursiveState.put(uuid, true);
        }
        return playersRecursiveState.get(uuid);
    }

    /**
     * The function toggles the player's crate recursive state.
     *
     * @param uuid The UUID of the player
     * @return The new state
     */
    public boolean toggleRecursiveCrateState(UUID uuid) {
        boolean currentState = isRecursiveOpeningEnabled(uuid);
        playersRecursiveState.put(uuid, !currentState);
        getDataConfig().set(uuid + "." + CONFIG_RECURSIVE, !currentState);
        save();
        return !currentState;
    }

    /**
     * The function gets the crate skip list of the player
     *
     * @param uuid The UUID of the player
     * @return The skip list of the player
     */
    public List<String> getCrateSkipList(UUID uuid) {
        if (playerSkippedCrates.containsKey(uuid)) {
            return playerSkippedCrates.get(uuid);
        }

        if (getDataConfig().contains(uuid + "." + CONFIG_SKIPPED_CRATES)) {
            playerSkippedCrates.put(uuid, getDataConfig().getStringList(uuid + "." + CONFIG_SKIPPED_CRATES));
        } else {
            // not adding it to the playerdata file to save some space
            // there's no point to add an empty section that only has default values
            playerSkippedCrates.put(uuid, new ArrayList<>());
        }
        return playerSkippedCrates.get(uuid);
    }

    /**
     * The function checks if the player has the specified crate in the skip list
     *
     * @param uuid      The uuid of teh player
     * @param crateName The name of the crate
     * @return true if the crate is in the skip list, false otherwise
     */
    public boolean isCrateInSkipList(UUID uuid, String crateName) {
        return getCrateSkipList(uuid).contains(crateName.toLowerCase());
    }

    /**
     * The function adds/removes a crate from the player's skipped crates list
     *
     * @param uuid      The uuid of the player
     * @param crateName The name of the crate
     * @return true if the key was added to the list
     * false if the key was removed from the list
     */
    public boolean toggleCrateSkipList(UUID uuid, String crateName) {
        crateName = crateName.toLowerCase();
        List<String> skipList;
        if (!getDataConfig().contains(uuid + "." + CONFIG_SKIPPED_CRATES)) {
            skipList = new ArrayList<>();
        } else {
            skipList = getDataConfig().getStringList(uuid + "." + CONFIG_SKIPPED_CRATES);
        }

        if (skipList.contains(crateName)) {
            skipList.remove(crateName);
        } else {
            skipList.add(crateName);
        }

        playerSkippedCrates.put(uuid, skipList);
        getDataConfig().set(uuid + "." + CONFIG_SKIPPED_CRATES, skipList);
        save();
        return isCrateInSkipList(uuid, crateName);
    }


    /**
     * The function saves the playerdata file
     */
    public void save() {
        try {
            dataConfig.save(dataFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
