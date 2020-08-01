package systems.amit.spigot.afastcratereloaded.events;

import com.hazebyte.crate.api.event.CrateInteractEvent;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import systems.amit.spigot.afastcratereloaded.Lang;
import systems.amit.spigot.afastcratereloaded.Main;

import java.util.HashMap;

public class Event_CrateInteract implements Listener {

    @EventHandler
    public void onCrateOpen(CrateInteractEvent e) {
        if (!e.getPlayer().isSneaking()
                || !e.getPlayer().getInventory().getItemInHand().getType().equals(Main.getInstance().keyMaterial)
                || !e.getPlayer().getInventory().getItemInHand().hasItemMeta()
                || !e.getPlayer().getInventory().getItemInHand().getItemMeta().hasDisplayName()
                || e.getPlayer().getGameMode().equals(GameMode.CREATIVE))
            return;

        ItemStack key = e.getPlayer().getInventory().getItemInHand();
        if (!e.getCrate().is(key)) return;

        e.setCancelled(true);

        if(Main.getInstance().getEmptySlotCount(e.getPlayer()) < Main.getInstance().minimumInventorySlots) {
            e.getPlayer().sendMessage(Lang.OPEN_MESSAGE_INV_SLOTS.get().replace("%slots%", String.valueOf(Main.getInstance().minimumInventorySlots)));
            return;
        }

        // make sure the key is a crate by parsing the config 'key-name-format' value
        String keyName = Main.getInstance().sanitizeKeyName(key.getItemMeta().getDisplayName());
        if (Main.getInstance().crateAPI.getCrateRegistrar().getCrate(keyName) == null) {
            return;
        }

        // remove the keys from the player's inv
        int keyAmount = key.getAmount();
        key.setAmount(0);

        HashMap<String, Integer> openedCrates = Main.getInstance().processKeys(e.getPlayer(),
                e.getPlayer().hasPermission(Main.getInstance().recursivePermission) && Main.getInstance().playerData.isRecursiveOpeningEnabled(e.getPlayer().getUniqueId()),
                keyName,
                keyAmount
        );

        e.getPlayer().sendMessage(Main.getInstance().makeOpenMessage(openedCrates));
    }
}
