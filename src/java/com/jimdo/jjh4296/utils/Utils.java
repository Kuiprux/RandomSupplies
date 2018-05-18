package com.jimdo.jjh4296.utils;

import java.util.Random;

public class Utils {

	static Random random = new Random();
	
	public static int getAmount(double probability, double amountRate, int maxAmount) {
		double rand = random.nextDouble();
		if (rand * 100 > probability)
			return 0;
		if (amountRate == 0)
			return maxAmount;
		int amount = 0;
		for (int i = 0; i < maxAmount; i++) {
			int rand1 = random.nextInt((int) Math.pow(amountRate, maxAmount) - 1);
			if (rand1 <= Math.pow(amountRate, i))
				amount++;
		}
		return amount;
	}
}
