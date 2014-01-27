package com.jonathan.survivor.components;


public class Position implements Component
{
	public float x, y;
	
	public String toString()
	{
		return "(" + x + ", " + y + ")";
	}
}
