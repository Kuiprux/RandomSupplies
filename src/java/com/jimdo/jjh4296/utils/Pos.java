package com.jimdo.jjh4296.utils;

public class Pos {
	public int x;
	public int y;
	public int z;

	public Pos(int x, int y, int z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public boolean equals(int x, int y, int z) {
		return this.x == x && this.y == y && this.z == z;
	}
}