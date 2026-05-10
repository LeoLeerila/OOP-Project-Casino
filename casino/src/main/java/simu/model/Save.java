package simu.model;

import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

import simu.model.game.GameAbstract;
/**
 * Save is used to save simulation data to the Statistics.csv file
 */
public class Save {
        /**
         * Create a new Statistics.csv file with string "time;revenue;loans;NPCNum;NPC;gameNum;game\n" to initialize saving
         */
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
        /**
         * Append values to Statistics.csv file
         * @param time
         * @param revenue
         * @param loans
         * @param NPCNum
         * @param NPCs
         * @param gameNum
         * @param games
         */
        public static void saveSimulation(double time, double revenue, double loans, int NPCNum, ConcurrentLinkedQueue<NPC> NPCs, int gameNum, List<GameAbstract> games){
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
