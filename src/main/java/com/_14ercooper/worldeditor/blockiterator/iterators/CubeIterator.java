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

// This is an annoying class
public class CubeIterator extends BlockIterator {

    int x1, y1, z1;
    int x2, y2, z2;
    long totalBlocks;
    long doneBlocks = 0;
    int x, y, z;
    int xStep = 1, yStep = 1, zStep = 1;
    int executionOrder = 0;

    @Override
    public CubeIterator newIterator(List<String> args, World world, CommandSender player) {
        try {
            CubeIterator iterator = new CubeIterator();
            iterator.iterWorld = world;
            iterator.x1 = Integer.parseInt(args.get(0));
            iterator.y1 = Integer.parseInt(args.get(1));
            iterator.z1 = Integer.parseInt(args.get(2));
            iterator.x2 = Integer.parseInt(args.get(3));
            iterator.y2 = Integer.parseInt(args.get(4));
            iterator.z2 = Integer.parseInt(args.get(5));
            if (args.size() > 6) {
                iterator.executionOrder = Integer.parseInt(args.get(6));
                if (iterator.executionOrder < 0)
                    iterator.executionOrder = 0;
                if (iterator.executionOrder > 5)
                    iterator.executionOrder = 0;
            }
            if (iterator.x2 < iterator.x1) {
                iterator.xStep = -1;
            }
            if (iterator.y2 < iterator.y1) {
                iterator.yStep = -1;
            }
            if (iterator.z2 < iterator.z1) {
                iterator.zStep = -1;
            }
            int dx = Math.abs(iterator.x2 - iterator.x1) + 1;
            int dy = Math.abs(iterator.y2 - iterator.y1) + 1;
            int dz = Math.abs(iterator.z2 - iterator.z1) + 1;
            iterator.totalBlocks = (long) dx * dy * dz;
            iterator.x = iterator.x1;
            iterator.y = iterator.y1;
            iterator.z = iterator.z1;
            if (iterator.executionOrder == 0 || iterator.executionOrder == 1)
                iterator.x -= iterator.xStep;
            if (iterator.executionOrder == 2 || iterator.executionOrder == 4)
                iterator.z -= iterator.zStep;
            if (iterator.executionOrder == 3 || iterator.executionOrder == 5)
                iterator.y -= iterator.yStep;
            Main.logDebug("From " + iterator.x1 + "," + iterator.y1 + "," + iterator.z1 + " to " + iterator.x2 + ","
                    + iterator.y2 + "," + iterator.z2);
            Main.logDebug("Starting block: " + iterator.x + "," + iterator.y + "," + iterator.z);
            Main.logDebug("Steps: " + iterator.xStep + "," + iterator.yStep + "," + iterator.zStep);
            Main.logDebug("Cube iterator execution order: " + iterator.executionOrder);
            return iterator;
        } catch (Exception e) {
            Main.logError(
                    "Could not create cube iterator. Please check your brush parameters/if you have a selection box.",
                    player, e);
            return null;
        }
    }

    @Override
    public BlockWrapper getNextBlock(CommandSender player, boolean getBlock) {
        if (executionOrder == 0) { // xzy
            x += xStep;
            doneBlocks++;
            if (inRange(x, x1, x2)) {
                z += zStep;
                x = x1;
            }
            if (inRange(z, z1, z2)) {
                y += yStep;
                z = z1;
            }
            if (inRange(y, y1, y2)) {
                return null;
            }
        }
        if (executionOrder == 1) { // xyz
            x += xStep;
            doneBlocks++;
            if (inRange(x, x1, x2)) {
                y += yStep;
                x = x1;
            }
            if (inRange(y, y1, y2)) {
                z += zStep;
                y = y1;
            }
            if (inRange(z, z1, z2)) {
                return null;
            }
        }
        if (executionOrder == 2) { // zxy
            z += zStep;
            doneBlocks++;
            if (inRange(z, z1, z2)) {
                x += xStep;
                z = z1;
            }
            if (inRange(x, x1, x2)) {
                y += yStep;
                x = x1;
            }
            if (inRange(y, y1, y2)) {
                return null;
            }
        }
        if (executionOrder == 3) { // yxz
            y += yStep;
            doneBlocks++;
            if (inRange(y, y1, y2)) {
                x += xStep;
                y = y1;
            }
            if (inRange(x, x1, x2)) {
                z += zStep;
                x = x1;
            }
            if (inRange(z, z1, z2)) {
                return null;
            }
        }
        if (executionOrder == 4) { // zyx
            z += zStep;
            doneBlocks++;
            if (inRange(z, z1, z2)) {
                y += yStep;
                z = z1;
            }
            if (inRange(y, y1, y2)) {
                x += xStep;
                y = y1;
            }
            if (inRange(x, x1, x2)) {
                return null;
            }
        }
        if (executionOrder == 5) { // yzx
            y += yStep;
            doneBlocks++;
            if (inRange(y, y1, y2)) {
                z += zStep;
                y = y1;
            }
            if (inRange(z, z1, z2)) {
                x += xStep;
                z = z1;
            }
            if (inRange(x, x1, x2)) {
                return null;
            }
        }

        if (getBlock) {
            return new BlockWrapper(iterWorld.getBlockAt(x, y, z), x, y, z);
        } else {
            return new BlockWrapper(null, x, y, z);
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

    private boolean inRange(int val, int r1, int r2) {
        int min;
        int max;
        if (r1 <= r2) {
            min = r1;
            max = r2;
        } else {
            min = r2;
            max = r1;
        }
        return (val < min || val > max);
    }
}
