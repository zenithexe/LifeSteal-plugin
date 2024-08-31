package org.strassburger.lifestealz.listeners;

import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.strassburger.lifestealz.LifeStealZ;
import org.strassburger.lifestealz.util.MessageUtils;
import org.strassburger.lifestealz.util.WhitelistManager;
import org.strassburger.lifestealz.util.storage.PlayerData;
import org.strassburger.lifestealz.util.storage.Storage;

public class PlayerLoginListener implements Listener {
    private final LifeStealZ plugin;

    public PlayerLoginListener(LifeStealZ plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerLogin(PlayerLoginEvent event) {
        Player player = event.getPlayer();
        Storage storage = plugin.getStorage();

        if (!WhitelistManager.isWorldWhitelisted(player)) return;

        PlayerData playerData = loadOrCreatePlayerData(player, storage);

        if (shouldKickPlayer(playerData)) {
            kickPlayer(event);
        }
    }

    private PlayerData loadOrCreatePlayerData(Player player, Storage storage) {
        PlayerData playerData = plugin.getStorage().load(player.getUniqueId());
        if (playerData == null) {
            playerData = new PlayerData(player.getName(), player.getUniqueId());
            storage.save(playerData);
        }
        return playerData;
    }

    private boolean shouldKickPlayer(PlayerData playerData) {
        boolean disabledBanOnDeath = plugin.getConfig().getBoolean("disablePlayerBanOnElimination");
        double minHearts = plugin.getConfig().getInt("minHearts") * 2;
        return playerData.getMaxhp() <= minHearts && !disabledBanOnDeath;
    }

    private void kickPlayer(PlayerLoginEvent event) {
        event.setResult(PlayerLoginEvent.Result.KICK_OTHER);
        Component kickmsg = MessageUtils.getAndFormatMsg(false, "eliminatedJoin", "&cYou don't have any hearts left!");
        event.kickMessage(kickmsg);
    }
}