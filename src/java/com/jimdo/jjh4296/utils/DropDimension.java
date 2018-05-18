package com.jimdo.jjh4296.utils;

import java.util.ArrayList;
import java.util.List;

public class DropDimension {
	public long waitTick = 0;
	public long curTick = 0;
	public boolean isPlaced = false;

	public List<Integer> dimensionIds = new ArrayList<Integer>();
	public long spawnTimeTicks = 2000;
	public long despawnTimeTicks = 2000;
	public long spawnTime = 0;
	public long despawnTime = 12000;
	public boolean useTimeTicks = false;
	public long timeRange = 0;
	public int xLength = 1;
	public int zLength = 1;
	public int maxAmount = 1;
	public double probability = 100;
	public double amountRate = 0;
	String broadcastStr = "";
	List<DropItem> dropItems;
	
	public DropDimension(List<Integer> dimensionIds, long spawnTimeTicks, long despawnTimeTicks, long spawnTime, long despawnTime, boolean useTimeTicks, long timeRange, int xLength, int zLength, int maxAmount, double probability,
			double amountRate, String broadcastStr, List<DropItem> dropItems) {
		this.spawnTimeTicks = spawnTimeTicks;
		this.despawnTimeTicks = despawnTimeTicks;
		this.spawnTime = spawnTime;
		this.despawnTime = despawnTime;
		this.useTimeTicks = useTimeTicks;
		this.timeRange = timeRange;
		this.xLength = xLength;
		this.zLength = zLength;
		this.maxAmount = maxAmount;
		this.probability = probability;
		this.amountRate = amountRate;
		this.broadcastStr = broadcastStr;
		this.dropItems = dropItems;
		this.dimensionIds = dimensionIds;
	}
	
	public boolean hasDimensionIds(int dimensionId) {
		for(int id : dimensionIds) {
			if(id == dimensionId) return true;
		}
		return false;
	}
	
	public List<DropItem> getDropItems() {
		return dropItems;
	}
}
