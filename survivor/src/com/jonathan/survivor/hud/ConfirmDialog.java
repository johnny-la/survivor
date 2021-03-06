package com.jonathan.survivor.hud;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.jonathan.survivor.Assets;

public class ConfirmDialog extends Dialog
{	
	/** Holds the Assets singleton used to retrieve pre-defined ui styles. */
	private Assets assets = Assets.instance;
	
	/** Holds the size of the background. Background is scaled up if it is too small for the dialog's widgets. */
	private static final float BACKGROUND_MIN_WIDTH = 380;
	private static final float BACKGROUND_MIN_HEIGHT = 230;
	
	/** Stores the spacing between the bottom of the buttons and the bottom of the nine-patch background. */
	private static final float BUTTON_BACKGROUND_SPACING = 10;
	
	/** Stores the horizontal spacing between the "Yes" and "No" buttons. */
	private static final float BUTTON_SPACING = 10;
	
	/** Holds the label displaying the confirm dialog's message. */
	private Label messageLabel;
	
	/** Stores the 'Yes' and the 'No' buttons which prompt the user for confirmation. */
	private TextButton yesButton, noButton;
	
	/** Creates the confirm dialog with the given confirmation message as a title. The clickListener is registered to the 'Yes' button.
	 *  Thus, its clicked() method will be called when the yesButton is pressed. */
	public ConfirmDialog(String message, ClickListener clickListener)
	{
		//Must call an explicit super constructor, as no default constructor is defined in the superclass. Sets the title to an empty string.
		super("", Assets.instance.confirmDialogWindowStyle);
		
		//Creates a new label displaying the message contained in the dialog.
		messageLabel = new Label(message, assets.mainMenuHeaderStyle);
		
		//Adds the message as the title of the dialog 
		text(messageLabel);
		
		//Creates the 'Yes' button using the pre-defined main menu's button style.
		yesButton = new TextButton("Yes", assets.mainMenuButtonStyle);
		//Sets the 'Yes' button's color to green.
		yesButton.setColor(new Color(0.5f, .8f, 0.2f, 1));
		//Re-scales the 'Yes' button to ensure that, no matter the atlas size chosen, the size of the button remains the same.
		yesButton.setSize(yesButton.getWidth() / assets.scaleFactor, yesButton.getHeight() / assets.scaleFactor);
		
		//Creates the 'No' button using the pre-defined main menu's button style.
		noButton = new TextButton("No", assets.mainMenuButtonStyle);
		//Sets the color of the 'No' button to red.
		noButton.setColor(new Color(0.9f, 0.2f, 0.1f, 1));
		//Re-scales the 'No' button by the scaleFactor to ensure that, no matter the atlas size chosen, the size of the button remains the same.
		noButton.setSize(noButton.getWidth() / assets.scaleFactor, noButton.getHeight() / assets.scaleFactor);
		
		//Adds the given listener to the 'yesButton'
		yesButton.addListener(clickListener);
		
		//Defines the fade duration in seconds of the dialog when it appears and disappears.
		fadeDuration = 0.09f;
		
		//Adds the 'Yes' and the 'No' button to be displayed in the confirmation dialog.
		button(yesButton);
		button(noButton);
		
		//Resizes the cells for each button so that they are the same size as the button. Prevents buttons from scaling up or down unwantingly.
		//Also pads the bottom of the buttons by the given value, and top-aligns them so that there is a space between the bottom of the background 
		//and the buttons.
		getButtonTable().getCell(yesButton).width(yesButton.getWidth()).height(yesButton.getHeight()).padBottom(BUTTON_BACKGROUND_SPACING).top().padRight(BUTTON_SPACING);
		getButtonTable().getCell(noButton).width(noButton.getWidth()).height(noButton.getHeight()).padBottom(BUTTON_BACKGROUND_SPACING).top();
		
		//Sets the minimum width and height of the dialog's background to ensure that it is slightly larger than dialog's contents.
		getBackground().setMinWidth(BACKGROUND_MIN_WIDTH);
		getBackground().setMinHeight(BACKGROUND_MIN_HEIGHT);
	}
	
	/** Returns the "Yes" button clicked when the user confirms his choice. */ 
	public TextButton getConfirmButton()
	{
		return yesButton;
	}

	public void setMessage(String message) 
	{
		messageLabel.setText(message);
	}
}
