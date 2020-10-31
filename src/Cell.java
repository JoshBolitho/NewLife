import java.awt.*;
import java.util.ArrayList;

public class Cell {
    //the SimComponent this cell is in
    SimComponent sim;

    //Gene determined parameters
    double healTendency;

    double reproduceTendency;
    double excreteTendency;

    double moveToFoodTendency;

    double attackTendency;
    double evadeTendency;

    double expendEnergyTendency;

    double idealCellMass;
    double idealWallMass;
    double idealEnergy;

    //0 to 255
    int R = 0;
    int G = 0;
    int B = 0;

    double mutationRate;

    //Current state parameters
    double cellMass;
    double wallMass;
    double energy;

    double x;
    double y;

    double acceleration = 0;
    double direction = 0;

    //Calculable state parameters
    double cellArea;//==cellMass (given density = 1 area unit / mass unit)
    double cellRadius = 15;// = sqrt(cellArea/pi)
    double cellCircumference;// = 2pi * cellRadius

    double wallArea;// = wallMass (given density = 1 area unit / mass unit)
    double wallRadius;// = cellRadius + wallArea/cellCircumference

    //subject to change
    double viewDistance = 3*wallRadius;
    double reachDistance = 2*wallRadius;


    //Temporary behaviour parameters
    double reproduceWillingness;

    //stores both attack and evade willingness as only one can be chosen per turn.
    double combatWillingness;
    //stores whether this cell wants to attack or defend itself.
    boolean willingToAttack;

    //stores both move to food and search for food willingness as only one can be chosen per turn.
    double feedWillingness;

    //used while determining what is visible to the cell before moving,
    //also used while determining what is reachable by the cell before acting.
    //the general term used here for a cell within view distance or within reach is an "actionable" cell.
    ArrayList<Cell> actionableCells;
    ArrayList<Food> actionableFood;

    //which cell or food has been chosen to be moved towards or acted on if combat or feeding is pursued
    Cell chosenEnemy;
    Food chosenFood;

    ///////////////////////////////////////////////////////////////////////////////////////////////////////
    //Enemy temporary information on this cell, not information used by this cell.
    //an enemy cell uses this data to determine how it wants to act in the presence of it.
    double proximity;
    //how willing the enemy cell is to enter combat.
    double enemyCombatWillingness;
    //true: the will to attack this cell is stronger than the will to defend from it, and visa versa.
    boolean enemyWillingToAttack;
    ///////////////////////////////////////////////////////////////////////////////////////////////////////

    public Cell(double x, double y, SimComponent sim) {
        this.x = x;
        this.y = y;
        this.sim = sim;
    }

    public void healStep(){
        //Healing step goes here
    }

    public void drift(){

    }

    public void calculateReproduceWillingness(){
        //Reproduce willingness
        reproduceWillingness = reproduceTendency*Math.sqrt(Math.pow(cellMass/idealCellMass,2)+Math.pow(energy/idealEnergy,2)+Math.pow(wallMass/idealWallMass,2));

    }

    public void determineCombatWillingness(double threshold){
        //Determine actionable cells
        actionableCells = new ArrayList<>();
        for(Cell c : sim.cells){
            //if c within range of view distance, add c to this cell's actionableCells array
            double dx = Math.min(this.x-c.x ,  sim.maxX - (this.x-c.x));
            double dy = Math.min(this.y-c.y , sim.maxY -(this.y-c.y));

            c.proximity = Math.sqrt(Math.pow(dx,2)+Math.pow(dy,2));
            if(c.proximity<=threshold){
                //add it to list of cells this cell can see or reach (depending on how this method is being used).
                this.actionableCells.add(c);
            }
        }

        //Max of all combat willingness values for actionable cells
        double maxCombatWillingness = 0;
        Cell chosenCell = null;

        double tau;
        double colourDifference;

        //enemy cell c
        for(Cell c : actionableCells){
            //species discrimination by colour
            colourDifference = (Math.sqrt(Math.pow(this.R-c.R,2)+Math.pow(this.G-c.G,2)+Math.pow(this.B-c.B,2)));
            //how much longer this cell would last than the other cell if both are attacking each other at current rates.
            tau = (this.wallMass*this.cellMass)/(c.wallRadius*cellMass);
            if(tau>1){
                c.enemyCombatWillingness = c.attackTendency*tau*colourDifference/c.proximity;
                c.willingToAttack = true;
                if(c.enemyCombatWillingness > maxCombatWillingness){maxCombatWillingness = c.enemyCombatWillingness;}
                chosenCell = c;
            }else{
                c.enemyCombatWillingness = c.evadeTendency/tau*colourDifference/c.proximity;
                c.willingToAttack = false;
                if(c.enemyCombatWillingness > maxCombatWillingness){maxCombatWillingness = c.enemyCombatWillingness;}
                chosenCell = c;
            }
        }
        this.combatWillingness = maxCombatWillingness;
        this.chosenEnemy = chosenCell;
    }


    public void determineFeedingWillingness(double threshold){
        actionableFood = new ArrayList<>();
        for(Food f : sim.food){
            //if f within range, add to actionableFood array.
            double dx = Math.min(this.x-f.x ,  sim.maxX - (this.x-f.x));
            double dy = Math.min(this.y-f.y , sim.maxY -(this.y-f.y));

            f.proximity = Math.sqrt(Math.pow(dx,2)+Math.pow(dy,2));
            if(f.proximity<=threshold){
                //add it to list of cells this cell can see or reach.
                this.actionableFood.add(f);
            }
        }

        double hunger = (this.energy/this.idealEnergy)*(this.cellMass/this.idealCellMass)*moveToFoodTendency;
        double maxEatWillingness = 0;
        Food chosenFood = null;
        if(actionableFood.size()>0) {
            for (Food f : actionableFood) {
                f.eatWillingness = f.energy * hunger / f.proximity;
                if(f.eatWillingness>maxEatWillingness){
                    maxEatWillingness = f.eatWillingness;
                    chosenFood = f;
                }
            }
            this.chosenFood = chosenFood;
            this.feedWillingness = chosenFood.eatWillingness;
        }else{
            this.feedWillingness = Math.pow(hunger,2)*feedWillingness;
            this.chosenFood = null;
        }
    }

    public void moveTowardsChosenAction(){
        if(combatWillingness>feedWillingness){
            //Move to attack/evade
            if(willingToAttack){
                //move towards chosenCell

            }else{
                //move away from chosenCell

            }

        }else{
            //Move towards food, or to seek food

        }
    }

    //Helper function for moving cell towards/back from
    public void moveTowards(Cell c, double d){

    }
    public void moveTowards(Food f, double d){

    }

    public void act(){
        if(combatWillingness>feedWillingness){
            //Move to attack/evade
            if(willingToAttack){
                //Attack

            }else{
                //Evade

            }

        }else{
            //Move towards food if reachable
            if(actionableFood.size()>0){

            }else{
                //otherwise seek food
            }
        }
    }



    public void paint(Graphics2D g2d, boolean drawViewRange){
        g2d.setColor(new Color(R,G,B));
        g2d.fillOval((int)(x-cellRadius),(int)(y-cellRadius),(int)(2*cellRadius),(int)(2*cellRadius));
        g2d.setColor(new Color((int)(R*0.8),(int)(G*0.8),(int)(B*0.8)));
        g2d.fillOval((int)(x-wallRadius),(int)(y-wallRadius),(int)(2*wallRadius),(int)(2*wallRadius));

        if(drawViewRange){
            g2d.drawOval((int)(x-viewDistance),(int)(y-viewDistance),(int)(2*viewDistance),(int)(2*viewDistance));
        }

    }
}
