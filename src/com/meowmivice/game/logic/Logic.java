package com.meowmivice.game.logic;

import com.apps.util.Console;
import com.apps.util.Prompter;
import com.meowmivice.game.GameScreen;
import com.meowmivice.game.cast.*;
import com.meowmivice.game.controller.Game;
import com.meowmivice.game.reader.Audio;
import com.meowmivice.game.reader.FileReader;
import com.meowmivice.game.reader.SaveAndLoad;
import org.json.simple.parser.ParseException;

import javax.swing.*;
import java.io.IOException;
import java.util.*;

public class Logic {
    static LocationsLoader locLoader; // loads from json

    static {
        try {
            locLoader = new LocationsLoader();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private static Map<String, Location> mapLocations = locLoader.load(); // creates a map of all the rooms
    private static Ascii art = new Ascii(); // used anytime ascii needs to be loaded
    private Player player = new Player(); // new player
    private static Prompter prompter; // prompts the player for input
    private static int count = 0; // counter to check failstate
    private static boolean checkCounter = false; // forces the player to have to check the item,clue before picking up

    private static Location currentSpot = mapLocations.get(Player.getInstance().getCurrentLocation()); // the current location of the player

    private static String plug = ""; // used to dynamically display what the user does

    private static CommandsLoader commandsLoader; // get the commands from the Loaderclass
    static {
        try {
            commandsLoader = new CommandsLoader();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    // Synonym List / prob can refactor the checks somewhere else
    public static List<String> go = commandsLoader.verbsObj().get("go");
    private static List<String> north = commandsLoader.directionsObj().get("north");
    private static List<String> east = commandsLoader.directionsObj().get("east");
    private static List<String> south = commandsLoader.directionsObj().get("south");
    private static List<String> west = commandsLoader.directionsObj().get("west");

    private static List<String> upstairs = commandsLoader.directionsObj().get("upstairs");
    private static List<String> downstairs = commandsLoader.directionsObj().get("downstairs");
    private static List<String> get = commandsLoader.verbsObj().get("get");
    private static List<String> help = commandsLoader.verbsObj().get("help");
    private static List<String> quit = commandsLoader.verbsObj().get("quit");
    private static List<String> solve = commandsLoader.verbsObj().get("solve");
    private static List<String> look = commandsLoader.verbsObj().get("look");
    private static List<String> talk = commandsLoader.verbsObj().get("talk");
    private static List<String> restart = commandsLoader.verbsObj().get("restart");
    private static List<String> map = commandsLoader.verbsObj().get("map");
    private static List<String> play = commandsLoader.verbsObj().get("play");
    private static List<String> stop = commandsLoader.verbsObj().get("stop");
    private static List<String> load = commandsLoader.verbsObj().get("load");
    private static List<String> save = commandsLoader.verbsObj().get("save");

    // CONSTRUCTOR
    public Logic(Prompter var1) throws Exception {
        prompter = var1;
        this.player = player; // unnecessary now - remove
    }

    public Logic() {

    }

    // BUSINESS LOGIC
    // basic logic
    public static void logic(List<String> textParser) throws Exception {
        if (textParser.size()>=2 && go.contains(textParser.get(0))) { // checks size and get(0) is in go synonym list
            String direction = getDirection(textParser); // takes the input and passes to function that finds the synonym
            go(textParser, direction);
        }
        else if (look.contains(textParser.get(0))) { // checks look synonym list
            look(textParser);
        } else if (talk.contains(textParser.get(0))) { // checks talk synonym list
            talk(textParser);
        } else if(solve.contains(textParser.get(0))) { // checks solve synonym list
            solve();
        } else if (quit.contains(textParser.get(0))) { // checks quit synonym list
            Game.quit();
        } else if(help.contains(textParser.get(0))){ // checks help synonym list
            Game.help();
        } else if(restart.contains(textParser.get(0))){ // checks restart synonym list
            Game.restart();
        } else if (map.contains(textParser.get(0))){ // checks map synonym list
            art.displayLocation(prompter, Player.getInstance());
            //TODO fix save and load
        } else if (save.contains(textParser.get(0))){ // checks save synonym list
            SaveAndLoad.save();
        } else if (load.contains(textParser.get(0))){ // checks load synonym list
            SaveAndLoad.load();
        }else if (play.contains(textParser.get(0))){ // checks play synonym list / Audio
            Audio.audio();
        } else if (stop.contains(textParser.get(0))){ // checks stop synonym list / Audio
            Audio.stopAudio();
        } else if(textParser.get(0).equals("suspects")){ // checks if get(0) is suspects
            showSuspects();
        } else if(textParser.get(0).equals("inventory")){ // checks if get(0) is inventory
            showInventory();
        } else if(get.contains(textParser.get(0)) && currentSpot.getItem() != null) {
            get(currentSpot.getItem());
        }
    }

    // go method
    private static void go(List<String> input, String direction) {
        // Allows the user to just go back to previous room
        if (input.get(1).equals("back")){
            String temp = Player.getInstance().getCurrentLocation(); // store the currentlocation to temp
            Player.getInstance().setCurrentLocation(Player.getInstance().getPreviousLocation()); // set currentlocation to prev
            Player.getInstance().setPreviousLocation(temp); // store temp into prev
            checkCounter = false; // prob can remove
        }
        // Move rooms
        else if (currentSpot.getDirections().containsKey(direction)) { // checks if the currentroom contains that direction
            Player.getInstance().setPreviousLocation(Player.getInstance().getCurrentLocation()); // set currentroom to prev
            Player.getInstance().setCurrentLocation(currentSpot.getDirections().get(direction)); //set currentrooom to new room
            checkCounter = false; // prob can remove
        }else {
            plug = "That is an invalid direction to go!";
        }
        currentSpot = mapLocations.get(Player.getInstance().getCurrentLocation()); // get the current spot
        checkCounter = false; // check counter is for the get method, just resets it when they move to a new room
    }

    // get directions / take that synonym and return the base word
    private static String getDirection(List<String> input) {
        String direction = "null";
        if(north.contains(input.get(1))) direction = "north";
        else if(east.contains(input.get(1))) direction = "east";
        else if(south.contains(input.get(1))) direction = "south";
        else if(west.contains(input.get(1))) direction = "west";
        else if(upstairs.contains(input.get(1))) direction = "upstairs";
        else if(downstairs.contains(input.get(1))) direction = "downstairs";
        return direction;
    }

    // get item
    private static void get(Item currentItem)throws Exception{
        Clue clue = currentItem.getClue();
        GameScreen.showPopUp(currentItem.getName() + " got!");
        Player.getInstance().getInventory().add(currentItem.getName()); // add to player inventory
        mapLocations.get(Player.getInstance().getCurrentLocation()).setItem(null); // remove from the map

        Player.getInstance().getClues().put(clue.getName(), // add to player clues
                "Name: " + clue.getName() +
                        "\nDescription: " + clue.getDescription() +
                        "\nObtained from: " + currentItem.getName() +
                        "\nFound in: " + Player.getInstance().getCurrentLocation());

        GameScreen.getInventoryTextArea().setText("Inventory: " + Player.getInstance().getInventory().toString());
    }

    // look
    private static void look(List<String> input) throws Exception {
        NPC currentNpc = currentSpot.getNpc(); // get currentNPC
        Item currentItem = currentSpot.getItem(); // get currentItem

        if (input.size() == 1){ // if they just pass look print out a different statement depending on the contents of the room
            if (currentNpc!=null && currentItem != null) { // if there is an npc and an item
                plug = currentNpc.getName() + " and a " + currentItem.getName() + " are at this location";
                GameScreen.showPopUp(plug);
//                Clickables.showItems(currentSpot);
            } else if (currentNpc!=null && currentItem==null){ // npc and no item
                plug = currentNpc.getName() + " is at this location.";
                GameScreen.showPopUp(plug);
            } else if(currentNpc==null && currentItem!=null){ // item and no npc
                plug = "There is a " + currentItem.getName() + " in this location.";
                GameScreen.showPopUp(plug);
            } else {
                plug = "There is nothing in this location to look at.";
                GameScreen.showPopUp(plug);
            }
        } else if (input.get(1).equals("item") && currentItem!=null){ // if user looks item
            get(currentItem);
        }
        else {
            plug = "Can't look there";
            GameScreen.showPopUp(plug);
        }

    }

    // status
    public static void showStatus() throws IOException {
        art.ascii(Player.getInstance().getCurrentLocation()); // call ascii to display room ascii of currentlocation
        System.out.println(plug); // print plug
        plug = "";// resets plug
        System.out.println("\033[1;36m===========================");
        System.out.println("You are in the " + Player.getInstance().getCurrentLocation()); // currentlocation
        System.out.println(currentSpot.getDescription());// location description
        System.out.println("Inventory:" +"\033[37m" + Player.getInstance().getInventory() + "\033[1;36m"); // player inventory
        System.out.println("Enter help to see a list of available commands");
        System.out.println("===========================");
        System.out.println("Directions you can go: " +"\033[37m" +
                showDirections(Player.getInstance().getCurrentLocation()) + "\033[0m"); // get directions
    }


    // show directions
    private static String showDirections(String currentLocation) {
        Map<String,String> directionsMap =  currentSpot.getDirections(); // create a new map
        return directionsMap.keySet().toString(); // can prob nix this method and display currentSpot.getDirections().toString() in the show status
    }

    // solve game mystery
    private static void solve() throws Exception {
        // So the player needs at least one clue to solve
        if(Player.getInstance().getInventory().size() == 0){
            GameScreen.showPopUp("You need at least 1 clue to be even close to solving this mystery!");
            return;
        }
        String culprit = JOptionPane.showInputDialog("Who is the culprit?").toLowerCase(Locale.ROOT); // choose a culprit
        //Set<String> evidence = new HashSet<>(getEvidence()); // user picks out the evidence to provide
        CulpritLoader culpLoader = new CulpritLoader(); // loader class for culprit / move to top
        Culprit reqCulprit = culpLoader.load(); // get the culprit / move to top

        // If you provided all the correct items and guessed the right suspect
        if(culprit.equals(reqCulprit.getName())){
            GameScreen.showPopUp("Congratulations you solved the mystery!");
            System.exit(0);
        }
        else {
            count++;
            if (count > 2) {
                GameScreen.showPopUp("You Lost. The culprit got away!");
                GameScreen.showPopUp("Game Over!");
                System.exit(0);
            }
            GameScreen.showPopUp("Sorry please collect more clues or try again.");
            solve();
        }
    }

    // talk to NPC
    private static void talk(List<String> input){
        NPC npc = currentSpot.getNpc();
        if (npc!=null && input.size()>=2 && input.get(1).equals("npc")) { //if there is an npc and input is "talk npc"
            // if not visited get 1st dialogue of the npc
            if (!npc.isVisited()) {
                plug = npc.getName() + ": " + npc.getDialogue();
                GameScreen.showPopUp(plug);
                addDialogue(npc.getName(), npc.getDialogue());
                npc.setVisited(true);
                return;
            }

            ArrayList<String> randDialogueList = npc.getRandDialogue(); // list from obj value
            int rand = new Random().nextInt(randDialogueList.size()); // make random int from size of list
            // TODO bug / only added rand diag
            String randDiag = randDialogueList.get(rand);
            plug = npc.getName() + ": " + randDiag;
            GameScreen.showPopUp(plug);
            addDialogue(npc.getName(),randDiag); // adds the dialogue to the suspectsList
        }
        else if (npc!=null){
            plug = "Talk to who?";
        }
        else {
            plug = "There is no one to talk to";
        }
    }

    // add dialogue / suspect
    private static void addDialogue(String name, String dialogue) {
        if(!Player.getInstance().getSuspects().containsKey(name)){ // if suspectlist doesn't contain the suspect
            Player.getInstance().getSuspects().put(name,dialogue); // add it
        }
    }

    // show current suspects
    private static void showSuspects(){
        if(Player.getInstance().getSuspects().size() == 0){
            plug = "No suspects at this time";
        }
        else{
            // user specifies suspect to review
            System.out.println("Who do you want to talk to?");
            System.out.println(Player.getInstance().getSuspects().keySet().toString());
            String input = prompter.prompt(">").trim();
            plug =  Player.getInstance().getSuspects().getOrDefault(input, "Don't know who that is");
        }
    }

    // show current inventory
    private static String showInventory() {
        String plug;
        if(Player.getInstance().getClues().size() == 0){
            plug = "Currently no clues collected";
        }
        else {
            // user specifies what clue to look at
            System.out.println("What clue would you like to review?");
            System.out.println(Player.getInstance().getClues().keySet());
            String input = prompter.prompt("> ").trim();
            plug = Player.getInstance().getClues().getOrDefault(input, "Invalid clue");
        }
        return plug;
    }
}