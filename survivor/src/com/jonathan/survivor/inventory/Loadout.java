package com.jonathan.survivor.inventory;

public class Loadout 
{
	/** Stores the MeleeWeapon held in the loadout. */
	private MeleeWeapon meleeWeapon;
	
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
}
