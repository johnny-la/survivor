Free the Bob
========

![project-banner](https://cloud.githubusercontent.com/assets/10332234/5574662/284a9868-8f94-11e4-9c3f-b819cc3004d4.png)

Free the Bob is a survival game built for Android devices. You play Bob, a gamer stuck in a virtual game world. His only task is to
to gather resources and craft weapons in order to survive in a forest infested by zombies. 

### Try it
  * [Windows](http://cs.mcgill.ca/~jlucui/free_the_bob/FreeTheBob.jar)
  * [Android](http://cs.mcgill.ca/~jlucui/free_the_bob/survivor-android.apk)
  
Controls
-----
  * <strong>Touch objects</strong> to interact with them
  * <strong>Swipe up/down</strong> - Jump up/down a layer
  
Features
-----

### Combat
Free the Bob features simple turn-based combat, wherein the player can choose
to either <b>jump</b>, <b>melee</b>, or <b>fire</b>. Watch out - you
must craft the necessary weapons first before using using the <b>melee</b> or
<b>fire</b> buttons.

If you don't have any weapons in your arsenal, jumping on a zombie's head
is the only way to damage his health.

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

![terrain-showcase](https://cloud.githubusercontent.com/assets/10332234/5574659/13af02fe-8f94-11e4-8226-889b804916fd.jpg)

### Forest Creation Algorithm

The forest is stored as a matrix of TerrainLayer objects. Each layer is defined by either a constant, linear, or trigonometric
curve which the player can walk accross using the directional buttons.

Code
-----

The game is coded using Java and the LibGDX framework. It leverages the Android's touch capabilities and is built from the
ground up with a touch-screen in mind.
