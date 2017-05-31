package game;

import java.util.ArrayList;

import game.entity.Pack;

public class TourManager {
	
	  // Holds our packages
    public static ArrayList<Pack> destinationPlaces = new ArrayList<Pack>();

    // Adds a destination package
    public static void addPack(Pack pack) {
    	destinationPlaces.add(pack);
    }
    
    // Get a package
    public static Pack getPack(int index){
        return (Pack)destinationPlaces.get(index);
    }
    
    // Get the number of destination packages
    public static int numberOfPacks(){
        return destinationPlaces.size();
    }

}
