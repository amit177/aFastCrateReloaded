package systems.amit.spigot.afastcratereloaded;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class Cmd_afastcratereloaded implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Player only command");
            return false;
        }
        Player p = (Player) sender;

        if (args.length == 0) {
            if (Main.getInstance().showHelpCredits) {
                p.sendMessage(Lang.PREFIX.get() + ChatColor.GRAY + " aFastCrateReloaded v" + Main.getInstance().getDescription().getVersion() + " was made by amit177");
            }
            p.sendMessage(Lang.COMMANDS_HEADER.get());
            p.sendMessage(Lang.COMMANDS_TOGGLE_HELP.get().replace("%cmd%", label));
            p.sendMessage(Lang.COMMANDS_SKIP_HELP.get().replace("%cmd%", label));
            p.sendMessage(Lang.COMMANDS_LIST_HELP.get().replace("%cmd%", label));
            return false;
        }

        if (!p.hasPermission(Main.getInstance().recursivePermission)) {
            p.sendMessage(Lang.NO_PERMISSION.get());
            return false;
        }

        if (args[0].equalsIgnoreCase("toggle")) {
            if (Main.getInstance().playerData.toggleRecursiveCrateState(p.getUniqueId())) {
                p.sendMessage(Lang.COMMANDS_TOGGLE_ENABLED.get());
            } else {
                p.sendMessage(Lang.COMMANDS_TOGGLE_DISABLED.get());
            }
        } else if (args[0].equalsIgnoreCase("skip")) {
            if (args.length < 2) {
                p.sendMessage(Lang.COMMANDS_SKIP_USAGE.get().replace("%cmd%", label));
                return false;
            }
            String crateName = args[1].toLowerCase();
            if (Main.getInstance().crateAPI.getCrateRegistrar().getCrate(crateName) == null) {
                p.sendMessage(Lang.COMMANDS_SKIP_INVALID_CRATE.get());
                return false;
            }
            if (Main.getInstance().playerData.toggleCrateSkipList(p.getUniqueId(), crateName)) {
                p.sendMessage(Main.getInstance().formatCratePlaceholder(Lang.COMMANDS_SKIP_CRATE_ADDED.get().replace("%cmd%", label), crateName));
            } else {
                p.sendMessage(Main.getInstance().formatCratePlaceholder(Lang.COMMANDS_SKIP_CRATE_REMOVED.get().replace("%cmd%", label), crateName));
            }
        } else if (args[0].equalsIgnoreCase("list")) {
            List<String> skippedCrates = Main.getInstance().playerData.getCrateSkipList(p.getUniqueId());
            if (skippedCrates.size() == 0) {
                p.sendMessage(Lang.COMMANDS_LIST_EMPTY.get());
                return false;
            }
            StringBuilder msg = new StringBuilder();
            for (String s : skippedCrates) {
                msg.append(" ").append(s.toUpperCase()).append(",");
            }
            String cleanMessage = msg.toString();
            cleanMessage = cleanMessage.substring(1, cleanMessage.length() - 1);
            p.sendMessage(Lang.COMMANDS_LIST_RESULT.get().replace("%crates%", cleanMessage));
        } else {
            p.sendMessage(Lang.COMMANDS_UNKNOWN.get());
        }
        return false;
    }
}
