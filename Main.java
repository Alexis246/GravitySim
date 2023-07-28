import java.awt.Canvas;

import javax.swing.JFrame;

public class Main {
    public static void main(String[] args) {
        JFrame frame = new JFrame("Newton's Gravity Simulation");
        Canvas canvas = new Drawing();

        canvas.setSize(400, 400);
        frame.add(canvas);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}