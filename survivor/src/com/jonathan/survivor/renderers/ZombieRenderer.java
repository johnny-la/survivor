package com.jonathan.survivor.renderers;


import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.esotericsoftware.spine.AnimationState;
import com.esotericsoftware.spine.AnimationState.AnimationStateListener;
import com.esotericsoftware.spine.AnimationStateData;
import com.esotericsoftware.spine.Skeleton;
import com.jonathan.survivor.Assets;
import com.jonathan.survivor.entity.GameObject;
import com.jonathan.survivor.entity.Human.Direction;
import com.jonathan.survivor.entity.Human.Mode;
import com.jonathan.survivor.entity.Human.State;
import com.jonathan.survivor.entity.Zombie;

public class ZombieRenderer 
{	
	/** Stores the SpriteBatcher used to draw the zombie's sprites. */
	private SpriteBatch batcher;	
	
	/** Stores the Assets singleton which stores all of the visual assets needed to draw the zombie. */
	private Assets assets = Assets.instance;
	
	/** Stores the color of transparent zombies, when they are on different layers than the player. */
	private static final Color TRANSPARENT_COLOR = new Color(0, 0, 0, 0.4f);

	/** Holds the color of the zombie when he is being targetted by the player. */
	private static final Color TARGETTED_COLOR = new Color(0.6f, 0.7f, 1, 1);
	
	/** Defines the crossfading times between the zombies' animations. */
	public static AnimationStateData animStateData;
	
	/** Stores the AnimationStateListener that receives animation events. */
	private AnimationStateListener animationListener;
	
	/** Helper Color instance used to color the zombies and avoid creating new color instances. */
	private Color workingColor;
	
	/** Stores the integers assigned to each event in Spine. Used to indicate which event was caught in the AnimationStateListener. */
	//private static final int HIT_TREE = 0;
	
	/** Accepts the zombie GameObject to render, the Spine skeleton used to to play his animations, the SpriteBatch used to draw the zombie. */
	public ZombieRenderer(SpriteBatch batcher)
	{
		//Stores the SpriteBatch used to draw the zombie.
		this.batcher = batcher;
		
		//Instantiates the helper Color object used to color the ItemObjects.
		workingColor = new Color(Color.WHITE);
		
		//Sets up the animation states of the zombie, along with the crossfading times between animations/
		setupAnimationStates();
	}
	
	/** Populates the AnimationStateData and AnimationData instances used by the zombie. */
	private void setupAnimationStates()
	{
		//Creates a new AnimationStateData instance from the zombie's skeleton to define the crossfading times between animations.
		animStateData = new AnimationStateData(assets.zombieSkeletonData);
		
		//Defines the crossfading times between animations. First two arguments specify the animations to crossfade. Third argument specifies crossfading time.
		animStateData.setMix(assets.zombieWalk, assets.zombieIdle, 0.3f);
		animStateData.setMix(assets.zombieIdle, assets.zombieWalk, 0.1f);
		animStateData.setMix(assets.zombieIdle, assets.zombieAlerted, 0.1f);
		animStateData.setMix(assets.zombieAlerted, assets.zombieIdle, 0.1f);
		animStateData.setMix(assets.zombieWalk, assets.zombieAlerted, 0.1f);
		animStateData.setMix(assets.zombieAlerted, assets.zombieWalk, 0.1f);
		animStateData.setMix(assets.zombieCharge_Start, assets.zombieCharge, 0.2f);
	}
	
	/** Draws the zombie using his Spine skeleton, which stores his animations, sprites, and everything needed to draw the zombie. Accepts a boolean which depicts
	 *  whether or not the zombie should be drawn transparently. */
	public void draw(Zombie zombie, boolean transparent, float deltaTime)
	{
		//Retrieves the Spine skeleton used to animate and display the zombie.
		Skeleton skeleton = zombie.getSkeleton();
		
		//If the zombie's AnimationState instance is null, create it. Note that the animationState for the zombies are always created in this renderer.
		if(zombie.getAnimationState() == null)
		{
			//Creates and sets a new AnimationState instance used to control the zombie's animations.
			zombie.setAnimationState(new AnimationState(ZombieRenderer.animStateData));
		}
		
		//Stores the AnimationState used to change and control the zombie's animations.
		AnimationState animationState = zombie.getAnimationState();
		
		//If the zombie is looking left
		if(zombie.getDirection() == Direction.LEFT)
			//Flip his skeleton to look the other way. The skeleton looks right by default.
			skeleton.setFlipX(true);
		//Else, if the zombie is facing right
		else
			//Don't flip his skeleton, since his skeleton is made to look to the right by default.
			skeleton.setFlipX(false);
		
		//Set the zombie skeleton's bottom-center position to the bottom-center position of the Player GameObject.
		skeleton.setX(zombie.getX());
		skeleton.setY(zombie.getY());
		
		//Updates the attachments on the zombie, such as his attached weapons.
		updateAttachments(zombie);
		
		//Change the animation if the zombie's state has changed. Re-setting the AnimationState to the same animation twice causes errors.
		if(zombie.getState() != zombie.getPreviousState())
		{
			//Update the zombie's animation since his state has changed. Passes in the zombie whose animations need to be updated.
			updateAnimation(zombie);
		}
		
		//Sets the zombie to be the correct color depending on the zombie's current state, and whether or not it should be transparent.
		updateColor(zombie, transparent);
		
		//Updates the state of the current zombie animation.
		animationState.update(deltaTime);
		//Applies the current animation to the zombie's skeleton.
		animationState.apply(skeleton);
		
		//Updates the skeleton world transform. Must be done to reset SRT coordinates (or something).
		skeleton.updateWorldTransform();
		
		//Draws the skeleton using the universal SkeletonRenderer instance used by the game.
		assets.skeletonRenderer.draw(batcher, skeleton);
	}

	/** Updates the current animation of the zombie accroding to his current state. Accepts the zombie to update. */
	private void updateAnimation(Zombie zombie)
	{
		//Retrieves the Spine skeleton used to animate and display the zombie.
		Skeleton skeleton = zombie.getSkeleton();
		
		//Stores the AnimationState used to change and control the zombie's animations.
		AnimationState animationState = zombie.getAnimationState();
		
		//Stores the previous state of the zombie to determine if his state changes on the next render() call.
		zombie.setPreviousState(zombie.getState());
		
		//If the zombie has just spawned
		if(zombie.getState() == State.SPAWN)
		{
			//Set the zombie's skeleton back to default pose.
			skeleton.setToSetupPose();
			
			//Sets the zombie to IDLE state, indicating that the renderer has received the message that the zombie spawned.
			zombie.setState(State.IDLE);
		}
		else if(zombie.getState() == State.IDLE)
		{
			//Sets the zombie to his idle animation. First argument is an arbitrary index, and third argument specifies to loop the animation.
			animationState.setAnimation(0, assets.zombieIdle, true);
		}
		else if(zombie.getState() == State.WALK)
		{
			//Plays the walk animation. First argument is an arbitrary index, and third argument specifies to loop the walk animation.
			animationState.setAnimation(0, assets.zombieWalk, true);
		}
		//Else, if the zombie has just been alerted that the player is close to him
		else if(zombie.getState() == State.ALERTED)
		{
			//Plays the alerted animation. First argument is an arbitrary index, and third argument specifies to play the animation only one.
			animationState.setAnimation(0, assets.zombieAlerted, false);			
		}
		//Else, if the zombie has just entered combat
		else if(zombie.getState() == State.ENTER_COMBAT)
		{
			//Plays the ENTER_COMBAT animation. First argument is an arbitrary index, and third argument specifies to play the animation only once.
			animationState.setAnimation(0, assets.zombieEnterCombat, false);
		}
		//Else, if the zombie is getting ready to charge towards the player
		else if(zombie.getState() == State.CHARGE_START)
		{
			//Plays the CHARGE_START animation. First argument is an arbitrary index, and third argument specifies to loop the walk animation.
			animationState.setAnimation(0, assets.zombieCharge_Start, true);
		}
		//Else, if the zombie is charging towards the player
		else if(zombie.getState() == State.CHARGE)
		{
			//Plays the CHARGE animation. First argument is an arbitrary index, and third argument specifies to loop the walk animation.
			animationState.setAnimation(0, assets.zombieCharge, true);
		}
		//Else, if the zombie was hit by the player
		else if(zombie.getState() == State.HIT)
		{
			//Plays the HIT animation. First argument is an arbitrary index, and third argument specifies to loop the walk animation.
			animationState.setAnimation(0, assets.zombieHitHead, true);
		}
		//Else, if the zombie was hit on the head by the player
		else if(zombie.getState() == State.HIT_HEAD)
		{
			//Plays the HIT_HEAD animation. First argument is an arbitrary index, and third argument specifies to loop the walk animation.
			animationState.setAnimation(0, assets.zombieHitHead, true);
		}
		
		//Updates the speed at which the zombie's animations play, depending on the zombie's current state.
		updateTimeScale(zombie);
		
	}
	
	/** Updates the attachments being rendered on the zombie. */
	private void updateAttachments(Zombie zombie) 
	{
		
		zombie.updateArmCollider();
	}
	
	/** Updates the zombie's color depending on whether its being targetted, and whether or not it should be drawn transparent. */
	private void updateColor(Zombie zombie, boolean transparent)
	{
		//Retrieves the Spine skeleton used to animate and display the zombie.
		Skeleton skeleton = zombie.getSkeleton();
		
		//Reset the working color instance to WHITE so that the helper color starts from a clean slate.
		workingColor.set(Color.WHITE);
		
		//If the zombie is being targetted by the player
		if(zombie.isTargetted())
		{
			//Updates the zombie's color so that the player knows he's targetted.
			workingColor.mul(TARGETTED_COLOR);
		}
		
		//If the ItemObject is supposed to be drawn transparent
		if(transparent)
			//Multiply the working color by the TRANSPARENT_COLOR constant so that the ItemObject is drawn transparent.
			workingColor.mul(TRANSPARENT_COLOR);
		
		//Set the Item GameObject's color to the workingColor instance, which stored the color that the ItemObject should be.
		skeleton.getColor().set(workingColor);
		
	}
	
	/** Updates the Zombie's TimeScale so that its animations play faster or slower, depending on the zombie's current state. */
	private void updateTimeScale(Zombie zombie)
	{
		//Stores the AnimationState used to change and control the zombie's animations.
		AnimationState animationState = zombie.getAnimationState();
		
		//If the zombie is alerted
		if(zombie.isAlerted())
			//Make the animation go faster since the zombie is walking faster
			animationState.setTimeScale(Zombie.ALERTED_ANIM_SPEED);
		//Else, if the zombie is not aware of the palyer's presence
		else 
			//Make the zombie's animations go at normal speed if the zombie is not alerted of the player's presence.
			animationState.setTimeScale(1);
	}
}
