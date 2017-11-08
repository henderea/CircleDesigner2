import java.awt.*;
import java.awt.event.*;
import java.awt.print.PrinterJob;
import javax.swing.*;
import javax.swing.event.*;

public class CircleDesigner extends JFrame
{
    private       DrawPanel panel      = null;
    private final int       numPoints  = 8;
    private final boolean   showBorder = false;

    public static void main(String[] args)
    {
        new CircleDesigner();
    }

    public CircleDesigner()
    {
        panel = new DrawPanel(numPoints, showBorder);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.addKeyListener(new KeyAdapter()
        {
            public void keyPressed(KeyEvent e)
            {
                //if(e.getKeyCode() == KeyEvent.VK_M && e.isControlDown()) setExtendedState(JFrame.ICONIFIED);
                if(e.getKeyCode() == KeyEvent.VK_ESCAPE)
                {
                    try
                    {
                        Thread.sleep(10);
                    }
                    catch(Exception ignore) {}
                    setVisible(false);
                    try
                    {
                        Thread.sleep(10);
                    }
                    catch(Exception ignore) {}
                    System.exit(0);
                }
                else if(e.getKeyCode() == KeyEvent.VK_S && e.isControlDown())
                {
                    //setExtendedState(JFrame.ICONIFIED);
                    panel.save();
                    //setExtendedState(JFrame.NORMAL);
                }
                else if(e.getKeyCode() == KeyEvent.VK_B)
                {
                    panel.toggleBorders();
                    panel.repaint();
                }
                else if(e.getKeyCode() == KeyEvent.VK_UP)
                {
                    panel.setNumPoints(true);
                    panel.repaint();
                }
                else if(e.getKeyCode() == KeyEvent.VK_DOWN)
                {
                    panel.setNumPoints(false);
                    panel.repaint();
                }
            }
        });
        this.setLayout(new GridLayout(1, 1));
        this.add(panel);
        //setUndecorated(true);
        this.setSize(600, 600);
        this.setVisible(true);
        //this.setExtendedState(JFrame.MAXIMIZED_BOTH);
        //initFullScreen();
        panel.repaint();
    }

    private void initFullScreen()
    {
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice gd = ge.getDefaultScreenDevice();
        if(!gd.isFullScreenSupported())
        {
            System.out.println("Full-screen exclusive mode not supported");
            return;
        }
        setResizable(false);
        gd.setFullScreenWindow(this);
    }
}