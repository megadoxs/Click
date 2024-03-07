import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws IOException {

        // add all buildings
        Building[] Buildings = new Building[35];
        Buildings[0] = new Building("Mine abandonnée", "miner", 0.1, 1250);
        Buildings[1] = new Building("Caverne aux gros cailloux", "miner", 0.18, 3500, Buildings[0]);
        Buildings[2] = new Building("Mine des nains", "miner", new String[]{"humanoide"}, 0.324, 6000, Buildings[1]);
        Buildings[3] = new Building("Jardin de mamie", "farmer", 0.584, 12500);
        Buildings[4] = new Building("Cabane de sorcière", "alchemist", 1.06, 27500, Buildings[3]);
        Buildings[5] = new Building("Ruche Bourdonnate", "alchemist", 1.89, 67500, Buildings[4]);
        Buildings[6] = new Building("Foret enneigé", "alchemist", new String[]{"gelé", "foret"}, 3.4, 125000);
        Buildings[7] = new Building("Banquise gelée", new String[]{"gelé", "eau"}, 6.12, 315000, Buildings[6]);
        Buildings[8] = new Building("Port de pirate", new String[]{"humanoide", "eau"}, 11, 650000);
        Buildings[9] = new Building("Ile volcanique", "farmer", new String[]{}, 20, 1574000, Buildings[8]);
        Buildings[10] = new Building("Dune caniculaire", "hunter", new String[]{}, 36, 3150000, Buildings[9]);
        Buildings[11] = new Building("Forêt mystique", "hunter", new String[]{"foret"}, 64, 6000000, Buildings[10]);
        Buildings[12] = new Building("Jungle luxuriante", "farmer", new String[]{"foret"}, 115.45, 11500000, Buildings[11]);
        Buildings[13] = new Building("Temple oublié", "hunter", new String[]{}, 208, 25750000,  Buildings[12]);
        Buildings[14] = new Building("Crypte Millénaire", "", new String[]{}, 375, 550000000,  Buildings[13]);
        Buildings[15] = new Building("Goufre sans fond", "", new String[]{}, 742, 950000000,  Buildings[14]);
        Buildings[16] = new Building("", "", new String[]{}, 0, 0);
        Buildings[17] = new Building("", "", new String[]{}, 0, 0);
        Buildings[18] = new Building("", "", new String[]{}, 0, 0);
        Buildings[19] = new Building("", "", new String[]{}, 0, 0);
        Buildings[20] = new Building("", "", new String[]{}, 0, 0);
        Buildings[21] = new Building("", "", new String[]{}, 0, 0);
        Buildings[22] = new Building("", "", new String[]{}, 0, 0);
        Buildings[23] = new Building("", "", new String[]{}, 0, 0);
        Buildings[24] = new Building("", "", new String[]{}, 0, 0);
        Buildings[25] = new Building("", "", new String[]{}, 0, 0);
        Buildings[26] = new Building("", "", new String[]{}, 0, 0);
        Buildings[27] = new Building("", "", new String[]{}, 0, 0);
        Buildings[28] = new Building("", "", new String[]{}, 0, 0);
        Buildings[29] = new Building("", "", new String[]{}, 0, 0);
        Buildings[30] = new Building("", "", new String[]{}, 0, 0);
        Buildings[31] = new Building("", "", new String[]{}, 0, 0);
        Buildings[32] = new Building("", "", new String[]{}, 0, 0);
        Buildings[33] = new Building("", "", new String[]{}, 0, 0);
        Buildings[34] = new Building("", "", new String[]{}, 0, 0);


        //makes the dir to store the data txt files
        Path path = Paths.get("./data");
        if (!Files.exists(path)) {
            try {
                Files.createDirectories(path);
            } catch (IOException ignored) {}
        }

        // check in the file for the values
        File file = new File("./data/buildings.txt");
        File file3 = new File("./data/global.txt");
        if (file.exists() && file3.exists()){
            Scanner inputFile = new Scanner(file);
            Scanner inputGlobal = new Scanner(file3);

            Building.setGlobalUpgradeLevel(Buildings, inputGlobal.nextInt());
            Building.setJobLevel(Buildings, "miner", inputGlobal.nextInt());
            Building.setJobLevel(Buildings, "farmer", inputGlobal.nextInt());
            Building.setJobLevel(Buildings, "hunter", inputGlobal.nextInt());
            Building.setJobLevel(Buildings, "alchemist", inputGlobal.nextInt());

            for (int i = 0; i < Buildings.length; i++){
                String[] line = inputFile.nextLine().split("\\|");
                Buildings[i].setLevel(Integer.parseInt(line[0]));
                Buildings[i].setJobUpgrade(Boolean.parseBoolean(line[1]));
                Buildings[i].setDoubleUpgrade(Boolean.parseBoolean(line[2]));
                Buildings[i].setTripleUpgrade(Boolean.parseBoolean(line[3]));
                Buildings[i].setSameBuildingUpgrade(Boolean.parseBoolean(line[4]));
                Buildings[i].setPreviousBuildingUpgrade(Boolean.parseBoolean(line[5]));
                Buildings[i].setGroupUpgrade(Boolean.parseBoolean(line[6]));

                if (line.length == 8){
                    String[] group = line[7].split(" ");
                    ArrayList<String> groups = new ArrayList<>(Arrays.asList(group));
                    Buildings[i].setGroupUpgrade50(groups);
                }
                Buildings[i].calculateUnitProduction();
            }

            inputFile.close();
            inputGlobal.close();
        }
        else{
            Building.setGlobalUpgradeLevel(Buildings, 0);
            Building.setJobLevel(Buildings, "miner", 0);
            Building.setJobLevel(Buildings, "farmer", 0);
            Building.setJobLevel(Buildings, "hunter", 0);
            Building.setJobLevel(Buildings, "alchemist", 0);
        }

        File file2 = new File("./data/upgrades.txt");
        ArrayList<Upgrade> upgrades = new ArrayList<>();
        if (file2.exists()){
            Scanner inputFile = new Scanner(file2);
            while (inputFile.hasNextLine()){
                String[] line;
                line = inputFile.nextLine().split("\\|");
                if (line[1].equals("50% group")){
                    upgrades.add(new Upgrade(line[0], line[1], line[2], Building.getGroup(Buildings, line[2], true), Integer.parseInt(line[3])));
                }
                else if (line[1].equals("10% group") || line[1].equals("Job")){
                    upgrades.add(new Upgrade(line[0], line[1], line[2], Building.getGroup(Buildings, line[2], false), Integer.parseInt(line[3])));
                }
                else if (line[1].equals("Global")){
                    upgrades.add(new Upgrade(line[0], line[1], line[2], Buildings, Integer.parseInt(line[3])));
                }
                else
                    upgrades.add(new Upgrade(line[0], line[1], Building.getBuildingByName(Buildings, line[2]).getBuilding(), Integer.parseInt(line[3])));
            }
            inputFile.close();
        }
        new GUI(Buildings, upgrades);
    }
}

