package com.jonathan.survivor.hud;

import java.util.Map;

import com.badlogic.gdx.scenes.scene2d.ui.List;
import com.jonathan.survivor.inventory.Inventory;
import com.jonathan.survivor.inventory.Item.Type;
import com.jonathan.survivor.managers.ItemManager;

public class InventoryList 
{
	/** Stores the list where items are selected. */
	private List list;
	
	private Map<Class, Integer> workingItemMap;
	
	private String[] itemNames;
	
	public InventoryList(Type itemType, ItemManager itemManager, Inventory inventory)
	{
		//workingItemMap = inventory.getItemMap().entrySet();
		
		
		
	}
}
