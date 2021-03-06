package com.jonathan.survivor.inventory;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.jonathan.survivor.Assets;
import com.jonathan.survivor.entity.Tree;

public class Axe extends MeleeWeapon
{
	/** Stores the properties of the axe. */
	public static final String NAME = "Axe";
	public static final String DESCRIPTION = "A weapon for the mightiest of lumberjacks";
	public static final Sprite INVENTORY_SPRITE = Assets.instance.axeSprite;
	public static final float DAMAGE = 40;
	public static final float REACH = 1;
	
	public static final String WEAPON_ATTACHMENT_NAME = "Axe0002";	//Stores the name of the image placed on the player in Spine which displays the Axe.
	
	/** Creates an axe. */
	public Axe()
	{
		super(NAME, DESCRIPTION, DAMAGE, REACH);
		
		//Sets the attachment name of the axe. This is the image on the player in Spine which displays the axe.
		setWeaponAttachment(WEAPON_ATTACHMENT_NAME);
		
	}
	
	/** Called when the MeleeWeapon has hit a tree and should deal damage to it. */
	@Override 
	public void hitTree(Tree tree)
	{
		//Deal damage to the tree.
		tree.takeDamage(getDamage());
	}
}
