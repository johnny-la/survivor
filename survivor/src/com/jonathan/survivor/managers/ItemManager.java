package com.jonathan.survivor.managers;

import java.util.HashMap;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.badlogic.gdx.utils.reflect.Constructor;
import com.badlogic.gdx.utils.reflect.ReflectionException;
import com.jonathan.survivor.Assets;
import com.jonathan.survivor.inventory.Axe;
import com.jonathan.survivor.inventory.Bullet;
import com.jonathan.survivor.inventory.Charcoal;
import com.jonathan.survivor.inventory.Gunpowder;
import com.jonathan.survivor.inventory.Iron;
import com.jonathan.survivor.inventory.Item;
import com.jonathan.survivor.inventory.Rifle;
import com.jonathan.survivor.inventory.Saltpeter;
import com.jonathan.survivor.inventory.Sulfur;
import com.jonathan.survivor.inventory.Teleporter;
import com.jonathan.survivor.inventory.Water;
import com.jonathan.survivor.inventory.Wood;

public class ItemManager
{
	/** Holds a HashMap where every item subclass is a key, and its value is a pool of items of that class. */
	private HashMap<Class, Pool<Item>> itemPools;
	
	/** Holds a HashMap where every item subclass is a key, and its value is a pool of inventory sprites for that item. */
	private HashMap<Class, Pool<Sprite>> itemSpritePools;
	
	/** Stores the universal assets singleton used to retrieve Sprite templates for each item. */
	private Assets assets = Assets.instance;
	
	/** Creates an itemManager which provides access to Item instances. Efficient, as it uses pools. */
	public ItemManager()
	{
		//Creates a new HashMap to hold a pools items for each Item subclass.
		itemPools = new HashMap<Class, Pool<Item>>();
		
		//Creates a new HashMap to hold a pool of sprites for each Item subclass.
		itemSpritePools = new HashMap<Class, Pool<Sprite>>();
	}
	
	/** Obtains an Item instance of the given class. */
	public <T extends Item> T obtainItem(Class<T> itemClass)
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
	
	/** Frees an Item instance back into the manager's internal pools for later reuse. */
	public void freeItem(Item item)
	{
		//Frees the item instance back inside a pool. Placed inside the pool which is stored as a value for the key of the item's class.
		itemPools.get(item.getClass()).free(item);
	}
	
	/** Obtains an Sprite instance of the given class to display in an inventory. */
	public <T extends Item> Sprite getSprite(Class<T> itemClass)
	{
		//If no pool exists for the item subclass
		if(itemSpritePools.get(itemClass) == null)
		{
			//Create a new sprite pool which stores inventory sprites for the given item.
			itemSpritePools.put(itemClass, new SpritePool(itemClass));
		}
		
		//Obtain a Sprite instance for the given class key in the HashMap, and return it.
		return itemSpritePools.get(itemClass).obtain();
	}
	
	/** Frees the specified sprite instance back inside an internal pool for later reuse. */
	public <T extends Item> void freeSprite(Sprite sprite, Class<T> itemClass)
	{
		//Frees the sprite back into the correct pool to be reused later. The sprite pools are stored inside the itemSpritePools HashMap.
		itemSpritePools.get(itemClass).free(sprite);
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
	
	/** Stores a pool of sprites for each item. The sprite displays the item passed as an argument to the constructor. */
	class SpritePool extends Pool<Sprite>
	{
		/** Stores an item subclass. This is the item for which sprites will be produced. These sprites represent the inventory sprites displayed for this item. */
		private Class itemClass;
		
		/** Creates a pool of items for the given class. */
		public SpritePool(Class itemClass)
		{
			//Stores the item subclass whose Sprites are pooled.
			this.itemClass = itemClass;
		}
		
		/** Returns a new inventory sprite for the item when none are already free in the pool. */
		@Override
		protected Sprite newObject() 
		{
			//Stores the template sprite for the item represented by this pool. A copy of this template will be returned.
			Sprite templateSprite = null;
			
			//Checks which itemClass this pool holds. Sets the template sprite to the one corresponding to the correct item subclass.
			if(itemClass.equals(Wood.class))
				templateSprite = assets.woodSprite;
			else if(itemClass.equals(Iron.class))
				templateSprite = assets.ironSprite;
			else if(itemClass.equals(Water.class))
				templateSprite = assets.waterSprite;
			else if(itemClass.equals(Charcoal.class))
				templateSprite = assets.charcoalSprite;
			else if(itemClass.equals(Saltpeter.class))
				templateSprite = assets.saltpeterSprite;
			else if(itemClass.equals(Sulfur.class))
				templateSprite = assets.sulfurSprite;
			else if(itemClass.equals(Gunpowder.class))
				templateSprite = assets.gunpowderSprite;
			else if(itemClass.equals(Bullet.class))
				templateSprite = assets.bulletSprite;
			else if(itemClass.equals(Teleporter.class))
				templateSprite = assets.teleporterSprite;
			else if(itemClass.equals(Axe.class))
				templateSprite = assets.axeSprite;
			else if(itemClass.equals(Rifle.class))
				templateSprite = assets.rifleSprite;

			//Creates a new copy of the template sprite.
			Sprite itemSprite = new Sprite(templateSprite);
			
			//Scales down the item sprite according to the size of the screen. Like this, the sprites are the same size no matter which atlas size was chosen.
			itemSprite.setSize(itemSprite.getWidth()/assets.scaleFactor, itemSprite.getHeight()/assets.scaleFactor);
			
			//Returns the new item sprite.
			return itemSprite;
			
		}
		
	}
}
