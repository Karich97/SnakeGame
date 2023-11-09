import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.*;
import java.util.Random;

public class GamePanel extends JPanel implements ActionListener {

    private static final Dimension sSize = Toolkit.getDefaultToolkit().getScreenSize();
    private static final int UNIT_SIZE = sSize.height / 20;
    private static final int SCREEN_Height = UNIT_SIZE * 10;
    private static final int SCREEN_WIDTH = SCREEN_Height * 3 / 2;
    private static final int GAME_UNITS = (SCREEN_WIDTH * SCREEN_Height) / UNIT_SIZE;
    private static final int DELAY = 150;
    private int[] x;
    private int[] y;
    private int bodyParts = 5;
    private int miceEaten;
    private int mouseX;
    private int mouseY;
    private char direction = 'R';
    private boolean running = false;
    private Timer timer;
    private final Random random;

    GamePanel(){
        random = new Random();
        this.setPreferredSize(new Dimension(SCREEN_WIDTH,SCREEN_Height));
        this.setBackground(Color.white);
        this.setFocusable(true);
        this.addKeyListener(new MyKeyAdapter());
        startGame();
    }

    public void startGame(){
        running = true;
        timer = new Timer(DELAY, this);
        newMouse();

        x = new int[GAME_UNITS];
        y = new int[GAME_UNITS];
        direction = 'R';

        miceEaten = 0;
        bodyParts = 5;

        timer.start();
        repaint();
    }

    public void paintComponent(Graphics g){
        super.paintComponent(g);
        draw(g);
    }

    public void draw(Graphics g){
        if (running){
            g.setColor(Color.BLACK);
            g.fillOval(mouseX,mouseY,UNIT_SIZE,UNIT_SIZE);

            for (int i = 0; i < bodyParts; i++) {
                if(i == 0) {
                    g.setColor(Color.GREEN);
                    g.fillRect(x[i],y[i], UNIT_SIZE, UNIT_SIZE);
                }
                else {
                    g.setColor(new Color(45,180,0));
                    g.fillRect(x[i],y[i], UNIT_SIZE, UNIT_SIZE);
                }
            }

            g.setColor(Color.ORANGE);
            g.setFont(new Font("Ink Free", Font.BOLD, 40));
            FontMetrics metrics = getFontMetrics(g.getFont());
            g.drawString("Score: " + miceEaten, (SCREEN_WIDTH - metrics.stringWidth("Score: " + miceEaten))/2, g.getFont().getSize());
        }
        else {
            gameOver(g);
        }
    }

    public void newMouse(){
        mouseX = random.nextInt(SCREEN_WIDTH/UNIT_SIZE)*UNIT_SIZE;
        mouseY = random.nextInt(SCREEN_Height/UNIT_SIZE)*UNIT_SIZE;
    }

    public void move(){
        for (int i = bodyParts; i > 0 ; i--) {
            x[i] = x[i-1];
            y[i] = y[i-1];
        }
        switch (direction){
            case 'U':
                y[0] = y[0] - UNIT_SIZE;
                break;
            case 'D':
                y[0] = y[0] + UNIT_SIZE;
                break;
            case 'L':
                x[0] = x[0] - UNIT_SIZE;
                break;
            case 'R':
                x[0] = x[0] + UNIT_SIZE;
                break;
        }
    }

    public void checkMouse(){ //I have done all this method by myself
        if((x[0] == mouseX) && (y[0] == mouseY)){
            bodyParts++;
            newMouse();
            miceEaten++; //Except this line. I forgot about that variable.
        }
    }

    public void checkCollisions(){
        //if head collides with body
        for (int i = bodyParts; i > 0 ; i--) {
            if ((x[0] == x[i]) && (y[0] == y[i])) {
                running = false;
                break;
            }
        }
        //if head collides with borders
        if((x[0] >= SCREEN_WIDTH) || (x[0] < 0) || (y[0] >= SCREEN_Height) || (y[0] < 0)){
            running = false;
        }
        if(!running){
            timer.stop();
        }
    }

    public void gameOver (Graphics g) {
        //Game over text
        g.setColor(Color.ORANGE);
        g.setFont(new Font("Ink Free", Font.BOLD, 75));
        FontMetrics metrics = getFontMetrics(g.getFont());
        g.drawString("Game Over", (SCREEN_WIDTH - metrics.stringWidth("Game Over"))/2, SCREEN_Height/2);

        g.setColor(Color.BLACK);
        g.setFont(new Font("Ink Free", Font.BOLD, 40));


        if (recordBeaten(miceEaten)){
            g.drawString("New Record: " + miceEaten, (SCREEN_WIDTH - metrics.stringWidth("Your Score: " + miceEaten))/2, g.getFont().getSize());
        }else {
            g.drawString("Your Score: " + miceEaten, (SCREEN_WIDTH - metrics.stringWidth("Your Score: " + miceEaten))/2, g.getFont().getSize());
        }
    }

    private boolean recordBeaten(int miceEaten) {
        String score = "New record!";
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader("score"))){
            score = bufferedReader.readLine();
        } catch (FileNotFoundException e) {
            System.out.println("fileNotFound");
        } catch (IOException ex) {
            System.out.println("IOException");
        }
        System.out.println(Integer.parseInt(score.split(" ")[1]));
        if (score.equals("New record") || Integer.parseInt(score.split(" ")[1]) < miceEaten){
            String newScore = "user " + miceEaten;
            try (FileWriter writer = new FileWriter("score")){
                writer.write(newScore);
                return true;
            } catch (IOException ex) {
                System.out.println(ex.getMessage());
            }
        }
        return false;
    }

        @Override
    public void actionPerformed(ActionEvent e) {
        if(running){
            move();
            checkMouse();
            checkCollisions();
        }
        repaint();
    }

    public class MyKeyAdapter extends KeyAdapter{
        @Override
        public void keyPressed(KeyEvent e){
            if (!running){
                startGame();
                return;
            }
            switch (e.getKeyCode()){
                case KeyEvent.VK_LEFT:
                    if(direction != 'R'){
                        direction = 'L';
                    }
                    break;
                case KeyEvent.VK_RIGHT:
                    if(direction != 'L'){
                        direction = 'R';
                    }
                    break;
                case KeyEvent.VK_UP:
                    if(direction != 'D'){
                        direction = 'U';
                    }
                    break;
                case KeyEvent.VK_DOWN:
                    if(direction != 'U'){
                        direction = 'D';
                    }
                    break;
            }
        }
    }
}
