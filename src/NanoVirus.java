
import javax.swing.*;
import java.awt.*;

public class NanoVirus {
    public static void main(String[] args) {
        Container container = new Container(); // Create CellEngine - JPanel Object
        JFrame jFrame = new JFrame("Nano-Virus");
        jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jFrame.add(container);
        jFrame.setBackground(Color.LIGHT_GRAY);
        jFrame.pack();
        jFrame.setVisible(true);
        jFrame.setResizable(false);

        CellEngine cellEngine = new CellEngine();
        container.Attach(cellEngine);

        Virus virus = new Virus();
        virus.Attach(cellEngine);
        virus.run();

        JSlider slider = new JSlider(JSlider.HORIZONTAL, 0, 100, 50);
        slider.setPaintTicks(true);
        slider.setPaintLabels(true);
        JLabel valueSlider = new JLabel();

        JLabel label = new JLabel("Time between move/draw calls");
        label.setForeground(Color.WHITE);


        while (true) {
            if (!cellEngine.loop) {

                container.Detach(cellEngine);
                virus.Detach(cellEngine);
                virus.stop();

                jFrame.dispose();
            }

            jFrame.repaint(); // Repaint JFrame's black background
            container.repaint(); // Move each Cell object
            try {
                Thread.sleep(slider.getValue());
                valueSlider.setText("Slider value: " + Integer.toString((slider.getValue())));
            } catch (Exception event) {
            }
        }


    } // End main
}
