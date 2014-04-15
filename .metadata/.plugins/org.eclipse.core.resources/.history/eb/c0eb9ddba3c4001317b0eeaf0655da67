package com.jonathan.survivor.components;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import com.badlogic.gdx.Gdx;

public class EntityManager 
{
	private int nextUnusedEntity = 0;
	
	private HashMap<Class, HashMap<Integer, ? extends Component>> componentMap;
	private HashMap<Integer, String> entityNames;
	
	private List<Integer> entities;
	
	public EntityManager()
	{
		componentMap = new HashMap<Class, HashMap<Integer, ? extends Component>>();
		entityNames = new HashMap<Integer, String>();
		
		entities = new ArrayList<Integer>();
	}
	
	public <T extends Component> T getComponent(int entity, Class<T> componentClass)
	{
		HashMap<Integer, ? extends Component> componentEntityMap = componentMap.get(componentClass);

		if(componentEntityMap == null)
		{
			System.out.println("EntityManager ERROR! No HashMap is registered to component " + componentClass);
			return null;
		}
		
		T entityComponent = (T) componentEntityMap.get(entity);
		
		if(entityComponent == null)
			System.out.println("EntityManager ERROR! Component " + componentClass + " doesn't exist for entity " + entity + ". Entity Name: " + entityNames.get(entity));
		
		return entityComponent;
	}
	
	public <T extends Component> void addComponent(int entity, T component)
	{
		HashMap<Integer, ? extends Component> componentEntityMap = componentMap.get(component.getClass());
		
		if(componentEntityMap == null)
		{
			componentEntityMap = new HashMap<Integer, T>();
			componentMap.put(component.getClass(), componentEntityMap);
		}
		
		((HashMap<Integer, T>)componentEntityMap).put(entity, component);
		
	}
	
	public <T extends Component> Collection<T> getAllComponents(Class<T> componentClass)
	{
		HashMap<Integer, ? extends Component> componentEntityMap = componentMap.get(componentClass);
		
		if(componentEntityMap == null)
			return new ArrayList<T>();
		
		return (Collection<T>)componentEntityMap.values();
	}
	
	public int createEntity()
	{
		int entity = nextUnusedEntity++;
		
		if(entity > Integer.MAX_VALUE)
			System.out.println("EntityManager ERROR! Creation of entity has surpassed max integer value. Handle this error inside EntityManager.createEntity() if it ever comes up.");
		
		entities.add(entity);
		
		return entity;
	}
	
	public int createEntity(String name)
	{
		int entity = nextUnusedEntity++;
		entityNames.put(entity, name);
		
		if(entity > Integer.MAX_VALUE)
			Gdx.app.error("Integer Overflow", "Creation of entity has surpassed max integer value. Handle this error inside EntityManager.createEntity() if it ever comes up.");
		
		entities.add(entity);
		
		return entity;
	}
	
	public void destroyEntity(int entity)
	{
		
		for(HashMap<Integer, ? extends Component> componentEntityMap : componentMap.values())
		{
			componentEntityMap.remove(entity);
		}
		
		entityNames.remove(entity);
		entities.remove(entity);
	}
}
