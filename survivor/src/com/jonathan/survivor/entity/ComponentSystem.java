package com.jonathan.survivor.entity;

/*
 * Every system implements this interface. Systems are meant to use the data inside components in order to control
 * an entity. Each system defines different behaviours for an entity.
 */

public interface ComponentSystem 
{
	/** Called every frame to control the behaviour of components. */
	void update(float deltaTime);
}
