import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;

public class Main{
    public static void main(String[] args){
       SwingUtilities.invokeLater(() -> new StopWatch());
    }
}

class StopWatch extends JFrame {
    // Logic Variables
    private long elapsedTime = 0, startTime = 0;
    private boolean isRunning = false, highPrecision = false;
    private int lightIndex = 0;
    
    // UI Components
    private Timer timer;
    private JLabel timeDisplay, hourDisplay, msDisplay, precisionDisplay;
    private BackgroundPanel bgPanel;
    private CircularButton btnStart, btnReset, btnLight, btnMode;
    private Image watchImages[] = {
        new ImageIcon("resources/Watch0.png").getImage(),
        new ImageIcon("resources/Watch1.png").getImage(),
        new ImageIcon("resources/Watch2.png").getImage(),
        new ImageIcon("resources/Watch3.png").getImage(),
        new ImageIcon("resources/Watch4.png").getImage()
    };
    
    // Theme Colors (topleft color, bottomright color, button color)
    private Color[][] themes = {
        {new Color(0, 105, 246), new Color(0,242,255)    ,new Color(195,253,255)}, //Blue
        {new Color(158, 0, 171), new Color(255, 210, 255), new Color(194,142,255)}, // Purple
        {new Color(255, 20, 3), new Color(255, 209, 72), new Color(255,204,50)}, // Red
        {new Color(255, 128, 4), new Color(255, 245, 0), new Color(255,252,36)}, //orange
        {new Color(20, 204, 0), new Color(6, 255, 227), new Color(122,255,99)} // Green
    };
    //constructor
    public StopWatch() {
        //initial window setup
        setTitle("StopWatch");
        setSize(680, 480);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        System.setProperty("awtsynchronize", "true");
        setIconImage(new ImageIcon("resources/stopwatch.png").getImage());
        //Background panel
        bgPanel = new BackgroundPanel();
        bgPanel.setLayout(null);
        add(bgPanel);
        // time displays
        //hours
        hourDisplay = new JLabel("00", SwingConstants.CENTER);
        hourDisplay.setFont(loadFont(17));
        hourDisplay.setForeground(Color.BLACK);
        hourDisplay.setBounds(272, 144, 50, 50); 
        // minutes and seconds
        timeDisplay = new JLabel("00:00", SwingConstants.CENTER);
        timeDisplay.setFont(loadFont(29));
        timeDisplay.setForeground(Color.BLACK);
        timeDisplay.setBounds(210, 210, 250, 50); 
        // milliseconds
        msDisplay = new JLabel("00", SwingConstants.CENTER);
        msDisplay.setFont(loadFont(17));
        msDisplay.setForeground(Color.BLACK);
        msDisplay.setBounds(338, 180, 80, 50); 
        // precision mode
        precisionDisplay = new JLabel("P1  1/100  s", SwingConstants.CENTER);
        precisionDisplay.setFont(new Font("Monospace", Font.BOLD, 12));
        precisionDisplay.setForeground(Color.BLACK);
        precisionDisplay.setBounds(300, 248, 80, 30);
        // adding Jlabels to panel
        bgPanel.add(hourDisplay);
        bgPanel.add(timeDisplay);
        bgPanel.add(msDisplay);
        bgPanel.add(precisionDisplay);
        //circular Buttons
        // start/stop
        btnStart = new CircularButton("START");
        btnStart.setBounds(465, 100, 75, 75); 
        btnStart.addActionListener(e -> toggleTimer()); 
        // reset
        btnReset = new CircularButton("RESET");
        btnReset.setBounds(465, 250, 75, 75 );
        btnReset.addActionListener(e -> resetTimer());
        // light
        btnLight = new CircularButton("LIGHT");
        btnLight.setBounds(135, 100, 75, 75);
        btnLight.addActionListener(e -> changeLight());
        // mode
        btnMode = new CircularButton("MODE");
        btnMode.setBounds(135, 250, 75, 75);
        btnMode.addActionListener(e -> toggleMode());
        // adding buttons to panel
        bgPanel.add(btnStart);
        bgPanel.add(btnReset);
        bgPanel.add(btnLight);
        bgPanel.add(btnMode);
        // initializing timer
        timer = new Timer(10, e -> updateTime()); // calls updateTime after every 10ms
        // setting up the keyboard buttons
        setupKeyBindings();
        // Initial theme setup
        updateTheme(); 
        setVisible(true);
    }
    // update time method
    private void updateTime() {
        elapsedTime = System.currentTimeMillis() - startTime;
        updateDisplay();
    }
    // update the timer display
    private void updateDisplay() {
        long h = (elapsedTime / 3600000);
        long m = (elapsedTime % 3600000) / 60000;
        long s = (elapsedTime % 60000) / 1000;
        long ms = (elapsedTime%1000); 
        if (highPrecision) {
            hourDisplay.setText(String.format("%02d", h));
            timeDisplay.setText(String.format("%02d:%02d",  m, s));
            msDisplay.setText(String.format("%03d", ms));
            precisionDisplay.setText("P2  1/1000 s");
        } else {
            ms/=10;
            hourDisplay.setText(String.format("%02d", h));
            timeDisplay.setText(String.format("%02d:%02d",  m, s));
            msDisplay.setText(String.format("%02d", ms));
            precisionDisplay.setText("P1  1/100  s");
        }
    }
    // change the background light and image
    private void changeLight() {
        lightIndex = (lightIndex + 1) % 5;
        updateTheme();
    }
    // update the background colors, gradient and watch image
    private void updateTheme() {
        Color mainColor1 = themes[lightIndex][0];
        Color mainColor2 = themes[lightIndex][1];
        Color btnColor = themes[lightIndex][2];
        
        bgPanel.setColors(mainColor1, mainColor2);
        btnStart.setButtonColor(btnColor);
        btnReset.setButtonColor(btnColor);
        btnLight.setButtonColor(btnColor);
        btnMode.setButtonColor(btnColor);
        
        // Update Image
        bgPanel.setWatchImage(watchImages[lightIndex]);
    }
    // toggle start and stop method
    private void toggleTimer() {
        if (!isRunning) {
            startTime = System.currentTimeMillis() - elapsedTime;
            timer.start();
        } else 
            timer.stop();
        isRunning = !isRunning;
    }
    // reset timer
    private void resetTimer() {
        if (!isRunning) { 
            elapsedTime = 0; updateDisplay(); 
        }
    }
    // toggle precision mode
    private void toggleMode() {
        if (!isRunning) { 
            highPrecision = !highPrecision; 
            updateDisplay(); 
        }
    }
    // Key Logic (Space, R, L, M)
    private void setupKeyBindings() {
        InputMap inputm = bgPanel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap actionm = bgPanel.getActionMap();
    
        inputm.put(KeyStroke.getKeyStroke(" "), "s");
        actionm.put("s", new AbstractAction() { public void actionPerformed(ActionEvent e) { btnStart.doClick(100); }});
    
        inputm.put(KeyStroke.getKeyStroke('r'), "r");
        actionm.put("r", new AbstractAction() { public void actionPerformed(ActionEvent e) { btnReset.doClick(100); }});
    
        inputm.put(KeyStroke.getKeyStroke('l'), "l");
        actionm.put("l", new AbstractAction() { public void actionPerformed(ActionEvent e) { btnLight.doClick(100); }});
    
        inputm.put(KeyStroke.getKeyStroke('m'), "m");
        actionm.put("m", new AbstractAction() { public void actionPerformed(ActionEvent e) { btnMode.doClick(100); }});
    }
    // loading custom for for time display
    private Font loadFont(float size) {
        try {
            return Font.createFont(Font.TRUETYPE_FONT, new File("resource/DSEG7Classic-Bold.ttf")).deriveFont(Font.BOLD, size);
        } 
        catch (FontFormatException | IOException e) {
            e.printStackTrace();
            return new Font("Arial", Font.BOLD, (int) size); // Fallback font
        }
    }
}
// class for custom circular buttons
class CircularButton extends JButton {
    // default button color
    private Color btnColor = Color.DARK_GRAY;

    public CircularButton(String text) {
        super(text);
        // This makes the button background transparent so only our circle shows
        setContentAreaFilled(false);
        setFocusPainted(false);
        setBorderPainted(false);
        setForeground(Color.BLACK);
        setFont(new Font("Arial", Font.BOLD, 12));
    }
    // set the button color
    public void setButtonColor(Color c) { 
        this.btnColor = c; 
        repaint(); 
    }
    // custom paint component to draw circle and center text
    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        //Draw  Circle
        int diameter = Math.min(getWidth(), getHeight()) - 2;
        int x = (getWidth() - diameter) / 2;
        int y = (getHeight() - diameter) / 2;
        // Change color if button is pressed
        g2d.setColor(getModel().isArmed() ? btnColor.darker() : btnColor);
        g2d.fillOval(x, y, diameter, diameter);
        //Center the Text
        FontMetrics fm = g2d.getFontMetrics();
        int textX = (getWidth() - fm.stringWidth(getText())) / 2;
        int textY = (getHeight() + fm.getAscent()) / 2 - 3;
        // Draw Text
        g2d.setColor(getForeground());
        g2d.drawString(getText(), textX, textY);
        // Dispose Graphics
        g2d.dispose();
    }

    // ensuring only the circle is clickable, not the square corners
    @Override
    public boolean contains(int x, int y) {
        double centerX = getWidth() / 2.0;
        double centerY = getHeight() / 2.0;
        double radius = Math.min(getWidth(), getHeight()) / 2.0;
        
        // Pythagorean theorem: a^2 + b^2 = c^2
        return Math.pow(x - centerX, 2) + Math.pow(y - centerY, 2) <= Math.pow(radius, 2);
    }
}
// custom background to handel gradient and image drawing
class BackgroundPanel extends JPanel {
    // defualt colors
    private Color c1 = Color.BLACK, c2 = Color.BLACK;

    private Image watchImg;
    // set gradient colors
    public void setColors(Color a, Color b) {
        this.c1 = a; this.c2 = b; repaint();
    }
    // set image
    public void setWatchImage(Image img) { 
        this.watchImg = img; repaint(); 
    }
    // custom paint component to draw gradient and image
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setPaint(new GradientPaint(0, 0, c1, getWidth(), getHeight(), c2));
        g2d.fillRect(0, 0, getWidth(), getHeight());

        // Draw horizontal line 
        g2d.setColor(Color.BLACK);
        g2d.setStroke(new BasicStroke(2));
        g2d.drawLine(280, 253, 400, 253);

        if (watchImg != null) {
            int x = (getWidth() - 450) / 2;
            int y = (getHeight() - 450) / 2;
            g2d.drawImage(watchImg, x, y, 450, 450, null);
        }
    }
}