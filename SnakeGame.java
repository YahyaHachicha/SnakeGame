import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.*;

public class SnakeGame extends JPanel implements ActionListener, KeyListener{


    private class Tile{
        int x,y;
        Tile (int x, int y){
            this.x = x;
            this.y = y;
        }

    }

    int boardHeight, boardWidth;
    int tileSize = 25;
    int score = 0, highestScore = 0;

    //Snake
    Tile snakeHead;
    ArrayList<Tile> snakeBody;

    //Apple
    Tile apple;
    Random random;

    //game logic
    Timer gameLoop;
    int velocityX, velocityY;
    boolean gameOver = false;
    boolean gameWon = false;

    SnakeGame(int boardWidth, int boardHeight){
        this.boardHeight = boardHeight;
        this.boardWidth = boardWidth;
        setPreferredSize(new Dimension(this.boardWidth, this.boardHeight));
        setBackground(Color.black);
        addKeyListener(this);
        setFocusable(true);

        snakeHead = new Tile(5, 5);
        snakeBody = new ArrayList<Tile>();

        apple = new Tile(10, 10);
        random = new Random();
        placeApple();

        velocityX = 0;
        velocityY = 0;

        gameLoop = new Timer(100, this); //how long it takes to start timer, milliseconds gone between frames
        gameLoop.start();
    }

    public void paintComponent (Graphics g){
        super.paintComponent(g);
        draw(g);
    }

    public void draw(Graphics g){
        //Grid Lines
//        for (int i = 0; i < boardWidth/tileSize; i++){
//            //(x1,y1,x2,y2)
//            g.drawLine(i*tileSize, 0, i*tileSize, boardHeight);
//            g.drawLine(0, i*tileSize, boardWidth, i*tileSize);
//        }

        //Apple
        g.setColor(Color.red);
//        g.fillRect(apple.x * tileSize, apple.y * tileSize, tileSize, tileSize);
        g.fill3DRect(apple.x * tileSize, apple.y * tileSize, tileSize, tileSize, true);

        //Snake Head
        g.setColor(Color.green);
//        g.fillRect(snakeHead.x, snakeHead.y, tileSize, tileSize);
        // g.fillRect(snakeHead.x, snakeHead.y, tileSize, tileSize);
        // g.fillRect(snakeHead.x*tileSize, snakeHead.y*tileSize, tileSize, tileSize);
        g.fill3DRect(snakeHead.x*tileSize, snakeHead.y*tileSize, tileSize, tileSize, true);

        //Snake Body
        for (int i = 0; i < snakeBody.size(); i++){
            Tile snakePart = snakeBody.get(i);
//            g.fillRect(snakePart.x * tileSize, snakePart.y * tileSize, tileSize, tileSize);
            g.fill3DRect(snakePart.x * tileSize, snakePart.y * tileSize, tileSize, tileSize, true);

        }

        //Score
        g.setFont(new Font("Arial", Font.PLAIN, 16));
        score = snakeBody.size();
        if (gameOver){
            g.setColor(gameWon ? Color.green : Color.red);
            int lineHeight = g.getFontMetrics().getHeight();
            g.drawString(gameWon ? "You Win! Score: " + String.valueOf(score)
                    : "Game Over: " + String.valueOf(score), tileSize - 16, tileSize);
            g.drawString("Highest Score: " + String.valueOf(highestScore), tileSize - 16, tileSize + lineHeight);

        }
        else{
            int lineHeight = g.getFontMetrics().getHeight();
            g.drawString("Score: " + String.valueOf(score), tileSize - 16, tileSize);
            g.drawString("Highest Score: " + String.valueOf(highestScore), tileSize - 16, tileSize + lineHeight);

        }
    }

    public void placeApple() {
        int totalCells = (boardWidth / tileSize) * (boardHeight / tileSize);
        int occupiedCells = 1 + snakeBody.size(); // head + body

        if (occupiedCells >= totalCells) {
            gameWon = true;
            gameOver = true;
            highestScore = Math.max(highestScore, snakeBody.size());
            return;
        }

        while (true) {
            int newX = random.nextInt(boardWidth / tileSize);
            int newY = random.nextInt(boardHeight / tileSize);

            if (newX == snakeHead.x && newY == snakeHead.y) {
                continue;
            }

            boolean onBody = false;
            for (Tile part : snakeBody) {
                if (part.x == newX && part.y == newY) {
                    onBody = true;
                    break;
                }
            }

            if (!onBody) {
                apple.x = newX;
                apple.y = newY;
                return;
            }
        }
    }


    public boolean collision(Tile tile1, Tile tile2){
        return tile1.x == tile2.x && tile1.y == tile2.y;
    }

    public void move(){
        //eat apple
        if (collision(snakeHead, apple)){
            snakeBody.add(new Tile(apple.x, apple.y));
            placeApple();
            if (gameOver) {
                return;
            }
        }


        //move Snake Body
        for (int i = snakeBody.size() - 1; i >= 0; i--){
            Tile snakePart = snakeBody.get(i);
            if (i == 0){
                snakePart.x = snakeHead.x;
                snakePart.y = snakeHead.y;
            }
            else{
                Tile prevSnakePart = snakeBody.get(i - 1);
                snakePart.x = prevSnakePart.x;;
                snakePart.y = prevSnakePart.y;
            }
        }

        //move snake Head
        snakeHead.x += velocityX;
        snakeHead.y += velocityY;

        //game over conditions
        for (int i = 0; i < snakeBody.size(); i++){
            Tile snakePart = snakeBody.get(i);
            //collide with the snake head
            if (collision(snakeHead, snakePart)) {
                gameOver = true;
                highestScore = Math.max(highestScore, snakeBody.size());
            }
        }

        if (snakeHead.x < 0 || snakeHead.x >= boardWidth / tileSize
                || snakeHead.y < 0 || snakeHead.y >= boardHeight / tileSize) {
            gameOver = true;
            highestScore = Math.max(highestScore, snakeBody.size());
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        move();
        repaint();
        if (gameOver){
            gameLoop.stop();
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        // System.out.println("KeyEvent: " + e.getKeyCode());
        if (e.getKeyCode() == KeyEvent.VK_UP && velocityY != 1){
            velocityX = 0;
            velocityY = -1;
        }

        if (e.getKeyCode() == KeyEvent.VK_DOWN && velocityY != -1){
            velocityX = 0;
            velocityY = 1;
        }

        if (e.getKeyCode() == KeyEvent.VK_LEFT && velocityX != 1){
            velocityX = -1;
            velocityY = 0;
        }

        if (e.getKeyCode() == KeyEvent.VK_RIGHT && velocityX != -1){
            velocityX = 1;
            velocityY = 0;
        }

        if (e.getKeyCode() == KeyEvent.VK_SPACE && gameOver){
            snakeHead = new Tile(5, 5);
            snakeBody.clear();
            velocityX = 0;
            velocityY = 0;
            gameOver = false;
            gameWon = false;
            placeApple();
            gameLoop.restart();
            repaint();
        }
    }

    //do not need
    @Override
    public void keyTyped(KeyEvent e) {}
    @Override
    public void keyReleased(KeyEvent e) {}
}
