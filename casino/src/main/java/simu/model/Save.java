package simu.model;

import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.util.ArrayList;

import simu.model.game.GameAbstract;

public class Save {
        public static void initialSaveSimulation(){
            try {
                FileWriter output = new FileWriter("Statistics.csv");
                output.write("time;revenue;loans;NPCNum;NPC;gameNum;game\n"); //column names
                output.close();
            } catch (FileNotFoundException e) {
                System.err.println(e);
                return;
            } catch (Exception e) {
                System.err.println(e);
                return;
            }
        }
        public static void saveSimulation(double time, double revenue, double loans, int NPCNum, ArrayList<NPC> NPCList, int gameNum, ArrayList<GameAbstract> gameList){
            ArrayList<NPC> NPCs = new ArrayList<>();
            NPCs.addAll(NPCList);
            ArrayList<GameAbstract> games = new ArrayList<>();
            games.addAll(gameList);
            String NPCString = "";
            String gameString = "";

            for (NPC NPC : NPCs) {
                NPCString += NPC;
            }

            for (GameAbstract game : games) {
                gameString += game;
            }

            try {
                FileWriter output = new FileWriter("Statistics.csv", true);
                output.append(time + ";" + revenue + ";" + loans + ";" + NPCNum + ";" + NPCString + ";" + gameNum + ";" + gameString + "\n");
                output.close();
            } catch (FileNotFoundException e) {
                System.err.println(e);
                return;
            } catch (Exception e) {
                System.err.println(e);
                return;
            }
        }
}
