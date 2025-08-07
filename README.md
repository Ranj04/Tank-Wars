# Tank Wars Game

## Author
**Name:** Ranjiv Jithendran  
**Student Email:** rjithendran@sfsu.edu  

## Description
Tank Wars is a two-player competitive game developed in Java for CSC 415. The game features tank movement, shooting, power-ups, destructible and indestructible walls, animations, sounds, and a dynamic split-screen view. Players battle until one runs out of lives.

## Features Implemented (Milestone 2 Requirements)
- End screen showing winner with options to Restart or Exit
- Two-player gameplay (Player 1 and Player 2)
- Tanks move forward and backward
- Tank rotation left/right without forward motion
- Split screen rendering for both players
- Mini-map display
- Health bars for each tank
- Lives counter (3 lives minimum)
- Three unique power-ups (Speed Boost, Shield, Double Damage)
- Breakable and unbreakable walls
- Three or more map layouts (via randomized placement)
- Bullet collisions with tanks and walls
- Three or more animations (start screen, power-up, explosion)
- Three or more sounds (background music, power-up pickup, tank explosion)
- Game exported as a `.jar` file and placed in the `jar` directory
- Populated `README.md` with all required information

## Controls

### Player 1:
- Move Forward: **W**
- Move Backward: **S**
- Rotate Left: **A**
- Rotate Right: **D**
- Fire: **SPACE**

### Player 2:
- Move Forward: **↑ (UP ARROW)**
- Move Backward: **↓ (DOWN ARROW)**
- Rotate Left: **← (LEFT ARROW)**
- Rotate Right: **→ (RIGHT ARROW)**
- Fire: **ENTER**

## System Requirements
- **Java Version:** openjdk 21.0.5 2024-10-15 LTS
- **IDE Used:** IntelliJ IDEA
- **Operating System:** Windows 11

## How to Run the Game (in IntelliJ)
1. Clone the repository or download the source code.
2. Open the project folder in IntelliJ IDEA.
3. Make sure the JDK is set to 21.0.5 under Project Structure → Project.
4. Run `TankGame.java` as the main class.
5. Alternatively, run the prebuilt `.jar` file located in the `/jar/` folder using:  
   ```sh
   java -jar jar/tankgame-Ranj04.jar
   ```

## Additional Notes
- All assets (sprites and sound files) are stored in the `res` folder.
- Randomized wall and power-up generation ensure dynamic gameplay maps each session.
- Power-ups have temporary effects like speed boost, shield protection, and double damage.
- Split-screen and mini-map allow both players to maintain situational awareness.
- Background music and sound effects enhance the gaming experience.
- Background music volume can be adjusted via the slider in the bottom left corner of the game screen.
- Double damage power-up increases bullet damage by two times
- All power-ups have a duration of 5 seconds
