package com.jimdo.jjh4296.utils;

import net.minecraft.item.Item;

public class DropItem {
	Item item;
	int metadata;
	int maxAmount;
	double probability;
	double amountRate;
	
	public DropItem(Item item, int maxAmount, int metadata, double probability, double amountRate) {
		this.item = item;
		this.metadata = metadata;
		this.maxAmount = maxAmount;
		this.probability = probability;
		this.amountRate = amountRate;
	}

	public Item getItem() {
		return item;
	}
	
	public int getMetadata() {
		return metadata;
	}

	public int getMaxAmount() {
		return maxAmount;
	}

	public double getProbability() {
		return probability;
	}

	public double getAmountRate() {
		return amountRate;
	}
	
}