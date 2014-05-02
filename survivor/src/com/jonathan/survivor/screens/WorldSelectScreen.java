package com.jonathan.survivor.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.ButtonGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.jonathan.survivor.Profile;
import com.jonathan.survivor.Survivor;
import com.jonathan.survivor.hud.ConfirmDialog;
import com.jonathan.survivor.hud.TiledImage;

public class WorldSelectScreen extends Screen
{
	/** Holds the width of the world selection list. That is, the width of the blue bar in pixels for the target resolution (480x320). */
	private final static float WORLD_LIST_WIDTH = 320f;	
	/** Stores the height of the world selection list. This is the width in pixels at base resolution (480x320). */
	private final static float WORLD_LIST_HEIGHT = 150f;
	
	/** Stores the x-offset of the background relative to the center of the stage. */
	private static final float BACKGROUND_X_OFFSET = -15.5f;
	/** Stores the y-offset of the background relative to the center of the stage. */
	private static final float BACKGROUND_Y_OFFSET = 2;
	
	/** Stores the offset used to anchor the back button to the bottom-right of the backpack background with a certain padding. */
	public static final float BACK_BUTTON_X_OFFSET = 10;
	public static final float BACK_BUTTON_Y_OFFSET = 7;

	/** Stores the stage used as a container for the UI widgets. It is essentially the camera that draws the widgets. */
	private Stage stage;	
	
	/** InputListener which receives an event when the BACK button is pressed on Android devices. Allows the user to switch back to the main menu. */
	private InputListener inputListener;
	
	/** Class allowing us to set multiple instance of InputListeners to receive input events. */
	private InputMultiplexer inputMultiplexer;
	
	/** Stores the table actor. This actor arranges the widgets at the center of the screen in a grid-like fashion. */
	private Table table;	
	
	/** Holds the background for the WorldSelectScreen, which is formed by a tiles of two images. */
	private TiledImage worldSelectBackground;
	
	/** Stores the label displaying the header. */
	private Label header;
	
	/** Holds the "Start" button */
	private TextButton startButton;	
	/** Holds the "Delete" button */
	private TextButton deleteButton;
	/** Stores the button used to go back to the main menu. */
	private Button backButton;	
	
	/** Holds the confirm dialog shown when the user presses the 'delete' button. */
	private ConfirmDialog confirmDialog;	
	
	/** Stores the list of buttons which the user can press to load a saved profile. */
	private Array<TextButton> profileButtons;
	/** Stores a table containing all of the profile buttons, arranged in a vertical list. The user can scroll through it in a scroll pane and select an profile. */
	private Table profileButtonTable;
	
	/** Holds the Listener which registers the profile button clicks. The selectedProfileId:int integer is updated when a profile button is pressed. */
	private ButtonListener buttonListener;
	
	/** Holds the ButtonGroup instance used to ensure that only one profile button can be checked at a time. */
	private ButtonGroup buttonGroup;
	
	/** Stores the ScrollPane which allows the items in the survival guide to be scollable. */
	private ScrollPane scrollPane;
	
	/** Stores the id of the selected profile in the profile list. */
	private int selectedProfileId = 0;

	public WorldSelectScreen(Survivor game)
	{
		super(game);
	}
	
	@Override
	public void show()
	{
		//Create a stage used to draw the UI widgets.
		stage = new Stage();
		
		//Creates the InputListener which receives an event when the Android BACK button is pressed. Allows the user to transition back to the main menu.
		inputListener = new InputListener();
		
		//Creates the multiplexer to allows several classes to receive touch input and keyboard input. 
		inputMultiplexer = new InputMultiplexer();

		//Allows the stage to handle any touch/mouse input. Tells the widgets that they can receive touch events.
		inputMultiplexer.addProcessor(stage);
		//Allows the inputListener to receive input events, for instance when the Android BACK button is pressed. 
		inputMultiplexer.addProcessor(inputListener);
		
		//Registers all the input processors from the multiplexer to receive input events.
		Gdx.input.setInputProcessor(inputMultiplexer);
		
		//Creates a table using the main menu Skin. This table places widgets in grid-like fashion. Why the skin is passed is unknown.
		table = new Table(assets.mainMenuSkin);
		
		//Instantiates a TiledImage formed by two consecutive images which form the background for the screen. Note: the background was too large to fit in a single atlas.
		worldSelectBackground = new TiledImage(assets.worldSelectBgRegion_0, assets.worldSelectBgRegion_1);
		
		//Creates a label displaying the header. We pass in the mainMenuHeaderStyle as the LabelStyle. This tells the label which font/color to choose.
		header = new Label("CHOOSE WORLD", assets.mainMenuHeaderStyle);
		
		//Create the start button using the main menu button style. This gives the button the desired image.
		startButton = new TextButton("Load", assets.mainMenuButtonStyle);
		//Make the start button red.
		startButton.setColor(Color.RED);
		//Resize the start button according to the scale factor. The scale factor is either 4, 2, or 1, depending on which texture size we are using. If we are
		//using @2x size textures, we need to shrink the button to half its orginal size. Like this, the buttons are always the same size, no matter which texture
		//atlas is used. This allows the stage to keep a constant size no matter the texture size.
		startButton.setSize(startButton.getWidth()/assets.scaleFactor, startButton.getHeight()/assets.scaleFactor);
		
		//Create the delete button using the main menu button style. Allows the user to delete the selected profile.
		deleteButton = new TextButton("Delete", assets.mainMenuButtonStyle);
		//Makes the deletebutton's image dark gray.
		deleteButton.setColor(new Color(0.2f, 0.6f, 0.9f, 1));
		//Resize the delete button according to the scale factor. The scale factor is either 4, 2, or 1, depending on which texture size we are using. If we are
		//using @2x size textures, we need to shrink the button to half its orginal size. Like this, the buttons are always the same size, no matter which texture
		//atlas is used. This allows the stage to keep a constant size no matter the texture size.
		deleteButton.setSize(deleteButton.getWidth()/assets.scaleFactor, deleteButton.getHeight()/assets.scaleFactor);
		
		//Creates the back button which prompts the user to return to the main menu.
		backButton = new Button(assets.backButtonStyle);
		//Scales the back button down so that according to the scaleFactor of the assets. This way, no matter the size of the atlases
		//chosen, the buttons are the same size.
		backButton.setSize(backButton.getWidth() / assets.scaleFactor, backButton.getHeight() / assets.scaleFactor);
		
		//Add a click listener to the start button
		startButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y)
			{
				//If there are no existing profiles in the profile manager, no profiles can be loaded. Therefore, return this method.
				if(profileManager.isEmpty())
					return;
				
				//Retrieves the profile corresponding to the chosen item in the world list using profileManager.getProfile(id, createNew), where the id of the profile
				//corresponds to the chosen button in the list.
				Profile profile = profileManager.getProfile(selectedProfileId);
				
				//Tell the PreferencesManager that the selected profile was loaded. This profile will be registered as the player's last used profile.
				prefsManager.profileLoaded(selectedProfileId);
				
				//Save the preferences in order to keep track of the profile that was just loaded, and to load it the next time 'Continue' is pressed.
				prefsManager.savePreferences();
				
				//Disposes of the assets used by the loading and company splash screen in order to free up system resources.
				assets.disposeInitialAssets();
				//Disposes of all the heavy assets used only by the main menu. Allows the game to free up heavy system resources when the user enters the game.
				assets.disposeMainMenuAssets();
				
				//Switch the GameScreen, passing in the chosen profile as a second argument.
				game.setScreen(new GameScreen(game, profile));
			}
		});
		
		//Add a click listener to the delete button
		deleteButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y)
			{
				//If the profile manager contains at least one profile that can be deleted
				if(profileManager.getNumProfiles() > 0)
					//Displays the confirm dialog, waiting for the user to accept deleting the selected profile.
					confirmDialog.show(stage);
			}
		});
		
		//Add a click listener to the back button
		backButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y)
			{
				//Play the "swoosh" sound effect when the "Back" button is pressed.
				//soundManager.play(assets.swoosh);
				//Return the user back to the main menu.
				backPressed();
			}
		});
		
		//Creates the confirmation dialog which opens when the delete button is pressed. Constructor accepts title of dialog, along with ClickListener 
		//which gets its clicked() method called when the 'Yes' button is pressed.
		confirmDialog = new ConfirmDialog("Are you sure you want\nto delete this profile?", new ClickListener() {
			//Called when the "Yes" button is clicked.
			@Override
			public void clicked(InputEvent event, float x, float y)
			{
				//Deletes the profile that is selected in the list. The Id of the selected profile is stored in 'selectedProfileId'.
				deleteProfile(selectedProfileId);
			}
		});
		
		//Creates the world selection list.
		createWorldList();
		
		
		//Add the gameSelectBackground to the stage so that it can be rendered under each UI element. 
		worldSelectBackground.addToStage(stage);
		
		//Add the header to the top of the table, make it span two columns, and skip a row.
		table.add(header).colspan(2).row();
		//Add the world selection list to the table, and make it span two columns. The width of the list is resized so that the blue box is the same size no matter how
		//big the largest label on the list is. Then, skip a row. Note that List.getWidth() actually returns the width of the largest string in the list.
		table.add(scrollPane).colspan(2).width(WORLD_LIST_WIDTH).height(WORLD_LIST_HEIGHT).row();
		//Add the play button to the table, and give it a 10 pixel blank space around its edges. We set the width and height of the table's cell to the button's width
		//and height to ensure that the button is not scaled when added to the table.
		table.add(startButton).pad(10).width(startButton.getWidth()).height(startButton.getHeight());
		//Add the delete button to the table, and give it a 10 pixel blank space around its edges. We set the width and height of the table's cell to the button's width
		//and height to ensure that the button is not scaled when added to the table.
		table.add(deleteButton).pad(10).width(deleteButton.getWidth()).height(deleteButton.getHeight());
		
		//Add the table to the stage, telling the stage to draw all of the widgets inside the table when stage.draw() is called.
		stage.addActor(table);
		//Adds the back button to be rendered on the stage at the position set above
		stage.addActor(backButton);
		
		//Plays the screen's fade-in animation.
		fadeIn();
	}
	
	/** Creates the world selection list, fetching the profiles to figure out what each item in the list should state. */
	private void createWorldList()
	{
		//Creates the profile buttons and adds them to the 'profileButtonTable'.
		createButtonList();
		
		//Places the table of entry buttons inside the ScrollPane to add scrolling functionality to that list.
		scrollPane = new ScrollPane(profileButtonTable, assets.inventoryScrollPaneStyle);
		//Modifies the overscroll of the scroll pane. Args: maxOverscrollDistance, minVelocity, maxVelocity
		scrollPane.setupOverscroll(30, 100, 200);
		//Disables scrolling in the x-direction.
		scrollPane.setScrollingDisabled(true, false);
		
		//Enables smooth scrolling. Its effects are unknown.
		scrollPane.setSmoothScrolling(true);
		
		//Always make the scroll bar appear.
		scrollPane.setFadeScrollBars(false);

	}
	
	/** Creates the profile buttons and adds them to the profileButtons array, and to the profileButtonTable. */
	private void createButtonList()
	{	
		//Instantiates the array which contains all of the entryButtons which serve to access an entry in the survival guide.
		profileButtons = new Array<TextButton>();
		
		//Creates the table which contains the profile buttons to be displayed in a list.
		profileButtonTable = new Table();
		
		//Creates a ButtonListener which will listen to any button clicks coming from the entry buttons.
		buttonListener = new ButtonListener();
		
		//Instantiates the ButtonGroup which ensures that only one profile button can be pressed at a time.
		buttonGroup = new ButtonGroup();
		
		//Stores the amount of profiles that the user has.
		int len = profileManager.getNumProfiles();
		
		//Cycles through all the profiles contained by the profile manager.
		for(int i = 0; i < len; i++)
		{
			//Stores the profile with the given index.
			Profile profile = profileManager.getProfile(i);
			
			//If the profile does not exist, throw an exception
			if(profile == null)
			{
				throw new RuntimeException("The profile with index " + i + " is null inside the profileManager. Thus, the world select list can't be created.");
			}
			//Else, if the profile exists, populate the list item with a string representation of the profile.
			else
			{
				//Create a profile button for the profile we are cycling through so that it can be shown in the profile list.
				TextButton profileButton = createProfileButton(i);
				
				//Adds the created profileButton to the list of profile buttons.
				profileButtons.add(profileButton);
				
				//Adds the button to the entry button table.
				profileButtonTable.add(profileButton).width(WORLD_LIST_WIDTH).height(profileButton.getHeight()).row();
			}
		}
	}
	
	/** Creates and returns a profile button which displays the information about a profile. Such a button is placed in the profile list. */
	private TextButton createProfileButton(int profileId)
	{
		//Retrieves the Profile for which the button has to be created.
		Profile profile = profileManager.getProfile(profileId);
		
		//Creates a button which displays information about the profile. Created with a pre-determined ButtonStyle.
		TextButton button = new TextButton(profile.toString(), assets.mainMenuListButtonStyle);
		
		//Ensures that the button always spans the same width as the list which contains it.
		button.setWidth(WORLD_LIST_WIDTH);
		
		//Sets the profileId of the button as its user object. Allows the button to know which profile it should load when selected.
		button.setUserObject(new Integer(profileId));
		
		//Register the ButtonListener to the button, so that it receives an event whenever each profile button is pressed.
		button.addListener(buttonListener);
		
		//Add the profile button to the button group, in order to ensure that only one profile button can be checked at a time.
		buttonGroup.add(button);
		
		//Returns the created profile button.
		return button;
	}
	
	/** Registers the profile buttons which were clicked. */
	private class ButtonListener extends ClickListener
	{
		/** Delegated when the user clicks a button. */
		@Override
		public void clicked(InputEvent event, float x, float y)
		{
			//Retrieves the profile button which was pressed. Since the label on the button can delegate this method call, the label's parent will be the TextButton itself.
			TextButton entryButton = (TextButton)event.getTarget().getParent();
			
			//Grabs the user object of the button, which is the id of the profile which should be loaded when the button is pressed. 
			selectedProfileId = (Integer)entryButton.getUserObject();
		}
	}
	
	/** Deletes the profile with the given index. The index corresponds to the item chosen in the world select list. */
	private void deleteProfile(int index)
	{
		//Deletes the profile that is currently being selected
		profileManager.deleteProfile(selectedProfileId);
		
		//Re-create the GUI so that the profile list is re-created
		this.show();
		//Resizes the stage and the table containing all the widgets to ensure that all widgets are placed correctly. Passes in (guiWidth,guiHeight), the pre-calculated size of the GUI.
		this.resize((int)guiWidth, (int)guiHeight);
		
		//Lets the PreferencesManager know that a profile was deleted. Records that the user now has one less profile.
		prefsManager.profileDeleted(selectedProfileId);
	}

	
	@Override
	public void render(float deltaTime)
	{
		//Sets OpenGL to clear the screen with white.
		Gdx.gl.glClearColor(1,1,1,1);
		//Clear the screen.
		super.render(deltaTime);
		
		//Prepare the widgets inside the stage to be drawn.
		stage.act(deltaTime);
		//Draw the stage, effectively drawing all the 2D widgets to the screen.
		stage.draw();
	}
	
	@Override
	public void resize(int width, int height)
	{
		//Re-compute the value of the guiWidth and guiHeight superclass variables to ensure that the gui fits the new aspect ratio of the screen.
		super.resize(width, height);
		
		//Set the viewport of the stage to the guiWidth/Height member variables. This ensures that the viewable area of the stage is the desired width and height
		//of the GUI. Note that these variables were defined in the superclass to be the desired default width and height of a GUI. However, the smallest dimension
		//is resized to fit the screen's aspect ratio to avoid uneven stretching. If the device's screen is guiWidth x guiHeight, the GUI is pixel perfect.
		stage.setViewport(guiWidth, guiHeight);
		//Sets the bounds of the table. This is essentially the largest the table can be. We set it to the full width of the GUI for the table to fill the screen.
		table.setBounds(0, 0, guiWidth, guiHeight);	
		
		//Positions the background at the center of the stage, using the given constants as offsets. Note that the background's position is denoted by its bottom-left corner.
		worldSelectBackground.setPosition(stage.getWidth()/2 - worldSelectBackground.getWidth()/2 + BACKGROUND_X_OFFSET, 
										  stage.getHeight()/2 - worldSelectBackground.getHeight()/2 + BACKGROUND_Y_OFFSET);
		
		//Anchors the back button to the bottom-right of the screen, using the given offsets. Note that button positions are the bottom-left of the buttons.
		backButton.setPosition(stage.getWidth() - backButton.getWidth() - BACK_BUTTON_X_OFFSET, BACK_BUTTON_Y_OFFSET);
	}

	@Override
	public void dispose()
	{
		//Disposes of the SpriteBatcher in the superclass.
		super.dispose();
		
		//Dispose of any resources used by the MainMenuScreen.
		stage.dispose();
	}
	
	@Override
	public void pause() 
	{
	}

	@Override
	public void resume() 
	{
	}
	
	/** Plays a fade in animation when the user enters this screen. */
	public void fadeIn()
	{
		//Makes sure that the table is transparent before making it fade in
		table.setColor(Color.CLEAR);
		//Make the table fade in
		table.addAction(Actions.fadeIn(0.5f));
		//Move the table down
		table.addAction(Actions.moveBy(0, -50));
		//Move the table back up in the given amount of time
		table.addAction(Actions.moveBy(0, 50, 0.75f, Interpolation.exp5Out));
	}
	
	/** Receives input-related events, such as the user pressing the BACK button on his Android device. */
	private class InputListener extends InputAdapter
	{
		/** Called when a key is pressed. */
		@Override
		public boolean keyDown(int keycode)
		{
			//If the Android BACK button has been pressed
			if(keycode == Keys.BACK)
			{
				//Lets the WorldSelectScreen know that the user should be brought back to the main menu
				backPressed();
			}
			
			return false;
		}
	}
	
	/** Called when either the visual BACK button is pressed, or when the Android BACK button is pressed. Move the user back to the main menu, */
	public void backPressed()
	{
		//Return to the game selection screen once the "Back" button is pressed.
		game.setScreen(new GameSelectScreen(game));
	}
}
