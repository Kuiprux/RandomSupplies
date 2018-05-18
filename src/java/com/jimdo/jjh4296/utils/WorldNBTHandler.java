package com.jimdo.jjh4296.utils;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.WorldSavedData;

public class WorldNBTHandler extends WorldSavedData {
	private NBTTagCompound data = new NBTTagCompound();
	private NBTTagCompound data1 = new NBTTagCompound();
	
	public static final String CHUNK = "RSChunks";
	public static final String DROP = "RSDrops";

	public WorldNBTHandler(String tagName) {
        super(tagName);
    }

	@Override
	public void readFromNBT(NBTTagCompound compound) {
		data = compound.getCompoundTag(CHUNK);
		data1 = compound.getCompoundTag(DROP);
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		compound.setTag(CHUNK, data);
		compound.setTag(DROP, data1);
		return compound;
	}
	
	public void setChunks(String key, List<Pos> chunks) {
		int[] intArray = new int[chunks.size()*3];
		for(int i = 0; i < chunks.size(); i++) {
			Pos chunk = chunks.get(i);
			intArray[i*3] = chunk.x;
			intArray[i*3+1] = chunk.y;
			intArray[i*3+2] = chunk.z;
		}
		data.setIntArray(key, intArray);
	}
	
	public void clear(String key) {
		data.removeTag(key);
	}
	
	public List<Pos> getChunks(String key) {
		int[] intArray = data.getIntArray(key);
		List<Pos> chunks = new ArrayList<Pos>();
		for(int i = 0; i < intArray.length/3; i++) {
			Pos pos = new Pos(intArray[i*3], intArray[i*3+1], intArray[i*3+2]);
			chunks.add(pos);
		}
		return chunks;
	}
	
	public void addChunk(String key, Pos chunk) {
		int[] prevArray = data.getIntArray(key);
		int[] intArray = new int[prevArray.length + 3];
		
		for(int i = 0; i < prevArray.length; i++) {
			intArray[i] = prevArray[i];
		}
		intArray[prevArray.length] = chunk.x;
		intArray[prevArray.length+1] = chunk.y;
		intArray[prevArray.length+2] = chunk.z;
		data.setIntArray(key, intArray);
	}
	
	public void addChunks(String key, List<Pos> chunks) {
		int[] prevArray = data.getIntArray(key);
		int[] intArray = new int[prevArray.length + chunks.size()*3];
		
		for(int i = 0; i < prevArray.length; i++) {
			intArray[i] = prevArray[i];
		}
		for(int j = 0; j < chunks.size(); j++) {
			Pos chunk = chunks.get(j);
			intArray[prevArray.length+j*3] = chunk.x;
			intArray[prevArray.length+j*3+1] = chunk.y;
			intArray[prevArray.length+j*3+2] = chunk.z;
		}
		data.setIntArray(key, intArray);
	}
}