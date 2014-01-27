package com.jonathan.survivor.entity;

import com.jonathan.survivor.inventory.Item;

public class ItemObject extends GameObject
{
	/** Stores the Item held by the GameObject. */
	private Item item;
	
	public ItemObject(int x, int y)
	{
		//super(x, y, , );
	}

	/** Gets the item contained by the GameObject. */
	public Item getItem() {
		return item;
	}

	/** Sets the item the GameObject represents. */
	public void setItem(Item item) {
		this.item = item;
	}

	@Override
	public void update(float deltaTime) {
		// TODO Auto-generated method stub
		
	}

	/** An ItemObject can never be targetted. */
	@Override
	public boolean canTarget() {
		//Return false as an ItemObject can't be targetted.
		return false;
	}
}
