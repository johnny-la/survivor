package com.jonathan.survivor.renderers;


import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.esotericsoftware.spine.AnimationState;
import com.esotericsoftware.spine.AnimationState.AnimationStateListener;
import com.esotericsoftware.spine.AnimationStateData;
import com.esotericsoftware.spine.Bone;
import com.esotericsoftware.spine.Event;
import com.esotericsoftware.spine.Skeleton;
import com.esotericsoftware.spine.attachments.RegionAttachment;
import com.jonathan.survivor.Assets;
import com.jonathan.survivor.entity.Human.Direction;
import com.jonathan.survivor.entity.Human.Mode;
import com.jonathan.survivor.entity.Human.State;
import com.jonathan.survivor.entity.Player;
import com.jonathan.survivor.inventory.MeleeWeapon;
import com.jonathan.survivor.math.Rectangle;

public class PlayerRenderer 
{	
	/** Stores the SpriteBatcher used to draw the player's sprites. */
	private SpriteBatch batcher;	
	
	/** Stores the Assets singleton which stores all of the visual assets needed to draw the player. */
	private Assets assets = Assets.instance;
	
	/** Stores the OrthographicCamera where the player is drawn. */
	private OrthographicCamera worldCamera;
	
	/** Stores the Player GameObject we draw to the screen by extracting its position and state. */
	private Player player;
	/** Stores the Spine skeleton instance used to display the player and play his animations. */
	private Skeleton playerSkeleton;
	
	/** Holds the bone where the melee weapons are equipped. */
	private Bone rightHandBone;
	
	/** Stores the RegionAttachment storing the image of the axe on the player. */
	private RegionAttachment axeAttachment;
	
	/** Defines the crossfading times between animations. */
	private AnimationStateData animStateData;
	/** Controls the animation of the player and applies the animations the player's skeleton. */
	private AnimationState animationState;
	
	/** Stores the AnimationStateListener that receives animation events. */
	private AnimationStateListener animationListener;
	
	/** Stores the integers assigned to each event in Spine. Used to indicate which event was caught in the AnimationStateListener. */
	private static final int HIT_TREE = 0;
	
	/** Accepts the player GameObject to render, the Spine skeleton used to to play his animations, the SpriteBatch used to draw the player, and the world 
	 * camera where the player is drawn. */
	public PlayerRenderer(Player player, SpriteBatch batcher, OrthographicCamera worldCamera)
	{
		//Stores the SpriteBatch used to draw the player.
		this.batcher = batcher;
		//Stores the world camera where the player's sprites are drawn.
		this.worldCamera = worldCamera;
		
		//Stores the Player GameObject we draw to the screen by extracting its position and state.
		this.player = player;
		//Stores the Spine skeleton instance used to display the player and play his animations.
		this.playerSkeleton = player.getSkeleton();
		
		//Grabs the bones mapped to the player's skeleton.
		rightHandBone = playerSkeleton.findBone("R_Hand");
		
		//Retrieves the attachments which store images on the player.
		axeAttachment = (RegionAttachment) playerSkeleton.getAttachment(MeleeWeapon.WEAPON_SLOT_NAME, "Axe0002");
		
		//Sets up the animation states of the player, along with the crossfading times between animations/
		setupAnimationStates();
	}
	
	/** Populates the AnimationStateData and AnimationData instances used by the player. */
	private void setupAnimationStates()
	{
		//Creates a new AnimationStateData instance from the player's skeleton to define the crossfading times between animations.
		animStateData = new AnimationStateData(assets.playerSkeletonData);
		
		//Defines the crossfading times between animations. First two arguments specify the crossfading animations. Third argument specifies crossfading time.
		animStateData.setMix(assets.playerIdle, assets.playerIdle, 0);
		animStateData.setMix(assets.playerWalk, assets.playerIdle, 0.1f);
		animStateData.setMix(assets.playerIdle, assets.playerWalk, 0.1f);
		animStateData.setMix(assets.playerWalk, assets.playerChopTree_Start, 0.2f);
		animStateData.setMix(assets.playerChopTree, assets.playerIdle, 0.35f);
		animStateData.setMix(assets.playerChopTree, assets.playerWalk, 0.27f);
		
		//Creates a listener to listen for events coming from the player's animations.
		animationListener = new AnimationStateListener() {
			/** Called when an event set up inside the Spine Timeline fires in one of the player's animations. */
			@Override
			public void event(int trackIndex, Event event) 
			{
				//If the player's CHOP_TREE animation is playing, and the HIT_TREE event was fired
				if(event.getInt() == HIT_TREE )
				{
					//Deal damage to the tree the player is chopping.
					player.hitTree();
				}
				
			}

			@Override
			public void complete(int trackIndex, int loopCount) 
			{
				//If the ENTER_COMBAT animation has just finished playing
				if(player.getState() == State.ENTER_COMBAT)
				{
					//Set the player back to IDLE state so that his correct animation plays.
					player.setState(State.IDLE);
				}
				//Else, if the player has just finished his MELEE animation
				else if(player.getState() == State.MELEE)
				{
					//Set the player back to IDLE state so that his correct animation plays.
					player.setState(State.IDLE);
				}
				//Else, if the HIT animation just finished playing
				else if(player.getState() == State.HIT)
				{
					//Set the player back to IDLE state
					player.setState(State.IDLE);
				}
				
			}

			@Override
			public void start(int trackIndex) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void end(int trackIndex) {
				// TODO Auto-generated method stub
				
			}
			
		};
		
		//Creates a new AnimationState instance used to control the player's animations.
		animationState = new AnimationState(animStateData);
		
		//Register the AnimationStateListener to the player's AnimationState. This will delegate player animation events to the listener.
		animationState.addListener(animationListener);
	}
	
	/** Draws the player using his Spine skeleton, which stores his animations, sprites, and everything needed to draw the player. */
	public void render(float deltaTime)
	{
		//If the player is looking left
		if(player.getDirection() == Direction.LEFT)
			//Flip his skeleton to look the other way. The skeleton looks right by default.
			playerSkeleton.setFlipX(true);
		//Else, if the player is facing right
		else
			//Don't flip his skeleton, since his skeleton is made to look to the right by default.
			playerSkeleton.setFlipX(false);
		
		//Set the player skeleton's bottom-center position to the bottom-center position of the Player GameObject.
		playerSkeleton.setX(player.getX());
		playerSkeleton.setY(player.getY());
		
		//Updates the attachments on the player, such as his attached weapons.
		updateAttachments();
		
		//Change the animation if the player's state has changed. Re-setting the AnimationState to the same animation twice causes errors.
		if(player.getState() != player.getPreviousState())
		{
			//Update the player's animation since his state has changed.
			updateAnimation();
		}
		
		//Updates the state of the current player animation.
		animationState.update(deltaTime);
		//Applies the current animation to the player's skeleton.
		animationState.apply(playerSkeleton);
		
		//Updates the skeleton world transform. Must be done to reset SRT coordinates (or something).
		playerSkeleton.updateWorldTransform();
		
		//Draws the skeleton using the universal SkeletonRenderer instance used by the game.
		assets.skeletonRenderer.draw(batcher, playerSkeleton);
	}

	/** Updates the current animation of the player depending on his state. */
	private void updateAnimation()
	{
		//Stores the previous state of the player to determine if his state changes on the next render() call.
		player.setPreviousState(player.getState());
		
		//If the player has just spawned
		if(player.getState() == State.SPAWN)
		{
			//Set the player's skeleton back to default pose.
			playerSkeleton.setToSetupPose();
			
			//Sets the character to IDLE state, indicating that the renderer has received the message that the player spawned.
			player.setState(State.IDLE);
		}
		//Else, if the player is in IDLE state
		else if(player.getState() == State.IDLE)
		{
			//If the player is in EXPLORATION mode
			if(player.getMode() == Mode.EXPLORING)
			{
				//Sets the player to his default idle animation. First argument is an arbitrary index, and third argument specifies to loop the animation.
				animationState.setAnimation(0, assets.playerIdle, true);
			}
			//Else, if the player is in COMBAT mode
			if(player.getMode() == Mode.COMBAT)
			{
				//Sets the player to his combat idle animation. First argument is an arbitrary index, and third argument specifies to loop the animation.
				animationState.setAnimation(0, assets.playerIdle_Combat, true);
			}	
		}
		else if(player.getState() == State.WALK)
		{
			//Plays the walk animation. First argument is an arbitrary index, and third argument specifies to loop the walk animation.
			animationState.setAnimation(0, assets.playerWalk, true);
		}
		//If the player is jumping
		if(player.getState() == State.JUMP)
		{	
			//If the player is in EXPLORATION mode
			if(player.getMode() == Mode.EXPLORING)
			{
				//Plays the default jump animation. First argument is an arbitrary index, and third argument specifies to play the animation only once.
				animationState.setAnimation(0, assets.playerJump, false);
			}
			//Else, if the player is in COMBAT mode
			if(player.getMode() == Mode.COMBAT)
			{
				//Plays the combat jump animation. First argument is an arbitrary index, and third argument specifies to play the animation only once.
				animationState.setAnimation(0, assets.playerJump_Combat, false);
			}

		}
		//Else, if the player is falling from one layer to the next
		else if(player.getState() == State.FALL)
		{
			//Plays the player's fall animation. First argument is an arbitrary index, and third argument specifies to play the animation only once.
			animationState.setAnimation(0, assets.playerFall, false);
		}
		//Else, if the player is melee-ing
		else if(player.getState() == State.CHOP_TREE)
		{
			//Plays the crossfading part of the ChopTree animation. First argument is an arbitrary index, and third argument specifies to play the animation once.
			animationState.setAnimation(0, assets.playerChopTree_Start, false);
			//Queues the ChopTree animation to play after the start animation. First argument is an arbitrary index, and third argument specifies to loop the animation.
			//Last zero specifies that the ChopTree should play right after the ChopTree_Start animation.
			animationState.addAnimation(0, assets.playerChopTree, true, 0);
		}
		//Else, if the player has just entered combat
		else if(player.getState() == State.ENTER_COMBAT)
		{
			//Plays the ENTER_COMBAT animation. First argument is an arbitrary index, and third argument specifies to play the animation only once.
			animationState.setAnimation(0, assets.playerEnterCombat, false);
		}
		//Else, if the player is supposed to perform a melee attack
		else if(player.getState() == State.MELEE)
		{
			//Play the player's MELEE animation. First argument is an arbitrary index, and third argument specifies to play the animation only once.
			animationState.setAnimation(0, assets.playerMelee, false);
		}
		//Else, if the player was hit by a zombie
		else if(player.getState() == State.HIT)
		{
			//Play the player's HIT animation. First argument is an arbitrary index, and third argument specifies to play the animation only once.
			animationState.setAnimation(0, assets.playerHit, false);
		}
	}
	
	/** Updates the attachments being rendered on the player. */
	private void updateAttachments() 
	{
		//Updates the collider bound to the player's melee weapon to test for hit detection.
		updateAttachmentColliders();
		
		//Stores the player's melee weapon, if it exists.
		MeleeWeapon meleeWeapon = player.getLoadout().getMeleeWeapon();
		
		//If the player has a weapon
		if(meleeWeapon != null)
		{
			//Set the melee weapon slot on the player's skeleton to display the image of the correctmelee weapon. The slot is
			//where the weapon's image is stored, and the attachment is the name of the image for each weapon.
			playerSkeleton.setAttachment(meleeWeapon.getSlotName(), meleeWeapon.getWeaponAttachment());
		}
		//Else, if the player doesn't have a weapon
		else
		{
			//Remove the melee weapon attachment from the player since he has no equipped melee weapon.
			playerSkeleton.setAttachment(MeleeWeapon.WEAPON_SLOT_NAME, null);
		}
	}

	/** Updates the position and scale of the collider on the player's equipped melee weapon. */
	private void updateAttachmentColliders() 
	{
		//If the player doesn't have a melee weapon, return this method, as its collider needn't be updated.
		if(!player.hasMeleeWeapon())
			return;
		
		//Stores the position of the player's hand in world coordinates.
		float handX = playerSkeleton.getX() +rightHandBone.getWorldX();
		float handY = playerSkeleton.getY() +rightHandBone.getWorldY();
		
		//Stores the player's melee weapon, if it exists.
		MeleeWeapon meleeWeapon = player.getLoadout().getMeleeWeapon();
		
		//Stores the collider mapped to the player's melee weapon, which checks for collisions against zombies.
		Rectangle weaponCollider = meleeWeapon.getCollider();
		
		//Finds the size of the melee weapon. It is equivalent to the size of the image displaying the axe.
		float meleeWeaponWidth = axeAttachment.getWidth();
		float meleeWeaponHeight = axeAttachment.getHeight();
		
		//If the player is facing the RIGHT, his weapon is to the right of him
		if(player.getDirection() == Direction.RIGHT)
		{
			//Set the weapon's collider to be at the position of the hand
			weaponCollider.setPosition(handX, handY);
			
			//Sets the size of the weapon's collider to that of the axe's image.
			weaponCollider.setSize(meleeWeaponWidth, meleeWeaponHeight);
		}
		//Else, if the player is facing the left, his weapon is to the left of him.
		else
		{
			//Set the weapon's collider to be at the position of the hand. The x-position starts at the left of the weapon.
			weaponCollider.setPosition(handX - meleeWeaponWidth, handY);
			
			//Sets the size of the weapon's collider to that of the axe's image.
			weaponCollider.setSize(meleeWeaponWidth, meleeWeaponHeight);
		}
		
	}
}
