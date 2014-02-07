package com.jonathan.survivor.managers;

import java.util.HashMap;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.badlogic.gdx.utils.reflect.Constructor;
import com.badlogic.gdx.utils.reflect.ReflectionException;
import com.jonathan.survivor.inventory.Item;
import com.jonathan.survivor.inventory.Wood;

public class ItemManager
{
	/** Holds a HashMap where every item subclass is a key, and its value is a pool of items of that class. */
	private HashMap<Class, Pool<Item>> itemPools;
	
	/** Creates an itemManager which provides access to Item instances. Efficient, as uses pools. */
	public ItemManager()
	{
		//Creates a new HashMap to hold a pools items for each Item subclass.
		itemPools = new HashMap<Class, Pool<Item>>();
	}
	
	/** Obtains an Item instance of the given class. */
	public <T extends Item> T obtain(Class<T> itemClass)
	{
		//If no pool exists for the class
		if(itemPools.get(itemClass) == null)
		{
			//Create a new item pool which stores items of the given class. Stores it in the correct key.
			itemPools.put(itemClass, new ItemPool(itemClass));
		}
		
		//Obtain an Item instance for the correct class in the HashMap, and return it.
		return (T) itemPools.get(itemClass).obtain();
	}
	
	/** Stores a pool of Item subclass. The Item subclass stored in this pool is determined by the argument passed into the constructor of this class. */
	class ItemPool extends Pool<Item>
	{
		/** Stores the constructor used to create instances of the item in the pool. */
		private Constructor itemConstructor;
		
		/** Creates a pool of items for the given class. */
		public ItemPool(Class itemClass)
		{
			//Grabbing the constructor for the ItemClass may cause exceptions.
			try 
			{
				//Grab the empty contructor of the Item's class using ClassReflection.getConstructor(java.lang.Class, args):Constructor.
				itemConstructor = ClassReflection.getConstructor(itemClass, null);
			} 
			catch (ReflectionException e) 
			{
				e.printStackTrace();
			}
		}
		
		/** Returns a new instance of the Item when none are free in the pool. */
		@Override
		protected Item newObject() 
		{
			try 
			{
				//Return a new instance of an Item using the correct constructor for the class provided in the constructor.
				return (Item) itemConstructor.newInstance();
			}
			catch (ReflectionException e) 
			{
				Gdx.app.error("Item Pool", "Error creating an item inside the an item pool in class ItemPool.ItemInnerPool.newObject()");
				e.printStackTrace();
			}
			
			return null;
		}
		
	}
}
