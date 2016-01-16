Free the Bob
========

![freethebob-banner](https://cloud.githubusercontent.com/assets/10332234/5578811/04ddecd6-8ffd-11e4-81c4-e1f9c7883374.jpg)

Free the Bob is a survival game built for Android devices. You play Bob, a gamer stuck in a virtual reality. As the
player, your task is to gather resources and craft weapons in order to survive in a forest infested by zombies. 

### Try it
  * [Windows/Mac/Linux](https://drive.google.com/uc?export=download&id=0B6MbXVer0CxbQ1VwYzJzeXIwMmc)
  * [Android](http://cs.mcgill.ca/~jlucui/portfolio/dls/free_the_bob/survivor-android.apk)

  
Controls
-----
  * <strong>Touch</strong> to interact with objects (Note: can't chop trees unless you craft an axe)
  * <strong>Swipe up/down</strong> - Jump up/down a layer
  
Features
-----

### Combat
Free the Bob features simple turn-based combat, wherein the player can either choose
to <b>jump</b>, <b>melee</b>, or <b>fire</b>. Watch out, however - you
have to craft either an axe or a rifle and bullets before you can use 
the <b>melee</b> or <b>fire</b> buttons.

If you don't have any weapons in your arsenal, jumping on a zombie's head
is the only way to damage its health.

![combat](https://cloud.githubusercontent.com/assets/10332234/5574656/f23d9162-8f93-11e4-806a-d1a09b85ba0d.jpg)

### Crafting
The crafting menu, accessible from the backpack, allows the player to combine
his collected resources in order to build weapons. The survival guide in the
backpack features a list of all crafting possibilities, along with the resources
necessary to create them.

![crafting-menu](https://cloud.githubusercontent.com/assets/10332234/5574666/5c549a28-8f94-11e4-8f73-ef9235320976.jpg)
  
### Procedurally-Generated World

The terrain in Free the Bob is procedurally-generated at run-time, updating dynamically as the player walks around the terrain.
The worlds are infinite and created using a world seed which spawns objects and defines the terrain's geometry.  

![terrain-showcase](https://cloud.githubusercontent.com/assets/10332234/5574696/050ca782-8f95-11e4-9ac4-4d9c51fa7962.jpg)

The forest is stored as a matrix of TerrainLayer objects. Each layer is defined by either a constant, linear, or trigonometric
curve which the player can walk accross using the directional buttons.

Code
-----

The game is coded using Java and the LibGDX framework. It leverages the Android's touch capabilities and is built from the
ground up with a touch-screen in mind.

![bob-sleeping](https://cloud.githubusercontent.com/assets/10332234/5578796/ae6a42dc-8ffc-11e4-84cd-c43989d7f257.jpg)

