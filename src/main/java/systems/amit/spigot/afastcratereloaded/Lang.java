package systems.amit.spigot.afastcratereloaded;

import org.bukkit.ChatColor;

import java.util.HashMap;

public enum Lang {

    PREFIX("prefix", "&8[&bCrates&8]"),
    NO_PERMISSION("no-permission", "%prefix% &cYou do not have access to that command."),
    OPEN_MESSAGE_INV_SLOTS("open-message.inv-slots", "%prefix% &cYou need at least &e%slots% &cempty inventory slots!"),
    OPEN_MESSAGE_FULL("open-message.full", "%prefix% &7You've opened %result% &7crates."),
    OPEN_MESSAGE_CRATE_FORMAT("open-message.crate-format", "&e%amount%x &c%crate_upper%,"),
    COMMANDS_HEADER("commands.header", "&7Command List:"),
    COMMANDS_UNKNOWN("commands.unknown", "%prefix% &cUnknown command."),
    COMMANDS_TOGGLE_HELP("commands.toggle.help", "&e/%cmd% toggle &8- &7Toggle fast crate opening"),
    COMMANDS_TOGGLE_ENABLED("commands.toggle.enabled", "%prefix% &aFast crate opening is now enabled!"),
    COMMANDS_TOGGLE_DISABLED("commands.toggle.disabled", "%prefix% &cFast crate opening is now disabled!"),
    COMMANDS_SKIP_HELP("commands.skip.help", "&e/%cmd% skip <name> &8- &7Skip a crate when opening multiple keys"),
    COMMANDS_SKIP_USAGE("commands.skip.usage", "%prefix% &eUsage: /%cmd% skip <name>"),
    COMMANDS_SKIP_INVALID_CRATE("commands.skip.invalid-crate", "%prefix% &cInvalid crate specified."),
    COMMANDS_SKIP_CRATE_ADDED("commands.skip.crate-added", "%prefix% &7The crate &e%crate_upper% &7has been added to your skip list!"),
    COMMANDS_SKIP_CRATE_REMOVED("commands.skip.crate-removed", "%prefix% &7The crate &e%crate_upper% &7has been removed from your skip list!"),
    COMMANDS_LIST_HELP("commands.list.help", "&e/%cmd% list &8- &7List the crates in your skip list"),
    COMMANDS_LIST_EMPTY("commands.list.empty", "%prefix% &7You have no crates in your skip list!"),
    COMMANDS_LIST_RESULT("commands.list.result", "%prefix% &eSkipped crates: %crates%.");

    private static HashMap<String, String> messageCache = new HashMap<>();
    private String path;
    private String defaultValue;

    Lang(String path, String defaultValue) {
        this.path = path;
        this.defaultValue = defaultValue;
    }

    /**
     * The function retrieves the value for the message and processes placeholders.
     *
     * @return The value for the message
     */
    public String get() {
        if (messageCache.containsKey(getPath())) {
            return messageCache.get(getPath());
        }

        String message;
        if (Main.getInstance().getConfig().contains("lang." + getPath())) {
            message = Main.getInstance().getConfig().getString("lang." + getPath());
        } else {
            message = getDefaultValue();
            Main.getInstance().getLogger().warning("Message '" + getPath() + "' is missing from the config. Using default value.");
        }

        if (!getPath().equals(PREFIX.getPath())) {
            message = message.replace("%prefix%", PREFIX.get());
        }

        message = ChatColor.translateAlternateColorCodes('&', message);

        messageCache.put(getPath(), message);

        return message;
    }

    public String getPath() {
        return path;
    }

    public String getDefaultValue() {
        return defaultValue;
    }
}
