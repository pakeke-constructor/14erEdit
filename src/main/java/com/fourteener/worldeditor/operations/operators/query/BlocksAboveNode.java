package com.fourteener.worldeditor.operations.operators.query;

import org.bukkit.block.Block;

import com.fourteener.worldeditor.main.*;
import com.fourteener.worldeditor.operations.Operator;
import com.fourteener.worldeditor.operations.operators.Node;
import com.fourteener.worldeditor.operations.operators.function.RangeNode;

public class BlocksAboveNode extends Node {
	
	RangeNode arg1;
	Node arg2;
	
	public BlocksAboveNode newNode() {
		BlocksAboveNode node = new BlocksAboveNode();
		node.arg1 = GlobalVars.operationParser.parseRangeNode();
		node.arg2 = GlobalVars.operationParser.parsePart();
		return node;
	}
	
	public boolean performNode () {
		Block currBlock = GlobalVars.world.getBlockAt(Operator.currentBlock.getLocation());
		int x = currBlock.getX();
		int y = currBlock.getY();
		int z = currBlock.getZ();
		int min = (int) arg1.getMin();
		int max = (int) arg1.getMax();
		boolean blockRangeMet = true;
		
		for (int dy = y + min; dy <= y + max; dy++) {
			Operator.currentBlock = GlobalVars.world.getBlockAt(x, dy, z);
			if (!(arg2.performNode()))
				blockRangeMet = false;
		}
		
		Operator.currentBlock = currBlock;
		return blockRangeMet;
	}
	
	public int getArgCount () {
		return 2;
	}
}
