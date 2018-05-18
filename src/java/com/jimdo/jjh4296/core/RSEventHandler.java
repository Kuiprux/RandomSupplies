package com.jimdo.jjh4296.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import com.jimdo.jjh4296.blocks.BlockSupplies;
import com.jimdo.jjh4296.blocks.RSBlocks;
import com.jimdo.jjh4296.utils.DropDimension;
import com.jimdo.jjh4296.utils.Pos;
import com.jimdo.jjh4296.utils.Utils;
import com.jimdo.jjh4296.utils.WorldNBTHandler;

import net.minecraft.block.Block;
import net.minecraft.block.BlockBush;
import net.minecraft.block.BlockDeadBush;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.BlockSnow;
import net.minecraft.block.BlockTallGrass;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.ChunkProviderServer;
import net.minecraftforge.event.world.ChunkDataEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ServerTickEvent;
import net.minecraftforge.oredict.OreDictionary;
import scala.util.Random;

public class RSEventHandler {
	/*
	 * supply {
	  time: Æ½ ¼ö
	  timeRange: Æ½ ¼ö, ·£´ý ¹üÀ§.
	  maxAmount: °³¼ö
	  probability: È®·ü
	  amountRate: °³¼ö È®·üÀ» °áÁ¤. n^0 : n^1 : n^2...
	  items: [{
	    name: ¾ÆÀÌÅÛÄÚµå¸í
	    maxAmount: °³¼ö
	    probability: È®·ü
	    amountRate: °³¼ö È®·üÀ» °áÁ¤. n^0 : n^1 : n^2...
	  }]
	}
	 */
	Random random = new Random();
	List<ItemStack> leaves = OreDictionary.getOres("treeLeaves");
	List<ItemStack> woods = OreDictionary.getOres("treeWood");

	List<DropDimension> dropDimensions = new ArrayList<DropDimension>();

	public RSEventHandler(List<DropDimension> dropDimensions) {
		this.dropDimensions = dropDimensions;
	}
	
	boolean isWorldLoaded = false;
	boolean isSupplying = false;
	
	
	@SubscribeEvent
	public void onChunkLoad(ChunkDataEvent.Load event) {
		Chunk chunk = event.getChunk();
		if(!isWorldLoaded || isSupplying) return;
		System.out.println("Loading!");
		List<Pos> chunks = RandomSupplies.nbtHandler.getChunks(WorldNBTHandler.CHUNK);
		List<DropDimension> dropDimensions = RandomSupplies.config.getDropDimensions();
		for (DropDimension dropDimension : dropDimensions) {
			World world = event.getWorld();
			if (dropDimension.hasDimensionIds(world.provider.getDimension())) {
				int x = Math.floorDiv(chunk.xPosition, dropDimension.xLength);
				int y = -1;
				int z = Math.floorDiv(chunk.zPosition, dropDimension.zLength);
				for (Pos aChunk : chunks) {
					if (aChunk.equals(x, y, z))
						return;
				}
				if (y == -1) {
					System.out.println("New Place! " + chunk.xPosition + ", " + chunk.zPosition);
					isSupplying = true;
					dropOne(world, dropDimension, new Pos(x, y, z));
					System.out.println("Dropping Attempt!");
					isSupplying = false;
				}

			}
		}
	}

	@SubscribeEvent
	public void onServerTick(ServerTickEvent e) {
		for (DropDimension dropDimension : dropDimensions) {
			if (dropDimension.useTimeTicks) {
				dropDimension.curTick++;
				if (dropDimension.curTick >= dropDimension.waitTick) {
					resetTick(dropDimension);
					for (int id : dropDimension.dimensionIds) {
						World world = FMLCommonHandler.instance().getMinecraftServerInstance()
								.worldServerForDimension(id);
						if (dropDimension.isPlaced) {
							List<Pos> drops = RandomSupplies.nbtHandler.getChunks(WorldNBTHandler.DROP);
							remove(drops, world);
							dropDimension.isPlaced = true;
						} else {
							supply(dropDimension, world);
							dropDimension.isPlaced = false;
						}
					}
				}
			} else {
				for (int id : dropDimension.dimensionIds) {
					World world = FMLCommonHandler.instance().getMinecraftServerInstance().worldServerForDimension(id);
					long time = world.getWorldTime();
					boolean isSpawnTimeGreater = dropDimension.spawnTime > dropDimension.despawnTime;
					long min = isSpawnTimeGreater ? dropDimension.despawnTime : dropDimension.spawnTime;
					long max = isSpawnTimeGreater ? dropDimension.spawnTime : dropDimension.despawnTime;
					if (dropDimension.isPlaced && isInRange(time, min, max, isSpawnTimeGreater)) {
						List<Pos> drops = RandomSupplies.nbtHandler.getChunks(WorldNBTHandler.DROP);
						remove(drops, world);
						dropDimension.isPlaced = false;
					} else if (!dropDimension.isPlaced && isInRange(time, min, max, !isSpawnTimeGreater)) {
						supply(dropDimension, world);
						dropDimension.isPlaced = true;
					}
				}
			}
		}
	}

	private boolean isInRange(long num, long min, long max, boolean isInside) {
		if (isInside) {
			return min <= num && num < max;
		}
		return num < min || max <= num;
	}

	private void supply(DropDimension dropDimension, World world) {
		List<Pos> availableChunks = getAvailableChunks(world, dropDimension.xLength, dropDimension.zLength);
		drop(world, dropDimension, availableChunks);
	}

	private void remove(List<Pos> drops, World world) {
		for(Pos drop : drops) {
			BlockPos blockPos = new BlockPos(drop.x, drop.y, drop.z);
			if(world.getBlockState(blockPos).getBlock() instanceof BlockSupplies) {
				world.setBlockToAir(blockPos);
			}
		}
		RandomSupplies.nbtHandler.clear(WorldNBTHandler.CHUNK);
		RandomSupplies.nbtHandler.clear(WorldNBTHandler.DROP);
		RandomSupplies.nbtHandler.markDirty();
	}

	private List<Pos> getAvailableChunks(World world, int xLength, int zLength) {
		ChunkProviderServer provider = (ChunkProviderServer) world.getChunkProvider();
		Collection<Chunk> chunks = provider.getLoadedChunks();
		List<Pos> availableChunks = new ArrayList<Pos>();

		for (Chunk chunk : chunks) {
			availableChunks.add(new Pos(Math.floorDiv(chunk.xPosition, xLength), -1, Math.floorDiv(chunk.zPosition, zLength)));
		}
		return availableChunks;
	}

	private void resetTick(DropDimension dimension) {
		dimension.curTick = 0;
		long baseTick;
		if (dimension.isPlaced)
			baseTick = dimension.despawnTimeTicks;
		else
			baseTick = dimension.spawnTimeTicks;
		dimension.waitTick = baseTick + ThreadLocalRandom.current().nextLong(-dimension.timeRange, dimension.timeRange);
	}
	
	public void dropOne(World world, DropDimension dropDimension, Pos chunk) {
		int supplyAmount = Utils.getAmount(dropDimension.probability, dropDimension.amountRate, dropDimension.maxAmount);
		int rootX = chunk.x;
		int rootZ = chunk.z;
		supply: for (int j = 0; j < supplyAmount; j++) {
			int x = rootX * 16 * dropDimension.xLength + random.nextInt(16 * dropDimension.xLength);
			int z = rootZ * 16 * dropDimension.zLength + random.nextInt(16 * dropDimension.zLength);
			int placeY = -1;

			System.out.println(x + " " + z);
			boolean isBlack = false;
			yLoop: for (int i = 255; i >= 0; i--) {
				Block block = world.getBlockState(new BlockPos(x, i, z)).getBlock();
				if (block instanceof BlockDeadBush || block instanceof BlockTallGrass || block instanceof BlockSnow) {
					placeY = i;
					continue;
				}
				if (block instanceof BlockLiquid || block instanceof BlockBush || block instanceof BlockSupplies) {
					continue supply;
				}
				if (block.equals(Blocks.AIR)) {
					placeY = i;
					isBlack = false;
				} else {
					for (ItemStack leaf : leaves) {
						if (Item.getItemFromBlock(block) != null
								&& Item.getItemFromBlock(block).equals(leaf.getItem())) {
							isBlack = true;
							continue yLoop;
						}
					}
					for (ItemStack wood : woods) {
						if (Item.getItemFromBlock(block) != null
								&& Item.getItemFromBlock(block).equals(wood.getItem())) {
							isBlack = true;
							continue yLoop;
						}
					}
					break;
				}
			}
			if(!isBlack) {
				Pos pos = new Pos(x, placeY, z);
				RandomSupplies.nbtHandler.addChunk(WorldNBTHandler.CHUNK, chunk);
				RandomSupplies.nbtHandler.addChunk(WorldNBTHandler.DROP, pos);
				RandomSupplies.nbtHandler.markDirty();
				world.setBlockState(new BlockPos(x, placeY, z), RSBlocks.blockSupply.getDefaultState());
				System.out.println(x + " " + placeY + " " + z);
			}
		}
	}

	public void drop(World world, DropDimension dropDimension, List<Pos> availableChunks) {
		for (Pos chunkPos : availableChunks) {
			dropOne(world, dropDimension, chunkPos);
		}
	}
}
