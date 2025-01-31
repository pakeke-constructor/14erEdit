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

import com._14ercooper.math.Point3;
import com._14ercooper.worldeditor.blockiterator.BlockIterator;
import com._14ercooper.worldeditor.blockiterator.BlockWrapper;
import com._14ercooper.worldeditor.main.Main;
import org.bukkit.World;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

public class RotatedEllipseIterator extends BlockIterator {

    long totalBlocks;
    int xC, yC, zC;
    double hFD, strL, dX, dY, dZ;
    int radMax;
    int maxDist;

    @Override
    public RotatedEllipseIterator newIterator(List<String> args, World world, CommandSender player) {
        try {
            RotatedEllipseIterator iterator = new RotatedEllipseIterator();
            iterator.iterWorld = world;
            iterator.xC = Integer.parseInt(args.get(0)); // Center
            iterator.yC = Integer.parseInt(args.get(1));
            iterator.zC = Integer.parseInt(args.get(2));
            iterator.hFD = Double.parseDouble(args.get(3)); // Half the distance between focal points
            iterator.strL = Double.parseDouble(args.get(4)); // "String length" of ellipse
            iterator.dX = Double.parseDouble(args.get(5)); // Direction from center to a focal point
            iterator.dY = Double.parseDouble(args.get(6));
            iterator.dZ = Double.parseDouble(args.get(7));
            iterator.maxDist = (int) (iterator.strL) + 1;
            iterator.totalBlocks = (2L * iterator.maxDist + 1) * (2L * iterator.maxDist + 1) * (2L * iterator.maxDist + 1);
            iterator.x = -iterator.maxDist - 1;
            iterator.y = -iterator.maxDist;
            iterator.z = -iterator.maxDist;
            iterator.radMax = iterator.maxDist;
            while (y + yC < 0) {
                y++;
            }
            iterator.setup();
            return iterator;
        } catch (Exception e) {
            Main.logError("Error creating rotated ellipse iterator. Please check your brush parameters.",
                    player, e);
            return null;
        }
    }

    Point3 focus1, focus2, negCenter;

    private void setup() {
        Point3 dir = new Point3(dX, dY, dZ);
        Point3 center = new Point3(xC, yC, zC);
        dir.normalize();
        dir = dir.mult(hFD);
        focus1 = center.add(dir);
        focus2 = center.add(dir.mult(-1));
        negCenter = center;
    }

    @Override
    public BlockWrapper getNextBlock(CommandSender player, boolean getBlock) {
        while (true) {
            if (incrXYZ(radMax, radMax, radMax, xC, yC, zC, player)) {
                return null;
            }

            // Check that it's within the ellipse
            // Get what would be the needed string length
            Point3 blockPoint = new Point3(x, y, z);
            blockPoint = blockPoint.add(negCenter);
            double dist = blockPoint.distance(focus1) + blockPoint.distance(focus2);

            // Make sure it's small enough
            if (dist > strL)
                continue;

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
