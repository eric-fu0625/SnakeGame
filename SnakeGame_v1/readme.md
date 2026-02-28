# Snake Game

## Project Introduction
Snake Game is a modern implementation of the classic snake game, developed with Java Swing. It includes a user login/registration system, score recording, customizable game speed, and interface themes. The game retains the core gameplay of the classic Snake while adding a user system and visually appealing effects to enhance the overall gaming experience.

## Features
### Core Features
- 🐍 Classic Snake gameplay with directional keys/WASD to control snake movement
- 📝 User login/registration system with username and password verification
- 🏆 Automatic recording of player scores and highest scores
- ⚡ Multiple game speed levels (Slow/Medium/Fast/Lightning)
- 🎨 Elegant UI interface with theme colors and visual feedback
- ⏸️ Game pause/resume functionality with shortcut key support

### Interface Features
- Login/registration tab-switching interface
- Real-time game status display (player name, score, highest score, game status)
- Buttons with hover effects
- Responsive menu system

### Shortcut Key Support
| Shortcut Key |                 Function                |
|--------------|-----------------------------------------|
| ↑↓←→/WASD    | Control snake movement direction        |
| Space Bar    | Pause/Resume game                       |
| F2           | Restart game                            |
| ESC          | Logout                                  |
| Alt+F4       | Exit game                               |
| 1-4          | Switch game speed (1=Slow, 4=Lightning) |
| G            | Show/Hide grid                          |

## Runtime Environment
- Java 8 or higher
- Operating systems supporting Swing (Windows/macOS/Linux)

## Running Instructions
1. Ensure Java Runtime Environment (JRE 8+) is installed
   ```bash
   java -version
   mvn -version

2. Compile all Java source files:
   ```bash
   javac -d bin src/com/game/**/*.java

   Or use maven to compile and package:
   ```bash
   mvn clean compile
   mvn clean package

3. Run the main class
   ```bash
   java -cp bin com.game.SnakeGame

   Or run the .jar:
   ```bash
   java -jar target/snake-game-1.0.0.jar

## Version Information
- Version: 1.0.0
- Developer: FDY
- Features: User login/registration system, automatic score saving, multiple game speeds, customizable grid display, beautiful visual effects

## License
This project is developed for learning purposes without specific license restrictions, and can be freely modified and distributed.

Enjoy the game! 