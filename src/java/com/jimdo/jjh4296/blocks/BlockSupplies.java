package com.jimdo.jjh4296.blocks;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import com.jimdo.jjh4296.core.RandomSupplies;
import com.jimdo.jjh4296.utils.DropDimension;
import com.jimdo.jjh4296.utils.DropItem;
import com.jimdo.jjh4296.utils.Utils;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockSupplies extends Block {

	protected BlockSupplies() {
		super(Material.WOOD);
	}

	@Override
	public boolean isBlockSolid(IBlockAccess worldIn, BlockPos pos, EnumFacing side) {
		return false;
	}
	
	@Override
	public boolean isVisuallyOpaque() {
		return false;
	}
	
	@Override
	public boolean isPassable(IBlockAccess worldIn, BlockPos pos) {
		return true;
	}

	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
		return new AxisAlignedBB(0.1875D, 0.0D, 0.125D, 0.75D, 0.1875D, 0.875D);
	}

	@Override
	public void harvestBlock(World worldIn, EntityPlayer player, BlockPos pos, IBlockState state,
			@Nullable TileEntity te, @Nullable ItemStack stack) {
		System.out.println("crack!");
		if(!player.capabilities.isCreativeMode) {
			List<ItemStack> dropItems = getDropItems(worldIn);
			for(ItemStack dropItem : dropItems) {
				spawnAsEntity(worldIn, pos, dropItem);
			}
		}
	}

	private List<ItemStack> getDropItems(World world) {
		List<ItemStack> dropItems = new ArrayList<ItemStack>();
		List<DropDimension> dropDimensions = RandomSupplies.config.getDropDimensions();
		for(DropDimension dropDimension : dropDimensions) {
			if(dropDimension.hasDimensionIds(world.provider.getDimension())) {
				List<DropItem> dropItemList = dropDimension.getDropItems();
				for(DropItem dropItem : dropItemList) {
					int amount = Utils.getAmount(dropItem.getProbability(), dropItem.getAmountRate(), dropItem.getMaxAmount());
					ItemStack dropItemStack = new ItemStack(dropItem.getItem(), amount, dropItem.getMetadata());
					dropItems.add(dropItemStack);
				}
			}
		}
		return dropItems;
	}

}
