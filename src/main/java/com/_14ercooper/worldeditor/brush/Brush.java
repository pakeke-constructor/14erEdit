package com._14ercooper.worldeditor.brush;

import com._14ercooper.worldeditor.async.AsyncManager;
import com._14ercooper.worldeditor.blockiterator.BlockIterator;
import com._14ercooper.worldeditor.brush.shapes.Multi;
import com._14ercooper.worldeditor.main.GlobalVars;
import com._14ercooper.worldeditor.main.Main;
import com._14ercooper.worldeditor.operations.Operator;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

public class Brush {
    // Together, these two parameters serve as the ID for the brush
    public UUID owner; // Different people can have different brushes
    public ItemStack item; // Each person can have a different brush for different items

    // Variables the brush needs
    BrushShape shapeGenerator;
    Operator operation;

    // Static player
    public static Player currentPlayer = null;

    // Store brushes
    public static final Map<String, BrushShape> brushShapes = new HashMap<>();

    public static boolean removeBrush(Player player) {
        ItemStack item = player.getInventory().getItemInMainHand();

        Brush br = null;

        for (Brush b : BrushListener.brushes) {
            if (b.owner.equals(player.getUniqueId()) && b.item.equals(item)) {
                br = b;
            }
        }
        if (br != null) {
            BrushListener.brushes.remove(br);
        }
        return true;
    }

    public Brush(String[] brushOperation, int brushOpOffset, Player player) {
        try {
            currentPlayer = player;

            ItemStack brushItem = player.getInventory().getItemInMainHand();

            // Make sure this brush doesn't already exist. If it does, remove it
            removeBrush(player);

            // Create a brush, and assign the easy variables to it
            owner = player.getUniqueId();
            item = brushItem;

            brushOpOffset += 2; // Used to remove brush parameters from the operation

            // Get the shape generator, and store the args
            shapeGenerator = brushShapes.get(brushOperation[1]).getClass().getDeclaredConstructor().newInstance();
            Main.logDebug("Brush type: " + shapeGenerator.getClass().getSimpleName());
            try {
                do {
                    if (brushOpOffset >= brushOperation.length
                            || brushOperation[brushOpOffset].equalsIgnoreCase("end")) {
                        brushOpOffset += 2;
                        break;
                    }
                    shapeGenerator.addNewArgument(brushOperation[brushOpOffset]);
                    Main.logDebug("Passed arg \"" + brushOperation[brushOpOffset] + "\", processed="
                            + shapeGenerator.lastInputProcessed());
                    brushOpOffset++;
                }
                while (shapeGenerator.lastInputProcessed());
                brushOpOffset--;
            } catch (Exception e) {
                Main.logError(
                        "Could not parse brush arguments. Please check that you provided enough numerical arguments for the brush shape.",
                        player, e);
                return;
            }

            if (shapeGenerator.gotEnoughArgs()) {
                Main.logError("Not enough inputs to the brush shape were provided. Please provide enough inputs.",
                        player, null);
            }

            if (!(shapeGenerator instanceof Multi)) {
                // Construct the operator
                // Start by removing brush parameters
                List<String> opArray = new LinkedList<>(Arrays.asList(brushOperation));
                while (brushOpOffset > 0) {
                    opArray.remove(0);
                    brushOpOffset--;
                }
                // Construct the string
                String opStr = "";
                for (String s : opArray) {
                    opStr = opStr.concat(s).concat(" ");
                }
                // And then construct the operator
                operation = new Operator(opStr, player);
            }

            // Store the brush and return success
            BrushListener.brushes.add(this);
            player.sendMessage("§dBrush created and bound to item in hand.");
            GlobalVars.errorLogged = false;

        } catch (Exception e) {
            Main.logError("Error creating brush. Please check your syntax.", player, e);
        }
    }

    public static BrushShape GetBrushShape(String name) {
        if (brushShapes.containsKey(name)) {
            try {
                return brushShapes.get(name).getClass().getDeclaredConstructor().newInstance();
            } catch (InstantiationException | IllegalAccessException | IllegalArgumentException
                    | InvocationTargetException | NoSuchMethodException | SecurityException e) {
                Main.logDebug("Error instantiating brush.");
                return null;
            }
        }
        return null;
    }

    public static void AddBrushShape(String name, BrushShape shape) {
        if (brushShapes.containsKey(name)) {
            return;
        }
        brushShapes.put(name, shape);
    }

    public void operate(double x, double y, double z) {
        try {
            currentPlayer = getOwner();

            if (!(shapeGenerator instanceof Multi)) {
                // Build an array of all blocks to operate on
                BlockIterator blockArray = shapeGenerator.GetBlocks(x, y, z, currentPlayer.getWorld(), currentPlayer);

                if (blockArray == null || blockArray.getTotalBlocks() == 0) {
                    return;
                }
                Main.logDebug("Block array size is " + blockArray.getTotalBlocks()); // -----

                AsyncManager.scheduleEdit(operation, getOwner(), blockArray);

            } else {
                // Create a multi-operator async chain
                Multi multiShape = (Multi) shapeGenerator;
                List<BlockIterator> iters = multiShape.getIters(x, y, z, getOwner().getWorld(), currentPlayer);
                List<Operator> ops = multiShape.getOps(x, y, z);

                AsyncManager.scheduleEdit(iters, ops, getOwner());

            }
        } catch (Exception e) {
            e.printStackTrace();
            Main.logError("Error operating with brush. Please check your syntax.", getOwner(), e);
        }
    }

    public Player getOwner() {
        return Bukkit.getServer().getPlayer(owner);
    }
}
