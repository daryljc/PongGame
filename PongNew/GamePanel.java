import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;

public class GamePanel extends JPanel implements Runnable {

  static final int GAME_WIDTH = 1000;
  static final int GAME_HEIGHT = (int) (GAME_WIDTH * (0.5555));
  static final Dimension SCREEN_SIZE = new Dimension(GAME_WIDTH, GAME_HEIGHT);
  static final int BALL_DIAMETER = 20;
  static final int PADDLE_WIDTH = 100;
  static final int PADDLE_HEIGHT = 100;
  Thread gameThread;
  Image image;
  Graphics graphics;
  Random random;
  Paddle paddle1;
  Paddle collision1;
  Paddle paddle2;
  Paddle collision2;
  Ball ball;
  Score score;

  GamePanel() {
    newPaddles();
    newBall();
    score = new Score(GAME_WIDTH, GAME_HEIGHT);
    this.setFocusable(true);
    this.addKeyListener(new AL()); // interclass. Extends key adapter class
    this.setPreferredSize(SCREEN_SIZE);

    gameThread = new Thread(this);
    gameThread.start();
  }

  public void newBall() {
    random = new Random();
    ball = new Ball((GAME_WIDTH / 2) - (BALL_DIAMETER / 2), random.nextInt(GAME_HEIGHT - BALL_DIAMETER), BALL_DIAMETER,
        BALL_DIAMETER * 2);
  }

  public void newPaddles() {
    collision1 = new Paddle(0 - PADDLE_WIDTH, (GAME_HEIGHT / 2) - (PADDLE_HEIGHT / 2), PADDLE_WIDTH, PADDLE_HEIGHT, 1);
    collision2 = new Paddle(0 - PADDLE_WIDTH * 2, (GAME_HEIGHT / 2) - (PADDLE_HEIGHT / 2), PADDLE_WIDTH, PADDLE_HEIGHT, 1);
    paddle1 = new Paddle(0 - PADDLE_WIDTH / 2, (GAME_HEIGHT / 2) - (PADDLE_HEIGHT / 2), PADDLE_WIDTH, PADDLE_HEIGHT, 1);
    paddle2 = new Paddle(GAME_WIDTH - PADDLE_WIDTH / 2, (GAME_HEIGHT / 2) - (PADDLE_HEIGHT / 2), PADDLE_WIDTH, PADDLE_HEIGHT, 2);
  }

  public void paint(Graphics g) {
    image = createImage(getWidth(), getHeight());
    graphics = image.getGraphics();
    draw(graphics);
    g.drawImage(image, 0, 0, this);
  }

  public void draw(Graphics g) {
    paddle1.draw(g);
    paddle2.draw(g);
    ball.draw(g);
    score.draw(g);
  }

  public void move() {
    paddle1.move();
    paddle2.move();
    ball.move();
  }

  public void checkCollision() {
    // bounce ball off top and bottom of window edges
    if (ball.y <= 0) {
      ball.setYDirection(-ball.yVelocity);
    }
    if (ball.y >= GAME_HEIGHT - BALL_DIAMETER) {
      ball.setYDirection(-ball.yVelocity);
    }

    // bounces ball off paddles
    if (ball.intersects(paddle1)) {
      ball.xVelocity = Math.abs(ball.xVelocity); // converts it to a positive number
      ball.xVelocity++; // increases speed each time ball is ralleyed
      if (ball.yVelocity > 0)
        ball.yVelocity++; // increaes speed
      else
        ball.yVelocity--;
      ball.setXDirection(ball.xVelocity);
      ball.setYDirection(ball.yVelocity);
    }

    if (ball.intersects(paddle2)) {
      ball.xVelocity = Math.abs(ball.xVelocity); // converts it to a positive number
      ball.xVelocity++; // increases speed each time ball is ralleyed
      if (ball.yVelocity > 0)
        ball.yVelocity++; // increaes speed
      else
        ball.yVelocity--;
      ball.setXDirection(-ball.xVelocity);
      ball.setYDirection(ball.yVelocity);
    }

    // stops paddles at window edges
    if (paddle1.y <= 0)
      paddle1.y = 0;
    if (paddle1.y >= (GAME_HEIGHT - PADDLE_HEIGHT))
      paddle1.y = GAME_HEIGHT - PADDLE_HEIGHT;
    if (paddle2.y <= 0)
      paddle2.y = 0;
    if (paddle2.y >= (GAME_HEIGHT - PADDLE_HEIGHT))
      paddle2.y = GAME_HEIGHT - PADDLE_HEIGHT;

    // give player 1 point and creates new paddles & ball
    if (ball.x <= 0) {
      score.player2++;
      newPaddles();
      newBall();
      System.out.println("Player 2: " + score.player2);
    }
    if (ball.x >= GAME_WIDTH - ball.getWidth() * 2) {
      score.player1++;
      newPaddles();
      newBall();
      System.out.println("Player 1: " + score.player1);
    }
  }

  public void run() {
    // game loop
    long lastTime = System.nanoTime();
    double amountOfTicks = 60.0;
    double ns = 1000000000 / amountOfTicks;
    double delta = 0;
    while (true) {
      long now = System.nanoTime();
      delta += (now - lastTime) / ns;
      lastTime = now;
      if (delta >= 1) {
        move();
        checkCollision();
        repaint();
        delta--;
      }
    }
  }

  // action listener
  public class AL extends KeyAdapter {
    public void keyPressed(KeyEvent e) {
      paddle1.keyPressed(e);
      paddle2.keyPressed(e);
    }

    public void keyReleased(KeyEvent e) {
      paddle1.keyReleased(e);
      paddle2.keyReleased(e);
    }
  }
}