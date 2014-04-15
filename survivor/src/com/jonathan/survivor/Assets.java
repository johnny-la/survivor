package com.jonathan.survivor;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.ui.Button.ButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton.ImageButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.List.ListStyle;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane.ScrollPaneStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Window.WindowStyle;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;
import com.badlogic.gdx.utils.Array;
import com.esotericsoftware.spine.Animation;
import com.esotericsoftware.spine.SkeletonData;
import com.esotericsoftware.spine.SkeletonJson;
import com.esotericsoftware.spine.SkeletonRenderer;
import com.jonathan.survivor.inventory.Axe;
import com.jonathan.survivor.inventory.Rifle;

/** Loads all visual/audio assets needed by the game and stores them in public static variables. An asset is fetched from this class whenever something needs
 *  to be drawn on screen or played to the speakers.
 * @author Jonathan
 *
 */

/* A TextureAtlas is created by passing the .txt file of the atlas created by TexturePacker.
 * A sprite is retrieved from this atlas using the TextureAtlas.createSprite("Name of Sprite"):Sprite method.
 * A texture is retrieved from the atlas using TextureAtlas.findTexture("Name of Texture"):AtlasTexture.
 * 
 * Fonts are loaded from a True-Type font using new FreeTypeFontGenerator("font.ttf").  Then, a BitmapFont is created from the .tff using generator.generateFont(size:int). 
 * Note that each font has a default size. Every label is placed in the world with this default size in mind. However, depending on the screen size, We want to choose a larger
 * size. Let's say the device is two times larger than our target resolution. In that case, we choose a two times larger font by multiplying the default size by fontScale:float.
 * Then, we take the font, and scale it down by the same font scale. Like this, the labels using this font are still the same size. We don't have to use a different coordinate
 * system. However, the quality remains good no matter the screen size.
 * 
 * A Drawable is retrieved from a Skin using Skin.getDrawable("Name"), which returns the Drawable for the sprite in the atlas with the given name. For instance, buttonStyle.up
 * = Skin.getDrawable("button_up") sets the 'up' state of a button to display the sprite with name "button_up" inside the texture atlas registered to the skin.
 * 
 */

public class Assets 
{
	/** Stores a singleton instance of the Assets class. All assets are loaded and stored in one instance for easy access. */
	public static Assets instance;	
	/** Stores an AssetManager instance. This allows for loading on a separate thread from the render thread. All assets are loaded through object.*/
	private AssetManager manager = new AssetManager();	
	
	/** Stores a file extension (i.e., "@4x", "@2x", "")  telling us which atlases to load depending on screen size. */
	public final String scaleExtension;	
	/** Stores the scale of the textures relative to the @1x textures (i.e., 4, 2, or 1). We downscale the sprites by this scale so that they take the same space as the @1x textures, to 
	 * avoid re-scaling our coordinate system. */
	public final int scaleFactor;	
	/** Stores the amount we scale our fonts by. If this is two, we choose a font size that is two times larger than default. In this case, it means the screen is two times larger than our
	 *  target resolution, for which the default font size was catered to. Thus, we need a larger font size to compensate. */
	public final float fontScale;
	
	/** Stores the amount the skeletons must be scaled in order to fit the world coordinates. This is done because the skeletons were made with larger textures than are used. The first
	 * float is the ratio of size of the skeleton using @1x textures compared to the size of the skeleton in Spine. World scale converts the skeleton's size from pixels to meters. */
	public static final float PLAYER_SKELETON_SCALE = 0.156f * Survivor.WORLD_SCALE;
	public static final float PLAYER_SKELETON_UI_SCALE = 0.21f;
	public static final float ZOMBIE_SKELETON_SCALE = 0.173f * Survivor.WORLD_SCALE;
	public static final float TREE_SKELETON_SCALE = 0.25f * Survivor.WORLD_SCALE;
	public static final float BOX_SKELETON_SCALE = 0.25f * Survivor.WORLD_SCALE;
	public static final float ITEM_SKELETON_SCALE = 0.25f * Survivor.WORLD_SCALE;
	public static final float PROJECTILE_SKELETON_SCALE = 0.25f * Survivor.WORLD_SCALE;
	public static final float BACKGROUND_TILE_SCALE = Survivor.WORLD_SCALE;
	public static final float VERSUS_ANIM_SKELETON_SCALE = 0.25f * Survivor.WORLD_SCALE;
	public static final float KO_ANIM_SKELETON_SCALE = 0.25f * Survivor.WORLD_SCALE;
	
	/** Holds the width and height of an item's sprite in an inventory. Used to resize sprites to the correct scale for inventories. */
	public static final float INVENTORY_ITEM_WIDTH = 32, INVENTORY_ITEM_HEIGHT = 32;
	
	/** Stores the amount certain buttons are offset when pressed. */
	public static final float BUTTON_PRESSED_OFFSET = 5;
	
	//Stores the assets used by the loading screen and company splash screen.
	public TextureAtlas loadingScreenAtlas;
	public Sprite companyLogo;
	public Sprite mugishaLogo;
	public Sprite loadingBackground;
	public LabelStyle loadingLabelStyle;
	
	//Stores the assets used by the main menu screen.
	public TextureAtlas mainMenuAtlas;
	private FreeTypeFontGenerator moonFlowerBoldGenerator;
	public BitmapFont moonFlowerBold_54;
	public BitmapFont moonFlowerBold_38;
	private FreeTypeFontGenerator sanchezRegularGenerator;
	public BitmapFont sanchezRegular_17;
	
	public Skin mainMenuSkin;	//We register an atlas to this skin (the main menu atlas). This lets us retrieve sprites from the atlas to use with 2D widgets.
	public TextButtonStyle mainMenuButtonStyle;	//Defines the look of main menu buttons
	public TextButtonStyle gameSelectButtonStyle;	//Defines the look of the buttons in the GameSelectScreen.
	public ButtonStyle deleteButtonStyle;	//Holds the ButtonStyle used to dictate the look of the delete button in the WorldSelectScreen.
	public LabelStyle mainMenuHeaderStyle;	//Defines the look of the headers in the main menu.
	public TextButtonStyle mainMenuListButtonStyle;	//Defines the look of the buttons in the world selection list in the WorldSelectScreen.
	public ScrollPaneStyle mainMenuScrollPaneStyle;	//Defines the look of the scroll pane in the WorldSelectMenu
	
	public NinePatch confirmDialogNinePatch;	//Stores the 9-patch used as the background for a confirm dialog.
	public WindowStyle confirmDialogWindowStyle;	//Needed for the constructor of the Dialog superclass. Does not define the look of the confirmDialog, but is nevertheless required.
	
	//Stores the assets used by the game.
	public TextureAtlas hudAtlas;
	public Skin hudSkin;
	public ButtonStyle circleButtonStyle;
	public ImageButtonStyle leftArrowButtonStyle;
	public ImageButtonStyle rightArrowButtonStyle;
	public ImageButtonStyle jumpButtonStyle;
	public ImageButtonStyle meleeButtonStyle;
	public ImageButtonStyle fireButtonStyle;
	public ButtonStyle backpackButtonStyle;	//The button on the top-left corner of the screen to open the backpack.
	public ButtonStyle pauseButtonStyle;
	
	public TextureAtlas backpackBgAtlas;
	public TextureRegion backpackBgRegion;
	public TextureAtlas survivalGuideBgAtlas;
	public TextureRegion survivalGuideBgRegion;
	public TextureRegion gameOverTextRegion;
	public ButtonStyle survivalGuideButtonStyle;
	public ButtonStyle craftingButtonStyle;	//"Crafting" button displayed in backpack menu
	public TextButtonStyle survivalGuideListButtonStyle; //The buttons which display the entries in the survival guide.
	public ScrollPaneStyle inventoryScrollPaneStyle;
	public ButtonStyle craftButtonStyle;	//"Craft" button displayed in Crafting Menu
	public ButtonStyle backButtonStyle;
	public LabelStyle hudHeaderStyle;
	public LabelStyle hudLabelStyle;
	public LabelStyle smallLabelStyle;
	public LabelStyle gameOverLabelStyle;
	
	public TextureAtlas versusAnimAtlas;
	public SkeletonJson versusAnimSkeletonJson;
	public SkeletonData versusAnimSkeletonData;
	public Animation versusPlay;
	
	public TextureAtlas koAnimAtlas;
	public SkeletonJson koAnimSkeletonJson;
	public SkeletonData koAnimSkeletonData;
	public Animation koPlay;
	
	public TextureAtlas playerAtlas;
	public SkeletonJson playerSkeletonJson;
	public SkeletonData playerSkeletonData;
	public SkeletonData playerSkeletonData_UI; //Stores the SkeletonData used for the player drawn in UIs. The only difference is that the skeleton is scaled in pixel units.
	public Animation playerIdle;
	public Animation playerIdle_Combat;
	public Animation playerWalk;
	public Animation playerJump;
	public Animation playerJump_Combat;
	public Animation playerDoubleJump;
	public Animation playerFall;
	public Animation playerChopTree;
	public Animation playerChopTree_Start;
	public Animation playerEnterCombat;
	public Animation playerMelee;
	public Animation playerCharge_Start;
	public Animation playerCharge;
	public Animation playerFire;
	public Animation playerHit;
	public Animation playerDead;
	public Animation playerTeleport;
	public Animation playerSleep;
	public Animation playerSleep_Alert;
	
	public TextureAtlas zombieAtlas;
	public SkeletonJson zombieSkeletonJson;
	public SkeletonData zombieSkeletonData;
	public Animation zombieIdle;
	public Animation zombieWalk;
	public Animation zombieAlerted;
	public Animation zombieEnterCombat;
	public Animation zombieMelee;
	public Animation zombieCharge_Start;
	public Animation zombieCharge;
	public Animation zombieSmash;
	public Animation zombieHitHead;
	public Animation zombieDead;
	
	public TextureAtlas interactableObjectAtlas;
	public SkeletonJson treeSkeletonJson;
	public SkeletonData treeSkeletonData;
	public Animation treeIdle;
	public Animation treeClicked;
	public Animation treeHit;
	public Animation treeScavenged;
	
	public SkeletonJson boxSkeletonJson;
	public SkeletonData boxSkeletonData;
	public Animation boxIdle;
	public Animation boxClicked;
	public Animation boxScavenged;
	
	public SkeletonJson itemSkeletonJson;
	public SkeletonData itemSkeletonData;
	public Animation itemFly;
	public Animation itemGrounded;
	public Animation itemClicked;
	
	public SkeletonJson projectileSkeletonJson;
	public SkeletonData projectileSkeletonData;
	public Animation projectileIdle;
	
	public Sprite woodSprite;	//Stores the sprites for each item when displayed in an inventory. Note these are template sprites which are copied. These copies are stored inside pools.
	public Sprite ironSprite;
	public Sprite waterSprite;
	public Sprite charcoalSprite;
	public Sprite sulfurSprite;
	public Sprite saltpeterSprite;
	public Sprite gunpowderSprite;
	public Sprite bulletSprite;
	public Sprite teleporterSprite;
	public Sprite axeSprite;
	public Sprite rifleSprite;
	
	public Sprite snow1;
	public Sprite snow2;
	public Sprite snow3;
	public Sprite snow4;
	
	public SkeletonRenderer skeletonRenderer; //Renderer used to draw spine skeletons with a SpriteBatch.
	
	//Stores the music used by the application.
	public Music mainMenuMusic;
	
	//Stores the sounds used by the application.
	public Sound buttonClick;
	public Sound swoosh;
	
	
	public Assets()
	{
		//Finds the amount we have to scale the width and height of the viewport to fit the device. To do this, we take the screen width/height, and divide it by the
		//DEFAULT_GUI_WIDTH/HEIGHT. These constants store the target resolution of the app. Thus, if we are on a 1920x1080 device, and the target resolution is 480x320,
		//scaleX = 1920/480 = 4, and scaleY = 3.4. Thus, since all our default textures are scaled for 480x320, it means we have to take the @4x sprites to make the 
		//textures look crisp.
		float scaleX = Gdx.graphics.getWidth() / Survivor.DEFAULT_GUI_WIDTH;
		float scaleY = Gdx.graphics.getHeight() / Survivor.DEFAULT_GUI_HEIGHT;
		
		//Compute the amount we scale our fonts by. We choose the minimum of scaleX and Y so that we don't scale the fonts excessively large to prevent large texture sizes.
		//This float will usually be between 1 and 4, depending on how large the device is.
		fontScale = Math.min(scaleX, scaleY);
		
		//If the maximum scale difference between target resolution and screen dimensions is >= 4, we scale up our textures by four by choosing the @4x texture atlases.
		//Note that we choose the maximum of the two scales to choose the larger textures more often.
		if(Math.max(scaleX, scaleY) >= 4)
		{
			//Assign a scale extension of @4x, and a scale factor of 4, since our textures should be four times larger than default.
			scaleExtension = "@4x";
			scaleFactor = 4;
		}
		//If the maximum scale difference between target resolution and screen dimensions is >= 2, we scale our textures two times by choosing the @2x texture atlases.
		else if(Math.max(scaleX, scaleY) >= 2)
		{
			//Assign a scale extension for image files of @2x and a scale factor of 2, since our textures should be two times larger than default.
			scaleExtension = "@2x";
			scaleFactor = 2;
		}
		//Else, if the actual screen size is not more than two times larger, choose the default texture atlases, made to look crisp on small screens.
		else
		{
			//Assign an empty scale extension, so that we choose the @1x texture atlases. Assign a scale factor of '1' since our textures are not scaled.
			scaleExtension = "";
			scaleFactor = 1;
		}
		
		//Queue all of the assets for loading. We have to queue assets inside the AssetManager instance before actually loading them.
		queueAssetsForLoading();
	}
	
	/** Loads the assets used by the company splash screen and the loading screen. We need to load their assets first before going to the loading screen
	 *  and loading the all of the assets.
	 */
	public void loadInitialAssets()
	{
		//Loads the atlases and sprites used by the loading screen and company splash screens.
		loadingScreenAtlas = new TextureAtlas(Gdx.files.internal("ui/loading screen/loading_items_atlas" + scaleExtension + ".txt"));
		companyLogo = loadingScreenAtlas.createSprite("Company Logo");
		mugishaLogo = loadingScreenAtlas.createSprite("Mugisha Logo");
		
		//Resize the sprites according to the scale factor of the screen. That is, if we are using an @2x texture atlas, scaleFactor=2. So, reduce the sprites
		//to half their size so that they are the same size as the original @1x textures. Like this, coordinate systems stay the same no matter the texture 
		//resolution.
		companyLogo.setSize(companyLogo.getWidth()/scaleFactor, companyLogo.getHeight()/scaleFactor);
		mugishaLogo.setSize(mugishaLogo.getWidth()/scaleFactor, mugishaLogo.getHeight()/scaleFactor);
	}
	
	/** Loads the assets which are needed for the loading screen. They are loaded when the splash screen is shown. */ 
	public void loadSplashScreenAssets()
	{
		//Loads the sprites which the splash screen need to load before switching to the loading screen. For instance, the player atlas needs to be loaded to be shown in the loading screen.
		playerAtlas = new TextureAtlas(Gdx.files.internal("game/player/atlas/player_atlas" + scaleExtension + ".txt"));
		loadingBackground = loadingScreenAtlas.createSprite("Loading Background");
		
		//Resizes the loaded sprites to ensure that they are the same scale no matter the atlas size chosen. Ensures that they always take the same screen space.
		loadingBackground.setSize(loadingBackground.getWidth()/scaleFactor, loadingBackground.getHeight()/scaleFactor);
		
		//Sets up the Spine data used to display and animate the player in the loading screen.
		playerSkeletonJson = new SkeletonJson(playerAtlas);
		playerSkeletonJson.setScale(PLAYER_SKELETON_UI_SCALE);	//Re-scale the skeleton to fit GUI camera units. 
		playerSkeletonData_UI = playerSkeletonJson.readSkeletonData(Gdx.files.internal("game/player/skeleton/player_skeleton.json"));		
		//Stores the player's animations which are needed for the loading screen.
		playerSleep = playerSkeletonData_UI.findAnimation("Sleep");
		playerSleep_Alert = playerSkeletonData_UI.findAnimation("Sleep_Alert");

		//Creates the Font Generator for the Sanchez Regular font.
		sanchezRegularGenerator = new FreeTypeFontGenerator(Gdx.files.internal("ui/fonts/sanchez regular/Sanchezregular.otf"));
		//Creates the Sanchez Regular 17pt font. This must be done after the loading is finished because AssetManagers can't load FreeTypeFontGenerators.
		sanchezRegular_17 = sanchezRegularGenerator.generateFont((int)(17 * fontScale));	
		sanchezRegular_17.setScale(sanchezRegular_17.getScaleX() / fontScale);	
		sanchezRegular_17.getRegion().getTexture().setFilter(TextureFilter.Linear, TextureFilter.Linear);
		
		//Creates the LabelStyle which determines the look of the text in the loading screen.
		loadingLabelStyle = new LabelStyle();
		loadingLabelStyle.font = sanchezRegular_17;
		loadingLabelStyle.fontColor = Color.WHITE;
		
		//Creates a new skeleton renderer to draw Spine skeletons using a SpriteBatch instance.
		skeletonRenderer = new SkeletonRenderer();
	}
	
	/** Queues all assets for loading. Loading is performed every time the updateLoading() method is called. Before calling updateLoading(), the AssetManager must know
	 *  which assets to load. This method puts all assets to queue inside the AssetManager instance.
	 */
	private void queueAssetsForLoading()
	{
		//Queues the main menu's assets for loading.
		queueMainMenuAssets();
		
		//Queues the game's assets for loading.
		queueGameAssets();
	}
	
	/** Queues the main menu's assets for loading. Loading is performed every time the updateLoading() method is called. Before calling updateLoading(), the AssetManager
	 *  which assets to load. This method puts all main menu assets assets to queue inside the AssetManager instance.
	 */
	public void queueMainMenuAssets()
	{
		//Put all assets to queue for loading using the AssetManager.load("fileName", class) method.
		manager.load("ui/main menu/atlas/main/main_menu_atlas" + scaleExtension + ".pack", TextureAtlas.class);
		
		//Puts music assets to queue inside the AssetManager using AssetManager.load("fileName", class).
		//manager.load("sound/music/Ashton Manor.mp3", Music.class);
		
		//Puts sound assets to queue for loading inside the AssetManager using AssetManager.load("fileName", class).
		manager.load("sound/sfx/ui/ButtonClick.ogg", Sound.class);
		manager.load("sound/sfx/ui/Swoosh.ogg", Sound.class);
	}
	
	/** Queues the game's assets for loading. Loading is performed every time the updateLoading() method is called. Before calling updateLoading(), the AssetManager which
	 *  assets to load. This method puts all game assets assets to queue inside the AssetManager instance.
	 */
	private void queueGameAssets()
	{
		//Put all assets to queue for loading using the AssetManager.load("fileName", class) method.
		manager.load("game/zombie/atlas/zombie_atlas" + scaleExtension + ".txt", TextureAtlas.class);
		manager.load("game/interactable_objects/atlas/interactable_objects_atlas" + scaleExtension + ".txt", TextureAtlas.class);
		manager.load("ui/hud/general/atlas/hud_atlas" + scaleExtension + ".pack", TextureAtlas.class);
		manager.load("ui/hud/backpack_bg/atlas/backpack_bg_atlas" + scaleExtension + ".txt", TextureAtlas.class);
		manager.load("ui/hud/survivalguide_bg/atlas/survivalguide_bg_atlas" + scaleExtension + ".txt", TextureAtlas.class);
		manager.load("ui/hud/versus_hud/atlas/versus_hud_atlas" + scaleExtension + ".txt", TextureAtlas.class);
		manager.load("ui/hud/ko_hud/atlas/ko_hud_atlas" + scaleExtension + ".txt", TextureAtlas.class);
		
		//Puts music assets to queue inside the AssetManager using AssetManager.load("fileName", class).
		
		//Puts sound assets to queue for loading inside the AssetManager using AssetManager.load("fileName", class).
	}
	
	/** Call every frame inside a render() method to load the assets stored inside the AssetManager instance. This performs loading on a separate thread so that the render() 
	 *  method which calls this method can also update its appearance.
	 * @return Returns true if the loading is complete.
	 */
	public boolean updateLoading()
	{
		//Calls the manager.update() method, which loads the assets queued inside this AssetManager instance. The assets were queued in queueAssetsForLoading(). The method
		//returns true if the loading is complete.
		boolean loadingComplete = manager.update();
		
		//If the loading is complete
		if(loadingComplete)
			//Store the loaded assets into the object's local variables. Like this, any class can easily get a hold of all assets using the Assets singleton.
			storeLoadedAssets();
		
		//Return true if the loading has completed.
		return loadingComplete;
	}
	
	/** Stores the assets loaded from the AssetManager into their respective member variables. Like this, any class with access to the singleton can easily access the assets
	 *  loaded from this class. */
	private void storeLoadedAssets()
	{
		/* Retrieve the loaded assets from the AssetManager using AssetManager.get("fileName"):class. */
		
		//Retrieves the assets for the main menu.
		mainMenuAtlas = manager.get("ui/main menu/atlas/main/main_menu_atlas" + scaleExtension + ".pack");
		
		//Retrieves the assets for the game.
		zombieAtlas = manager.get("game/zombie/atlas/zombie_atlas" + scaleExtension + ".txt");
		interactableObjectAtlas = manager.get("game/interactable_objects/atlas/interactable_objects_atlas" + scaleExtension + ".txt");
		hudAtlas = manager.get("ui/hud/general/atlas/hud_atlas" + scaleExtension + ".pack");
		backpackBgAtlas = manager.get("ui/hud/backpack_bg/atlas/backpack_bg_atlas" + scaleExtension + ".txt");
		survivalGuideBgAtlas = manager.get("ui/hud/survivalguide_bg/atlas/survivalguide_bg_atlas" + scaleExtension + ".txt");
		versusAnimAtlas = manager.get("ui/hud/versus_hud/atlas/versus_hud_atlas" + scaleExtension + ".txt");
		koAnimAtlas = manager.get("ui/hud/ko_hud/atlas/ko_hud_atlas" + scaleExtension + ".txt");
		
		//Retrieves the music files.
		//mainMenuMusic = manager.get("sound/music/Ashton Manor.mp3");
		
		//Retrieves sound files
		buttonClick = manager.get("sound/sfx/ui/ButtonClick.ogg", Sound.class);
		swoosh = manager.get("sound/sfx/ui/Swoosh.ogg", Sound.class);
		
		/* Loads any assets that couldn't be loaded using the AssetManager. */
		loadExtraAssets();	
	}
	
	/** Loads any extra assets that couldn't be loaded using the AssetManager. */
	private void loadExtraAssets()
	{
		//Loads the assets used by the main menu which couldn't be loaded by an AssetManager.
		loadMainMenuAssets();
		
		//Loads the assets used in-game.
		loadGameAssets();
	}
	
	/** Loads the assets used by the main menu which can't be loaded by the Asset Manager in the updateLoading() method, such as TTF fonts or button styles. MUST be called after
	 *  loading in the loading screen is complete, and and after updateLoading() returns true. */
	public void loadMainMenuAssets()
	{
		//Creates the Moon Flower Bold 54pt font. This must be done after the loading is finished because AssetManagers can't load FreeTypeFontGenerators.
		moonFlowerBoldGenerator = new FreeTypeFontGenerator(Gdx.files.internal("ui/fonts/moon flower bold/Moon Flower Bold.ttf"));
		moonFlowerBold_54 = moonFlowerBoldGenerator.generateFont((int)(54 * fontScale));
		moonFlowerBold_54.setScale(moonFlowerBold_54.getScaleX() / fontScale);
		moonFlowerBold_54.getRegion().getTexture().setFilter(TextureFilter.Linear, TextureFilter.Linear);
		
		//Creates the Moon Flower Bold 38pt font. This must be done after the loading is finished because AssetManagers can't load FreeTypeFontGenerators.
		moonFlowerBold_38 = moonFlowerBoldGenerator.generateFont((int)(38 * fontScale));	
		moonFlowerBold_38.setScale(moonFlowerBold_38.getScaleX() / fontScale);	
		moonFlowerBold_38.getRegion().getTexture().setFilter(TextureFilter.Linear, TextureFilter.Linear);
		
		//Creates the skin and the styles used for the main menu GUI.
		mainMenuSkin = new Skin(mainMenuAtlas);
		
		//Creates the TextButtonStyle which dictates the look of the main menu buttons
		mainMenuButtonStyle = new TextButtonStyle();
		mainMenuButtonStyle.up = mainMenuSkin.getDrawable("ClickButton_Up");	//Sets the sprite for the 'up' state of the main menu buttons
		mainMenuButtonStyle.down = mainMenuSkin.getDrawable("ClickButton_Down");	//Sets the sprite for the 'down' state of the main menu buttons
		mainMenuButtonStyle.font = moonFlowerBold_54;

		//Creates the TextButtonStyle which dictates the look of the "New Game", "Continue", and "Load" buttons in the GameSelectScreen.
		gameSelectButtonStyle = new TextButtonStyle();
		gameSelectButtonStyle.up = mainMenuSkin.getDrawable("ClickButton_Up");	//Sets the sprite for the 'up' state of the main menu buttons
		gameSelectButtonStyle.down = mainMenuSkin.getDrawable("ClickButton_Down");	//Sets the sprite for the 'down' state of the main menu buttons
		gameSelectButtonStyle.font = moonFlowerBold_54;
		gameSelectButtonStyle.fontColor = Color.WHITE;
		
		//Instantiates the ButtonStyle instance used to display the delete button in the WorldSelectMenu.
		deleteButtonStyle = new ButtonStyle();
		deleteButtonStyle.up = mainMenuSkin.getDrawable("DeleteButton");
		deleteButtonStyle.down = deleteButtonStyle.down;
		
		//Creates the LabelStyle used to determine the appearance of headers in the main menu. A gray color is chosen as a second argument.
		mainMenuHeaderStyle = new LabelStyle(moonFlowerBold_54, new Color(0.2941f, 0.3216f, 0.2316f, 1f));
		
		//Creates the button style used by the profile buttons in the WorldSelectScreen's list.
		mainMenuListButtonStyle = new TextButtonStyle();
		mainMenuListButtonStyle.font = moonFlowerBold_38;
		mainMenuListButtonStyle.fontColor = new Color(0.5f, 0.5f, 0.5f, 1);	//If unselected, the text for the item in the world selection list will be light gray.
		mainMenuListButtonStyle.checkedFontColor = Color.WHITE;	//If selected, the button text will be white.
		mainMenuListButtonStyle.checked = mainMenuSkin.getDrawable("ListSelection"); //The background of the button when it is selected
		mainMenuListButtonStyle.pressedOffsetX = 1.5f;
		mainMenuListButtonStyle.pressedOffsetX = -2;
		
		//Instantiates the ScrollPaneStyle which dictates the background and scrolling knob images used by the world list in the WorldSelectMenu.
		mainMenuScrollPaneStyle = new ScrollPaneStyle();
		mainMenuScrollPaneStyle.background = hudSkin.getDrawable("List_Background");
		mainMenuScrollPaneStyle.vScrollKnob = hudSkin.getDrawable("ScrollKnob");
		
		//Retrieves the 9-patch from the main menu atlas used to display the confirm dialog's background
		confirmDialogNinePatch = mainMenuSkin.getPatch("ConfirmDialog");
		
		//Creates the WindowStyle used to define the look of the confirm dialog. The only useful argument is the last, which defines the background of the dialog.
		confirmDialogWindowStyle = new WindowStyle(moonFlowerBold_54, new Color(0.2941f, 0.3216f, 0.2316f, 1f), new NinePatchDrawable(confirmDialogNinePatch));
	}
	
	/** Loads the assets used in-game which couldn't be loaded by the Asset Manager in the updateLoading() method, such as SkeletonJson files. MUST be called after
	 *  loading in the loading screen is complete, and and after updateLoading() returns true. Otherwise, there will be certain atlases and assets that won't be loaded
	 *  that will cause NullPointerExceptions. */
	public void loadGameAssets()
	{		
		//Loads the assets needed for the HUD
		hudSkin = new Skin(hudAtlas);
		
		//Creates the style for the circle button
		circleButtonStyle = new ButtonStyle();		
		
		//Fetches the CircleButton sprite from the atlas and makes a dark gray version of it.
		Sprite circleButtonDown = hudSkin.getSprite("CircleButton");
		circleButtonDown.setColor(Color.BLACK);
		SpriteDrawable circleButtonDownDrawable = new SpriteDrawable(circleButtonDown);
		
		//Register the normal circle button as the 'up' image, and the dark gray one as the 'down' image.
		circleButtonStyle.up = hudSkin.getDrawable("CircleButton");
		circleButtonStyle.down = circleButtonDownDrawable;
		
		//Fetches the arrow sprite from the hud atlas to place on the left arrow button.
		Sprite leftArrow = hudSkin.getSprite("Arrow");
		//Scale the arrow down so that, no matter the atlas size used, the arrow takes the same world space.
		leftArrow.setSize(leftArrow.getWidth()/scaleFactor, leftArrow.getHeight()/scaleFactor);
		SpriteDrawable leftArrowDrawable = new SpriteDrawable(leftArrow);
		
		//Create a style for the left arrow button
		leftArrowButtonStyle = new ImageButtonStyle(circleButtonStyle);
		leftArrowButtonStyle.imageUp = leftArrowDrawable;
		leftArrowButtonStyle.imageDown = leftArrowDrawable;
		
		//Fetches the arrow sprite from the hud atlas and flips it to point to the right
		Sprite rightArrow = hudSkin.getSprite("Arrow");
		rightArrow.setScale(rightArrow.getScaleX() * -1, rightArrow.getScaleY());	//Flips the direction of the arrow.
		//Scale the arrow down so that, no matter the atlas size used, the arrow takes the same world space.
		rightArrow.setSize(rightArrow.getWidth()/scaleFactor, rightArrow.getHeight()/scaleFactor);
		rightArrow.setOrigin(rightArrow.getWidth()/2, 0);	//Centers the arrow's pivot point so that it gets placed at the center of the button. 
		SpriteDrawable rightArrowDrawable = new SpriteDrawable(rightArrow);	//Creates a drawable to place on the right arrow's ImageButtonStyle.
		
		//Creates the style for the right arrow button
		rightArrowButtonStyle = new ImageButtonStyle(circleButtonStyle);
		rightArrowButtonStyle.imageUp = rightArrowDrawable;
		rightArrowButtonStyle.imageDown = rightArrowDrawable;
		
		//Fetches the jump sprite from the hud atlas to place on the jump button.
		Sprite jumpImage = hudSkin.getSprite("Jump");
		//Scale the jump sprite down so that, no matter the atlas size used, the arrow takes the same world space.
		jumpImage.setSize(jumpImage.getWidth()/scaleFactor, jumpImage.getHeight()/scaleFactor);
		SpriteDrawable jumpImageDrawable = new SpriteDrawable(jumpImage);	//Creates a Drawable wrapper for the sprite.
		
		//Create a style for the jump button
		jumpButtonStyle = new ImageButtonStyle(circleButtonStyle);
		jumpButtonStyle.imageUp = jumpImageDrawable;
		jumpButtonStyle.imageDown = jumpImageDrawable;
		
		//Fetches the melee sprite from the hud atlas to place on the melee button.
		Sprite meleeImage = hudSkin.getSprite("Knife");
		//Scale the melee sprite down so that, no matter the atlas size used, the arrow takes the same world space.
		meleeImage.setSize(meleeImage.getWidth()/scaleFactor, meleeImage.getHeight()/scaleFactor);
		SpriteDrawable meleeImageDrawable = new SpriteDrawable(meleeImage);	//Creates a Drawable wrapper for the sprite.
		
		//Create a style for the melee button
		meleeButtonStyle = new ImageButtonStyle(circleButtonStyle);
		meleeButtonStyle.imageUp = meleeImageDrawable;
		meleeButtonStyle.imageDown = meleeImageDrawable;
		
		//Fetches the gun sprite from the hud atlas to place on the gun button.
		Sprite gunImage = hudSkin.getSprite("Gun");
		//Scale the gun sprite down so that, no matter the atlas size used, the arrow takes the same world space.
		gunImage.setSize(gunImage.getWidth()/scaleFactor, gunImage.getHeight()/scaleFactor);
		SpriteDrawable gunImageDrawable = new SpriteDrawable(gunImage);	//Creates a Drawable wrapper for the sprite.
		
		//Create a style for the fire button
		fireButtonStyle = new ImageButtonStyle(circleButtonStyle);
		fireButtonStyle.imageUp = gunImageDrawable;
		fireButtonStyle.imageDown = gunImageDrawable;
		
		//Creates the style for the backpack button. This is the button that appears on the top-left corner of the screen in exploration mode.
		backpackButtonStyle = new ButtonStyle();
		backpackButtonStyle.up = hudSkin.getDrawable("Backpack");
		backpackButtonStyle.down = backpackButtonStyle.up;
		
		//Creates the pause button's ButtonStyle, to dictate the look of the pause button.
		pauseButtonStyle = new ButtonStyle();
		pauseButtonStyle.up = hudSkin.getDrawable("PauseButton");
		Sprite pauseButtonDown = hudSkin.getSprite("PauseButton");	//Create a sprite out of the pause button
		pauseButtonDown.setColor(Color.GRAY);	//Tint the sprite gray.
		pauseButtonStyle.down = new SpriteDrawable(pauseButtonDown); //Wrap the tinted sprite into a SpriteDrawable to set it as the button's 'down' image.
		
		
		//Retrieves the TextureRegion for the backpack's backgrounds.
		backpackBgRegion = backpackBgAtlas.findRegion("Backpack_BG");
		survivalGuideBgRegion = survivalGuideBgAtlas.findRegion("Backpack_BG0002");
		
		//Retrieves the TextureRegion to display the GameOver text in the GameOverHud
		gameOverTextRegion = hudAtlas.findRegion("GameOverText");
		
		//Creates the style for the Survival Guide button
		survivalGuideButtonStyle = new ButtonStyle();
		survivalGuideButtonStyle.up = hudSkin.getDrawable("SurvivalGuideButton");
		//Creates a sprite from the SurvivalGuideButton, tints it gray so that the button in pressed state is a different color.
		Sprite guideButtonDown = hudSkin.getSprite("SurvivalGuideButton");
		guideButtonDown.setColor(Color.GRAY);
		//Sets the 'down' state of the button to a tinted version of the original button.
		survivalGuideButtonStyle.down = new SpriteDrawable(guideButtonDown);
		survivalGuideButtonStyle.pressedOffsetX = BUTTON_PRESSED_OFFSET;	//Adds an offset to the button when pressed.
		survivalGuideButtonStyle.pressedOffsetY = BUTTON_PRESSED_OFFSET;
		
		//Creates the style for the Crafting button, the button with the chemistry bottle.
		craftingButtonStyle = new ButtonStyle(survivalGuideButtonStyle);
		craftingButtonStyle.up = hudSkin.getDrawable("CraftingButton");
		//Creates a sprite from the CraftingButton, tints it gray so that the button in pressed state is a different color.
		Sprite craftingButtonDown = hudSkin.getSprite("CraftingButton");
		craftingButtonDown.setColor(Color.GRAY);
		//Sets the 'down' state of the button to a tinted version of the original button.
		craftingButtonStyle.down = new SpriteDrawable(craftingButtonDown);
		craftingButtonStyle.pressedOffsetX = BUTTON_PRESSED_OFFSET;	//Adds an offset to the button when pressed.
		craftingButtonStyle.pressedOffsetY = BUTTON_PRESSED_OFFSET;
		
		//Creates the button style used by the survival guide's list of entries.
		survivalGuideListButtonStyle = new TextButtonStyle();
		survivalGuideListButtonStyle.font = moonFlowerBold_38;
		survivalGuideListButtonStyle.fontColor = new Color(0f, 0f, 0f, 1);	//If unselected, the text for the item in the world selection list will be black.
		survivalGuideListButtonStyle.downFontColor = new Color(0.0f, 0.4f, 0.8f, 1);
		//survivalGuideListButtonStyle.up = survivalGuideListButtonStyle.down = mainMenuSkin.getDrawable("ListSelection");
		survivalGuideListButtonStyle.pressedOffsetX = 1f;
		survivalGuideListButtonStyle.pressedOffsetY = -1.5f;
		
		//Instantiates the ScrollPaneStyle which dictates the background and scrolling knob images used by the inventory list which displays inventory items.
		inventoryScrollPaneStyle = new ScrollPaneStyle();
		inventoryScrollPaneStyle.background = hudSkin.getDrawable("List_Background");
		inventoryScrollPaneStyle.vScrollKnob = hudSkin.getDrawable("ScrollKnob");
		
		//Defines the style used by the "Craft" button displayed in the crafting menu.
		craftButtonStyle = new ButtonStyle();
		craftButtonStyle.up = hudSkin.getDrawable("CraftButton");
		Sprite craftButtonDown = hudSkin.getSprite("CraftButton");	//Creates a sprite to display the craftButton when it is pressed
		craftButtonDown.setColor(Color.GRAY);	
		craftButtonStyle.down = new SpriteDrawable(craftButtonDown);	//Registers the tinted sprite as the image drawn when the button is pressed.
		
		//Creates the Style for the BackButton
		backButtonStyle = new ButtonStyle();
		backButtonStyle.up = hudSkin.getDrawable("BackButton");
		Sprite backButtonDown = hudSkin.getSprite("BackButton"); //Creates a sprite from the backpack button and tints it gray.
		backButtonDown.setColor(Color.DARK_GRAY);
		backButtonStyle.down = new SpriteDrawable(backButtonDown);	//Registers the tinted sprite as the image drawn when the back button is down.
		
		//Creates the LabelStyle for the headers displayed in the HUDs.
		hudHeaderStyle = new LabelStyle();
		hudHeaderStyle.font = moonFlowerBold_54;
		hudHeaderStyle.fontColor = Color.BLACK;
		
		//Creates the style for the labels in the HUD displays.
		hudLabelStyle = new LabelStyle();
		hudLabelStyle.font = moonFlowerBold_38;
		hudLabelStyle.fontColor = Color.BLACK;
		
		//Creates the style for the small labels like those in the entries of the survival guide.
		smallLabelStyle = new LabelStyle();
		smallLabelStyle.font = sanchezRegular_17;
		smallLabelStyle.fontColor = Color.BLACK;
		
		//Creates the style which dictates the look of the "Game Over" label in the GameOverHud.
		gameOverLabelStyle = new LabelStyle();
		gameOverLabelStyle.font = moonFlowerBold_54;
		gameOverLabelStyle.fontColor = Color.WHITE;

		//Sets up the Spine data used to display the portion of the UI used to show the versus Hud.
		versusAnimSkeletonJson = new SkeletonJson(versusAnimAtlas);
		versusAnimSkeletonJson.setScale(VERSUS_ANIM_SKELETON_SCALE);	//Re-scale the skeleton to fit world-units. 
		versusAnimSkeletonData = versusAnimSkeletonJson.readSkeletonData(Gdx.files.internal("ui/hud/versus_hud/skeleton/versus_hud_skeleton.json"));		
		//Gets the animations from the SpineUI's SkeletonData instance.
		versusPlay = versusAnimSkeletonData.findAnimation("Play");
		
		//Sets up the Spine data used to display the KO animation when someone dies in COMBAT mode
		koAnimSkeletonJson = new SkeletonJson(koAnimAtlas);
		koAnimSkeletonJson.setScale(KO_ANIM_SKELETON_SCALE);	//Re-scale the skeleton to fit world-units. 
		koAnimSkeletonData = koAnimSkeletonJson.readSkeletonData(Gdx.files.internal("ui/hud/ko_hud/skeleton/ko_hud_skeleton.json"));		
		//Gets the animations from the SpineUI's SkeletonData instance.
		koPlay = koAnimSkeletonData.findAnimation("Play");

		//Sets up the Spine data used to display and animate the player in the world. Note that the skeleton JSON was already created inside loadSplashScreenAssets().
		playerSkeletonJson.setScale(PLAYER_SKELETON_SCALE);	//Re-scale the skeleton to fit world-units. Atlas data is read the same no matter the scale of the SkeletonJson.
		playerSkeletonData = playerSkeletonJson.readSkeletonData(Gdx.files.internal("game/player/skeleton/player_skeleton.json"));		
		//Gets the player's animations from the Player's SkeletonData instance.
		playerIdle = playerSkeletonData.findAnimation("Idle");
		playerIdle_Combat = playerSkeletonData.findAnimation("Idle_Combat");
		playerWalk = playerSkeletonData.findAnimation("Walk");
		playerJump = playerSkeletonData.findAnimation("Jump");
		playerJump_Combat = playerSkeletonData.findAnimation("Jump_Combat");
		playerDoubleJump = playerSkeletonData.findAnimation("DoubleJump");
		playerFall = playerSkeletonData.findAnimation("Fall");
		playerChopTree = playerSkeletonData.findAnimation("ChopTree");
		playerChopTree_Start = playerSkeletonData.findAnimation("ChopTree_Start");
		playerEnterCombat = playerSkeletonData.findAnimation("Enter_Combat");
		playerMelee = playerSkeletonData.findAnimation("Melee");
		playerCharge_Start = playerSkeletonData.findAnimation("Charge_Start");
		playerCharge = playerSkeletonData.findAnimation("Charge");
		playerFire = playerSkeletonData.findAnimation("Fire");
		playerHit = playerSkeletonData.findAnimation("Hit");
		playerDead = playerSkeletonData.findAnimation("Dead");
		playerTeleport = playerSkeletonData.findAnimation("Teleport");
		
		//Sets up the Spine data used to display and animate the zombie.
		zombieSkeletonJson = new SkeletonJson(zombieAtlas);
		zombieSkeletonJson.setScale(ZOMBIE_SKELETON_SCALE);	//Re-scale the skeleton to fit world-units. Atlas data is read the same no matter the scale of the SkeletonJson.
		zombieSkeletonData = zombieSkeletonJson.readSkeletonData(Gdx.files.internal("game/zombie/skeleton/zombie_skeleton.json"));		
		//Gets the animations from the Zombie's SkeletonData instance.
		zombieIdle = zombieSkeletonData.findAnimation("Idle");
		zombieWalk = zombieSkeletonData.findAnimation("Walk");
		zombieAlerted = zombieSkeletonData.findAnimation("Alerted");
		zombieEnterCombat = zombieSkeletonData.findAnimation("Enter_Combat");
		zombieMelee = zombieSkeletonData.findAnimation("Melee");
		zombieCharge_Start = zombieSkeletonData.findAnimation("Charge_Start");
		zombieCharge = zombieSkeletonData.findAnimation("Charge");
		zombieSmash = zombieSkeletonData.findAnimation("Smash");
		zombieHitHead = zombieSkeletonData.findAnimation("Hit_Head");
		zombieDead = zombieSkeletonData.findAnimation("Dead");
		
		
		//Sets up the Spine data used to display and animate the trees.
		treeSkeletonJson = new SkeletonJson(interactableObjectAtlas);
		treeSkeletonJson.setScale(TREE_SKELETON_SCALE);
		treeSkeletonData = treeSkeletonJson.readSkeletonData(Gdx.files.internal("game/interactable_objects/tree/tree_skeleton.json"));
		//Loads the animations used by the trees
		treeIdle = treeSkeletonData.findAnimation("Idle");
		treeClicked = treeSkeletonData.findAnimation("Clicked");
		treeHit = treeSkeletonData.findAnimation("Hit");
		treeScavenged = treeSkeletonData.findAnimation("Scavenged");
		
		//Sets up the Spine information needed to animate and display boxes.
		boxSkeletonJson = new SkeletonJson(interactableObjectAtlas);
		boxSkeletonJson.setScale(BOX_SKELETON_SCALE);
		boxSkeletonData = boxSkeletonJson.readSkeletonData(Gdx.files.internal("game/interactable_objects/box/box_skeleton.json"));
		//Fetches the animations that the boxes will use.
		boxIdle = boxSkeletonData.findAnimation("Idle");
		boxClicked = boxSkeletonData.findAnimation("Clicked");
		boxScavenged = boxSkeletonData.findAnimation("Scavenged");
		
		//Sets up the skeleton used by the item GameObjects the user collects on the ground. 
		itemSkeletonJson = new SkeletonJson(interactableObjectAtlas);
		itemSkeletonJson.setScale(ITEM_SKELETON_SCALE);
		itemSkeletonData = itemSkeletonJson.readSkeletonData(Gdx.files.internal("game/item/skeleton/item_skeleton.json"));
		//Stores the animations of an item's skeleton.
		itemFly = itemSkeletonData.findAnimation("Fly");
		itemGrounded = itemSkeletonData.findAnimation("Grounded");
		itemClicked = itemSkeletonData.findAnimation("Clicked");
		
		//Sets up the skeleton used to display projectiles such as an earthquake. 
		projectileSkeletonJson = new SkeletonJson(zombieAtlas);
		projectileSkeletonJson.setScale(PROJECTILE_SKELETON_SCALE);
		projectileSkeletonData = projectileSkeletonJson.readSkeletonData(Gdx.files.internal("game/projectile/skeleton/projectile_skeleton.json"));
		//Stores the animations of the projectile's skeleton.
		projectileIdle = projectileSkeletonData.findAnimation("Idle");
		
		//Creates the sprites displayed in the inventory for each item. These are template sprites which are copied to be placed inside pools.
		woodSprite = interactableObjectAtlas.createSprite("Wood");
		ironSprite = interactableObjectAtlas.createSprite("Iron");
		waterSprite = interactableObjectAtlas.createSprite("Water");
		charcoalSprite = interactableObjectAtlas.createSprite("Charcoal");
		saltpeterSprite = interactableObjectAtlas.createSprite("Saltpeter");
		sulfurSprite = interactableObjectAtlas.createSprite("Sulfur");
		gunpowderSprite = interactableObjectAtlas.createSprite("Gunpowder");
		bulletSprite = interactableObjectAtlas.createSprite("Bullet");
		teleporterSprite = interactableObjectAtlas.createSprite("Teleporter");
		axeSprite = playerAtlas.createSprite(Axe.WEAPON_ATTACHMENT_NAME);	//The name of the sprite in the atlas is the same as the name of the axe's attachment.
		axeSprite.setSize(axeSprite.getWidth()/2, axeSprite.getHeight()/2);	//Resizes the axe sprite to fit an inventory box.
		rifleSprite = interactableObjectAtlas.createSprite(Rifle.WEAPON_ATTACHMENT_NAME);	//Grabs the rifle sprite from the player's atlas.
		//rifleSprite.setSize(rifleSprite.getWidth()/5, rifleSprite.getHeight()/5);	//Resizes the sprite to fit an inventory box.
		//rifleSprite.setOrigin(rifleSprite.getWidth()/2, 0);
		//rifleSprite.setRotation(15);
		
		//Creates the template sprites for the background tiles.
		snow1 = interactableObjectAtlas.createSprite("Snow0001");
		snow2 = interactableObjectAtlas.createSprite("Snow0002");
		snow3 = interactableObjectAtlas.createSprite("Snow0003");
		snow4 = interactableObjectAtlas.createSprite("Snow0004");
		
		//Resizes the background sprites so that they are the same size no matter the scale of the atlas. Note that multiplying by BACKGROUND_TILE_SCALE converts their scale to world units.
		snow1.setSize(BACKGROUND_TILE_SCALE * snow1.getWidth()/scaleFactor, BACKGROUND_TILE_SCALE * snow1.getHeight()/scaleFactor);
		snow2.setSize(BACKGROUND_TILE_SCALE * snow2.getWidth()/scaleFactor, BACKGROUND_TILE_SCALE * snow2.getHeight()/scaleFactor);
		snow3.setSize(BACKGROUND_TILE_SCALE * snow3.getWidth()/scaleFactor, BACKGROUND_TILE_SCALE * snow3.getHeight()/scaleFactor);
		snow4.setSize(BACKGROUND_TILE_SCALE * snow4.getWidth()/scaleFactor, BACKGROUND_TILE_SCALE * snow4.getHeight()/scaleFactor);
	}
	
	/** Returns the loading progress for the assets. Note that this method will return zero if the updateLoading() method has not been called yet. 
	 * 
	 * @return A value between 0.0f and 1.0f indicating the progress of the loading.
	 */
	public float getProgress()
	{
		//Returns a value between 0.0f and 1.0f indicating the progress of the AssetManager. This AssetManager instance is the one which loads the assets.
		return manager.getProgress();
	}
	
	/** Disposes of any assets used by the loading screen or the company splash screen to free up system resources. */
	public void disposeInitialAssets()
	{
		//Disposes of the assets used by the loading screen and the company splash screen.
		loadingScreenAtlas.dispose();
		
		companyLogo = null;
		mugishaLogo = null;
		loadingBackground = null;
		loadingLabelStyle = null;
	}
	
	/** Called on application quit inside the Survivor class. Frees any audio/visual resources used by the application. */
	public void dispose()
	{
		System.out.println("Assets Disposed");
		
		//Dispose of texture atlases, font generators, and other assets which weren't loaded by the AssetManager.
		moonFlowerBoldGenerator.dispose();
		moonFlowerBold_54.dispose();
		moonFlowerBold_38.dispose();
		
		mainMenuSkin.dispose();
		
		//Dispose of all assets loaded by the AssetManager.
		manager.clear();
		manager.dispose();
	}
}
