package com.fourteener.worldeditor.main;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.Leaves;

public class SetBlock {
	public static void setMaterial(Block b, Material mat) {
		b.setType(mat, false);
		if (mat.toString().toLowerCase().contains("leaves")) {
			Leaves leafData = (Leaves) b.getBlockData();
			leafData.setPersistent(true);
			b.setBlockData(leafData);
		}
	}
	public static void setMaterial(Block b, Material mat, boolean physics) {
		b.setType(mat, physics);
		if (mat.toString().toLowerCase().contains("leaves")) {
			Leaves leafData = (Leaves) b.getBlockData();
			leafData.setPersistent(true);
			b.setBlockData(leafData);
		}
	}
	
	public static void setMaterialData(Block b, Material mat, BlockData dat) {
		b.setType(mat);
		b.setBlockData(dat);
	}
}
