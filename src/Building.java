import java.text.DecimalFormat;
import java.util.ArrayList;

public class Building {
    private final String name;
    private String[] groups;
    private String group;
    private int jobLevel;
    private int level;
    private boolean upgradable;
    private int globalUpgrade;
    private final double defaultProduction;
    private double unitProduction;
    private final double defaultPrice;
    private Building previousBuilding;
    private boolean jobUpgrade; //unknown;
    private boolean doubleUpgrade; //100%
    private boolean tripleUpgrade; //200%
    private boolean sameBuildingUpgrade; //1% per level
    private boolean previousBuildingUpgrade; //1% per previous level
    private boolean groupUpgrade; // 10% group
    private String groupUpgrade10;
    private ArrayList<String> groupUpgrade50 = new ArrayList<>(); // 50% groups upgrades
    public static double defaultProd = 0.1;
    private static final DecimalFormat smallProd = new DecimalFormat("#0.00");
    private static final DecimalFormat bigProd = new DecimalFormat("#,#00");

    public Building(String name, String group, String[] groups, double defaultProduction, double defaultPrice, Building previousBuilding) { // constructor with all upgrades
        this.name = name;
        this.group = group;
        this.groups = groups;
        this.defaultProduction = defaultProduction;
        this.defaultPrice = defaultPrice;
        this.previousBuilding = previousBuilding;
        this.unitProduction = defaultProduction;
        this.level = 0;
        this.upgradable = true;
    }

    public Building(String name, String[] groups, double defaultProduction, double defaultPrice, Building previousBuilding) { // constructor without job
        this.name = name;
        this.groups = groups;
        this.defaultProduction = defaultProduction;
        this.defaultPrice = defaultPrice;
        this.previousBuilding = previousBuilding;
        this.unitProduction = defaultProduction;
        this.level = 0;
        this.upgradable = true;
    }
    public Building(String name, String group, double defaultProduction, double defaultPrice, Building previousBuilding) { // constructor with all upgrades
        this.name = name;
        this.group = group;
        this.defaultProduction = defaultProduction;
        this.defaultPrice = defaultPrice;
        this.previousBuilding = previousBuilding;
        this.unitProduction = defaultProduction;
        this.level = 0;
        this.upgradable = true;
    }

    public Building(String name, String group, String[] groups, double defaultProduction, double defaultPrice) { // constructor without previous upgrade & with groups
        this.name = name;
        this.group = group;
        this.groups = groups;
        this.defaultProduction = defaultProduction;
        this.defaultPrice = defaultPrice;
        this.unitProduction = defaultProduction;
        this.level = 0;
        this.upgradable = true;
    }

    public Building(String name, String[] groups, double defaultProduction, double defaultPrice) { // constructor without previous upgrade & with groups
        this.name = name;
        this.groups = groups;
        this.defaultProduction = defaultProduction;
        this.defaultPrice = defaultPrice;
        this.unitProduction = defaultProduction;
        this.level = 0;
        this.upgradable = true;
    }

    public Building(String name, String group, double defaultProduction, double defaultPrice) { // constructor without previous upgrade & without groups
        this.name = name;
        this.group = group;
        this.defaultProduction = defaultProduction;
        this.defaultPrice = defaultPrice;
        this.unitProduction = defaultProduction;
        this.level = 0;
        this.upgradable = true;
    }

    public Building(String name, String group, boolean upgradable, double defaultProduction, double defaultPrice) { // constructor without previous upgrade & without groups
        this.name = name;
        this.group = group;
        this.defaultProduction = defaultProduction;
        this.defaultPrice = defaultPrice;
        this.unitProduction = defaultProduction;
        this.level = 0;
        this.upgradable = upgradable;
    }

    public Building(Building building){
        this.globalUpgrade = building.getGlobalUpgrade();
        this.jobLevel = building.getJobLevel();
        this.name = building.getName();
        this.groups = building.getGroups();
        this.level = building.getLevel();
        this.defaultProduction = building.getDefaultProduction();
        this.defaultPrice = building.getDefaultPrice();
        this.previousBuilding = building.getPreviousBuilding();
        this.jobUpgrade = building.isJobUpgrade();
        this.doubleUpgrade = building.isDoubleUpgrade();
        this.tripleUpgrade = building.isTripleUpgrade();
        this.sameBuildingUpgrade = building.isSameBuildingUpgrade();
        this.previousBuildingUpgrade = building.isPreviousBuildingUpgrade();
        this.groupUpgrade = building.isGroupUpgrade();
        this.groupUpgrade50 = new ArrayList<>();
        this.groupUpgrade50.addAll(building.getGroupUpgrade50());
        this.groupUpgrade10 = building.groupUpgrade10;
        this.upgradable = building.isUpgradable();
        calculateUnitProduction();
    }

    public boolean isUpgradable() {
        return upgradable;
    }

    public void setJobLevel(int level){
        this.jobLevel = level;
    }

    public int getJobLevel() {
        return jobLevel;
    }

    public int getGlobalUpgrade() {
        return globalUpgrade;
    }

    public void setGlobalUpgrade(int globalUpgrade) {
        this.globalUpgrade = globalUpgrade;
    }

    public String getName() {
        return name;
    }

    public String[] getGroups() {
        return groups;
    }

    public String getGroup() {
        return group;
    }

    public void addGroupUpgrade50(String group){
        groupUpgrade50.add(group);
    }

    public void removeGroupUpgrade50(String group){
        groupUpgrade50.remove(group);
    }

    public ArrayList<String> getGroupUpgrade50() {
        return groupUpgrade50;
    }

    public void setGroupUpgrade50(ArrayList<String> groupUpgrade50) {
        this.groupUpgrade50 = groupUpgrade50;
    }

    public boolean isJobUpgrade() {
        return jobUpgrade;
    }

    public void setJobUpgrade(boolean jobUpgrade) {
        this.jobUpgrade = jobUpgrade;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public double getDefaultProduction() {
        return defaultProduction;
    }

    public String getFormattedUnitProduction() {
        if (this.unitProduction < 10)
            return smallProd.format(this.unitProduction);
        else
            return bigProd.format(this.unitProduction);
    }

    public double getUnitProduction() {
            return this.unitProduction;
    }

    public double getDefaultPrice() {
        return defaultPrice;
    }

    public Building getPreviousBuilding() {
        return previousBuilding;
    }

    public boolean isDoubleUpgrade() {
        return doubleUpgrade;
    }

    public void setDoubleUpgrade(boolean doubleUpgrade) {
        this.doubleUpgrade = doubleUpgrade;
    }

    public boolean isTripleUpgrade() {
        return tripleUpgrade;
    }

    public void setTripleUpgrade(boolean tripleUpgrade) {
        this.tripleUpgrade = tripleUpgrade;
    }

    public boolean isSameBuildingUpgrade() {
        return sameBuildingUpgrade;
    }

    public void setSameBuildingUpgrade(boolean sameBuildingUpgrade) {
        this.sameBuildingUpgrade = sameBuildingUpgrade;
    }

    public boolean isPreviousBuildingUpgrade() {
        return previousBuildingUpgrade;
    }

    public void setPreviousBuildingUpgrade(boolean previousBuildingUpgrade) {
        this.previousBuildingUpgrade = previousBuildingUpgrade;
    }

    public boolean isGroupUpgrade() {
        return groupUpgrade;
    }

    public void setGroupUpgrade(boolean groupUpgrade) {
        this.groupUpgrade = groupUpgrade;
    }

    public void addLevel(){
        if(this.level < 99)
            this.level += 1;
    }

    public void delLevel(){
        if(this.level > 0)
            this.level -= 1;
    }

    public double getUpgradePrice(){
        return this.defaultPrice*Math.pow(1.1, (this.getLevel()));
    }

    public String getFormattedUpgradePrice(){
        if (this.defaultPrice*Math.pow(1.1, (this.getLevel() + 1)) < 10)
            return smallProd.format(this.defaultPrice*Math.pow(1.1, (this.getLevel())));
        else
            return bigProd.format(this.defaultPrice*Math.pow(1.1, (this.getLevel())));
    }

    public String getFormattedProduction(){
        if (this.unitProduction*this.level < 10)
            return smallProd.format(this.unitProduction*this.level);
        else
            return bigProd.format(this.unitProduction*this.level);
    }

    public double getProduction(){
        return this.unitProduction*this.level;
    }

    public void calculateUnitProduction(){
        double production = this.defaultProduction;
        if (this.jobUpgrade){
            production += this.defaultProduction*0.01*this.jobLevel;
        }
        if (this.globalUpgrade > 0){
            for (int i = 1; i <= this.globalUpgrade; i++){
                production += this.defaultProduction*0.1*i;
            }
        }
        if (this.doubleUpgrade) {
            production += this.defaultProduction;
        }
        if (this.tripleUpgrade) {
            production += this.defaultProduction;
        }
        if (this.previousBuildingUpgrade && this.previousBuilding != null) {
            production += this.defaultProduction * 0.01 * this.previousBuilding.getLevel();
        }
        if (this.sameBuildingUpgrade) {
            production += this.defaultProduction * 0.01 * this.level;
        }
        if (this.groupUpgrade) {
            production += this.defaultProduction * 0.1;
        }
        if (this.groupUpgrade50 != null){
            for(int i = 0; i < this.groupUpgrade50.size(); i++) {
                production += this.defaultProduction * 0.5;
            }
        }
        this.unitProduction = production;
    }

    public static String getFormattedTotalProd(Building[] buildings){
        double total = 0;
        for (int i = 0; i < buildings.length; i++){
            total += buildings[i].getProduction();
        }
        if (total < 10)
            return smallProd.format(total);
        else
            return bigProd.format(total);
    }

    public static double getTotalProd(Building[] buildings){
        double total = 0;
        for (int i = 0; i < buildings.length; i++){
            total += buildings[i].getProduction();
        }
        return total;
    }

    public double getUpgradeValue(Building[] buildings){
        return this.unitProduction/(getUpgradePrice()+this.getUpgradePrice()/Building.getTotalProd(buildings));
    }

    public static bestValue getBestPurchase(Building[] buildings){
        int index = 0;
        double value = 0;
        for (int i = 0; i < buildings.length; i++){
            if (buildings[i].getUpgradeValue(buildings) > value && (i == 0 || buildings[i-1].getLevel() > 0)){
                value = buildings[i].getUpgradeValue(buildings);
                index = i;
            }
        }
        return new bestValue(value, index);
    }

    public static String getFormattedUpgradeBoost(Building building){
        Building newBuilding = new Building(building);
        newBuilding.addLevel();
        newBuilding.calculateUnitProduction();
        if (newBuilding.getUnitProduction() < 10)
            return smallProd.format(newBuilding.getUnitProduction());
        else
            return bigProd.format(newBuilding.getUnitProduction());
    }

    public static double getUpgradeBoost(Building building){
        Building newBuilding = new Building(building);
        newBuilding.addLevel();
        newBuilding.calculateUnitProduction();
        return newBuilding.getUnitProduction();
    }

    public static String getFormattedNewProd(Building[] buildings, Building building){
        double New = getTotalProd(buildings) + getUpgradeBoost(building);
        if (New < 10)
            return smallProd.format(New);
        else
            return bigProd.format(New);
    }
    
    public static betterBuilding getBuildingByName(Building[] buildings, String name){
        Building building = null;
        int index = 0;
        for (int i = 0; i < buildings.length; i++){
            if(buildings[i].getName().equals(name)) {
                building = buildings[i];
                index = i;
            }
        }
        return new betterBuilding(building, index);
    }

    public static Building[] getGroup(Building[] buildings, String group, boolean group50){
        ArrayList<Building> groupBuildings = new ArrayList<>();
        if(group50)
            for (int i = 0; i < buildings.length; i++){
                if(buildings[i].getGroups() != null)
                    for (int j = 0; j < buildings[i].getGroups().length; j++){
                        if(buildings[i].getGroups()[j].equals(group)){
                            groupBuildings.add(buildings[i]);
                        }
                    }
            }
        else
            for (int i = 0; i < buildings.length; i++){
                if(buildings[i].getGroup() != null)
                    if(buildings[i].getGroup().equals(group))
                        groupBuildings.add(buildings[i]);
            }
        return groupBuildings.toArray(new Building[0]);
    }

    public boolean hasUpgrade(String upgrade){
        switch(upgrade){
            case "Job":
                return this.jobUpgrade;
            case "100%":
                if (this.upgradable)
                    return this.doubleUpgrade;
            case "200%":
                if (this.upgradable)
                    return this.tripleUpgrade;
            case "1% per same building":
                if (this.upgradable)
                    return this.sameBuildingUpgrade;
            case "1% per previous building":
                if (this.previousBuilding != null && this.upgradable)
                    return this.previousBuildingUpgrade;
                else
                    return true;
        }
        return true;
    }

    public boolean hasUpgrade(String upgrade, String group){
        switch(upgrade){
            case "Job":
                return this.jobUpgrade;
            case "10% group":
                return this.groupUpgrade;
            case "50% group":
                if(this.groupUpgrade50 != null){
                    for (int i = 0; i < this.groupUpgrade50.size(); i++){
                        if (this.groupUpgrade50.get(i).equals(group))
                            return true;
                    }
                }
                return false;
        }
        return true;
    }

    public static void setJobLevel(Building[] buildings, String job, int level){
        for (int i = 0; i < buildings.length; i++){
            if(buildings[i].getGroup() != null)
                if(buildings[i].getGroup().equals(job))
                    buildings[i].setJobLevel(level);
        }
    }

    public static void setGlobalUpgradeLevel(Building[] buildings, int level){
        for (int i = 0; i < buildings.length; i++){
            buildings[i].setGlobalUpgrade(level);
        }
    }

    public static void calculateAllUnitProduction(Building[] buildings){
        for (int i = 0; i < buildings.length; i++){
            buildings[i].calculateUnitProduction();
        }
    }

    public static double getDefaultProd(){
        defaultProd = defaultProd*1.8;
        return defaultProd;
    }

    public static class bestValue {
        private double value;
        private int index;

        public bestValue(double value, int index){
            this.value = value;
            this.index = index;
        }

        public double getValue() {
            return value;
        }

        public int getIndex() {
            return index;
        }
    }

    public static class betterBuilding {
        private Building building;
        private int index;

        public betterBuilding(Building building, int index){
            this.building = building;
            this.index = index;
        }

        public Building getBuilding() {
            return building;
        }

        public int getIndex() {
            return index;
        }
    }
}
