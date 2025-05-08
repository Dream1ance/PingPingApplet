package pingpong.ui;

import java.applet.Applet;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import pingpong.db.ScoreManager;

public class PingPongApplet extends Applet implements Runnable, KeyListener, ActionListener {
    // Game variables
    int ballX = 160, ballY = 100, ballDX = 2, ballDY = 2;
    int paddleX = 150, paddleWidth = 60, paddleHeight = 10;
    int score = 0;
    boolean leftPressed = false, rightPressed = false;
    Thread gameThread;
    ScoreManager scoreMgr;
    String username = "Player";
    Image bufferImage;
    Graphics bufferGraphics;

    // UI components
    Button scoreboardButton;
    boolean showScoreboard = false;
    boolean gamePaused = false;

    // Data for scoreboard
    ArrayList<String> userList = new ArrayList<>();
    ArrayList<Integer> scoreList = new ArrayList<>();

    public void init() {
        setSize(400, 340);
        setLayout(null);

        addKeyListener(this);
        setFocusable(true);
        requestFocus();

        // Prompt username
        username = promptUsername();
        if (username == null || username.trim().isEmpty()) {
            username = "Player";
        }
        System.out.println("PingPongApplet: Username set to " + username);

        // Create scoreboard Button
        scoreboardButton = new Button("Scoreboard");
        scoreboardButton.setBounds(10, 5, 80, 25);
        scoreboardButton.addActionListener(this);
        add(scoreboardButton);

        // Initialize ScoreManager
        scoreMgr = new ScoreManager();
    }

    private String promptUsername() {
        Dialog dialog = new Dialog(new Frame(), "Username", true);
        dialog.setLayout(new BorderLayout(10, 10));
        TextField input = new TextField(20);
        Button okButton = new Button("OK");
        Button cancelButton = new Button("Cancel");
        Label label = new Label("Enter your username:");
        Panel buttonPanel = new Panel(new FlowLayout());
        buttonPanel.add(okButton);
        buttonPanel.add(cancelButton);
        dialog.add(label, BorderLayout.NORTH);
        dialog.add(input, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        String[] result = new String[1];
        okButton.addActionListener(e -> {
            String text = input.getText().trim();
            if (!text.isEmpty()) {
                result[0] = text;
                dialog.dispose();
            }
        });
        input.addActionListener(e -> {
            String text = input.getText().trim();
            if (!text.isEmpty()) {
                result[0] = text;
                dialog.dispose();
            }
        });
        cancelButton.addActionListener(e -> {
            result[0] = null;
            dialog.dispose();
        });
        dialog.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                result[0] = null;
                dialog.dispose();
            }
        });

        dialog.setSize(250, 150);
        dialog.setLocationRelativeTo(null);
        System.out.println("PingPongApplet: Showing username prompt dialog");
        dialog.setVisible(true);
        System.out.println("PingPongApplet: Username prompt returned: " + result[0]);
        return result[0];
    }

    public void start() {
        if (gameThread == null) {
            gameThread = new Thread(this);
            gameThread.start();
        }
    }

    public void stop() {
        if (gameThread != null) {
            gameThread = null;
        }
        if (scoreMgr != null) scoreMgr.close();
    }

    public void run() {
        while (Thread.currentThread() == gameThread) {
            if (!gamePaused) {
                updateGame();
                repaint();
            }
            try {
                Thread.sleep(15);
            } catch (InterruptedException e) {}
        }
    }

    public void updateGame() {
        if (leftPressed && paddleX > 0) paddleX -= 5;
        if (rightPressed && paddleX + paddleWidth < getWidth()) paddleX += 5;

        ballX += ballDX;
        ballY += ballDY;

        if (ballX <= 0 || ballX >= getWidth() - 15) ballDX = -ballDX;
        if (ballY <= 30) ballDY = -ballDY;

        if (ballY + 15 >= getHeight() - paddleHeight - 10 &&
            ballY + 15 <= getHeight() - paddleHeight &&
            ballX + 15 >= paddleX && ballX <= paddleX + paddleWidth) {
            ballDY = -ballDY;
            score++;
            System.out.println("PingPongApplet: Score incremented to " + score);
        }

        if (ballY > getHeight()) {
            System.out.println("PingPongApplet: Ball missed, saving score: " + score);
            saveHighScore();
            score = 0;
            resetBall();
        }
    }

    public void resetBall() {
        ballX = 160;
        ballY = 100;
        ballDX = 2;
        ballDY = 2;
        System.out.println("PingPongApplet: Ball reset, score: " + score);
    }

    public void saveHighScore() {
        if (scoreMgr != null) {
            scoreMgr.saveHighScore(username, score);
            System.out.println("PingPongApplet: Saved high score for " + username + ": " + score);
        }
    }

    public void update(Graphics g) {
        if (bufferImage == null) {
            bufferImage = createImage(getWidth(), getHeight());
            bufferGraphics = bufferImage.getGraphics();
        }
        paint(bufferGraphics);
        g.drawImage(bufferImage, 0, 0, this);
    }

    public void paint(Graphics g) {
        g.setColor(Color.black);
        g.fillRect(0, 0, getWidth(), getHeight());

        g.setColor(Color.darkGray);
        g.fillRect(0, 0, getWidth(), 30);

        if (showScoreboard) {
            drawScoreboard(g);
        } else {
            g.setColor(Color.white);
            g.fillOval(ballX, ballY, 15, 15);

            g.setColor(Color.blue);
            g.fillRect(paddleX, getHeight() - paddleHeight - 10, paddleWidth, paddleHeight);

            g.setColor(Color.green);
            g.drawString("Player: " + username, 10, 50);
            g.drawString("Score: " + score, 10, 65);
        }
    }

    private void drawScoreboard(Graphics g) {
        if (scoreMgr != null) {
            userList = scoreMgr.getUsers();
            scoreList = scoreMgr.getScores();
            System.out.println("PingPongApplet: Scoreboard retrieved " + userList.size() + " entries");
        }

        g.setColor(new Color(0, 0, 0, 220));
        g.fillRect(20, 40, getWidth() - 40, getHeight() - 60);

        g.setColor(Color.white);
        g.setFont(new Font("Arial", Font.BOLD, 16));
        g.drawString("Scoreboard", getWidth() / 2 - 50, 70);

        g.setFont(new Font("Arial", Font.PLAIN, 14));
        int startY = 100;
        g.drawString("Username", 80, startY - 20);
        g.drawString("High Score", getWidth() - 160, startY - 20);

        for (int i = 0; i < userList.size(); i++) {
            int y = startY + i * 20;
            g.drawString(userList.get(i), 80, y);
            g.drawString(String.valueOf(scoreList.get(i)), getWidth() - 160, y);
        }

        if (userList.size() == 0) {
            g.drawString("No scores recorded.", getWidth() / 2 - 60, startY + 20);
        }

        g.setFont(new Font("Arial", Font.ITALIC, 12));
        g.drawString("Click the button again to close scoreboard and resume game.", 50, getHeight() - 30);
    }

    public void keyPressed(KeyEvent e) {
        if (!gamePaused) {
            if (e.getKeyCode() == KeyEvent.VK_LEFT) leftPressed = true;
            if (e.getKeyCode() == KeyEvent.VK_RIGHT) rightPressed = true;
        }
    }

    public void keyReleased(KeyEvent e) {
        if (!gamePaused) {
            if (e.getKeyCode() == KeyEvent.VK_LEFT) leftPressed = false;
            if (e.getKeyCode() == KeyEvent.VK_RIGHT) rightPressed = false;
        }
    }

    public void keyTyped(KeyEvent e) {}

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == scoreboardButton) {
            showScoreboard = !showScoreboard;
            gamePaused = showScoreboard;
            if (!showScoreboard) {
                requestFocus();
            }
            repaint();
            System.out.println("PingPongApplet: Scoreboard toggled, showScoreboard: " + showScoreboard);
        }
    }
}