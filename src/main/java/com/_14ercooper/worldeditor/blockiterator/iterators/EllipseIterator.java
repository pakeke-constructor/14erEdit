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

import com._14ercooper.worldeditor.blockiterator.BlockIterator;
import com._14ercooper.worldeditor.blockiterator.BlockWrapper;
import com._14ercooper.worldeditor.main.Main;
import org.bukkit.World;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

public class EllipseIterator extends BlockIterator {

    long totalBlocks;
    int xC, yC, zC;
    double rx, ry, rz;
    double radCorr;

    @Override
    public EllipseIterator newIterator(List<String> args, World world, CommandSender player) {
        try {
            EllipseIterator iterator = new EllipseIterator();
            iterator.iterWorld = world;
            iterator.xC = Integer.parseInt(args.get(0));
            iterator.yC = Integer.parseInt(args.get(1));
            iterator.zC = Integer.parseInt(args.get(2));
            iterator.rx = Integer.parseInt(args.get(3));
            iterator.ry = Integer.parseInt(args.get(4));
            iterator.rz = Integer.parseInt(args.get(5));
            iterator.radCorr = Double.parseDouble(args.get(6));
            iterator.totalBlocks = (2L * (int) iterator.rx + 1) * (2L * (int) iterator.ry + 1)
                    * (2L * (int) iterator.rz + 1);
            iterator.x = (int) (-iterator.rx - 1);
            iterator.y = (int) -iterator.ry;
            iterator.z = (int) -iterator.rz;
            return iterator;
        } catch (Exception e) {
            Main.logError("Error creating ellipse iterator. Please check your brush parameters.",
                    player, e);
            return null;
        }
    }

    @Override
    public BlockWrapper getNextBlock(CommandSender player, boolean getBlock) {
        while (true) {
            if (incrXYZ((int) rx, (int) ry, (int) rz, xC, yC, zC, player)) {
                return null;
            }

            // Radius check
            if ((x * x / (rx * rx)) + (y * y / (ry * ry)) + (z * z / (rz * rz)) > 1 + radCorr) {
                continue;
            }

            break;
        }

        if (getBlock) {
            return new BlockWrapper(iterWorld.getBlockAt(x + xC, y + yC, z + zC), x + xC, y + yC, z + zC);
        } else {
            return new BlockWrapper(null, x + xC, y + yC, z + zC);
        }
    }

    @Override
    public long getTotalBlocks() {
        return totalBlocks;
    }

    @Override
    public long getRemainingBlocks() {
        return totalBlocks - doneBlocks;
    }

}
