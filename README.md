PingPong Applet
PingPong is a simple Java applet game where players use a paddle to hit a ball, scoring points for each hit. The game features a username prompt at startup, a scoreboard to view high scores, and persistent score storage in a text file. Built using Java AWT, it runs in appletviewer and is designed for educational or demonstration purposes.
Features

Move the paddle with left/right arrow keys to hit the ball.
Score points for each paddle hit; save high scores when the ball is missed.
Enter a username at startup to track your scores.
Toggle a scoreboard to view all high scores.
Scores saved to scores.txt in CSV format.

Prerequisites

Operating System: Windows (tested on Windows 10/11).
Java Development Kit (JDK): Java 8u202 or compatible (e.g., Oracle JDK 8).
Download: Oracle JDK 8 Archive
Verify: java -version should show 1.8.0_202 or similar.


appletviewer: Included with JDK 8 for running applets.
Text Editor: Any (e.g., Notepad, VS Code) for viewing/editing files.
Command Prompt: For compiling and running commands.

Directory Structure
pingpong/
├── src/
│   └── pingpong/
│       ├── ui/
│       │   └── PingPongApplet.java
│       ├── db/
│       │   └── ScoreManager.java
├── index.html
├── scores.txt
├── java.policy
├── README.md


PingPongApplet.java: Main game logic, UI, and controls.
ScoreManager.java: Handles score storage in scores.txt.
index.html: Embeds the applet for appletviewer.
java.policy: Grants file permissions for scores.txt.
scores.txt: Stores high scores (e.g., Player,10).

Setup Instructions

Clone the Repository:
git clone https://github.com/your-username/pingpong.git
cd pingpong


Verify JDK Installation:
java -version
javac -version

Ensure both show Java 8 (e.g., 1.8.0_202).

Create scores.txt:Create an empty scores.txt file in the project root:
echo. > scores.txt

Or add sample data:
echo Player,10> scores.txt


Check java.policy:Ensure java.policy contains:
grant codeBase "file:/E:/new pong/-" {
    permission java.io.FilePermission "E:\\new pong\\scores.txt", "read,write";
    permission java.awt.AWTPermission "showWindowWithoutWarningBanner";
};

Update the path if your project is not in E:\new pong.

Set File Permissions (Windows):Ensure scores.txt has write permissions:
icacls scores.txt /grant Everyone:F



Compilation
Compile the Java source files:
cd pingpong
javac -d . src\pingpong\ui\*.java src\pingpong\db\*.java

This creates class files in the pingpong directory.
Running the Game
Run the applet using appletviewer:
appletviewer -J-Djava.security.policy=java.policy -J-Djava.security.debug=access index.html


The -J-Djava.security.policy flag applies file permissions.
The -J-Djava.security.debug=access flag logs permission issues (optional).

Testing the Game

Username Prompt:
On startup, a dialog asks for a username (e.g., "TestPlayer").
Enter a name and click "OK," or click "Cancel" to use "Player."


Gameplay:
Use left/right arrow keys to move the paddle.
Hit the ball to increase the score (displayed as "Score: X").
Miss the ball to save the high score and reset.


Scoreboard:
Click the "Scoreboard" button to view high scores.
Verify usernames and scores (e.g., "TestPlayer, 10").
Click again to resume the game.


Check scores.txt:type scores.txt

Expected output: TestPlayer,10.

Troubleshooting

"access denied" Error:
Verify java.policy path matches your directory.
Check scores.txt permissions:icacls scores.txt

Run: icacls scores.txt /grant Everyone:F.


Username Prompt Not Showing:
Ensure index.html has no <param name="username" value="Player">.
Check console for SecurityException (e.g., AWTPermission).


Java Version Issue:
Confirm Java 8:java -version


If incorrect, install JDK 8u202.


Compilation Errors:
Ensure files are in the correct src/pingpong/ui and src/pingpong/db directories.
Re-run javac command.



License
MIT License (or specify your preferred license).
Contact
For issues or contributions, open a GitHub issue or contact [your-email@example.com].
