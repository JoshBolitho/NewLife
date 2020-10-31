import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Random;

public class SimComponent extends JComponent{
    boolean running;
    int frame;
    ArrayList<Cell> cells;
    ArrayList<Food> food;

    Random random;

    int delayMillis = 15;
    Thread t;


    //Simulation parameters

    // the relationship between the mass of a cell and the damage it can do to another cell.
    double massDamageCoefficient = 1;

    //velocity lost per step
    double friction = 0;

    // bounds for the looped simulated area
    double maxX = 500;
    double maxY = 500;


    boolean drawViewRanges = false;

    public SimComponent() {
        running = false;
        frame = 0;
        cells = new ArrayList<>();
        food = new ArrayList<>();
        random = new Random(System.currentTimeMillis());

        //testing
        for(int i=0; i<10; i++){
            addCell();
            addFood();
        }
    }
    public void start(){
        t = new Thread(new Runnable() {
            @Override
            public void run() {
                running = true;

                while(running){
                    repaint();
//                    revalidate();
//                    stepSimulation();
                    try{
                        Thread.sleep(delayMillis);
                    }catch (Exception e){}
                }
            }
        });
        t.start();
    }

    public void stepSimulation(){
        for (Cell c : cells) {
            //heal before choosing actions
            c.healStep();
            //coasting with excess velocity from previous step,
            //as well as the added acceleration chosen in the previous moveTowardsChosenAction() step.
            c.drift();
        }


        //choose action given the healed state of the cell, and it's position after drifting/moving.
        for(Cell c : cells){
            //first, determine willingness for each action
            c.calculateReproduceWillingness();
            c.determineCombatWillingness(c.reachDistance);
            c.determineFeedingWillingness(c.reachDistance);
        }
        for(Cell c : cells){
            //calculate cell deaths
            if(c.wallMass<0){
                cells.remove(c);
                //add a new food particle corresponding to the cell's mass and energy where the cell died.
                food.add(new Food(c.energy,c.cellMass,c.x,c.y));
            }else {
                // cell has survived actions of all cells before it, so it gets to act itself.
                c.act();
            }
        }

        //determine what the cell wants to do next, to decide which
        //direction it will apply acceleration for the next step.
        for(Cell c : cells) {
            c.calculateReproduceWillingness();
            c.determineCombatWillingness(c.viewDistance);
            c.determineFeedingWillingness(c.viewDistance);
        }
        for(Cell c: cells) {
            c.moveTowardsChosenAction();
        }

    }

    public void reset(){
        running = false;
        frame = 0;
        cells = new ArrayList<>();
        food = new ArrayList<>();
        random = new Random(System.currentTimeMillis());

        for(int i=0; i<10; i++){
            addCell();
            addFood();
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        for(Cell c : cells){
            c.paint(g2d,drawViewRanges);
        }

        for(Food f : food){
            f.paint(g2d);
        }
    }

    public boolean removeCell(){
        if(cells.size()<=0){return false;}

        //Remove last in cells list
        cells.remove(-1);
        return true;
    }

    public void addCell(){
        cells.add(new Cell(
                maxX*random.nextDouble(), maxY*random.nextDouble(), this
        ));
    }

    public void addFood(){
        food.add(new Food(
                100*random.nextDouble(), 100*random.nextDouble(),maxX*random.nextDouble(),maxY*random.nextDouble()
        ));
    }
}
