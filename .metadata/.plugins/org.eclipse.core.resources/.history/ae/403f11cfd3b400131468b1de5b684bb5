package com.jonathan.survivor.renderers;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;
import com.esotericsoftware.spine.Event;
import com.esotericsoftware.spine.Skeleton;
import com.jonathan.survivor.Assets;
import com.jonathan.survivor.entity.Box;
import com.jonathan.survivor.entity.InteractiveObject;
import com.jonathan.survivor.entity.InteractiveObject.InteractiveState;
import com.jonathan.survivor.entity.Tree;

public class InteractiveObjectRenderer 
{
	/** Stores the SpriteBatcher used to draw the GameObjects. */
	private SpriteBatch batcher;	
	
	/** Stores the Assets singleton which stores all of the visual assets needed to draw the interactive GameObjects. */
	private Assets assets = Assets.instance;
	
	/** Stores the color of transparent GameObjects. */
	private static final Color TRANSPARENT_COLOR = new Color(0, 0, 0, 0.4f);
	
	/** Helper Color instance used to color GameObjects and avoid creating new color instances. */
	private Color workingColor;
	
	//Helper Array that's passed to the Animation.set() method.
	private Array<Event> events = new Array<Event>();
	
	/** Accepts the SpriteBatch instance used to draw the Interactive GameObjects. */
	public InteractiveObjectRenderer(SpriteBatch batcher)
	{
		//Stores the SpriteBatch instance used to draw the Interactive GameObjects.
		this.batcher = batcher;
		
		//Helper Color instance used to avoid instantiation. Defaults to white.
		workingColor = new Color(Color.WHITE);
	}
	
	/** Draws the given InteractiveObject. Accepts whether or not the GameObject should be drawn transparent. */
	public void draw(InteractiveObject gameObject, boolean transparent)
	{
		//If the GameObject is a Tree
		if(gameObject instanceof Tree)
			//Delegate the drawing to the drawTree() method.
			drawTree((Tree)gameObject, transparent);
		//Else, if the GameObject to draw is a box
		else if(gameObject instanceof Box)
			//Delegate the rendering call to the drawBox() method.
			drawBox((Box)gameObject, transparent);
			
	}
	
	/** Renders a Tree GameObject, which contains a Spine Skeleton instance which can be drawn to the screen. Accepts whether or not it should be drawn transparent. */
	private void drawTree(Tree tree, boolean drawTransparent)
	{
		//Stores the Spine skeleton of the tree which controls its appearance.
		Skeleton skeleton = tree.getSkeleton();
		
		//Sets the bottom-center position of the skeleton to the tree's bottom-center position. Note that Skeleton/GameObject.position hold the bottom-center of the tree.
		skeleton.setX(tree.getX());
		skeleton.setY(tree.getY());
		
		//Set the default color of the tree to white, using the workingColor instance as a helper object.
		workingColor.set(Color.WHITE);
		
		//If the tree has jump spawned
		if(tree.getInteractiveState() == InteractiveState.SPAWN)
		{
			//Reset the tree's skeleton to its setup pose to undo any changes previously done to the skeleton's bones.
			skeleton.setToSetupPose();
			
			//Sets the state time of the tree to a random time so that the idle animation starts playing at a random place for every tree.
			tree.setStateTime((float)Math.random() * 10);
			//Sets the tree's state to IDLE, indicating that the renderer has received the message that the tree has spawned.
			tree.setInteractiveState(InteractiveState.IDLE);
		}
		//Else, if the tree is in IDLE state
		else if(tree.getInteractiveState() == InteractiveState.IDLE)
		{
			//Apply the 'treeIdle' animation to the tree's skeleton. Second and third arguments specify how much time the tree has been idle, third indicates we want to 
			//loop the animation, and last is an array where any possible animation events are delegated.
			assets.treeIdle.apply(skeleton, tree.getStateTime(), tree.getStateTime(), true, events);
		}
		//Else, if the tree was clicked
		else if(tree.getInteractiveState() == InteractiveState.CLICKED)
		{
			//Apply the 'treeIdle' animation to the tree's skeleton. Second and third arguments specify how much time the tree has been idle, third indicates we want to 
			//loop the animation, and last is an array where any possible animation events are delegated.
			assets.treeClicked.apply(skeleton, tree.getStateTime(), tree.getStateTime(), false, events);
		}
		//Else, if the tree was hit
		else if(tree.getInteractiveState() == InteractiveState.HIT)
		{	
			//Apply the 'treeIdle' animation to the tree's skeleton. Second and third arguments specify how much time the tree has been idle, third indicates we want to 
			//play the animation once, and last is an array where any possible animation events are delegated.
			assets.treeHit.apply(skeleton, tree.getStateTime(), tree.getStateTime(), false, events);
			
			if(tree.getStateTime() > assets.treeHit.getDuration())
				//The tree renderer has received the HIT message, so the tree can be reset to its CLICKED state.
				tree.setInteractiveState(InteractiveState.CLICKED);
		}
		//Else, if the tree has been scavenged (i.e., its health has dropped below zero)
		else if(tree.getInteractiveState() == InteractiveState.SCAVENGED)
		{
			//Apply the 'treeScavenged' animation to the tree's skeleton. Second and third arguments specify how much time the tree has been idle, third indicates we want to 
			//play the animation once, and last is an array where any possible animation events are delegated.
			assets.treeScavenged.apply(skeleton, tree.getStateTime(), tree.getStateTime(), false, events);
		}
		
		//If the tree is supposed to be transparent
		if(drawTransparent)
			//Apply transparency to the working color.
			workingColor.mul(TRANSPARENT_COLOR);
		
		//Color the tree's skeleton to the working color.
		skeleton.getColor().set(workingColor);
		
		//Updates the Skeleton in the world.
		skeleton.updateWorldTransform();
		
		//Draws the tree's skeleton using the universal SkeletonRenderer instance, along with the GameScreen's SpriteBatch.
		assets.skeletonRenderer.draw(batcher, skeleton);
	}
	
	/** Helper method called when a Box instance needs to be rendered. Second parameter accepts whether box should be drawn transparent. */
	private void drawBox(Box box, boolean drawTransparent)
	{
		//Stores the skeleton used to render the box to the screen. Each skeleton is unique to each box, and acts as an actor on-screen.
		Skeleton skeleton = box.getSkeleton();
		
		//Sets the skeleton's position to that of the box. Note that the box's position is the bottom-center, just like the skeleton.
		skeleton.setX(box.getX());
		skeleton.setY(box.getY());
		
		//Reset the workingColor to a blank WHITE slate. Used to modify the color white accordingly and apply it as the box's final color.
		workingColor.set(Color.WHITE);
		
		//If the box has jump spawned
		if(box.getInteractiveState() == InteractiveState.SPAWN)
		{
			//Reset the box's skeleton to its setup pose to undo any changes previously done to the skeleton's bones.
			skeleton.setToSetupPose();
			
			//Sets the box IDLE state, indicating that the renderer has received the message that the box has spawned.
			box.setInteractiveState(InteractiveState.IDLE);
		}
		//Else, if the box is in IDLE state
		else if(box.getInteractiveState() == InteractiveState.IDLE)
		{
			//Apply the 'boxIdle' animation to the box's skeleton. Second and third arguments specify how much time the box has been idle, third indicates we want to 
			//loop the animation, and last is an array where any possible animation events are delegated.
			assets.boxIdle.apply(skeleton, box.getStateTime(), box.getStateTime(), true, events);
		}
		//Else, if the box was clicked
		else if(box.getInteractiveState() == InteractiveState.CLICKED)
		{
			//Apply the 'boxIdle' animation to the box's skeleton. Second and third arguments specify how much time the box has been idle, third indicates we want to 
			//loop the animation, and last is an array where any possible animation events are delegated.
			assets.boxClicked.apply(skeleton, box.getStateTime(), box.getStateTime(), false, events);
		}
		//Else, if the box has been scavenged (i.e., it has been opened by the player)
		else if(box.getInteractiveState() == InteractiveState.SCAVENGED)
		{
			//Apply the 'boxScavenged' animation to the box's skeleton. Second and third arguments specify how much time the box has been idle, third indicates we want to 
			//play the animation once, and last is an array where any possible animation events are delegated.
			assets.boxScavenged.apply(skeleton, box.getStateTime(), box.getStateTime(), false, events);
		}
		
		//If the box is supposed to be transparent
		if(drawTransparent)
			//Apply transparency to the working color.
			workingColor.mul(TRANSPARENT_COLOR);
		
		//Set the box's color to the working color, which holds the box's final color.
		skeleton.getColor().set(workingColor);
		
		//Updates the Skeleton in the world.
		skeleton.updateWorldTransform();
		
		//Draws the box's skeleton using the universal SkeletonRenderer instance, along with the GameScreen's SpriteBatch.
		assets.skeletonRenderer.draw(batcher, skeleton);
	}
	
}
