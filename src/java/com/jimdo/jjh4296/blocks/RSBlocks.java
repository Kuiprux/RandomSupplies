package com.jimdo.jjh4296.blocks;

import net.minecraft.block.Block;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class RSBlocks {
	public static Block blockSupply = new BlockSupplies().setRegistryName("supplies");

	public static void init() {
		GameRegistry.register(blockSupply);
	}
}
