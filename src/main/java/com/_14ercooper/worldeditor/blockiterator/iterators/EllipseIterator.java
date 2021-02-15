package com._14ercooper.worldeditor.blockiterator.iterators;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.block.Block;

import com._14ercooper.worldeditor.blockiterator.BlockIterator;
import com._14ercooper.worldeditor.main.Main;
import com._14ercooper.worldeditor.operations.Operator;

public class EllipseIterator extends BlockIterator {

    long totalBlocks;
//    long doneBlocks = 0;
//    double x, y, z;
    int xC, yC, zC;
    double rx, ry, rz;
    double radCorr;

    @Override
    public EllipseIterator newIterator(List<String> args) {
	try {
	    EllipseIterator iterator = new EllipseIterator();
	    iterator.xC = Integer.parseInt(args.get(0));
	    iterator.yC = Integer.parseInt(args.get(1));
	    iterator.zC = Integer.parseInt(args.get(2));
	    iterator.rx = Integer.parseInt(args.get(3));
	    iterator.ry = Integer.parseInt(args.get(4));
	    iterator.rz = Integer.parseInt(args.get(5));
	    iterator.radCorr = Double.parseDouble(args.get(6));
	    iterator.totalBlocks = (2 * (int) iterator.rx + 1) * (2 * (int) iterator.ry + 1)
		    * (2 * (int) iterator.rz + 1);
	    iterator.x = (int) (-iterator.rx - 1);
	    iterator.y = (int) -iterator.ry;
	    iterator.z = (int) -iterator.rz;
	    return iterator;
	}
	catch (Exception e) {
	    Main.logError("Error creating ellipse iterator. Please check your brush parameters.",
		    Operator.currentPlayer);
	    return null;
	}
    }

    @Override
    public Block getNext() {
	while (true) {
//	    x++;
//	    doneBlocks++;
//	    if (x > rx) {
//		z++;
//		x = -rx;
//	    }
//	    if (z > rz) {
//		y++;
//		z = -rz;
//	    }
//	    if (y > ry) {
//		return null;
//	    }
	    if (incrXYZ((int) rx, (int) ry, (int) rz, xC, yC, zC)) {
		    return null;
	    }

	    // Radius check
	    if (((double) (x * x) / (double) (rx * rx)) + ((double) (y * y) / (double) (ry * ry)) + ((double) (z * z) / (double) (rz * rz)) > (double) (1 + radCorr)) {
		continue;
	    }

	    break;
	}

	try {
	    return Operator.currentPlayer.getWorld().getBlockAt((int) x + xC, (int) y + yC, (int) z + zC);
	}
	catch (NullPointerException e) {
	    return Bukkit.getWorlds().get(0).getBlockAt((int) x + xC, (int) y + yC, (int) z + zC);
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
