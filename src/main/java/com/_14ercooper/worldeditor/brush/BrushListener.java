package com._14ercooper.worldeditor.brush;

import com._14ercooper.worldeditor.main.Main;
import com._14ercooper.worldeditor.player.PlayerManager;
import com._14ercooper.worldeditor.player.PlayerWrapper;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.BlockIterator;

public class BrushListener implements Listener {

    // Used to prevent the double brush if you click a block
    boolean dedupe = false;

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        try {

            // Check the player is holding a brush
            // Do a quick check first (so this is a bit faster)
            if (event.getAction().equals(Action.PHYSICAL))
                return;

            // Then do a more detailed check
            Player player = event.getPlayer();
            ItemStack item = player.getInventory().getItemInMainHand();
            Brush brush = null;
            PlayerWrapper playerWrapper = PlayerManager.INSTANCE.getPlayerWrapper(player);
            for (Brush b : playerWrapper.getBrushes()) {
                if (b.item.equals(item)) {
                    brush = b;
                }
            }
            if (brush == null)
                return;

            // The event has been verified, take control of it
            event.setCancelled(true);

            // Close to block deduplication
            if (event.getAction() == Action.LEFT_CLICK_AIR) {
                return;
            }
            if (event.getAction() == Action.RIGHT_CLICK_BLOCK && event.getClickedBlock() != null) {
                if (!dedupe) {
                    dedupe = true;
                    return;
                }
                dedupe = false;
            }

            // Then get the location where the brush should operate
            // This is a block where the player is looking, at a range no more than 256
            // blocks away
            Block block = getTargetBlock(player);
            // If it's air, return
            if (block.getType().equals(Material.AIR))
                return;

            // And perform the operation there
            brush.operate(block.getX(), block.getY(), block.getZ());
        } catch (Exception e) {
            Main.logError("Could not process brush click.", event.getPlayer(), e);
        }
    }

    public static Block getTargetBlock(Player player) {
        BlockIterator iter = new BlockIterator(player, 256);
        Block lastBlock = iter.next();
        while (iter.hasNext()) {
            lastBlock = iter.next();

            PlayerWrapper playerWrapper = PlayerManager.INSTANCE.getPlayerWrapper(player);

            if (playerWrapper.getBrushMask().contains(lastBlock.getType())) {
                continue;
            }

            break;
        }
        return lastBlock;
    }
}
