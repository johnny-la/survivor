package com.jonathan.survivor.inventory;

import com.jonathan.survivor.inventory.Item.Type;

public class Weapon extends Item
{
	
	/** The amount of damage done by the weapon with one hit. */
	protected float damage;
	
	/** Stores the slot in Spine where the image of the gun is attached. */
	private String slotName;
	/** Stores the name of the attachment image used to display the weapon. */
	private String attachmentName;
	
	/** Creates a weapon with the given name, description, and damage. */
	public Weapon(String name, String description, float damage)
	{
		super(Type.WEAPON, name, description);
		
		//Populates the member variables with their respective constructor arguments. 
		this.damage = damage;
	}

	/** Gets the amount of damage the weapon deals in one hit. */
	public float getDamage() {
		return damage;
	}
	
	/** Sets the amount of damage the weapon deals in one hit. */
	public void setDamage(float damage) {
		this.damage = damage;
	}

	/** Returns the slot name on the player in Spine where the weapon's image is attached. */
	public String getSlotName() {
		return slotName;
	}

	/** Sets the slot name on the player in Spine where the weapon's image is attached. */
	public void setSlotName(String slotName) {
		this.slotName = slotName;
	}

	/** Gets the name of the image (attachment) in Spine which displays the weapon. */
	public String getAttachmentName() {
		return attachmentName;
	}

	/** Sets the name of the image (attachment) used in Spine to display the weapon. */
	public void setAttachmentName(String attachmentName) {
		this.attachmentName = attachmentName;
	}
}