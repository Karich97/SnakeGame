import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.*;
import java.util.Random;

public class GamePanel extends JPanel implements ActionListener {

    static final int SCREEN_WIDTH = 600;
    static final int SCREEN_Height = 600;
    static final int UNIT_SIZE = 25;
    static final int GAME_UNITS = (SCREEN_WIDTH*SCREEN_Height)/UNIT_SIZE;
    static final int DELAY = 75;
    final int x[] = new int[GAME_UNITS];
    final int y[] = new int[GAME_UNITS];
    int bodyParts = 6;
    int miceEaten;
    int mouseX;
    int mouseY;
    char direction = 'R';
    boolean running = false;
    Timer timer;
    Random random;

    GamePanel(){
        random = new Random();
        this.setPreferredSize(new Dimension(SCREEN_WIDTH,SCREEN_Height));
        this.setBackground(Color.white);
        this.setFocusable(true);
        this.addKeyListener(new MyKeyAdapter());
        startGame();
    }

    public void startGame(){
        newMouse();
        running = true;
        timer = new Timer(DELAY, this);
        timer.start();
    }

    public void paintComponent(Graphics g){
        super.paintComponent(g);
        draw(g);
    }

    public void draw(Graphics g){
        if (running == true){
            /*
            for(int i=0; i < SCREEN_Height/UNIT_SIZE; i++){
                g.drawLine(i*UNIT_SIZE, 0, i*UNIT_SIZE, SCREEN_Height);
                g.drawLine(0, i*UNIT_SIZE, SCREEN_WIDTH, i*UNIT_SIZE);
            }

         */
            g.setColor(Color.BLACK);
            g.fillOval(mouseX,mouseY,UNIT_SIZE,UNIT_SIZE);

            for (int i = 0; i < bodyParts; i++) {
                if(i == 0) {
                    g.setColor(Color.GREEN);
                    g.fillRect(x[i],y[i], UNIT_SIZE, UNIT_SIZE);
                }
                else {
                    g.setColor(new Color(45,180,0));
                    //g.setColor(new Color(random.nextInt(255),random.nextInt(255),random.nextInt(255)));  //Rainbow snake, too childish
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
        mouseX = random.nextInt((int)(SCREEN_WIDTH/UNIT_SIZE))*UNIT_SIZE;
        mouseY = random.nextInt((int)(SCREEN_Height/UNIT_SIZE))*UNIT_SIZE;
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
            if((x[0] == x[i]) && (y[0] == y[i])){
                running = false;
            }
        }
        //if head collides with borders
        if((x[0] > SCREEN_WIDTH) || (x[0] < 0) || (y[0] > SCREEN_Height) || (y[0] < 0)){ //I did it by myself
            running = false;
        }
        if(running == false){
            timer.stop();
        }
    }

    public void gameOver (Graphics g) {
        //Game over text
        g.setColor(Color.RED);
        g.setFont(new Font("Ink Free", Font.BOLD, 75));
        FontMetrics metrics = getFontMetrics(g.getFont());
        g.drawString("Game Over", (SCREEN_WIDTH - metrics.stringWidth("Game Over"))/2, SCREEN_Height/2);

        g.setColor(Color.BLACK);
        g.setFont(new Font("Ink Free", Font.BOLD, 40));
        g.drawString("Your Score: " + miceEaten, (SCREEN_WIDTH - metrics.stringWidth("Your Score: " + miceEaten))/2, g.getFont().getSize());

        JFrame frame = new JFrame(){};
        frame.setVisible(true);
        frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Dimension dimension = toolkit.getScreenSize();
        frame.setSize(dimension.width/2,dimension.height/2);
        frame.setLocation(dimension.width/4,dimension.height/4);
        frame.setTitle("Save you Score");
        Container container = new Container();
        Container containerScore = new Container();
        Container containerButton = new Container();
        container.setLayout(new GridLayout(2,1,2,2));
        containerScore.setLayout(new GridLayout(1,1,2,2));
        containerButton.setLayout(new GridLayout(1,2,2,2));
        container.add(containerScore);
        container.add(containerButton);
        JLabel label = new JLabel(" Your score = " + miceEaten);
        label.setFont(new Font("Ink Free", Font.BOLD, dimension.width/15));
        containerScore.add(label);
        JButton saveButton = new JButton("Save?");
        saveButton.setSize(dimension.width/15,dimension.width/15);
        containerScore.add(label);
        String score = "New record!";;
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader("Score"));
            score = bufferedReader.readLine();
        } catch (FileNotFoundException e) {
            String newScore = "user " + miceEaten;
            FileWriter writer = null;
            try {
                writer = new FileWriter("score");
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
            try {
                writer.write(newScore);
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
            try {
                writer.close();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        JLabel label2 = new JLabel(score);
        containerButton.add(label2);
        containerButton.add(saveButton);
        frame.add(container);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(running = true){
            move();
            checkMouse();
            checkCollisions();
        }
        repaint();
    }

    public class MyKeyAdapter extends KeyAdapter{
        @Override
        public void keyPressed(KeyEvent e){
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
