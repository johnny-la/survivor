package com.jonathan.survivor.inventory;

import com.jonathan.survivor.entity.Tree;

public class Axe extends MeleeWeapon
{
	/** Stores the properties of the axe. */
	public static final String NAME = "Axe";
	public static final String DESCRIPTION = "A weapon for the mightiest of lumberjacks";
	public static final float DAMAGE = 1;
	public static final float RANGE = 1;
	
	public static final String ATTACHMENT_NAME = "Axe0002";	//Stores the name of the image on the player which stores the Axe.
	
	/** Creates an axe. */
	public Axe()
	{
		super(NAME, DESCRIPTION, DAMAGE, RANGE);
		
		//Sets the attachment name of the axe. This is the name of the image on the player used to display the axe.
		setAttachmentName(ATTACHMENT_NAME);
		
	}
	
	/** Called when the MeleeWeapon has hit a tree and should deal damage to it. */
	@Override 
	public void hitTree(Tree tree)
	{
		//Deal damage to the tree.
		tree.takeDamage(getDamage());
	}
}