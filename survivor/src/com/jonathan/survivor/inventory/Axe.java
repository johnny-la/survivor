package com.jonathan.survivor.inventory;

import com.jonathan.survivor.entity.Tree;

public class Axe extends MeleeWeapon
{
	public static String NAME = "Axe";
	public static String DESCRIPTION = "A weapon for the mightiest of lumberjacks";
	public static float DAMAGE = 1;
	public static float RANGE = 1;
	
	/** Creates an axe. */
	public Axe()
	{
		super(NAME, DESCRIPTION, DAMAGE, RANGE);
	}
	
	/** Called when the MeleeWeapon has hit a tree and should deal damage to it. */
	@Override 
	public void hitTree(Tree tree)
	{
		//Deal damage to the tree.
		tree.takeDamage(getDamage());
	}
}
