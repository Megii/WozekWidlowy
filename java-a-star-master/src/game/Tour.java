package game;

import java.util.ArrayList;
import java.util.Collections;

import game.entity.Pack;

public class Tour{

    // Holds our tour of packages
    private ArrayList<Pack> tour = new ArrayList<Pack>();
    // Cache
    private double fitness = 0;
    private int distance = 0;
    
    // Constructs a blank tour
    public Tour(){
        for (int i = 0; i < TourManager.numberOfPacks(); i++) {
            tour.add(null);
        }
    }
    
    public Tour(ArrayList tour){
        this.tour = tour;
    }

    // Creates a random individual
    public void generateIndividual() {
        // Loop through all our destination cities and add them to our tour
        for (int packIndex = 0; packIndex < TourManager.numberOfPacks(); packIndex++) {
          setPack(packIndex, TourManager.getPack(packIndex));
        }
        // Randomly reorder the tour
        Collections.shuffle(tour);
    }

    // Gets a city from the tour
    public Pack getPack(int tourPosition) {
        return (Pack)tour.get(tourPosition);
    }

    // Sets a city in a certain position within a tour
    public void setPack(int tourPosition, Pack pack) {
        tour.set(tourPosition, pack);
        // If the tours been altered we need to reset the fitness and distance
        fitness = 0;
        distance = 0;
    }
    
    // Gets the tours fitness
    public double getFitness() {
        if (fitness == 0) {
            fitness = 1/(double)getDistance();
        }
        return fitness;
    }
    
    // Gets the total distance of the tour
    public int getDistance(){
        if (distance == 0) {
            int tourDistance = 0;
            // Loop through our tour's packages
            for (int packIndex=0; packIndex < tourSize(); packIndex++) {
                // Get Package we're travelling from
                Pack fromPack = getPack(packIndex);
                // Package we're travelling to
                Pack destinationPack;
                // Check we're not on our tour's last package, if we are set our 
                // tour's final destination package to our starting package
                if(packIndex+1 < tourSize()){
                    destinationPack = getPack(packIndex+1);
                }
                else{
                    destinationPack = getPack(0);
                }
                // Get the distance between the two packages
                tourDistance += fromPack.distanceTo(destinationPack);
            }
            distance = tourDistance;
        }
        return distance;
    }

    // Get number of packages on our tour
    public int tourSize() {
        return tour.size();
    }
    
    // Check if the tour contains a package
    public boolean containsPack(Pack pack){
        return tour.contains(pack);
    }
    
    public void remove(int x){
    	 tour.remove(x);
    }
    
    @Override
    public String toString() {
        String geneString = "|";
        for (int i = 0; i < tourSize(); i++) {
            geneString += getPack(i)+"|";
        }
        return geneString;
    }
}
