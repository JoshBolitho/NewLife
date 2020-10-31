import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class NewLifeSimulator extends JFrame{

    private SimComponent simComp = new SimComponent();

    public NewLifeSimulator() {
        //gui setup
        super("NewLife");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setPreferredSize(new Dimension(1600,900));
        setResizable(true);

        add(simComp, BorderLayout.CENTER);

        createMenu();
        pack();

        setVisible(true);
    }

    private void createMenu() {

        JMenuBar menu = new JMenuBar();
        final JMenuItem start = new JMenuItem("Start");
        final JMenuItem reset = new JMenuItem("Reset");
        JMenuItem quit = new JMenuItem("Quit");

        menu.add(start);
        menu.add(reset);
        menu.add(quit);
        setJMenuBar(menu);

        // Add listeners to menu buttons.

        start.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                simComp.start();
            }
        });

        reset.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                simComp.reset();
                simComp.repaint();
            }
        });

        quit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });

    }

    public static void main(String[] args) {
        new NewLifeSimulator();
    }
}
