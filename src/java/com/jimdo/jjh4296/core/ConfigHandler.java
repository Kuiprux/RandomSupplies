package com.jimdo.jjh4296.core;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import com.jimdo.jjh4296.utils.DropDimension;
import com.jimdo.jjh4296.utils.DropItem;

import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;

/*
 * supply [{
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
  }]
 */

public class ConfigHandler {

	List<DropDimension> dropDimensions;
	
	public List<DropDimension> getDropDimensions() {
		return dropDimensions;
	}
	
	public void init() {
		try {
			File file = new File("config/RandomSupply.cfg");
			if (!file.exists()) {
				String content = getDummyContent();
				writeFile(file, content);
			}
			String content = readFile(file);
			parse(content);
		} catch (IOException e) {

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public String getDummyContent() {
		Gson gson = new Gson();
		JsonObject mainObject = new JsonObject();
		JsonArray supplyArray = new JsonArray();
		JsonObject supplyObject = new JsonObject();
		JsonArray dimensionArray = new JsonArray();
		JsonPrimitive overworld = new JsonPrimitive(0);
		dimensionArray.add(overworld);
		supplyObject.add("dimensionIds", dimensionArray);
		supplyObject.addProperty("spawnTimeTicks", 2000);
		supplyObject.addProperty("despawnTimeTicks", 2000);
		supplyObject.addProperty("spawnTime", 8000);
		supplyObject.addProperty("despawnTime", 18000);
		supplyObject.addProperty("useTimeTick", false);
		supplyObject.addProperty("timeRange", 1);
		supplyObject.addProperty("xLength", 1);
		supplyObject.addProperty("zLength", 1);
		supplyObject.addProperty("maxAmount", 1);
		supplyObject.addProperty("probability", 100);
		supplyObject.addProperty("amountRate", 0);
		supplyObject.addProperty("broadcast", "It looks like something appeared at %s..");
		JsonArray itemArray = new JsonArray();
		JsonObject anItem = new JsonObject();
		anItem.addProperty("itemName", "minecraft:diamond");
		anItem.addProperty("maxAmount", 1);
		anItem.addProperty("probability", 100);
		anItem.addProperty("amountRate", 1);
		itemArray.add(anItem);
		supplyObject.add("items", itemArray);
		supplyArray.add(supplyObject);
		mainObject.add("supply", supplyArray);
		return gson.toJson(mainObject);
	}
	
	public void writeFile(File file, String str) throws IOException {
		BufferedWriter bw = new BufferedWriter(new FileWriter(file, false));
		bw.write(str);
		bw.flush();
		bw.close();
	}
	
	public String readFile(File file) throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(file));
		StringBuilder sb = new StringBuilder();
		
		String str;
		while((str = br.readLine()) != null) {
			sb.append(str);
		}
		
		br.close();
		return sb.toString();
	}
	
	public void parse(String str) {
		dropDimensions = new ArrayList<DropDimension>();
		
		JsonParser parser = new JsonParser();
		JsonElement element = parser.parse(str);
		JsonArray rootArray = element.getAsJsonObject().get("supply").getAsJsonArray();
		for(JsonElement aSupply : rootArray) {
			JsonObject rootObject = aSupply.getAsJsonObject();

			List<Integer> dimensionIds = new ArrayList<Integer>();
			long spawnTimeTicks = 2000;
			long despawnTimeTicks = 2000;
			long spawnTime = 0;
			long despawnTime = 12000;
			boolean useTimeTicks = false;
			long timeRange = 0;
			int xLength = 1;
			int zLength = 1;
			int maxAmount = 1;
			double probability = 100;
			double amountRate = 0;
			String broadcastStr = "";
			List<DropItem> dropItems = new ArrayList<DropItem>();
			if (!rootObject.has("dimensionIds")) {
				System.out.println("\'dimensionIds\' is missing.");
				continue;
			}
			JsonArray dimensionArray = rootObject.getAsJsonArray("dimensionIds");
			for(JsonElement dimension : dimensionArray) {
				dimensionIds.add(dimension.getAsInt());
			}
			if (rootObject.has("useTimeTicks")) {
				useTimeTicks = rootObject.get("useTimeTicks").getAsBoolean();
			}
			if(useTimeTicks && !rootObject.has("spawnTimeTicks") && !rootObject.has("despawnTimeTicks")) {
				continue;
			}
			if(!useTimeTicks && !rootObject.has("spawnTime") && !rootObject.has("despawnTime")) {
				continue;
			}
			long stTc = rootObject.get("spawnTime").getAsLong();
			if (0 <= stTc)
				spawnTimeTicks = stTc;
			long dtTc = rootObject.get("despawnTime").getAsLong();
			if (0 <= dtTc)
				despawnTimeTicks = dtTc;
			if (rootObject.has("spawnTime")) {
				long spTm = rootObject.get("spawnTime").getAsLong();
				if (0 <= spTm && spTm < 24000)
					spawnTime = spTm;
			}
			if (rootObject.has("despawnTime")) {
				long dsTm = rootObject.get("despawnTime").getAsLong();
				if (0 <= dsTm && dsTm < 24000)
					despawnTime = dsTm;
			}
			if (rootObject.has("timeRange")) {
				long tmRn = rootObject.get("timeRange").getAsLong();
				if (0 < tmRn)
					timeRange = tmRn;
			}
			if (rootObject.has("maxAmount")) {
				int am = rootObject.get("maxAmount").getAsInt();
				if (0 <= am)
					maxAmount = am;
			}
			if (rootObject.has("xLength")) {
				int xL = rootObject.get("xLength").getAsInt();
				if (0 < xL)
					maxAmount = xL;
			}
			if (rootObject.has("zLength")) {
				int yL = rootObject.get("zLength").getAsInt();
				if (0 < yL)
					maxAmount = yL;
			}
			if (rootObject.has("probability")) {
				double prob = rootObject.get("probability").getAsDouble();
				if (0 <= prob && prob <= 100)
					probability = prob;
			}
			if (rootObject.has("amountRate")) {
				double amRt = rootObject.get("amountRate").getAsDouble();
				if (0 <= amRt)
					amountRate = amRt;
			}
			if (rootObject.has("broadcast")) {
				broadcastStr = rootObject.get("broadcast").getAsString();
			}
			if (rootObject.has("items")) {
				JsonArray itemLists = rootObject.getAsJsonArray("items");
				for (JsonElement anElement : itemLists) {
					JsonObject anObject = anElement.getAsJsonObject();
					Item item = null;
					int metadata = 0;
					int maxItemAmount = 1;
					double itemProbability = 100;
					double itemAmountRate = 1;
					if (anObject.has("name")) {
						String itemName = anObject.get("name").getAsString();
						ResourceLocation resourcelocation = new ResourceLocation(itemName);
						item = (Item) Item.REGISTRY.getObject(resourcelocation);
						if (item == null)
							continue;
					}
					if (anObject.has("metadata")) {
						int md = anObject.get("metadata").getAsInt();
						if (0 <= md)
							metadata = md;
					}
					if (anObject.has("maxAmount")) {
						int am = anObject.get("maxAmount").getAsInt();
						if (0 <= am)
							maxItemAmount = am;
					}
					if (anObject.has("probability")) {
						double prob = anObject.get("probability").getAsDouble();
						if (0 <= prob && prob <= 100)
							itemProbability = prob;
					}
					if (anObject.has("amountRate")) {
						double amRt = anObject.get("amountRate").getAsDouble();
						if (0 <= amRt)
							itemAmountRate = amRt;
					}
					dropItems.add(new DropItem(item, metadata, maxItemAmount, itemProbability, itemAmountRate));
				}
			}
			dropDimensions.add(new DropDimension(dimensionIds, spawnTimeTicks, despawnTimeTicks, spawnTime, despawnTime, useTimeTicks, timeRange, xLength, zLength, maxAmount, probability,
			amountRate, broadcastStr, dropItems));
		}
	}
}