the primary goal of this is to display my knowledge and abilities at java 
i need to connect the visualiser
   must use abstract classes and interfaces.
   stupid silly game.

we need to finish this game. and fast. 

add sounds to the game. 


in the rooms inventories, on the map, the index number of the item corresponds to the number that the item has when dropped in a room

we are making a game. 
we are making a game.
lets make this game. 1123. lets get moving. 

basic game movement functionality is locked and loaded now. 

need to integrate inventory logic.
every room must have a string. 
   the string is for spatial positioning of the items
      thus each room also needs a list of the exact items they have.

character 
   needs to implement their inventory too
   current positions of the character are inhereted through the creation of the character classes


fix the level counter. 
   each room needs an attribute called time taken to complete. 

each room needs their own unique wall color
each room need to have their own unique map string initialised

have the rooms description printed out when you are in it 


we need buttons for user input in the ui. 
up arrow, down arrow, left, right, clear memory and scan, these should operate the same as keypresses. 

these can go into the bottom row.

then we can put the text flow into one of the sidebars, narrating what you do. 
and in the other we need a current inventory


in the text dialogue bar we can have the timing for each level. 
   you took x seconds to 
            .... you wake up in a fuzzzy half-space

ok. this sounds good.

we also need 
   we need the left bar to be a text dialogue where you can enter commands: thus connecting with the original game engine. 

   Create a basic GUI using JavaFX for richer game interaction (e.g., movement direction, display commands, room descriptions, input fields).
   Implement a model view control design pattern approach to your system
   Implement File I/O to allow saving and loading of game state or configurations.

   game saving
   need game to copy
   save game state
   need gui
   JUnit test cases covering major functionality.
   README with instructions for compiling, running, and using the game.
   Executable JAR with GUI support.



   Advanced Features (Optional/Bonus) only when the above has been completed:

   Integrate Multithreading for timed events or background NPC actions.
   Add Networking capability for multiplayer gameplay.
   

   Some possible gameplay Enhancements:

   Add Items & Inventory: Player can pick up, use, and drop items.
   Introduce Puzzles & Locked Doors: Require keys or codes to progress.
   Create NPC Characters: Add dialogues, quests, and trading.
   Implement Scoring or Objectives to motivate players.
   Extend Room Complexity: Hidden passageways, branching paths, special events.
   Enhance Command Parser: Support synonyms, multi-word commands for richer interactions.
   Add Sound or Basic Graphics like ASCII art or audio cues for immersion.
   Provide Save/Load Functionality for persistent progress.
   Optionally add Difficulty Levels and Hint Systems for accessibility.



==========================================================================================

threads --- using my fancy shmancy approach 

anonymous classes 


add interfaces..... 
add interfaces..... 
add interfaces..... 
add interfaces..... 

default method java
default method java
default method java
default method javadefault method javadefault method javadefault method java

threaded event. 
threaded event. 
threaded event. 
threaded event. 


mvc please?

how to save?

survive matrix?
-- have to do minigames to pick up objects,

minimap. inventory viewer. 


todo
   need game to copy
   save game state
   need gui
JUnit test cases covering major functionality.
README with instructions for compiling, running, and using the game.
Executable JAR with GUI support.


model view controller

we are being graded on our ability to explain the code and concepts we are presenting

randomly generating world, all must be connected somehow
   and item placement

i need to have game save functionality


Inventory.
   both rooms and ppl need them.
therefore we can make it an abstract superclass



teleportation?

** need a cheat to win the game fast, for the interview. **

tab: gui and text based tab switcher. the underlying game mechanics are the same



Assignment Specification:

Assignment: Enhanced Java Text Adventure Game

 

Objective:

Develop an enhanced version of a Java text adventure game that illustrates key Java programming functionalities. The game should retain classic text-based exploration mechanics while integrating advanced Java features for improved design, usability, and extensibility.

 

Requirements:

User Interface:

Create a basic GUI using JavaFX for richer game interaction (e.g., movement direction, display commands, room descriptions, input fields).
Implement a model view control design pattern approach to your system
Implement File I/O to allow saving and loading of game state or configurations.

Core Java Concepts Implementation:

   Utilize Object-Oriented Programming (OOP) concepts:
      Define classes and objects representing rooms, items, characters, commands.
      Apply inheritance and interfaces with polymorphism (e.g., various item or NPC types).
      Implement Collections Framework (e.g., List, Map, Set) for managing rooms, items, commands, and NPC collections.
      Use Enums to define command types, directions, game states, or item categories.
      Implement Exception Handling to gracefully manage invalid inputs and runtime errors.
      Apply Generics where appropriate to enhance container code reusability.
      Use Input Parsing with String manipulation and tokenization for user commands.
      Utilize Serialization for saving complex game objects.
      Design and implement Unit Tests using JUnit to verify game mechanics.
      Use Annotations to provide metadata for commands or game configuration.


Advanced Features (Optional/Bonus) only when the above has been completed:

Integrate Multithreading for timed events or background NPC actions.
Add Networking capability for multiplayer gameplay.
 

Some possible gameplay Enhancements:

Add Items & Inventory: Player can pick up, use, and drop items.
Introduce Puzzles & Locked Doors: Require keys or codes to progress.
Create NPC Characters: Add dialogues, quests, and trading.
Implement Scoring or Objectives to motivate players.
Extend Room Complexity: Hidden passageways, branching paths, special events.
Enhance Command Parser: Support synonyms, multi-word commands for richer interactions.
Add Sound or Basic Graphics like ASCII art or audio cues for immersion.
Provide Save/Load Functionality for persistent progress.
Optionally add Difficulty Levels and Hint Systems for accessibility.
 

Deliverables:

Complete source code files.
JUnit test cases covering major functionality.
README with instructions for compiling, running, and using the game.
Executable JAR with GUI support.
2 minute demo of game, walkthrough.
 

Evaluation Criteria:

(Priority) An in-depth understanding and ability to completely explain all the code you submit.
A good and appropriate Implementation of listed requirements
Code clarity, organization, and documentation.
Robustness and exception handling.