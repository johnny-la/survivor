package com.jonathan.survivor.inventory;

import java.util.HashMap;

/*
 * Holds all of the items belonging to the player.
 */

public class Inventory 
{
	/** Holds a map between an Item class and the amount of items of that class inside the inventory. */
	private HashMap<Class, Integer> itemMap; 
	
	/** Creates an empty inventory. */
	public Inventory()
	{
		//Populates the item map with a HashMap.
		itemMap = new HashMap<Class, Integer>();
	}
	
	/** Adds the Item of the given class inside the Inventory in the given quantity. */
	public <T extends Item> void addItem(Class<T> itemClass, int quantity)
	{	
		//If the entry for the item class is null
		if(itemMap.get(itemClass) == null)
			//Insert a new integer value for the item class key.
			itemMap.put(itemClass, 0);
		
		//Add the current amount of items in the inventory to the provided quantity.
		quantity += itemMap.get(itemClass);
		
		System.out.println("Inventory.addItem(): There are " + quantity + " " + itemClass + " inside the player's inventory.");
		
		//Insert the new quantity into the itemMap.
		itemMap.put(itemClass, quantity);
		
		//If the item has just been removed from the inventory
		if(quantity == 0)
			//Remove the reference to the item inside the inventory, so that the item is essentially gone from the inventory.
			itemMap.remove(itemClass);
	}
	
	/** Returns the quantity of items of the given class inside the inventory. */
	public int getQuantity(Class itemClass)
	{
		//If the entry for the item class is null
		if(itemMap.get(itemClass) == null)
			//Insert a new integer value for the item class key.
			itemMap.put(itemClass, 0);
		
		//Returns the value stored inside the itemClass key, which corresponds to the quantity of items for the given class inside the player's inventory.
		return itemMap.get(itemClass);
	}

	/** Clears all of the items stored in the inventory. */
	public void clear() 
	{
		//Removes every value inside the itemMap. Essentially clears the inventory.			
		itemMap.clear();
	}
	
	/** Returns the ItemMap which maps the Item classes to the amount of the item stored inside the inventory. */
	public HashMap<Class, Integer> getItemMap() {
		return itemMap;
	}

	/** Sets the ItemMap which maps the Item classes to the amount of the item stored inside the inventory. */
	public void setItemMap(HashMap<Class, Integer> itemMap) {
		this.itemMap = itemMap;
	}
	
}
