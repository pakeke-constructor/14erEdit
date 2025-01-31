/**
 * This file is part of 14erEdit.
 *
  * 14erEdit is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * 14erEdit is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with 14erEdit.  If not, see <https://www.gnu.org/licenses/>.
 */

package com._14ercooper.worldeditor.blockiterator.iterators;

import com._14ercooper.schematics.SchemLite;
import com._14ercooper.worldeditor.blockiterator.BlockIterator;
import com._14ercooper.worldeditor.blockiterator.BlockWrapper;
import com._14ercooper.worldeditor.main.Main;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandSender;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SchemBrushIterator extends BlockIterator {

    SchemLite schem;
    BlockIterator schemIter;

    // Statics
//    public static String blockType = "";
//    public static String blockData = "";
//    public static String nbt = "";

    @Override
    public BlockIterator newIterator(List<String> args, World world, CommandSender player) {
        try {
            SchemBrushIterator iter = new SchemBrushIterator();
            iter.iterWorld = world;
            int x = Integer.parseInt(args.get(0));
            int y = Integer.parseInt(args.get(1));
            int z = Integer.parseInt(args.get(2));
            iter.schem = new SchemLite(args.get(3), true, 0);
            iter.schem.openRead();
            iter.schemIter = iter.schem.getIterator(x - (iter.schem.getXSize() / 2), y - (iter.schem.getYSize() / 2),
                    z - (iter.schem.getZSize() / 2), world);
            return iter;
        } catch (Exception e) {
            Main.logError("Could not create schem brush iterator", player, e);
            return null;
        }
    }

    public void cleanup() {
        try {
            schem.closeRead();
        } catch (Exception e) {
            // This isn't a problem
        }
    }

    @Override
    public BlockWrapper getNextBlock(CommandSender player, boolean getBlock) {

        String blockType;
        String blockData;
        String nbt;

        // Update the schem block
        try {
            String[] data = schem.readNext();
            blockType = data[0];
            blockData = data[1];
            nbt = data[2];
        } catch (IOException e) {
            Main.logError("Could not read next block from schematic.", Bukkit.getConsoleSender(), e);
            blockType = blockData = nbt = "";
        }

        // Return the next world block
        BlockWrapper wrapper = schemIter.getNextBlock(player, getBlock);
        if (wrapper != null) {
            wrapper.otherArgs.add(blockType);
            wrapper.otherArgs.add(blockData);
            wrapper.otherArgs.add(nbt);
        }
        return wrapper;
    }

    @Override
    public long getTotalBlocks() {
        // How big is the schematic?
        return schemIter.getTotalBlocks();
    }

    @Override
    public long getRemainingBlocks() {
        // About how much longer to go?
        return schemIter.getRemainingBlocks();
    }

}
