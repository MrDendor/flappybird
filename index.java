import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.Random;

public class FlappyBird extends JPanel implements ActionListener {
    private Bird bird;
    private ArrayList<Pipe> pipes;
    private Timer timer;
    private final int PIPE_WIDTH = 80;
    private final int PIPE_HEIGHT = 600;
    private final int GAP = 150;
    private final int PIPE_SPACING = 300;
    private final int BIRD_WIDTH = 40;
    private final int BIRD_HEIGHT = 30;

    public FlappyBird() {
        bird = new Bird(100, 300, BIRD_WIDTH, BIRD_HEIGHT);
        pipes = new ArrayList<>();
        timer = new Timer(20, this);

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_SPACE) {
                    bird.jump();
                }
            }
        });

        setBackground(Color.CYAN);
        setFocusable(true);
        setPreferredSize(new Dimension(800, 600));
        timer.start();

        // Add initial pipes
        addPipe();
    }

    private void addPipe() {
        int height = new Random().nextInt(300) + 100;
        pipes.add(new Pipe(800, height, PIPE_WIDTH, PIPE_HEIGHT, GAP));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        bird.draw(g);
        for (Pipe pipe : pipes) {
            pipe.draw(g);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        bird.update();

        // Move pipes
        ArrayList<Pipe> toRemove = new ArrayList<>();
        for (Pipe pipe : pipes) {
            pipe.update();
            if (pipe.getBounds().x + PIPE_WIDTH < 0) {
                toRemove.add(pipe);
            }
        }
        pipes.removeAll(toRemove);

        // Add new pipes
        if (pipes.isEmpty() || pipes.get(pipes.size() - 1).getBounds().x < 800 - PIPE_SPACING) {
            addPipe();
        }

        // Check collisions
        Rectangle birdBounds = bird.getBounds();
        for (Pipe pipe : pipes) {
            if (pipe.getTopBounds().intersects(birdBounds) || pipe.getBottomBounds().intersects(birdBounds)) {
                timer.stop();
                JOptionPane.showMessageDialog(this, "Game Over!");
                System.exit(0);
            }
        }

        // Check for falling out of bounds
        if (birdBounds.y > getHeight() || birdBounds.y < 0) {
            timer.stop();
            JOptionPane.showMessageDialog(this, "Game Over!");
            System.exit(0);
        }

        repaint();
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Flappy Bird");
        FlappyBird game = new FlappyBird();
        frame.add(game);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}

class Bird {
    private int x, y, width, height, velocity;
    private final int GRAVITY = 1;
    private final int JUMP_STRENGTH = -15;

    public Bird(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.velocity = 0;
    }

    public void jump() {
        velocity = JUMP_STRENGTH;
    }

    public void update() {
        velocity += GRAVITY;
        y += velocity;
    }

    public void draw(Graphics g) {
        g.setColor(Color.YELLOW);
        g.fillRect(x, y, width, height);
    }

    public Rectangle getBounds() {
        return new Rectangle(x, y, width, height);
    }
}

class Pipe {
    private int x, y, width, height, gap;
    private final int SPEED = 2;

    public Pipe(int x, int y, int width, int height, int gap) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.gap = gap;
    }

    public void update() {
        x -= SPEED;
    }

    public void draw(Graphics g) {
        g.setColor(Color.GREEN);
        g.fillRect(x, 0, width, y);
        g.fillRect(x, y + gap, width, height - y - gap);
    }

    public Rectangle getTopBounds() {
        return new Rectangle(x, 0, width, y);
    }

    public Rectangle getBottomBounds() {
        return new Rectangle(x, y + gap, width, height - y - gap);
    }

    public Rectangle getBounds() {
        return new Rectangle(x, 0, width, height);
    }
}
