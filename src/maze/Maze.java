package maze;

import java.io.*; 
import java.awt.*;
import java.awt.geom.*;
import java.awt.event.*;
import javax.swing.*;




public class Maze extends JFrame implements Runnable {

    static final int numRows = 20;
    static final int numColumns = 20;
    static final int XBORDER = 40;
    static final int YBORDER = 60;
    static final int YTITLE = 30;
    static final int WINDOW_BORDER = 8;
    static final int WINDOW_WIDTH = 2*(WINDOW_BORDER + XBORDER) + numColumns*30;
    static final int WINDOW_HEIGHT = YTITLE + WINDOW_BORDER + 2 * YBORDER + numRows*30;
    
    boolean animateFirstTime = true;
    int xsize = -1;
    int ysize = -1;
    Image image;
    Graphics2D g;

    final int PATH = 0;
    final int WALL = 1;
    final int SECR = 2;
    int board[][] = 
    {{WALL,WALL,WALL,WALL,PATH,WALL,WALL,WALL,WALL,WALL,WALL,WALL,WALL,WALL,WALL,WALL,WALL,WALL,WALL,WALL},
     {WALL,PATH,PATH,PATH,PATH,PATH,PATH,WALL,WALL,WALL,WALL,WALL,WALL,WALL,PATH,PATH,PATH,PATH,PATH,WALL},        
     {WALL,PATH,WALL,WALL,PATH,PATH,PATH,SECR,SECR,SECR,SECR,SECR,PATH,PATH,PATH,WALL,WALL,WALL,PATH,WALL},        
     {WALL,PATH,WALL,WALL,PATH,PATH,PATH,WALL,WALL,WALL,WALL,PATH,PATH,WALL,WALL,WALL,WALL,WALL,PATH,WALL},        
     {PATH,PATH,PATH,WALL,PATH,PATH,PATH,WALL,WALL,WALL,WALL,PATH,WALL,WALL,WALL,WALL,WALL,WALL,PATH,PATH},        
     {WALL,WALL,PATH,WALL,PATH,WALL,PATH,PATH,PATH,PATH,PATH,PATH,PATH,PATH,PATH,PATH,PATH,PATH,PATH,WALL},        
     {WALL,WALL,PATH,WALL,PATH,WALL,WALL,WALL,SECR,WALL,WALL,PATH,WALL,WALL,WALL,WALL,PATH,PATH,PATH,WALL},        
     {WALL,PATH,PATH,PATH,PATH,WALL,WALL,WALL,SECR,WALL,WALL,PATH,WALL,WALL,WALL,WALL,PATH,PATH,PATH,WALL},        
     {WALL,PATH,WALL,WALL,PATH,WALL,SECR,SECR,SECR,WALL,WALL,PATH,WALL,WALL,WALL,WALL,PATH,PATH,PATH,WALL},        
     {WALL,PATH,WALL,WALL,PATH,WALL,SECR,WALL,WALL,WALL,WALL,PATH,WALL,WALL,WALL,WALL,PATH,PATH,PATH,WALL},        
     {WALL,PATH,WALL,WALL,PATH,SECR,SECR,WALL,WALL,WALL,WALL,PATH,WALL,WALL,WALL,WALL,PATH,PATH,PATH,WALL},        
     {WALL,PATH,WALL,WALL,PATH,WALL,WALL,WALL,WALL,WALL,WALL,PATH,WALL,WALL,WALL,WALL,PATH,WALL,WALL,WALL},        
     {WALL,PATH,PATH,PATH,PATH,PATH,PATH,PATH,PATH,PATH,PATH,PATH,WALL,WALL,WALL,WALL,PATH,WALL,WALL,WALL},        
     {WALL,WALL,WALL,WALL,PATH,WALL,WALL,WALL,PATH,PATH,PATH,PATH,SECR,SECR,SECR,SECR,PATH,WALL,WALL,WALL},        
     {WALL,WALL,WALL,WALL,PATH,WALL,WALL,WALL,PATH,PATH,PATH,PATH,WALL,WALL,WALL,WALL,PATH,WALL,WALL,WALL},        
     {WALL,WALL,WALL,WALL,PATH,WALL,WALL,WALL,PATH,PATH,PATH,PATH,WALL,WALL,WALL,WALL,PATH,WALL,WALL,WALL},        
     {WALL,WALL,WALL,WALL,PATH,WALL,PATH,PATH,PATH,PATH,PATH,PATH,PATH,PATH,WALL,WALL,PATH,WALL,WALL,WALL},        
     {WALL,WALL,WALL,WALL,PATH,WALL,PATH,WALL,WALL,WALL,WALL,WALL,WALL,PATH,WALL,WALL,PATH,WALL,WALL,WALL},        
     {WALL,WALL,WALL,WALL,PATH,PATH,PATH,WALL,WALL,WALL,WALL,WALL,WALL,PATH,PATH,PATH,PATH,WALL,WALL,WALL},        
     {WALL,WALL,WALL,WALL,PATH,WALL,WALL,WALL,WALL,WALL,WALL,WALL,WALL,WALL,WALL,WALL,WALL,WALL,WALL,WALL}
    };
    
//Variables for player.
    int columnDir;
    int rowDir;
    Character player;
    
    Coin coin[];
    Character npcs[];
    boolean gameOver;

    int timeCount;
    boolean secretVisible;
    
    
    static Maze frame;
    public static void main(String[] args) {
        frame = new Maze();
        frame.setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    public Maze() {
        addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                if (e.BUTTON1 == e.getButton()) {
                    //left button

// location of the cursor.
                    int xpos = e.getX();
                    int ypos = e.getY();

                }
                if (e.BUTTON3 == e.getButton()) {
                    //right button
                    reset();
                }
                repaint();
            }
        });

    addMouseMotionListener(new MouseMotionAdapter() {
      public void mouseDragged(MouseEvent e) {
        repaint();
      }
    });

    addMouseMotionListener(new MouseMotionAdapter() {
      public void mouseMoved(MouseEvent e) {

        repaint();
      }
    });

        addKeyListener(new KeyAdapter() {

            public void keyPressed(KeyEvent e) {

                if (e.VK_UP == e.getKeyCode()) {
                    rowDir = -1;
                    columnDir = 0;
                    
                } else if (e.VK_DOWN == e.getKeyCode()) {
                    rowDir = 1;
                    columnDir = 0;

                } else if (e.VK_LEFT == e.getKeyCode()) {
                    rowDir = 0;
                    columnDir = -1;

                } else if (e.VK_RIGHT == e.getKeyCode()) {
                    rowDir = 0;
                    columnDir = 1;
                }
                
                repaint();
            }
        });
        init();
        start();
    }
    Thread relaxer;
////////////////////////////////////////////////////////////////////////////
    public void init() {
        requestFocus();
    }
////////////////////////////////////////////////////////////////////////////
    public void destroy() {
    }

 

////////////////////////////////////////////////////////////////////////////
    public void paint(Graphics gOld) {
        if (image == null || xsize != getSize().width || ysize != getSize().height) {
            xsize = getSize().width;
            ysize = getSize().height;
            image = createImage(xsize, ysize);
            g = (Graphics2D) image.getGraphics();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
        }
//fill background
        g.setColor(Color.cyan);
        g.fillRect(0, 0, xsize, ysize);

        int x[] = {getX(0), getX(getWidth2()), getX(getWidth2()), getX(0), getX(0)};
        int y[] = {getY(0), getY(0), getY(getHeight2()), getY(getHeight2()), getY(0)};
//fill border
        g.setColor(Color.white);
        g.fillPolygon(x, y, 4);
// draw border
        g.setColor(Color.red);
        g.drawPolyline(x, y, 5);

        if (animateFirstTime) {
            gOld.drawImage(image, 0, 0, null);
            return;
        }

        
        g.setColor(Color.red);
//horizontal lines
        for (int zi=1;zi<numRows;zi++)
        {
            g.drawLine(getX(0) ,getY(0)+zi*getHeight2()/numRows ,
            getX(getWidth2()) ,getY(0)+zi*getHeight2()/numRows );
        }
//vertical lines
        for (int zi=1;zi<numColumns;zi++)
        {
            g.drawLine(getX(0)+zi*getWidth2()/numColumns ,getY(0) ,
            getX(0)+zi*getWidth2()/numColumns,getY(getHeight2())  );
        }
        
//Display the objects of the board
        for (int zrow=0;zrow<numRows;zrow++)
        {
            for (int zcolumn=0;zcolumn<numColumns;zcolumn++)
            {
                if (board[zrow][zcolumn] == WALL)
                {
//Draw a wall square if it is a wall value.               
                    g.setColor(Color.gray);
                        
                    g.fillRect(getX(0)+zcolumn*getWidth2()/numColumns,
                    getY(0)+zrow*getHeight2()/numRows,
                    getWidth2()/numColumns,
                    getHeight2()/numRows);
                
                } 
                else if (board[zrow][zcolumn] == SECR && !secretVisible)
                {
//Draw a wall square if it is secret value but secret is off.            
                    g.setColor(Color.gray);
                        
                    g.fillRect(getX(0)+zcolumn*getWidth2()/numColumns,
                    getY(0)+zrow*getHeight2()/numRows,
                    getWidth2()/numColumns,
                    getHeight2()/numRows);                    
                }
                else if (board[zrow][zcolumn] == SECR && secretVisible)
                {
//Draw a secret passage square if it is secret value and secret is on.                
                    g.setColor(Color.cyan);
                        
                    g.fillRect(getX(0)+zcolumn*getWidth2()/numColumns,
                    getY(0)+zrow*getHeight2()/numRows,
                    getWidth2()/numColumns,
                    getHeight2()/numRows);                    
                }                
            }
        }            

        player.draw(g,frame);

        for (int i=0;i<coin.length;i++)
            coin[i].draw(g,frame);
        
        for (int i=0;i<npcs.length;i++)
            npcs[i].draw(g,frame);
        
  
        if (gameOver)
        {
            g.setColor(Color.black);
            g.setFont (new Font ("Arial",Font.PLAIN, 50)); 
            g.drawString("GAME OVER",200 , 400);            
            
        }

        g.setColor(Color.black);
        g.setFont (new Font ("Arial",Font.PLAIN, 20)); 
        g.drawString("Lives = " + player.numLives,50 , 80);            
        
        
        gOld.drawImage(image, 0, 0, null);
    }

////////////////////////////////////////////////////////////////////////////
// needed for     implement runnable
    public void run() {
        while (true) {
            animate();
            repaint();
            
            
            double seconds = .05;    //time that 1 frame takes.
            int miliseconds = (int) (1000.0 * seconds);
            try {
                Thread.sleep(miliseconds);
            } catch (InterruptedException e) {
            }
        }
    }
/////////////////////////////////////////////////////////////////////////
    public void reset() {
        secretVisible = false;
        timeCount = 0;
        gameOver = false;
        
//Initialize the values for the player.        
//The player will initially not move.        
        rowDir = 0;
        columnDir = 0;
        player.numLives = 3;
        player = new Character(frame);
        player.setPlayer(true);
        player.setColor(Color.green);
        player.setName("");
        
//Initialize the values for the coins.      
        coin = new Coin[Coin.numCoins];
        for (int i=0;i<coin.length;i++)
            coin[i] = new Coin(frame);
        
//Initialize the values for the npcs.        
        npcs = new Character[Character.numNpcs];
        for (int i=0;i<npcs.length;i++)
            npcs[i] = new Character(frame);
        
        npcs[0].setColor(Color.blue);
        npcs[0].setName("Jojo");
        npcs[0].setSpeed(3);
        
        npcs[1].setColor(Color.red);
        npcs[1].setName("Kiki");
        npcs[1].setSpeed(15);
        
        npcs[2].setColor(Color.pink);
        npcs[2].setName("Rocky");
        npcs[2].setSpeed(8);
       

    }
/////////////////////////////////////////////////////////////////////////
    public void animate() {
        if (animateFirstTime) {
            animateFirstTime = false;
            if (xsize != getSize().width || ysize != getSize().height) {
                xsize = getSize().width;
                ysize = getSize().height;
            }
            reset();
        }
        if (gameOver)
            return;

        player.animate(frame,-1,-1,coin,rowDir,columnDir); 
 //Stop the player from continuously moving.        
        rowDir = 0;
        columnDir = 0;
        
//Turn on the secret passage if the player has coins.
        if (player.getValue() > 0)
            secretVisible = true;
//Turn off the secret passage if the player has no coins.
        else
            secretVisible = false;

      
//Loop through all the npcs.        
        for (int i=0;i<npcs.length;i++)
        {
            if (npcs[i].animate(frame,player.getRow(),player.getColumn(),coin,rowDir,columnDir))
            {
//If an npc runs into the player, move the player to a random location.
//If player lives is 0 then the game is over.                
                player.moveRandom(frame);
                if (player.numLives <= 0)
                    gameOver = true;
            }
        }
        
        timeCount++;
    }
    

////////////////////////////////////////////////////////////////////////////
    public void start() {
        if (relaxer == null) {
            relaxer = new Thread(this);
            relaxer.start();
        }
    }
////////////////////////////////////////////////////////////////////////////
    public void stop() {
        if (relaxer.isAlive()) {
            relaxer.stop();
        }
        relaxer = null;
    }


/////////////////////////////////////////////////////////////////////////
    public int getX(int x) {
        return (x + XBORDER + WINDOW_BORDER);
    }

    public int getY(int y) {
        return (y + YBORDER + YTITLE );
    }

    public int getYNormal(int y) {
        return (-y + YBORDER + YTITLE + getHeight2());
    }
    
    public int getWidth2() {
        return (xsize - 2 * (XBORDER + WINDOW_BORDER));
    }

    public int getHeight2() {
        return (ysize - 2 * YBORDER - WINDOW_BORDER - YTITLE);
    }
}

class Coin {
    private static Image coinImage;    
    public static int numCoins = 10;
    private int row;
    private int column;
    private boolean visible;
    private int value;
    
    Coin(Maze frame) {
        
        coinImage = Toolkit.getDefaultToolkit().getImage("./coin.GIF");
        
//Coin values will be random from 3 to 8.        
        value = (int)(Math.random()*6)+3;
//Randomly choose a location on a path for the coin.        
        boolean keepLooping = true;
        while (keepLooping)
        {
            row = (int)(Math.random()*frame.numRows);
            column = (int)(Math.random()*frame.numColumns);
            if (frame.board[row][column] == frame.PATH)
            {
                keepLooping = false;
            }
        }          
//Make the coin visible.        
        visible = true;
    }

    public void draw(Graphics2D g,Maze frame)
    {
        if (!visible)
            return;

        
        
//        g.setColor(Color.yellow);
//        g.fillOval(frame.getX(0)+column*frame.getWidth2()/frame.numColumns,
//        frame.getY(0)+row*frame.getHeight2()/frame.numRows,
//        frame.getWidth2()/frame.numColumns,
//        frame.getHeight2()/frame.numRows);
//
//        g.setColor(Color.black);
//        g.drawOval(frame.getX(0)+column*frame.getWidth2()/frame.numColumns,
//        frame.getY(0)+row*frame.getHeight2()/frame.numRows,
//        frame.getWidth2()/frame.numColumns,
//        frame.getHeight2()/frame.numRows);
        drawImage(g,frame,coinImage,frame.getX(0)+column*frame.getWidth2()/frame.numColumns+15,
                frame.getY(0)+row*frame.getHeight2()/frame.numRows+16,0.0,.6,.6 );
            
        g.setColor(Color.black);
        g.setFont (new Font ("Arial",Font.PLAIN, 20)); 
        g.drawString("" + value,frame.getX(0)+column*frame.getWidth2()/frame.numColumns+10 , 
        frame.getY(0)+row*frame.getHeight2()/frame.numRows+20);            
        
             
    }
    public void drawImage(Graphics2D g,Maze thisObj,Image image,int xpos,int ypos,double rot,double xscale,double yscale) {
        int width = image.getWidth(thisObj);
        int height = image.getHeight(thisObj);
        g.translate(xpos,ypos);
        g.rotate(rot  * Math.PI/180.0);
        g.scale( xscale , yscale );

        g.drawImage(image,-width/2,-height/2,
        width,height,thisObj);

        g.scale( 1.0/xscale,1.0/yscale );
        g.rotate(-rot  * Math.PI/180.0);
        g.translate(-xpos,-ypos);
    }        
    public int animate(int characterRow,int characterColumn,Maze frame)
    {
//Return 0 if the coin is not visible.        
        if (!visible)
            return(0);
        
        if (characterRow == row && characterColumn == column)
        {
//If a character runs into the coin, make the coin go away and return the value of the coin.  
            visible = false;
            return(value);
        }
//Return 0 if the character did not run into the coin.        
        return(0);
    }
}


class Character {
    public static int numNpcs = 3;
    public static int numLives = 3;
    private int row;
    private int column;
    private int value;
    private boolean isPlayer;
    private Color color;
    private String name;
    private int speed;
    
// accessor
    public int getRow()
    {
        return (row);
    }
    public int getColumn()
    {
        return (column);
    }
    public int getValue()
    {
        return (value);
    }      
// mutator      
    public void setPlayer(boolean _isPlayer)
    {
        isPlayer = _isPlayer;
    }
    public void setName(String _name)
    {
        name = _name;
    }
    public void setColor(Color _color)
    {
        color = _color;
    }
    public void setSpeed(int _speed)
    {
        speed = _speed;
    }

    
    Character(Maze frame) {
//By default, the character is not the player.        
        isPlayer = false;
//The character starts with 0 coins.        
        value = 0;
//Randomly choose a location on a path for the coin.                
        boolean keepLooping = true;
        while (keepLooping)
        {
            row = (int)(Math.random()*frame.numRows);
            column = (int)(Math.random()*frame.numColumns);
            if (frame.board[row][column] == frame.PATH)
            {
                keepLooping = false;
            }
        }             
    }

    
  
    
    public void draw(Graphics2D g,Maze frame)
    {
        g.setColor(color);
        g.fillRect(frame.getX(0)+column*frame.getWidth2()/frame.numColumns,
        frame.getY(0)+row*frame.getHeight2()/frame.numRows,
        frame.getWidth2()/frame.numColumns,
        frame.getHeight2()/frame.numRows);

        g.setColor(Color.black);
        g.setFont (new Font ("Arial",Font.PLAIN, 20)); 
        g.drawString(name + " " + value,frame.getX(0)+column*frame.getWidth2()/frame.numColumns+10 , 
        frame.getY(0)+row*frame.getHeight2()/frame.numRows+20);            
    }
    
    public void moveRandom(Maze frame)
    {
//Move the character to a random location on a path.        
        boolean keepLooping = true;
        while (keepLooping)
        {
            row = (int)(Math.random()*frame.numRows);
            column = (int)(Math.random()*frame.numColumns);
            if (frame.board[row][column] == frame.PATH)
            {
                keepLooping = false;
            }
        }           
    }
    
    public boolean animate(Maze frame,int playerRow,int playerColumn,Coin coin[],int rowDir,int columnDir)
    {
        if (!isPlayer)
        {
//Randomly move the non player character.
            if (frame.timeCount % speed == speed-1)
            {
                int randomVal = (int)(Math.random()*4);
                rowDir = 0;
                columnDir = 0;
                if (randomVal == 0) {
                    rowDir = -1;
                    columnDir = 0;
                } else if (randomVal == 1) {
                    rowDir = 1;
                    columnDir = 0;
                } else if (randomVal == 2) {
                    rowDir = 0;
                    columnDir = -1;
                } else if (randomVal == 3) {
                    rowDir = 0;
                    columnDir = 1;
                }      
            }
            
        }
//Code for moving the character from 1 side to the other.
        if (column + columnDir == -1)
        {
            column = Maze.numColumns;
        }
        else if (column + columnDir == Maze.numColumns)
        {
            column = -1;
        }
        if (row + rowDir == -1)
        {
            row = Maze.numRows;
        }
        else if (row + rowDir == Maze.numRows)
        {
            row = -1;
        }
//Move the character if the new location is on a path.        
        if (frame.board[row + rowDir][column + columnDir] == frame.PATH)
        {
            row += rowDir;
            column += columnDir;  
        }
//Move the character if the new location is a visible secret passage.        
        else if (frame.board[row + rowDir][column + columnDir] == frame.SECR && frame.secretVisible)
        {
            row += rowDir;
            column += columnDir; 
        }
//Check if the character has collected a coin.
        for (int i=0;i<coin.length;i++)
            value += coin[i].animate(row, column,frame);
         
//Check if the non player character has run into the player.
        if (playerRow == row && playerColumn == column)
        {
            numLives--;                
            return true;
        }
        return false;

    }
}

