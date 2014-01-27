package com.jonathan.survivor.entity;

public interface PlayerListener 
{
	/** Delegates when an Interactive GameObject has been scavenged by the player. */
	void scavengedObject(InteractiveObject object);
}
