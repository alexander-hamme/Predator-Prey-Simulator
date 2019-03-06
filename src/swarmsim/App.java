package swarmsim;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

public class App extends JFrame {


    public static final int DISPL_WDTH = 1300;
    public static final int DISPL_HGHT = 730;

    public App() throws IOException {
        this.setup();
    }



    private void setup() throws IOException {

        // create the buffering strategy which will allow AWT
        // to manage our accelerated graphics
//        createBufferStrategy(2);
//        strategy = getBufferStrategy();

        add(new Simulation());

        setSize(DISPL_WDTH, DISPL_HGHT);
        setResizable(false);
        pack();
        setTitle("Frogger");
        setLocationRelativeTo(null); // center the display on the screen
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }


    public static void main(String[] args) throws IOException {

        EventQueue.invokeLater(() -> {

            try {
                App app = new App();
                app.setVisible(true);
            } catch (IOException e) {
                System.out.println("Could not load Game: " + e.getMessage());
            }
        });
    }
}

