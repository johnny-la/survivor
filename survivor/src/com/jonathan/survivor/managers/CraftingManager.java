package com.jonathan.survivor.managers;

import com.badlogic.gdx.utils.Array;
import com.jonathan.survivor.Survivor;
import com.jonathan.survivor.inventory.Axe;
import com.jonathan.survivor.inventory.Bullet;
import com.jonathan.survivor.inventory.Charcoal;
import com.jonathan.survivor.inventory.Gunpowder;
import com.jonathan.survivor.inventory.Iron;
import com.jonathan.survivor.inventory.Rifle;
import com.jonathan.survivor.inventory.Saltpeter;
import com.jonathan.survivor.inventory.Sulfur;
import com.jonathan.survivor.inventory.Teleporter;
import com.jonathan.survivor.inventory.Water;
import com.jonathan.survivor.inventory.Wood;

public class CraftingManager 
{
	/** Holds the singleton instance to the CraftingManager. */
	public static final CraftingManager instance = new CraftingManager();
	
	/** Stores an array of all possible crafting combinations. */
	private Array<Combination> combinations;
	
	/** Holds all of the possible crafting combinations. */
	public Combination axe, rifle, gunpowder, bullet, teleporter;
	
	private CraftingManager()
	{
		//Creates the array which will hold all crafting combinations.
		combinations = new Array<Combination>();
		
		//Creates all of the different crafting combinations in the game.
		axe = new Combination();
		if(!Survivor.DEBUG_MODE)
		{
			axe.addItem(Iron.class, 5);
			axe.addItem(Wood.class, 10);
		}
		else
		{
			axe.addItem(Sulfur.class, 1);
		}
		axe.setResult(Axe.class, 1);
		
		rifle = new Combination();
		if(!Survivor.DEBUG_MODE)
		{
			rifle.addItem(Iron.class, 10);
			rifle.addItem(Wood.class, 15);
		}
		else
		{
			rifle.addItem(Charcoal.class, 1);
		}
		rifle.setResult(Rifle.class, 1);
		
		gunpowder = new Combination();
		if(!Survivor.DEBUG_MODE)
		{
			gunpowder.addItem(Saltpeter.class, 12);
			gunpowder.addItem(Charcoal.class, 8);
			gunpowder.addItem(Sulfur.class, 6);
			gunpowder.addItem(Water.class, 4);
		}
		else
		{
			gunpowder.addItem(Water.class, 1);
		}
		gunpowder.setResult(Gunpowder.class, 12);
		
		bullet = new Combination();
		if(!Survivor.DEBUG_MODE)
		{
			bullet.addItem(Iron.class, 2);
			bullet.addItem(Gunpowder.class, 4);
		}
		else
		{
			bullet.addItem(Gunpowder.class, 4);
		}
		bullet.setResult(Bullet.class, 6);
		
		teleporter = new Combination();
		if(!Survivor.DEBUG_MODE)
		{
			teleporter.addItem(Saltpeter.class, 40);
			teleporter.addItem(Wood.class, 50);
			teleporter.addItem(Sulfur.class, 40);
			teleporter.addItem(Iron.class, 30);
		}
		else
		{
			teleporter.addItem(Wood.class, 1);
		}
		teleporter.setResult(Teleporter.class, 1);
		
		//Adds all of the combinations into an array
		combinations.add(axe);
		combinations.add(rifle);
		combinations.add(gunpowder);
		combinations.add(bullet);
		combinations.add(teleporter);
	}
	
	/** Returns the resulting item crafted using the given array of items. If null, no result is formed using the given list of items. */
	public Item getResult(Array<Item> items)
	{
		//Cycles through the list of all possible combinations.
		for(int i = 0; i < combinations.size; i++)
		{
			//If the combination matches the list of given items
			if(combinations.get(i).validItems(items))
				//Return the result of the combination which is formed by the given items.
				return combinations.get(i).getResult();
		}
		
		//If this statement is reached, the items don't form any possible combination. Thus, return null.
		return null;
	}
	
	/** Describes a combination of items which together craft an item. */
	private class Combination
	{		
		/** Stores the item which results from the combination. */
		private Item result;
		
		/** The array of items which must be combined to form the above the resulting item. */
		private Array<Item> items;
		
		/** Creates a new, empty combination. */
		public Combination()
		{
			items = new Array<Item>();
		}
		
		/** Adds a specific item to the combination. */
		public void addItem(Class item, int quantity)
		{
			//Adds the item to the list of items in the combination.
			items.add(new Item(item, quantity));
		}

		/** Tests if the given list of items corresponds to the items needed in this combination. Returns true if the given list of items form this combination. */
		public boolean validItems(Array<Item> givenItems)
		{
			//If the amount of given items is not the same as the amount of items in the combination, the given items don't form this combination. Thus, return false.
			if(givenItems.size != items.size)
				return false;
			
			//Cycles through the given items 
			for(int i = 0; i < givenItems.size; i++)
			{
				//If the given item is not contained in this combination, return false
				if(!this.contains(givenItems.get(i)))
				{
					//Return false, since the given list of items don't form this combination.
					return false;
				}
			}
			
			//If this statement is reached, the given items produce the combination's result. So, return true.
			return true;
		}
		
		/** Returns true if this combination contains this item, which is needed to form this combination.	*/
		public boolean contains(Item item)
		{
			//Cycles through all the items in the combination
			for(int i = 0; i < items.size; i++)
			{
				//If the given item equals to an element in this combination
				if(items.get(i).equals(item))
					//Return true, since this combination contains the given item
					return true;
			}
			
			//If this statement is reached, the item is not contained in this combination. Thus, return false.
			return false;
		}
		
		/** Sets the resulting item from the crafting combination */
		public Item getResult() {
			return result;
		}

		/** Gets the resulting item from the crafting combination */
		public void setResult(Class item, int quantity) {
			this.result = new Item(item, quantity);
		}
	}
	
	/** Pairs an item with a specific quantity for use inside a Combination. */
	public class Item
	{
		private int quantity;	//Stores the quantity of the item needed in a combination
		private Class item;	//Holds the item needed in a combination
		
		/** Creates a pair between an item and a specific quantity. */
		public Item(Class item, int quantity)
		{
			this.item = item;
			this.quantity = quantity;
		}
		
		/** Adds the given quantity to this item instance. */
		public void add(int quantity)
		{
			//Increments the quantity integer by the given amount.
			this.quantity += quantity;
		}
		
		//Returns true if the given item is equal to this item.
		public boolean equals(Item other)
		{
			return other.item.equals(this.item) && other.quantity == this.quantity;
		}
		
		/** Gets the item class held by this instance. */
		public Class getItem() {
			return item;
		}
		
		/** Sets the item class held by this instance. */
		public void setItem(Class item) {
			this.item = item;
		}

		/** Gets the quantity of the item. */
		public int getQuantity() {
			return quantity;
		}
		
		/** Sets the quantity of the item. */
		public void setQuantity(int quantity) {
			this.quantity = quantity;
		}
		
		public String toString()
		{
			return item + ": " + quantity;
		}
	}
}
