import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Scanner;

public class Upgrade {
    private String name;
    private String type;
    private Building building;
    private Building[] group;
    private String groupName;

    private boolean multiple;
    private int price;

    public Upgrade(String name, String type, Building building, int price) {
        this.name = name;
        this.type = type;
        this.building = building;
        this.price = price;
        this.multiple = false;
    }

    public Upgrade(String name, String type, String groupName, Building[] group, int price) {
        this.name = name;
        this.type = type;
        this.groupName = groupName;
        this.group = group;
        this.price = price;
        this.multiple = true;
    }

    public Upgrade(Upgrade upgrade) {
        this.name = upgrade.getName();
        this.type = upgrade.getType();
        this.groupName = upgrade.getGroupName();
        this.group = upgrade.getGroup();
        this.building = upgrade.getBuilding();
        this.price = upgrade.getPrice();
        this.multiple = upgrade.isMultiple();
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getGroupName() {
        return groupName;
    }

    public boolean isMultiple() {
        return multiple;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public int getPrice() {
        return price;
    }

    public String getFormattedPrice(){
        DecimalFormat smallProd = new DecimalFormat("#0.00");
        DecimalFormat bigProd = new DecimalFormat("#,#00");
        if (this.price < 10)
            return smallProd.format(this.price);
        else
            return bigProd.format(this.price);
    }

    public Building getBuilding() {
        return building;
    }

    public String getTargetName() {
        if(this.multiple){
            return this.groupName;
        }else
            return this.building.getName();
    }


    public Building[] getGroup() {
        return group;
    }

    public void applyUpgrade(){
        switch (this.type){
            case "Job":
                this.building.setJobUpgrade(true);
                break;
            case "100%":
                this.building.setDoubleUpgrade(true);
                break;
            case "200%":
                this.building.setTripleUpgrade(true);
                break;
            case "1% per same building":
                this.building.setSameBuildingUpgrade(true);
                break;
            case "1% per previous building":
                this.building.setPreviousBuildingUpgrade(true);
                break;
            case "10% group":
                for (int i = 0; i < this.group.length; i++){
                    this.group[i].setGroupUpgrade(true);
                }
                break;
            case "50% group":
                for (int i = 0; i < this.group.length; i++){
                    this.group[i].addGroupUpgrade50(this.groupName);
                }
                break;
        }
    }

    public String getFormattedNewProd(Building[] buildings){
        DecimalFormat smallProd = new DecimalFormat("#0.00");
        DecimalFormat bigProd = new DecimalFormat("#,#00");
        double newProd = Building.getTotalProd(buildings) + getUpgradeBoost();
        if (newProd < 10)
            return smallProd.format(newProd);
        else
            return bigProd.format(newProd);
    }

    public String getFormattedUpgradeBoost(){
        DecimalFormat smallProd = new DecimalFormat("#0.00");
        DecimalFormat bigProd = new DecimalFormat("#,#00");
        double boost = getUpgradeBoost();
        if (boost < 10)
            return smallProd.format(boost);
        else
            return bigProd.format(boost);
    }

    public double getUpgradeBoost(){
        double total = 0;
        if(this.multiple){
            Building[] group = new Building[this.group.length];
            for (int i = 0; i < this.group.length; i++){
                group[i] = new Building(this.group[i]);
            }
            switch (this.type){
                case "10% group":
                    for (int i = 0; i < this.group.length; i++){
                        group[i].setGroupUpgrade(true);
                    }
                    break;
                case "50% group":
                    for (int i = 0; i < this.group.length; i++){
                        if(!group[i].hasUpgrade(this.type, this.groupName))
                            group[i].addGroupUpgrade50(this.groupName);
                    }
                    break;
            }
            for (int i = 0; i < group.length; i++){
                group[i].calculateUnitProduction();
                total += group[i].getProduction() - this.group[i].getProduction();
            }
        }else {
            Building building = new Building(this.building);
            switch (type){
                case "Job":
                    building.setJobUpgrade(true);
                    break;
                case "100%":
                    building.setDoubleUpgrade(true);
                    break;
                case "200%":
                    building.setTripleUpgrade(true);
                    break;
                case "1% per same building":
                    building.setSameBuildingUpgrade(true);
                    break;
                case "1% per previous building":
                    building.setPreviousBuildingUpgrade(true);
                    break;
            }
            building.calculateUnitProduction();
            total += building.getProduction() - this.building.getProduction();
        }
        return total;
    }

    public double getUpgradeValue(){
        return getUpgradeBoost()/this.price;
    }

    public static Building.bestValue getBestPurchase(ArrayList<Upgrade> upgrades){
        int index = 0;
        double value = 0;
        for (int i = 0; i < upgrades.size(); i++){
            if (upgrades.get(i).getUpgradeValue() > value){
                value = upgrades.get(i).getUpgradeValue();
                index = i;
            }
        }
        return new Building.bestValue(value, index);
    }

    public static Boolean upgradeExist(ArrayList<Upgrade> upgrades, String target, String type){
        for (int i = 0; i < upgrades.size(); i++){
            if(target.equals(upgrades.get(i).getTargetName()) && type.equals(upgrades.get(i).getType()))
                return true;
        }
        return false;
    }

    public boolean isBestPurchase(ArrayList<Upgrade> upgrades, Building[] buildings){
        double value = 0;
        for (int i = 0; i < upgrades.size(); i++){
            if (upgrades.get(i).getUpgradeValue() > value){
                value = upgrades.get(i).getUpgradeValue();
            }
        }
        for (int i = 0; i < buildings.length; i++){
            if (buildings[i].getUpgradeValue() > value){
                value = buildings[i].getUpgradeValue();
            }
        }
        if (this.getUpgradeValue() > value)
            return true;
        else
            return false;
    }

    public static void delUpgrade(ArrayList<Upgrade> upgrades, Upgrade upgrade){
        try {
            File iFile = new File("./data/upgrades.txt");
            Scanner inputFile = new Scanner(iFile);
            File OFile = new File("temp.txt");
            PrintWriter outputFile = new PrintWriter(OFile);

            while (inputFile.hasNextLine()) {
                String lineUpgrade = inputFile.nextLine();
                if(!lineUpgrade.equals(upgrade.getName()+ "|" + upgrade.getType() + "|" + upgrade.getTargetName() + "|" + upgrade.getPrice()))
                    outputFile.println(lineUpgrade);
            }

            upgrades.remove(upgrade);

            inputFile.close();
            outputFile.close();
            iFile.delete();
            OFile.renameTo(new File("./data/upgrades.txt"));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public static void sortUpgrades(ArrayList<Upgrade> upgrades){
        ArrayList<Upgrade> sort = new ArrayList<>();
        while(!upgrades.isEmpty()){
            int id = 0;
            double value = 0;
            for (int i = 0; i < upgrades.size(); i++){
                if (upgrades.get(i).getUpgradeValue() > value) {
                    value = upgrades.get(i).getUpgradeValue();
                    id = i;
                }
            }
            sort.add(upgrades.get(id));
            upgrades.remove(id);
        }
        upgrades.addAll(sort);
    }
}
