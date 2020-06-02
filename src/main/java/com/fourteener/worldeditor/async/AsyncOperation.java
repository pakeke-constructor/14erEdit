package com.fourteener.worldeditor.async;

import java.util.ArrayDeque;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import com.fourteener.schematics.SchemLite;
import com.fourteener.worldeditor.blockiterator.BlockIterator;
import com.fourteener.worldeditor.operations.Operator;
import com.fourteener.worldeditor.undo.Undo;

public class AsyncOperation {
	protected String key = "";
	protected Operator operation = null;
	protected ArrayDeque<Block> toOperate = null;
	protected BlockIterator blocks = null;
	protected Player player = null;
	protected Undo undo = null;
	protected boolean undoRunning = false;
	
	public AsyncOperation (Operator o, Player p, BlockIterator b) {
		key = "iteredit";
		operation = o;
		player = p;
		blocks = b;
	}
	
	public AsyncOperation (Operator o, BlockIterator b) {
		key = "rawiteredit";
		operation = o;
		blocks = b;
	}
	
	public AsyncOperation (Operator o, Player p, ArrayDeque<Block> b) {
		key = "edit";
		operation = o;
		player = p;
		toOperate = b;
	}
	
	public AsyncOperation (Operator o, ArrayDeque<Block> b) {
		key = "rawedit";
		operation = o;
		toOperate = b;
	}
	
	public AsyncOperation (Operator o) {
		key = "messyedit";
		operation = o;
	}
	
	// New schematics system
	protected SchemLite schem = null;
	protected BlockIterator bIter = null;
	protected int[] origin = {};
	
	public AsyncOperation (SchemLite sl, boolean saveSchem, int[] o, Player p) {
		schem = sl;
		origin = o;
		bIter = schem.getIterator(origin[0], origin[1], origin[2]);
		if (saveSchem) {
			key = "saveschem";
		}
		else {
			key = "loadschem";
		}
	}
}
