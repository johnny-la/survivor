package com.jonathan.survivor.inventory;


public class Loadout 
{
	/** Stores the MeleeWeapon held in the loadout. */
	private MeleeWeapon meleeWeapon;
	
	/** Holds the RangedWeapon equipped by the player. */
	private RangedWeapon rangedWeapon;
	
	/** Creates an empty loadout with nothing inside it. */
	public Loadout()
	{
	}
	
	/** Creates a loadout with the given items. */
	public Loadout(MeleeWeapon meleeWeapon)
	{
		this.meleeWeapon = meleeWeapon;
	}

	/** Gets the MeleeWeapon held in the loadout. */
	public MeleeWeapon getMeleeWeapon() {
		return meleeWeapon;
	}

	/** Sets the MeleeWeapon held in the loadout. */
	public void setMeleeWeapon(MeleeWeapon meleeWeapon) {
		this.meleeWeapon = meleeWeapon;
	}
	
	/** Returns the RangedWeapon equipped by the player. */
	public RangedWeapon getRangedWeapon() {
		return rangedWeapon;
	}
	
	/** Sets the RangedWeapon equipped by the player. */
	public void setRangedWeapon(RangedWeapon rangedWeapon) {
		this.rangedWeapon = rangedWeapon;
	}

	/** Clears all of the weapons held by the player. */
	public void clear() 
	{
		//Nullify the player's melee and ranged weapon. Ensures that the player loses his weapons.
		meleeWeapon = null;		
		rangedWeapon = null;
	}

}
