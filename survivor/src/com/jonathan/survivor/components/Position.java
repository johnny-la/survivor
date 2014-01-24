package com.jonathan.survivor.components;

import com.jonathan.survivor.entity.Component;

public class Position implements Component
{
	public float x, y;
	
	public String toString()
	{
		return "(" + x + ", " + y + ")";
	}
}
