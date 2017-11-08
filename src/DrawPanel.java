import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.awt.image.BufferedImage;
import javax.swing.*;
import javax.swing.event.*;
import java.awt.print.*;
import java.io.File;
import javax.imageio.ImageIO;
import javax.swing.filechooser.FileNameExtensionFilter;

public class DrawPanel extends JPanel
{
    private int                     numPoints  = 8;
    private int                     x          = this.getWidth();
    private int                     y          = this.getHeight();
    private int                     side       = (x <= y ? x - 20 : y - 20);
    private double                  xOffset    = (x - side) / 2;
    private double                  yOffset    = (y - side) / 2;
    private Point2D.Double[]        points     = null;
    private int                     radius     = side / 4;
    private FileNameExtensionFilter filter     = new FileNameExtensionFilter("PNG Image", "png");
    private JFileChooser            chooser    = new JFileChooser();
    private boolean                 canPaint   = true;
    private boolean                 showBorder = true;

    public DrawPanel(int numPoints, boolean showBorder)
    {
        this.numPoints = numPoints;
        this.showBorder = showBorder;
        chooser.setAcceptAllFileFilterUsed(false);
        chooser.addChoosableFileFilter(filter);
    }

    public void toggleBorders()
    {
        showBorder = !showBorder;
    }

    public void setNumPoints(boolean up)
    {
        numPoints += (up ? 1 : -1);
        if(numPoints < 4) numPoints = 4;
        if(numPoints > 50) numPoints = 50;
    }

    public void paint(Graphics g)
    {
        if(!canPaint) return;
        refreshPoints();
        Graphics2D g2 = (Graphics2D) g;
        g2.clearRect(0, 0, x, y);
        paintPoints(g2, points, radius);
    }

    private void paintPoints(Graphics2D g2, Point2D.Double[] usePoints, int useRadius)
    {
        //g2.setColor(new Color(63, 127, 255, 15));
        g2.setColor(new Color(0, 0, 255, 10));
        for(int i = 0; i < numPoints * numPoints; i++)
        {
            //g2.setColor(new Color(63, 127, 255, 15));
            //g2.setColor(new Color(0, 0, 255, 15));
            g2.fill(new Ellipse2D.Double(usePoints[i].getX() /*+ radius / 2*/, usePoints[i].getY() /*+ radius / 2*/,
                                         useRadius, useRadius));
            //g2.setColor(Color.black);
            //g2.draw(new Ellipse2D.Double(usePoints[i].getX() /*+ radius / 2*/, usePoints[i].getY() /*+ radius / 2*/, radius * 2, radius * 2));
        }
        if(!showBorder) return;
        g2.setColor(Color.black);
        for(int i = 0; i < numPoints * numPoints; i++)
        {
            //g2.setColor(new Color(0, 0, 255, 15));
            //g2.fill(new Ellipse2D.Double(usePoints[i].getX() /*+ radius / 2*/, usePoints[i].getY() /*+ radius / 2*/, radius * 2, radius * 2));
            //g2.setColor(Color.black);
            g2.draw(new Ellipse2D.Double(usePoints[i].getX() /*+ radius / 2*/, usePoints[i].getY() /*+ radius / 2*/,
                                         useRadius, useRadius));
        }
    }

    private void refreshSize()
    {
        x = this.getWidth();
        y = this.getHeight();
        side = (x <= y ? x - 20 : y - 20);
        xOffset = (x <= y ? 10 : (x - side) / 2);
        yOffset = (y <= x ? 10 : (y - side) / 2);
        radius = side / 4;
    }

    private void refreshPoints()
    {
        refreshSize();
        points = new Point2D.Double[numPoints * numPoints];
        double angleDiff = (2 * Math.PI) / numPoints;
        for(int i = 0; i < numPoints; i++)
        {
            for(int j = 0; j < numPoints; j++)
            {
                points[i * numPoints + j] = new Point2D.Double(
                        (radius * (1 + Math.cos((angleDiff * i) - (Math.PI / 2)) + 0.5 * (1 + Math
                                .cos((angleDiff * j) - (Math.PI / 2))))) + xOffset,
                        (radius * (1 + Math.sin((angleDiff * i) - (Math.PI / 2)) + 0.5 * (1 + Math
                                .sin((angleDiff * j) - (Math.PI / 2))))) + yOffset);
            }
        }
    }

    private int findLineEnd(int startIndex, int interval, int numPoints)
    {
        int end = startIndex + interval;
        return (end > numPoints - 1 ? end - numPoints : end);
    }

    public void save()
    {
        if(points == null) return;
        canPaint = false;
        File file = showSaveDialog();
        if(file == null)
        {
            canPaint = true;
            return;
        }
        BufferedImage img = new BufferedImage(1500, 1500, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2 = img.createGraphics();
        g2.setBackground(Color.white);
        g2.setColor(Color.black);
        g2.clearRect(0, 0, 1500, 1500);
        int saveSide = 1490;
        int saveXOffset = 5;
        int saveYOffset = 5;
        int saveRadius = saveSide / 4;
        Point2D.Double[] savePoints = new Point2D.Double[numPoints];
        double angleDiff = (2 * Math.PI) / numPoints;
        for(int i = 0; i < numPoints; i++)
        {
            savePoints[i] = new Point2D.Double(
                    (saveRadius * (1 + Math.cos((angleDiff * i) - (Math.PI / 2)))) + saveXOffset,
                    (saveRadius * (1 + Math.sin((angleDiff * i) - (Math.PI / 2)))) + saveYOffset);
        }
        paintPoints(g2, savePoints, saveRadius);
        try
        {
            ImageIO.write(img, "jpg", file);
        }
        catch(Exception ignore) {}
        canPaint = true;
    }

    public File showSaveDialog()
    {
        File file = null;  // create the File instance that the JFileChooser will return
        int returnVal = chooser.showSaveDialog(this);  // Show the SaveDialog and store the return value
        if(returnVal == JFileChooser.APPROVE_OPTION)  // if the user accepted the dialog
            file = chooser.getSelectedFile();  // get the file from the JFileChooser
        else  // the user canceled the action
            return null;  // the action failed
        if(!filter.accept(file))  // if the file does not match the types allowed
        {
            String path = file.getAbsolutePath();  // get the absolute path of the file
            String name = path.substring(path.lastIndexOf(File.pathSeparator) + 1);  // get the filename
            if(name.indexOf(".") >= 0)  // if the name has an extension
                name = name.substring(0, name.indexOf("."));  // get rid of the extension
            path = path.substring(0, path.lastIndexOf(File.pathSeparator) + 1);  // take the name out of the path
            try
            {
                file = new File(path + name + ".png");  // recreate the file as a png
            }
            catch(Exception ignore) {}
        }
        if(file.exists())  // if the file already exists (means that it would have to overwrite)
        {
            boolean confirmed = showConfirmDialog("This file already exists. Are you sure you want to overwrite it?",
                                                  "Yes",
                                                  "No");  // asks the user to confirm that they want to overwrite the file
            if(!confirmed)  // if the user did not want to overwrite
                return showSaveDialog();  // show the dialog again
        }
        return file;  // return the file to save the image to
    }

    private JDialog dialogC;
    private int     chosenChange;

    public boolean showConfirmDialog(String toConfirm, String yesText, String noText)
    {
        dialogC = new JDialog();
        dialogC.addWindowListener(new WindowAdapter()
        {
            public void windowClosing(WindowEvent event)
            {
                if(dialogC != null) dialogC.setVisible(false);
                chosenChange = -1;
                dialogC = null;
            }
        });
        dialogC.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        toConfirm = toConfirm.trim();
        dialogC.setTitle(toConfirm.equalsIgnoreCase("") ? "Are you sure you want to do this?" : toConfirm);
        dialogC.setAlwaysOnTop(true);
        dialogC.setModal(true);
        dialogC.setSize(400, 200);
        JLabel promptLabel = new JLabel(
                toConfirm.equalsIgnoreCase("") ? "Are you sure you want to do this?" : toConfirm);
        promptLabel.setHorizontalAlignment(JLabel.CENTER);
        noText = noText.trim();
        yesText = yesText.trim();
        JButton yesButton = new JButton(yesText.equalsIgnoreCase("") ? "Yes" : yesText);
        JButton noButton = new JButton(noText.equalsIgnoreCase("") ? "No" : noText);
        Box box1 = Box.createVerticalBox();
        box1.add(Box.createVerticalGlue());
        JPanel panel1 = new JPanel(new GridLayout(1, 1));
        panel1.add(promptLabel);
        box1.add(promptLabel);
        box1.add(Box.createVerticalGlue());
        Box box2 = Box.createHorizontalBox();
        box2.add(Box.createHorizontalGlue());
        box2.add(yesButton);
        box2.add(Box.createHorizontalGlue());
        box2.add(noButton);
        box2.add(Box.createHorizontalGlue());
        box1.add(box2);
        box1.add(Box.createVerticalGlue());
        dialogC.add(box1, BorderLayout.CENTER);
        yesButton.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent event)
            {
                if(dialogC != null) dialogC.setVisible(false);
                chosenChange = 0;
                dialogC = null;
            }
        });
        noButton.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent event)
            {
                if(dialogC != null) dialogC.setVisible(false);
                chosenChange = -1;
                dialogC = null;
            }
        });
        dialogC.setVisible(true);
        while(dialogC != null) { ; }
        if(chosenChange == 0)
        {
            chosenChange = -1;
            return true;
        }
        return false;
    }
}