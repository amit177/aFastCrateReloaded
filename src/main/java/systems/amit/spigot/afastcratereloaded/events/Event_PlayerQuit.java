package systems.amit.spigot.afastcratereloaded.events;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import systems.amit.spigot.afastcratereloaded.Main;

public class Event_PlayerQuit implements Listener {

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        // remove just the skipped crates because it might be big
        // unlike the recursive state, it isn't worth keeping it in memory
        Main.getInstance().playerData.playerSkippedCrates.remove(e.getPlayer().getUniqueId());
    }
}
