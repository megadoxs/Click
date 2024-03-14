import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Scanner;

public class GUI extends JFrame{
    private final Building[] buildings;
    private final String[] groups10 = new String[]{"miner", "farmer", "hunter", "alchemist"};
    private final String[] groups50 = new String[]{"humanoide", "gelé", "eau", "foret"};
    private ArrayList<Upgrade> upgrades;

    // utility
    private static final Insets noMargin = new Insets(0,0,0,0);
    private static final Insets margin5 = new Insets(5,5,5,5);
    private static final Insets margin30L = new Insets(5,30,5,5);
    private final Font big = new Font("Dialog", Font.PLAIN, 20 );


    private JTextField[] levels;
    private JTextField[] prod;
    private JTextField[] unitprod;
    private JLabel[] statsValue;

    // event listener

    private final ActionListener addUpgradeListener = new ActionListener() {
        public void actionPerformed(ActionEvent evt) {
            if (addUpgradeGUI == null) {
                addUpgradeGUI = new AddUpgradeGUI();
            } else {
                addUpgradeGUI.setExtendedState(NORMAL);
                addUpgradeGUI.toFront();
            }
        }
    };

    private final ActionListener upgradeListListener = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (listUpgradeGUI == null) {
                listUpgradeGUI = new listUpgradeGUI();
            } else {
                listUpgradeGUI.setExtendedState(NORMAL);
                listUpgradeGUI.toFront();
            }
        }
    };

    private final ActionListener buildingsUpgradesListener = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (buildingsUpgradesGUI == null) {
                buildingsUpgradesGUI = new BuildingsUpgradeGUI();
            } else {
                buildingsUpgradesGUI.setExtendedState(NORMAL);
                buildingsUpgradesGUI.toFront();
            }
        }
    };

    // common items
    private final SharedButton addUpgrade = new SharedButton("Add Upgrade", noMargin, addUpgradeListener);
    private final SharedButton upgradeList = new SharedButton("Upgrade List", noMargin, upgradeListListener);
    private final SharedButton buildingsUpgrades = new SharedButton("Configuration", noMargin, buildingsUpgradesListener);

    private final SharedLabel options = new SharedLabel("Options", big);

    // other GUI
    private static AddUpgradeGUI addUpgradeGUI;
    private static listUpgradeGUI listUpgradeGUI;
    private static BuildingsUpgradeGUI buildingsUpgradesGUI;

    public GUI(Building[] buildings, ArrayList<Upgrade> upgrades){
        this.buildings = buildings;
        this.upgrades = upgrades;
        setGUI();
    }

    public void setGUI(){
        setTitle("Clicker");
        setLayout(new GridBagLayout());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        addClosingListener();

        //layouts
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(2, 5, 2, 5);

        //header
        JLabel[] header = new JLabel[4];
        header[0] = new JLabel("Buildings");
        header[1] = new JLabel("Levels");
        header[2] = new JLabel("Unit Prod/s");
        header[3] = new JLabel("Building Prod/s");
        header[1].setHorizontalAlignment(JTextField.CENTER);
        for (int j = 0; j < header.length; j++){
            header[j].setFont(big);
            gbc.gridy = 0;
            if (j > 2)
                gbc.gridx = (j+2);
            if (j == 1)
                gbc.gridwidth = 3;
            else gbc.gridwidth = 1;
            add(header[j], gbc);
        }
        gbc.gridwidth = 1;

        //body
        buildingAdd addListener = new buildingAdd();
        buildingDel delListener = new buildingDel();
        BetterButton[] addButtons = new BetterButton[35];
        BetterButton[] delButtons = new BetterButton[35];
        levels = new JTextField[35];
        prod = new JTextField[35];
        unitprod = new JTextField[35];
        JLabel[] buildingNames = new JLabel[35];
        for (int i = 0; i < addButtons.length; i++){
            //prod
            prod[i] = new JTextField(this.buildings[i].getFormattedProduction());
            prod[i].setEditable(false);
            prod[i].setPreferredSize(new Dimension(40,20));
            prod[i].setHorizontalAlignment(JTextField.CENTER);

            //unit prod
            unitprod[i] = new JTextField(this.buildings[i].getFormattedUnitProduction());
            unitprod[i].setEditable(false);
            unitprod[i].setPreferredSize(new Dimension(40,20));
            unitprod[i].setHorizontalAlignment(JTextField.CENTER);

            //levels
            levels[i] = new JTextField(this.buildings[i].getLevel() + "");
            levels[i].setEditable(false);
            levels[i].setPreferredSize(new Dimension(40,20));
            levels[i].setHorizontalAlignment(JTextField.CENTER);

            //button
            addButtons[i] = new BetterButton("+1", i);
            delButtons[i] = new BetterButton("-1", i);
            addButtons[i].setMargin(noMargin);
            delButtons[i].setMargin(noMargin);
            addButtons[i].setPreferredSize(new Dimension(20, 20));
            delButtons[i].setPreferredSize(new Dimension(20, 20));

            //buildings
            buildingNames[i] = new JLabel(this.buildings[i].getName());
            gbc.gridy = (i+1);
            Object[] body = new Object[]{buildingNames, levels, addButtons, delButtons, unitprod, prod};

            //add
            for (int j = 0; j < body.length; j++){
                if (j == 1)
                    gbc.insets = new Insets(2, 5, 2, 0);
                else if (j == 2)
                    gbc.insets = new Insets(2, 0, 2, 0);
                else if (j == 3)
                    gbc.insets = new Insets(2, 0, 2, 5);
                else
                    gbc.insets = new Insets(2, 5, 2, 5);
                gbc.gridx = j;
                add(((Component[]) body[j])[i], gbc);
            }
            addButtons[i].addActionListener(addListener);
            delButtons[i].addActionListener(delListener);
        }

        //stats
        JLabel stats = new JLabel("Stats");
        stats.setFont(big);
        gbc.insets = new Insets(2, 30, 2, 5);
        gbc.gridx = 6;
        gbc.gridy = 0;
        add(stats, gbc);
        JLabel[] statsName = new JLabel[5];
        statsName[0] = new JLabel("Current Total Production:");
        statsName[1] = new JLabel("Best Purchase:");
        statsName[2] = new JLabel("Purchase Price:");
        statsName[3] = new JLabel("Production Boost:");
        statsName[4] = new JLabel("New Total Production:");
        for (int i = 0; i < statsName.length; i++){
            gbc.gridy = (i+1);
            add(statsName[i], gbc);
        }
        gbc.insets = new Insets(2, 5, 2, 5);
        gbc.gridx = 7;
        statsValue = new JLabel[5];
        statsValue[0] = new JLabel(Building.getFormattedTotalProd(buildings));
        statsValue[1] = new JLabel(getBestPurchase()[0]);
        statsValue[2] = new JLabel(getBestPurchase()[4]);
        statsValue[3] = new JLabel(getBestPurchase()[2]);
        statsValue[4] = new JLabel(getBestPurchase()[3]);
        for (int i = 0; i < statsValue.length; i++){
            gbc.gridy = (i+1);
            add(statsValue[i], gbc);
        }

        //options
        gbc.insets = new Insets(2, 30, 2, 5);
        gbc.gridx = 6;
        gbc.gridy = 6;
        add(options.clone(), gbc);

        gbc.gridy++;
        purchaseRecommended purchaseListener = new purchaseRecommended();
        BetterButton purchaseRecommended = new BetterButton("Purchase Recommended");
        purchaseRecommended.setMargin(noMargin);
        purchaseRecommended.addActionListener(purchaseListener);
        add(purchaseRecommended, gbc);

        gbc.gridy++;
        add(GUI.this.addUpgrade.clone(), gbc);

        gbc.gridy++;
        add(GUI.this.upgradeList.clone(), gbc);

        gbc.gridy++;
        add(GUI.this.buildingsUpgrades.clone(), gbc);

        gbc.gridy++;
        JButton reset = new JButton("Reset Data");
        reset.setMargin(noMargin);
        add(reset, gbc);

        //pack
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private class BetterButton extends JButton{
        private int id;

        public BetterButton(String label, int id) throws HeadlessException {
            super(label);
            this.id = id;
        }
        public BetterButton(String label) throws HeadlessException {
            super(label);
        }

        public int getId() {
            return id;
        }

        public void setId(int id){
            this.id = id;
        }
    }

    private void updateAllBuildings(){
        for (int i = 0; i < buildings.length; i++){
            levels[i].setText(Integer.toString(buildings[i].getLevel()));
            unitprod[i].setText(buildings[i].getFormattedUnitProduction());
            prod[i].setText(buildings[i].getFormattedProduction());
        }
        updateStats();
        pack();
    }

    private void updateBuildings(ActionEvent evt){

        //updates target building
        GUI.this.levels[((BetterButton) evt.getSource()).getId()].setText(GUI.this.buildings[((BetterButton) evt.getSource()).getId()].getLevel() + "");
        GUI.this.buildings[((BetterButton) evt.getSource()).getId()].calculateUnitProduction();
        GUI.this.unitprod[((BetterButton) evt.getSource()).getId()].setText(GUI.this.buildings[((BetterButton) evt.getSource()).getId()].getFormattedUnitProduction());
        GUI.this.prod[((BetterButton) evt.getSource()).getId()].setText(GUI.this.buildings[((BetterButton) evt.getSource()).getId()].getFormattedProduction());

        //updates next building
        if (((BetterButton) evt.getSource()).getId() < GUI.this.buildings.length-1){
            GUI.this.buildings[((BetterButton) evt.getSource()).getId()+1].calculateUnitProduction();
            GUI.this.unitprod[((BetterButton) evt.getSource()).getId()+1].setText(GUI.this.buildings[((BetterButton) evt.getSource()).getId()+1].getFormattedUnitProduction());
            GUI.this.prod[((BetterButton) evt.getSource()).getId()+1].setText(GUI.this.buildings[((BetterButton) evt.getSource()).getId()+1].getFormattedProduction());
        }

        updateStats();
        GUI.this.pack();
    }

    private void updateStats(){
        GUI.this.statsValue[0].setText(Building.getFormattedTotalProd(buildings));
        GUI.this.statsValue[1].setText(getBestPurchase()[0]);
        GUI.this.statsValue[2].setText(getBestPurchase()[4]);
        GUI.this.statsValue[3].setText(getBestPurchase()[2]);
        GUI.this.statsValue[4].setText(getBestPurchase()[3]);
        GUI.this.pack();
    }

    private String[] getBestPurchase(){
        String[] bestPurchase = new String[5];
        if (Building.getBestPurchase(GUI.this.buildings).getValue() > Upgrade.getBestPurchase(GUI.this.upgrades, Building.getTotalProd(buildings)).getValue()){
            bestPurchase[0] = GUI.this.buildings[Building.getBestPurchase(GUI.this.buildings).getIndex()].getName();
            bestPurchase[1] = "Building";
            bestPurchase[2] = Building.getFormattedUpgradeBoost(GUI.this.buildings[Building.getBestPurchase(GUI.this.buildings).getIndex()]);
            bestPurchase[3] = Building.getFormattedNewProd(GUI.this.buildings, GUI.this.buildings[Building.getBestPurchase(GUI.this.buildings).getIndex()]);
            bestPurchase[4] = GUI.this.buildings[Building.getBestPurchase(GUI.this.buildings).getIndex()].getFormattedUpgradePrice();
        }else {
            bestPurchase[0] = GUI.this.upgrades.get(Upgrade.getBestPurchase(GUI.this.upgrades, Building.getTotalProd(buildings)).getIndex()).getName();
            bestPurchase[1] = "Upgrade";
            bestPurchase[2] = GUI.this.upgrades.get(Upgrade.getBestPurchase(GUI.this.upgrades, Building.getTotalProd(buildings)).getIndex()).getFormattedUpgradeBoost();
            bestPurchase[3] = GUI.this.upgrades.get(Upgrade.getBestPurchase(GUI.this.upgrades, Building.getTotalProd(buildings)).getIndex()).getFormattedNewProd(GUI.this.buildings);
            bestPurchase[4] = GUI.this.upgrades.get(Upgrade.getBestPurchase(GUI.this.upgrades, Building.getTotalProd(buildings)).getIndex()).getFormattedPrice();
        }
        return bestPurchase;
    }

    private class buildingDel implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent evt) {
            GUI.this.buildings[((BetterButton) evt.getSource()).getId()].delLevel();
            updateBuildings(evt);
            updateStats();
        }
    }
    private class buildingAdd implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent evt) {
            GUI.this.buildings[((BetterButton) evt.getSource()).getId()].addLevel();
            updateBuildings(evt);
            updateStats();
        }
    }

    private class purchaseRecommended implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent evt) {
            if (getBestPurchase()[1].equals("Building")){
                ((BetterButton) evt.getSource()).setId(Building.getBestPurchase(GUI.this.buildings).getIndex());
                GUI.this.buildings[Building.getBestPurchase(GUI.this.buildings).getIndex()].addLevel();
                updateBuildings(evt);
            }
            else{
                int id = Upgrade.getBestPurchase(upgrades, Building.getTotalProd(buildings)).getIndex();
                upgrades.get(id).applyUpgrade();
                if(upgrades.get(id).getGroup() != null){
                    for (int i = 0; i < upgrades.get(id).getGroup().length; i++){
                        ((BetterButton) evt.getSource()).setId(Building.getBuildingByName(buildings, upgrades.get(id).getGroup()[i].getName()).getIndex());
                        updateBuildings(evt);
                    }
                }
                else{
                    ((BetterButton) evt.getSource()).setId(Building.getBuildingByName(buildings, upgrades.get(id).getBuilding().getName()).getIndex());
                    updateBuildings(evt);
                }
                Upgrade.delUpgrade(upgrades, upgrades.get(id));
                updateStats();
                if (GUI.listUpgradeGUI != null)
                    listUpgradeGUI.updateUpgrades();
            }
            if (addUpgradeGUI != null)
                addUpgradeGUI.updateStats();
        }
    }
    private void addClosingListener(){
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                try {
                    PrintWriter outputFile = new PrintWriter("./data/buildings.txt");
                    for (int i = 0; i < GUI.this.buildings.length; i++){
                        outputFile.print(GUI.this.buildings[i].getLevel() + "|");
                        outputFile.print(GUI.this.buildings[i].isJobUpgrade() + "|");
                        outputFile.print(GUI.this.buildings[i].isDoubleUpgrade() + "|");
                        outputFile.print(GUI.this.buildings[i].isTripleUpgrade() + "|");
                        outputFile.print(GUI.this.buildings[i].isSameBuildingUpgrade() + "|");
                        outputFile.print(GUI.this.buildings[i].isPreviousBuildingUpgrade() + "|");
                        outputFile.print(GUI.this.buildings[i].isGroupUpgrade() + "|");
                        if(GUI.this.buildings[i].getGroupUpgrade50() != null){
                            for (int j = 0; j < GUI.this.buildings[i].getGroupUpgrade50().size(); j++)
                                outputFile.print(GUI.this.buildings[i].getGroupUpgrade50().get(j) + " ");
                        }
                        outputFile.println();
                    }
                    outputFile.close();
                } catch (FileNotFoundException ex) {
                    throw new RuntimeException(ex);
                }
                try {
                    PrintWriter outputFile2 = new PrintWriter("./data/global.txt");
                    outputFile2.println(buildings[0].getGlobalUpgrade());
                    outputFile2.println(Building.getGroup(buildings, "miner", false)[0].getJobLevel());
                    outputFile2.println(Building.getGroup(buildings, "farmer", false)[0].getJobLevel());
                    outputFile2.println(Building.getGroup(buildings, "hunter", false)[0].getJobLevel());
                    outputFile2.print(Building.getGroup(buildings, "alchemist", false)[0].getJobLevel());
                    outputFile2.close();
                } catch (FileNotFoundException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });
    }

    private class AddUpgradeGUI extends JFrame{

        JTextField enterName;
        JComboBox<String> selectType;
        JComboBox<String> selectTarget;
        NumericTextField enterPrice;
        JLabel prodBoostValue = new JLabel();
        JLabel newTotalProd = new JLabel();
        JLabel isBestPurchase = new JLabel();
        public AddUpgradeGUI() throws HeadlessException {
            //layout
            setTitle("Add Upgrade");
            setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            addClosingListener();
            setLayout(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.insets = margin5;

            //page body
            gbc.gridx = 0;
            gbc.gridy = 0;
            gbc.gridwidth = 2;
            JLabel addUpgrade = new JLabel("New Upgrade");
            addUpgrade.setFont(big);
            add(addUpgrade, gbc);
            gbc.gridwidth = 1;

            gbc.gridy++;
            JLabel name = new JLabel("Name:");
            add(name, gbc);

            gbc.gridy++;
            JLabel type = new JLabel("Type:");
            add(type, gbc);

            gbc.gridy++;
            JLabel target = new JLabel("Target:");
            add(target, gbc);

            gbc.gridy++;
            JLabel price = new JLabel("Price:");
            add(price, gbc);

            gbc.gridx = 1;
            gbc.gridy = 1;
            enterName = new JTextField();
            add(enterName, gbc);

            gbc.gridy++;
            selectType = new JComboBox<>();
            selectType.addItem("Job");
            selectType.addItem("100%");
            selectType.addItem("200%");
            selectType.addItem("1% per same building");
            selectType.addItem("1% per previous building");
            selectType.addItem("10% group");
            selectType.addItem("50% group");
            selectType.addItem("Global");
            selectType.setSelectedItem(null);
            add(selectType, gbc);


            gbc.gridy++;
            selectTarget = new JComboBox<>();
            selectTarget.setEnabled(false);
            add(selectTarget, gbc);

            gbc.gridy++;
            enterPrice = new NumericTextField("addUpgrade", Integer.MAX_VALUE);
            add(enterPrice, gbc);

            gbc.gridy++;
            gbc.gridx = 0;
            gbc.gridwidth = 2;
            JButton createUpgrade = new JButton("add Upgrade");
            createUpgrade.setHorizontalAlignment(JTextField.CENTER);
            add(createUpgrade, gbc);

            //stats
            gbc.insets = margin30L;
            gbc.gridy = 0;
            gbc.gridx = 2;
            JLabel stats = new JLabel("Stats");
            stats.setFont(big);
            add(stats, gbc);
            gbc.gridwidth = 1;

            gbc.gridy++;
            JLabel totalProd = new JLabel("Current Total Production:");
            add(totalProd, gbc);

            gbc.gridy++;
            JLabel prodBoost = new JLabel("Production Boost:");
            add(prodBoost, gbc);

            gbc.gridy++;
            JLabel prodTotal = new JLabel("New Total Production:");
            add(prodTotal, gbc);

            gbc.gridy++;
            JLabel isBestPurchase = new JLabel("Best Purchase?");
            add(isBestPurchase, gbc);


            gbc.insets = margin5;
            gbc.gridy = 1;
            gbc.gridx = 3;
            JLabel totalProdValue = new JLabel(Building.getFormattedTotalProd(GUI.this.buildings));
            add(totalProdValue, gbc);

            gbc.insets = margin30L;
            gbc.gridx = 4;
            gbc.gridy = 0;
            add(options.clone(), gbc);

            gbc.gridy++;
            add(GUI.this.upgradeList.clone(), gbc);

            gbc.gridy++;
            add(GUI.this.buildingsUpgrades.clone(), gbc);

            //listeners

            class updateUpgrade implements ActionListener {
                @Override
                public void actionPerformed(ActionEvent evt) {
                    updateStats();
                }
            }

            updateUpgrade updateUpgradeListener = new updateUpgrade();
            selectType.addActionListener(updateUpgradeListener);

            selectType.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    selectTarget.removeActionListener(updateUpgradeListener);
                    selectTarget.removeAllItems();
                    if (selectType.getSelectedItem() != null && selectType.getSelectedItem().toString().equals("10% group")){
                        for (int i = 0; i < GUI.this.groups10.length; i++){
                            if(!Building.getGroup(buildings, GUI.this.groups10[i], false)[0].hasUpgrade("10% group", GUI.this.groups10[i]) && !Upgrade.upgradeExist(GUI.this.upgrades, GUI.this.groups10[i], selectType.getSelectedItem().toString()))
                                selectTarget.addItem(GUI.this.groups10[i]);
                        }
                        selectTarget.setSelectedItem(null);
                    }
                    else if (selectType.getSelectedItem() != null && selectType.getSelectedItem().toString().equals("50% group")){
                        for (int i = 0; i < GUI.this.groups50.length; i++){
                            if(!Building.getGroup(buildings, GUI.this.groups50[i], true)[0].hasUpgrade("50% group", GUI.this.groups50[i]) && !Upgrade.upgradeExist(GUI.this.upgrades, GUI.this.groups50[i], selectType.getSelectedItem().toString()))
                                selectTarget.addItem(GUI.this.groups50[i]);
                        }
                        selectTarget.setSelectedItem(null);
                    }
                    else if (selectType.getSelectedItem() != null && selectType.getSelectedItem().toString().equals("200%")){
                        for (int i = 0; i < GUI.this.buildings.length; i++){
                            if(GUI.this.buildings[i].getLevel() > 0 && !GUI.this.buildings[i].hasUpgrade("200%") && GUI.this.buildings[i].hasUpgrade("100%") && !Upgrade.upgradeExist(GUI.this.upgrades, buildings[i].getName(), selectType.getSelectedItem().toString()))
                                selectTarget.addItem(GUI.this.buildings[i].getName());
                        }
                        selectTarget.setSelectedItem(null);
                    }
                    else if (selectType.getSelectedItem() != null && selectType.getSelectedItem().toString().equals("Job")){
                        for (int i = 0; i < GUI.this.groups10.length; i++){
                            if(!Building.getGroup(buildings, GUI.this.groups10[i], false)[0].hasUpgrade("Job", GUI.this.groups10[i]) && !Upgrade.upgradeExist(GUI.this.upgrades, GUI.this.groups10[i], selectType.getSelectedItem().toString()))
                                selectTarget.addItem(GUI.this.groups10[i]);
                        }
                        selectTarget.setSelectedItem(null);
                    }
                    else if (selectType.getSelectedItem() != null && selectType.getSelectedItem().toString().equals("Global") && buildings[0].getGlobalUpgrade() <= 10 && !Upgrade.upgradeExist(GUI.this.upgrades, ("Global " + (buildings[0].getGlobalUpgrade()+1) + "0%"), selectType.getSelectedItem().toString())){
                        selectTarget.addItem("Global " + (buildings[0].getGlobalUpgrade()+1) + "0%");
                        selectTarget.setSelectedItem(null);
                    }
                    else if (selectType.getSelectedItem() != null) {
                        for (int i = 0; i < GUI.this.buildings.length; i++){
                            if(GUI.this.buildings[i].getLevel() > 0 && !GUI.this.buildings[i].hasUpgrade(selectType.getSelectedItem().toString()) && !Upgrade.upgradeExist(GUI.this.upgrades, buildings[i].getName(), selectType.getSelectedItem().toString()))
                                selectTarget.addItem(GUI.this.buildings[i].getName());
                        }
                        selectTarget.setSelectedItem(null);
                    }
                    if(selectTarget.getModel().getSize() > 1)
                        selectTarget.setEnabled(true);
                    else {
                        selectTarget.setSelectedItem(selectTarget.getModel().getElementAt(0));
                        selectTarget.setEnabled(false);
                    }
                    selectTarget.addActionListener(updateUpgradeListener);
                }
            });

            createUpgrade.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    if(!enterName.getText().trim().isEmpty() && selectType.getSelectedItem() != null && selectTarget.getSelectedItem() != null && !enterPrice.getText().trim().isEmpty()){
                        try {
                            PrintWriter outputFile = new PrintWriter(new FileWriter("./data/upgrades.txt", true));
                            Scanner inputFile = new Scanner(new File("./data/upgrades.txt"));
                            if(inputFile.hasNext())
                                outputFile.println();
                            outputFile.print(enterName.getText() + "|");
                            outputFile.print(selectType.getSelectedItem().toString() + "|");
                            if(selectType.getSelectedItem().toString().equals("50% group")){
                                GUI.this.upgrades.add(new Upgrade(enterName.getText(), selectType.getSelectedItem().toString(), selectTarget.getSelectedItem().toString(), Building.getGroup(GUI.this.buildings, selectTarget.getSelectedItem().toString(), true), Integer.parseInt(enterPrice.getText().replaceAll("[^0-9]", ""))));
                                outputFile.print(selectTarget.getSelectedItem().toString() + "|");
                            }
                            else if(selectType.getSelectedItem().toString().equals("10% group") || selectType.getSelectedItem().toString().equals("Job")){
                                GUI.this.upgrades.add(new Upgrade(enterName.getText(), selectType.getSelectedItem().toString(), selectTarget.getSelectedItem().toString(), Building.getGroup(GUI.this.buildings, selectTarget.getSelectedItem().toString(), false), Integer.parseInt(enterPrice.getText().replaceAll("[^0-9]", ""))));
                                outputFile.print(selectTarget.getSelectedItem().toString() + "|");
                            }
                            else if(selectType.getSelectedItem().toString().equals("Global")){
                                GUI.this.upgrades.add(new Upgrade(enterName.getText(), selectType.getSelectedItem().toString(), selectTarget.getSelectedItem().toString(), buildings, Integer.parseInt(enterPrice.getText().replaceAll("[^0-9]", ""))));
                                outputFile.print(selectTarget.getSelectedItem().toString() + "|");
                            }
                            else {
                                GUI.this.upgrades.add(new Upgrade(enterName.getText(), selectType.getSelectedItem().toString(), Building.getBuildingByName(GUI.this.buildings, selectTarget.getSelectedItem().toString()).getBuilding(), Integer.parseInt(enterPrice.getText().replaceAll("[^0-9]", ""))));
                                outputFile.print(selectTarget.getSelectedItem().toString() + "|");
                            }
                            outputFile.print(Integer.parseInt(enterPrice.getText().replaceAll("[^0-9]", "")));
                            inputFile.close();
                            outputFile.close();
                        } catch (IOException ex) {
                            throw new RuntimeException(ex);
                        }
                        enterName.setText("");
                        enterPrice.setText("");
                        selectType.setSelectedItem(null);
                        GUI.this.updateStats();
                        if (GUI.listUpgradeGUI != null)
                            listUpgradeGUI.updateUpgrades();
                    }
                }
            });

            //pack
            pack();
            setLocationRelativeTo(null);
            setVisible(true);
        }

        public void updateStats(){
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.insets = margin5;
            gbc.gridy = 2;
            gbc.gridx = 3;
            // upgrade boost & New prod
            if(selectType.getSelectedItem() != null && selectTarget.getSelectedItem() != null){
                if(selectType.getSelectedItem().toString().equals("50% group")){
                    AddUpgradeGUI.this.prodBoostValue.setText((new Upgrade("", selectType.getSelectedItem().toString(), selectTarget.getSelectedItem().toString(), Building.getGroup(GUI.this.buildings, selectTarget.getSelectedItem().toString(), true), 0)).getFormattedUpgradeBoost());
                    AddUpgradeGUI.this.newTotalProd.setText((new Upgrade("", selectType.getSelectedItem().toString(), selectTarget.getSelectedItem().toString(), Building.getGroup(GUI.this.buildings, selectTarget.getSelectedItem().toString(), true), 0)).getFormattedNewProd(GUI.this.buildings));
                }
                else if(selectType.getSelectedItem().toString().equals("10% group") || selectType.getSelectedItem().toString().equals("Job")){
                    AddUpgradeGUI.this.prodBoostValue.setText((new Upgrade("", selectType.getSelectedItem().toString(), selectTarget.getSelectedItem().toString(), Building.getGroup(GUI.this.buildings, selectTarget.getSelectedItem().toString(), false), 0)).getFormattedUpgradeBoost());
                    AddUpgradeGUI.this.newTotalProd.setText((new Upgrade("", selectType.getSelectedItem().toString(), selectTarget.getSelectedItem().toString(), Building.getGroup(GUI.this.buildings, selectTarget.getSelectedItem().toString(), false), 0)).getFormattedNewProd(GUI.this.buildings));
                }
                else if(selectType.getSelectedItem().toString().equals("Global")){
                    AddUpgradeGUI.this.prodBoostValue.setText((new Upgrade("", selectType.getSelectedItem().toString(), selectTarget.getSelectedItem().toString(), GUI.this.buildings, 0)).getFormattedUpgradeBoost());
                    AddUpgradeGUI.this.newTotalProd.setText((new Upgrade("", selectType.getSelectedItem().toString(), selectTarget.getSelectedItem().toString(), GUI.this.buildings, 0)).getFormattedNewProd(GUI.this.buildings));
                }
                else {
                    AddUpgradeGUI.this.prodBoostValue.setText(new Upgrade("", selectType.getSelectedItem().toString(), Building.getBuildingByName(GUI.this.buildings, selectTarget.getSelectedItem().toString()).getBuilding(), 0).getFormattedUpgradeBoost());
                    AddUpgradeGUI.this.newTotalProd.setText(new Upgrade("", selectType.getSelectedItem().toString(), Building.getBuildingByName(GUI.this.buildings, selectTarget.getSelectedItem().toString()).getBuilding(), 0).getFormattedNewProd(GUI.this.buildings));
                }
                AddUpgradeGUI.this.add(AddUpgradeGUI.this.prodBoostValue, gbc);
                gbc.gridy++;
                AddUpgradeGUI.this.add(AddUpgradeGUI.this.newTotalProd, gbc);
            }
            else {
                if(AddUpgradeGUI.this.prodBoostValue != null && AddUpgradeGUI.this.newTotalProd != null) {
                    AddUpgradeGUI.this.prodBoostValue.setText("");
                    AddUpgradeGUI.this.newTotalProd.setText("");
                }
            }
            // is best purchase
            if(selectType.getSelectedItem() != null && selectTarget.getSelectedItem() != null && !enterPrice.getText().trim().isEmpty()){
                // upgrade boost
                if(selectType.getSelectedItem().toString().equals("50% group")){
                    AddUpgradeGUI.this.isBestPurchase.setText(Boolean.toString((new Upgrade("", selectType.getSelectedItem().toString(), selectTarget.getSelectedItem().toString(), Building.getGroup(GUI.this.buildings, selectTarget.getSelectedItem().toString(), true), Integer.parseInt(enterPrice.getText().replaceAll("[^0-9]", "")))).isBestPurchase(GUI.this.upgrades, GUI.this.buildings)));
                }
                else if(selectType.getSelectedItem().toString().equals("10% group") || selectType.getSelectedItem().toString().equals("Job")){
                    AddUpgradeGUI.this.isBestPurchase.setText(Boolean.toString((new Upgrade("", selectType.getSelectedItem().toString(), selectTarget.getSelectedItem().toString(), Building.getGroup(GUI.this.buildings, selectTarget.getSelectedItem().toString(), false), Integer.parseInt(enterPrice.getText().replaceAll("[^0-9]", "")))).isBestPurchase(GUI.this.upgrades, GUI.this.buildings)));
                }
                else if(selectType.getSelectedItem().toString().equals("Global")){
                    AddUpgradeGUI.this.isBestPurchase.setText(Boolean.toString(new Upgrade("", selectType.getSelectedItem().toString(), selectTarget.getSelectedItem().toString(), GUI.this.buildings, Integer.parseInt(enterPrice.getText().replaceAll("[^0-9]", ""))).isBestPurchase(GUI.this.upgrades, GUI.this.buildings)));
                }
                else {
                    AddUpgradeGUI.this.isBestPurchase.setText(Boolean.toString(new Upgrade("", selectType.getSelectedItem().toString(), Building.getBuildingByName(GUI.this.buildings, selectTarget.getSelectedItem().toString()).getBuilding(), Integer.parseInt(enterPrice.getText().replaceAll("[^0-9]", ""))).isBestPurchase(GUI.this.upgrades, GUI.this.buildings)));
                }
                gbc.gridy++;
                AddUpgradeGUI.this.add(AddUpgradeGUI.this.isBestPurchase, gbc);
            }
            else {
                if(AddUpgradeGUI.this.isBestPurchase != null) {
                    AddUpgradeGUI.this.isBestPurchase.setText("");
                }
            }
            AddUpgradeGUI.this.pack();
        }

        private void addClosingListener() {
            addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    addUpgradeGUI = null;
                }
            });
        }
    }

    private class listUpgradeGUI extends JFrame{

        private JLabel[] upgradeNumber;
        private JTextField[] upgradeName;
        private JTextField[] upgradeType;
        private JTextField[] upgradeTarget;
        private JTextField[] upgradeProd;
        private NumericTextField[] upgradePrice;
        private BetterButton[] buyUpgrade;
        private BetterButton[] editUpgrade;
        private BetterButton[] delUpgrade;
        private BetterButton[] applyEdit;
        private BetterButton[] cancelEdit;

        listUpgradeGUI(){
            setTitle("Upgrade List");
            setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            addClosingListener();
            setLayout(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.insets = margin5;

            //page body
            gbc.gridx = 0;
            gbc.gridy = 0;
            gbc.gridwidth = 9;
            JLabel listUpgrade = new JLabel("Upgrade List");
            listUpgrade.setFont(big);
            add(listUpgrade, gbc);
            gbc.gridwidth = 1;

            gbc.gridy++;
            JLabel number = new JLabel("Number");
            add(number, gbc);
            gbc.gridx++;

            JLabel name = new JLabel("Name");
            add(name, gbc);
            gbc.gridx++;

            JLabel type = new JLabel("Type");
            add(type, gbc);
            gbc.gridx++;

            JLabel target = new JLabel("Target");
            add(target, gbc);
            gbc.gridx++;

            JLabel prod = new JLabel("Prod Boost");
            add(prod, gbc);
            gbc.gridx++;

            JLabel price = new JLabel("Price");
            add(price, gbc);

            gbc.insets = margin30L;
            gbc.gridx = 9;
            add(options.clone(), gbc);

            gbc.gridy++;
            add(GUI.this.addUpgrade.clone(), gbc);

            gbc.gridy++;
            add(GUI.this.buildingsUpgrades.clone(), gbc);

            applyEdit = new BetterButton[GUI.this.buildings.length];
            cancelEdit = new BetterButton[GUI.this.buildings.length];

            updateUpgrades();
        }

        private void updateUpgrades(){
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.insets = margin5;

            if (upgradeNumber != null)
                for (int i = 0; i < upgradeNumber.length; i++){
                    try {
                        remove(upgradeNumber[i]);
                        remove(upgradeName[i]);
                        remove(upgradeType[i]);
                        remove(upgradeTarget[i]);
                        remove(upgradeProd[i]);
                        remove(upgradePrice[i]);
                        remove(buyUpgrade[i]);
                        remove(editUpgrade[i]);
                        remove(delUpgrade[i]);
                        remove(applyEdit[i]);
                        remove(cancelEdit[i]);
                    }catch (NullPointerException ignored){}
                }

            //make new objects
            upgradeNumber = new JLabel[GUI.this.upgrades.size()];
            upgradeName = new JTextField[GUI.this.upgrades.size()];
            upgradeType = new JTextField[GUI.this.upgrades.size()];
            upgradeTarget = new JTextField[GUI.this.upgrades.size()];
            upgradeProd = new JTextField[GUI.this.upgrades.size()];
            upgradePrice = new NumericTextField[GUI.this.upgrades.size()];
            buyUpgrade = new BetterButton[GUI.this.upgrades.size()];
            editUpgrade = new BetterButton[GUI.this.upgrades.size()];
            delUpgrade = new BetterButton[GUI.this.upgrades.size()];
            applyEdit = new BetterButton[GUI.this.upgrades.size()];
            cancelEdit = new BetterButton[GUI.this.upgrades.size()];

            gbc.gridx = 0;
            gbc.gridy = 2;
            Upgrade.sortUpgrades(upgrades, Building.getTotalProd(buildings));
            for (int i = 0; i < GUI.this.upgrades.size(); i++){
                //start
                gbc.gridx = 0;
                gbc.insets = margin5;

                //upgrade number
                upgradeNumber[i] = new JLabel(String.valueOf(i+1));
                upgradeNumber[i].setHorizontalAlignment(JTextField.CENTER);
                add(upgradeNumber[i], gbc);
                gbc.gridx++;

                //upgrade name
                upgradeName[i] = new JTextField(upgrades.get(i).getName());
                upgradeName[i].setEditable(false);
                add(upgradeName[i], gbc);
                gbc.gridx++;

                //upgrade type
                upgradeType[i] = new JTextField(upgrades.get(i).getType());
                upgradeType[i].setEditable(false);
                add(upgradeType[i], gbc);
                gbc.gridx++;

                //upgrade target
                upgradeTarget[i] = new JTextField(upgrades.get(i).getTargetName());
                upgradeTarget[i].setEditable(false);
                add(upgradeTarget[i], gbc);
                gbc.gridx++;

                //upgrade price
                upgradeProd[i] = new JTextField();
                upgradeProd[i].setText(upgrades.get(i).getFormattedUpgradeBoost());
                upgradeProd[i].setEditable(false);
                upgradeProd[i].setHorizontalAlignment(JTextField.RIGHT);
                add(upgradeProd[i], gbc);
                gbc.gridx++;

                //upgrade price
                upgradePrice[i] = new NumericTextField("listUpgrade", Integer.MAX_VALUE);
                upgradePrice[i].setText(upgrades.get(i).getFormattedPrice());
                upgradePrice[i].setEditable(false);
                upgradePrice[i].setHorizontalAlignment(JTextField.RIGHT);
                add(upgradePrice[i], gbc);
                gbc.gridx++;

                //delete button
                gbc.insets = new Insets(5, 2, 5, 2);
                delUpgrade[i] = new BetterButton("delete", i);
                delUpgrade[i].setMargin(noMargin);
                add(delUpgrade[i], gbc);
                delUpgrade delUpgradeListener = new delUpgrade();
                delUpgrade[i].addActionListener(delUpgradeListener);
                gbc.gridx++;

                //apply button
                editUpgrade[i] = new BetterButton("edit", i);
                editUpgrade[i].setMargin(noMargin);
                add(editUpgrade[i], gbc);
                editUpgrade editUpgradeListener = new editUpgrade();
                editUpgrade[i].addActionListener(editUpgradeListener);
                gbc.gridx++;

                //apply button
                buyUpgrade[i] = new BetterButton("buy", i);
                buyUpgrade[i].setMargin(noMargin);
                add(buyUpgrade[i], gbc);
                buyUpgrade buyUpgradeListener = new buyUpgrade();
                buyUpgrade[i].addActionListener(buyUpgradeListener);

                //end
                gbc.gridy++;
            }
            pack();
            setLocationRelativeTo(null);
            setVisible(true);
        }


        private class buyUpgrade implements ActionListener {
            public void actionPerformed(ActionEvent evt) {
                int id = ((BetterButton) evt.getSource()).getId();
                upgrades.get(id).applyUpgrade();
                if(upgrades.get(id).getGroup() != null){
                    for (int i = 0; i < upgrades.get(id).getGroup().length; i++){
                        ((BetterButton) evt.getSource()).setId(Building.getBuildingByName(buildings, upgrades.get(id).getGroup()[i].getName()).getIndex());
                        GUI.this.updateBuildings(evt);
                    }
                }
                else{
                    ((BetterButton) evt.getSource()).setId(Building.getBuildingByName(buildings, upgrades.get(id).getBuilding().getName()).getIndex());
                    GUI.this.updateBuildings(evt);
                }
                Upgrade.delUpgrade(upgrades, upgrades.get(id));
                updateUpgrades();
                if (addUpgradeGUI != null)
                    addUpgradeGUI.updateStats();
            }
        }

        private class editUpgrade implements ActionListener {
            public void actionPerformed(ActionEvent evt) {
                int id = ((BetterButton) evt.getSource()).getId();
                remove(buyUpgrade[id]);
                remove(editUpgrade[id]);
                remove(delUpgrade[id]);

                GridBagConstraints gbc = new GridBagConstraints();
                gbc.fill = GridBagConstraints.HORIZONTAL;
                gbc.insets = new Insets(5, 2, 5, 2);
                gbc.gridy = (id+2);

                upgradePrice[id].setEditable(true);
                upgradeName[id].setEditable(true);

                gbc.gridx = 6;
                cancelEdit[id] = new BetterButton("Cancel", id);
                cancelEdit[id].setMargin(noMargin);
                cancelEdit cancelEditListener = new cancelEdit();
                cancelEdit[id].addActionListener(cancelEditListener);
                add(cancelEdit[id], gbc);

                gbc.gridx++;
                applyEdit[id] = new BetterButton("Apply", id);
                applyEdit[id].setMargin(noMargin);
                applyEdit applyEditListener = new applyEdit();
                applyEdit[id].addActionListener(applyEditListener);
                add(applyEdit[id], gbc);

                pack();
            }
        }

        private class delUpgrade implements ActionListener {
            public void actionPerformed(ActionEvent evt) {
                Upgrade.delUpgrade(upgrades, upgrades.get(((BetterButton) evt.getSource()).getId()));
                updateUpgrades();
                updateStats();
            }
        }

        private class applyEdit implements ActionListener {
            public void actionPerformed(ActionEvent evt) {
                int i = ((BetterButton) evt.getSource()).getId();
                if (!upgradeName[i].getText().trim().isEmpty() && !upgradePrice[i].getText().trim().isEmpty()){
                    Upgrade edited = new Upgrade(upgrades.get(i));
                    edited.setName(upgradeName[i].getText());
                    edited.setPrice(Integer.parseInt(upgradePrice[i].getText().replaceAll("[^0-9]", "")));
                    Upgrade.delUpgrade(upgrades, upgrades.get(i));
                    upgrades.add(edited);

                    try {
                        PrintWriter outputFile = new PrintWriter(new FileWriter("./data/upgrades.txt", true));
                        outputFile.print(edited.getName() + "|");
                        outputFile.print(edited.getType() + "|");
                        outputFile.print(edited.getTargetName() + "|");
                        outputFile.print(edited.getPrice());
                        outputFile.close();
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }

                    updateUpgrades();
                    pack();
                    updateStats();
                }
            }
        }

        private class cancelEdit implements ActionListener {
            public void actionPerformed(ActionEvent evt) {
                int i = ((BetterButton) evt.getSource()).getId();
                upgradeName[i].setText(upgrades.get(i).getName());
                upgradeName[i].setEditable(false);
                upgradePrice[i].setText(upgrades.get(i).getFormattedPrice());
                upgradePrice[i].setEditable(false);

                GridBagConstraints gbc = new GridBagConstraints();
                gbc.fill = GridBagConstraints.HORIZONTAL;
                gbc.insets = new Insets(5, 2, 5, 2);
                gbc.gridy = (i+2);

                gbc.gridx = 6;
                add(buyUpgrade[i], gbc);
                gbc.gridx++;
                add(editUpgrade[i], gbc);
                gbc.gridx++;
                add(delUpgrade[i], gbc);

                remove(cancelEdit[i]);
                remove(applyEdit[i]);
                pack();
            }
        }

        private void addClosingListener() {
            addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    listUpgradeGUI = null;
                }
            });
        }
    }

    private class BuildingsUpgradeGUI extends JFrame{

        // per building
        JLabel[] name = new JLabel[buildings.length];
        JTextField[] levels;
        JCheckBox[] doubleProd;
        JCheckBox[] tripleProd;
        JCheckBox[] sameBuilding;
        JCheckBox[] previousBuilding;

        //global
        JCheckBox[] group10 = new JCheckBox[GUI.this.groups10.length];
        JCheckBox[] group50 = new JCheckBox[GUI.this.groups50.length];
        JCheckBox[] job = new JCheckBox[GUI.this.groups10.length];
        NumericTextField globalLevel;

        //buttons
        JButton modify = new JButton("modify");
        JButton apply = new JButton("Apply");
        JButton cancel = new JButton("Cancel");
        JButton addUpgrade = GUI.this.addUpgrade.clone();
        JButton upgradeList = GUI.this.upgradeList.clone();

        public BuildingsUpgradeGUI() throws HeadlessException {
            setTitle("Buildings's Upgrades");
            setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            addClosingListener();
            setLayout(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.insets = new Insets(3, 5, 3, 5);

            gbc.gridx = 0;
            gbc.gridy = 0;
            JLabel buildings = new JLabel("Buildings");
            buildings.setFont(big);
            add(buildings, gbc);

            gbc.gridy++;
            for (int i = 0; i < GUI.this.buildings.length; i++){
                name[i] = new JLabel(GUI.this.buildings[i].getName());
                add(name[i], gbc);
                gbc.gridy++;
            }
            levels = new JTextField[GUI.this.buildings.length];
            doubleProd = new JCheckBox[GUI.this.buildings.length];
            tripleProd = new JCheckBox[GUI.this.buildings.length];
            sameBuilding = new JCheckBox[GUI.this.buildings.length];
            previousBuilding = new JCheckBox[GUI.this.buildings.length];

            gbc.gridy = 0;
            gbc.gridx = 1;
            add(new JLabel("Level"), gbc);
            gbc.gridx++;
            add(new JLabel("100%"), gbc);
            gbc.gridx++;
            add(new JLabel("200%"), gbc);
            gbc.gridx++;
            add(new JLabel("1% per same Building"), gbc);
            gbc.gridx++;
            add(new JLabel("1% per previous Building"), gbc);

            gbc.gridx = 6;
            gbc.gridy = 0;

            gbc.insets = new Insets(3, 30, 3, 5);
            JLabel global = new JLabel("Global Upgrades");
            global.setFont(big);
            gbc.gridwidth = 3;
            add(global, gbc);
            gbc.gridy++;
            gbc.gridwidth = 1;

            gbc.insets = new Insets(3, 5, 3, 5);
            gbc.gridx++;
            JLabel[] group50names = new JLabel[GUI.this.groups50.length];
            for (int i = 0; i < GUI.this.groups50.length; i++){
                group50names[i] = new JLabel(GUI.this.groups50[i]);
                group50names[i].setHorizontalAlignment(JTextField.CENTER);
                add(group50names[i], gbc);
                gbc.gridx++;
            }
            gbc.gridy++;

            gbc.insets = new Insets(3, 30, 3, 5);
            gbc.gridx = 6;
            JLabel group50 = new JLabel("group 50%");
            add(group50, gbc);
            gbc.gridy++;
            gbc.gridy++;
            gbc.gridy++;

            JLabel job = new JLabel("job");
            job.setHorizontalAlignment(JTextField.CENTER);
            add(job, gbc);
            gbc.gridy++;

            JLabel group10 = new JLabel("group 10%");
            add(group10, gbc);


            gbc.gridy++;
            gbc.gridy++;
            JLabel globalUP = new JLabel("global level");
            add(globalUP, gbc);


            gbc.insets = new Insets(3, 5, 3, 5);
            gbc.gridx = 7;
            gbc.gridy = 4;
            JLabel[] group10names = new JLabel[GUI.this.groups10.length];
            for (int i = 0; i < GUI.this.groups10.length; i++){
                group10names[i] = new JLabel(GUI.this.groups10[i]);
                group10names[i].setHorizontalAlignment(JTextField.CENTER);
                add(group10names[i], gbc);
                gbc.gridx++;
            }

            gbc.insets = new Insets(3, 30, 3, 5);
            gbc.gridy = 10;
            gbc.gridx = 6;
            add(options.clone(), gbc);

            gbc.gridy++;
            modify.setMargin(noMargin);
            add(modify, gbc);

            apply.setMargin(noMargin);
            cancel.setMargin(noMargin);

            gbc.gridy = 13;
            add(addUpgrade, gbc);

            gbc.gridy++;
            add(upgradeList, gbc);

            updateBuildingUpgrades();
            setLocationRelativeTo(null);
            setVisible(true);
            //listeners
            modify.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    remove(modify);
                    remove(addUpgrade);
                    remove(upgradeList);
                    for (int i = 0; i < GUI.this.buildings.length; i++){
                        levels[i].setEditable(true);
                        if (GUI.this.buildings[i].isUpgradable()) {
                            doubleProd[i].setEnabled(true);
                            tripleProd[i].setEnabled(true);
                            sameBuilding[i].setEnabled(true);
                        }
                        if (!(previousBuilding[i] instanceof BetterCheckBox) && GUI.this.buildings[i].isUpgradable())
                            previousBuilding[i].setEnabled(true);
                    }
                    for (int i = 0; i < GUI.this.groups50.length; i++){
                        BuildingsUpgradeGUI.this.group50[i].setEnabled(true);
                        BuildingsUpgradeGUI.this.group10[i].setEnabled(true);
                        BuildingsUpgradeGUI.this.job[i].setEnabled(true);
                    }
                    globalLevel.setEditable(true);
                    gbc.gridy = 11;
                    gbc.gridx = 6;
                    add(apply, gbc);
                    gbc.gridy++;
                    add(cancel, gbc);
                    gbc.gridy = 14;
                    add(addUpgrade, gbc);
                    gbc.gridy++;
                    add(upgradeList, gbc);
                    pack();
                }
            });

            apply.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    remove(apply);
                    remove(cancel);
                    remove(addUpgrade);
                    remove(upgradeList);
                    gbc.gridy = 11;
                    gbc.gridx = 6;
                    add(modify,gbc);
                    gbc.gridy = 13;
                    add(addUpgrade, gbc);
                    gbc.gridy++;
                    add(upgradeList, gbc);
                    for(int i = 0; i < GUI.this.buildings.length; i++){
                        GUI.this.buildings[i].setLevel(!levels[i].getText().isEmpty()?Integer.parseInt(levels[i].getText()):GUI.this.buildings[i].getLevel());
                        GUI.this.buildings[i].setDoubleUpgrade(doubleProd[i].isSelected());
                        GUI.this.buildings[i].setTripleUpgrade(tripleProd[i].isSelected());
                        GUI.this.buildings[i].setSameBuildingUpgrade(sameBuilding[i].isSelected());
                        GUI.this.buildings[i].setPreviousBuildingUpgrade(previousBuilding[i].isSelected());
                    }
                    for(int i = 0; i < 4; i++){
                        for(int j = 0; j < Building.getGroup(GUI.this.buildings, GUI.this.groups50[i], true).length; j++){
                            if(BuildingsUpgradeGUI.this.group50[i].isSelected() && !Building.getGroup(GUI.this.buildings, GUI.this.groups50[i], true)[j].hasUpgrade("50% group", GUI.this.groups50[i]))
                                Building.getGroup(GUI.this.buildings, GUI.this.groups50[i], true)[j].addGroupUpgrade50(GUI.this.groups50[i]);
                            else if(!BuildingsUpgradeGUI.this.group50[i].isSelected() && Building.getGroup(GUI.this.buildings, GUI.this.groups50[i], true)[j].hasUpgrade("50% group", GUI.this.groups50[i]))
                                Building.getGroup(GUI.this.buildings, GUI.this.groups50[i], true)[j].removeGroupUpgrade50(GUI.this.groups50[i]);
                        }
                        for(int j = 0; j < Building.getGroup(GUI.this.buildings, GUI.this.groups10[i], false).length; j++){
                            Building.getGroup(GUI.this.buildings, GUI.this.groups10[i], false)[j].setGroupUpgrade(BuildingsUpgradeGUI.this.group10[i].isSelected());
                            Building.getGroup(GUI.this.buildings, GUI.this.groups10[i], false)[j].setJobUpgrade(BuildingsUpgradeGUI.this.job[i].isSelected());
                        }
                    }
                    Building.setGlobalUpgradeLevel(GUI.this.buildings, !globalLevel.getText().isEmpty()?Integer.parseInt(globalLevel.getText()):GUI.this.buildings[0].getGlobalUpgrade());
                    for (int i = 0; i < GUI.this.buildings.length; i++){
                        if (levels[i].getText().isEmpty())
                            levels[i].setText(Integer.toString(GUI.this.buildings[i].getLevel()));
                        levels[i].setEditable(false);
                        doubleProd[i].setEnabled(false);
                        tripleProd[i].setEnabled(false);
                        sameBuilding[i].setEnabled(false);
                        if (!(previousBuilding[i] instanceof BetterCheckBox))
                            previousBuilding[i].setEnabled(false);
                    }
                    for (int i = 0; i < GUI.this.groups50.length; i++){
                        BuildingsUpgradeGUI.this.group50[i].setEnabled(false);
                        BuildingsUpgradeGUI.this.group10[i].setEnabled(false);
                        BuildingsUpgradeGUI.this.job[i].setEnabled(false);
                    }
                    globalLevel.setEditable(false);
                    if (globalLevel.getText().isEmpty())
                        globalLevel.setText(Integer.toString(GUI.this.buildings[0].getGlobalUpgrade()));
                    pack();
                    Upgrade.delUselessUpgrades(upgrades, GUI.this.buildings);
                    Building.calculateAllUnitProduction(GUI.this.buildings);
                    if (GUI.listUpgradeGUI != null)
                        listUpgradeGUI.updateUpgrades();
                    if (GUI.addUpgradeGUI != null)
                        addUpgradeGUI.updateStats();
                    GUI.this.updateAllBuildings();
                }
            });

            cancel.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    remove(apply);
                    remove(cancel);
                    remove(addUpgrade);
                    remove(upgradeList);
                    gbc.gridy = 11;
                    gbc.gridx = 6;
                    add(modify,gbc);
                    gbc.gridy = 13;
                    add(addUpgrade, gbc);
                    gbc.gridy++;
                    add(upgradeList, gbc);
                    for (int i = 0; i < GUI.this.buildings.length; i++){
                        remove(levels[i]);
                        remove(doubleProd[i]);
                        remove(tripleProd[i]);
                        remove(sameBuilding[i]);
                        if (!(previousBuilding[i] instanceof BetterCheckBox))
                            remove(previousBuilding[i]);
                    }
                    for (int i = 0; i < GUI.this.groups50.length; i++){
                        remove(BuildingsUpgradeGUI.this.group50[i]);
                        remove(BuildingsUpgradeGUI.this.group10[i]);
                        remove(BuildingsUpgradeGUI.this.job[i]);
                    }
                    remove(globalLevel);
                    updateBuildingUpgrades();
                }
            });
        }

        public void updateBuildingUpgrades(){
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.insets = new Insets(3, 5, 3, 5);

            gbc.gridy = 1;
            for (int i = 0; i < buildings.length; i++){
                gbc.gridx = 1;

                levels[i] = new NumericTextField("levels",99);
                levels[i].setText(Integer.toString(buildings[i].getLevel()));
                levels[i].setEditable(false);
                levels[i].setHorizontalAlignment(JTextField.CENTER);
                add(levels[i], gbc);
                gbc.gridx++;

                if (buildings[i].isUpgradable()){
                    sameBuilding[i] = new JCheckBox((Icon) null, buildings[i].isSameBuildingUpgrade());
                    tripleProd[i] = new JCheckBox((Icon) null, buildings[i].isTripleUpgrade());
                    doubleProd[i] = new JCheckBox((Icon) null, buildings[i].isDoubleUpgrade());
                }else {
                    sameBuilding[i] = new BetterCheckBox();
                    tripleProd[i] = new BetterCheckBox();
                    doubleProd[i] = new BetterCheckBox();
                }

                doubleProd[i].setEnabled(false);
                doubleProd[i].setHorizontalAlignment(JTextField.CENTER);
                add(doubleProd[i], gbc);
                gbc.gridx++;

                tripleProd[i].setEnabled(false);
                tripleProd[i].setHorizontalAlignment(JTextField.CENTER);
                add(tripleProd[i], gbc);
                gbc.gridx++;

                sameBuilding[i].setEnabled(false);
                sameBuilding[i].setHorizontalAlignment(JTextField.CENTER);
                add(sameBuilding[i], gbc);
                gbc.gridx++;

                if (buildings[i].getPreviousBuilding() != null && buildings[i].isUpgradable()){
                    previousBuilding[i] = new JCheckBox((Icon) null, buildings[i].isPreviousBuildingUpgrade());
                }else {
                    previousBuilding[i] = new BetterCheckBox();
                }
                previousBuilding[i].setEnabled(false);
                previousBuilding[i].setHorizontalAlignment(JTextField.CENTER);
                add(previousBuilding[i], gbc);

                gbc.gridy++;
            }
            gbc.gridy = 2;
            gbc.gridx = 7;

            for (int i = 0; i < GUI.this.groups50.length; i++){
                group50[i] = new JCheckBox((Icon) null, Building.getGroup(GUI.this.buildings, GUI.this.groups50[i], true)[0].hasUpgrade("50% group", GUI.this.groups50[i]));
                group50[i].setEnabled(false);
                group50[i].setHorizontalAlignment(JTextField.CENTER);
                add(group50[i], gbc);
                gbc.gridx++;
            }

            gbc.gridy = 5;
            gbc.gridx = 6;
            for (int i = 0; i < GUI.this.groups10.length; i++){
                gbc.gridx++;
                job[i] = new JCheckBox((Icon) null, Building.getGroup(GUI.this.buildings, GUI.this.groups10[i], false)[0].isJobUpgrade());
                group10[i] = new JCheckBox((Icon) null, Building.getGroup(GUI.this.buildings, GUI.this.groups10[i], false)[0].isGroupUpgrade());
                job[i].setEnabled(false);
                group10[i].setEnabled(false);
                job[i].setHorizontalAlignment(JTextField.CENTER);
                group10[i].setHorizontalAlignment(JTextField.CENTER);

                add(job[i], gbc);
                gbc.gridy++;

                add(group10[i], gbc);
                gbc.gridy--;
            }

            gbc.gridy = 8;
            gbc.gridx = 7;
            globalLevel = new NumericTextField("Global Level", 10);
            globalLevel.setText(Integer.toString(GUI.this.buildings[0].getGlobalUpgrade()));
            globalLevel.setEditable(false);
            add(globalLevel, gbc);

            pack();
        }

        private void addClosingListener() {
            addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    buildingsUpgradesGUI = null;
                }
            });
        }
    }

    public static class SharedLabel extends JLabel implements Cloneable{
        public SharedLabel(String text, Font font) {
            super(text);
            setFont(font);
        }
        public SharedLabel(){}

        @Override
        public SharedLabel clone() {
            SharedLabel clone = new SharedLabel();
            clone.setText(this.getText());
            clone.setFont(this.getFont());
            return clone;
        }
    }

    public static class SharedButton extends JButton implements Cloneable{
        public SharedButton(String text, Insets margin, ActionListener actionListener) {
            super(text);
            this.setMargin(margin);
            this.addActionListener(actionListener);
        }

        public SharedButton(){}

        @Override
        public SharedButton clone() {
            SharedButton clone = new SharedButton();
            clone.setMargin(this.getMargin());
            clone.setText(this.getText());
            for (int i = 0; i < this.getActionListeners().length; i++){
                clone.addActionListener(this.getActionListeners()[i]);
            }
            return clone;
        }
    }

    static class BetterCheckBox extends JCheckBox {
        public BetterCheckBox() {
            setIcon(new Icon() {
                @Override
                public void paintIcon(Component c, Graphics g, int x, int y) {
                    g.setColor(Color.BLACK);
                    g.fillRect(x, y, getIconWidth(), getIconHeight());
                }

                @Override
                public int getIconWidth() {
                    return 13;
                }

                @Override
                public int getIconHeight() {
                    return 13;
                }
            });
        }
    }

    // NumericTextField for edit upgrade and add upgrade
    public class NumericTextField extends JTextField {
        private final DecimalFormat formatter = new DecimalFormat("#,###");;
        private String GUI; // will be removed
        private double max;

        public NumericTextField(){
            super();
            addListener();
        }
        public NumericTextField(double max){
            super();
            this.max = max;
            addListener();
        }

        public NumericTextField(String GUI, double max) {
            super();
            this.GUI = GUI;
            this.max = max;
            addListener();

            formatter.setGroupingSize(3);
            formatter.setParseIntegerOnly(true);
        }

        private void  addListener(){ // make it given by default
            addKeyListener(new KeyListener() {
                @Override
                public void keyTyped(KeyEvent e) {
                    char c = e.getKeyChar();
                    if (!(Character.isDigit(c) || c == KeyEvent.VK_BACK_SPACE || c == KeyEvent.VK_DELETE) || (!Character.isDigit(c) && getText().isEmpty()) || (c == '0' && getCaretPosition() == 0) || Long.parseLong((getText()+ c).replaceAll("\\D", "")) > Integer.MAX_VALUE) {
                        e.consume();
                    }
                    if (Character.isDigit(c) && !getText().isEmpty() && max > 0){
                        long numb = Long.parseLong((getText()+ c).replaceAll("\\D", ""));
                        if (numb > max)
                            e.consume();
                    }
                }

                @Override
                public void keyPressed(KeyEvent e) {
                }

                @Override
                public void keyReleased(KeyEvent e) {
                    if(!(e.getKeyCode() == KeyEvent.VK_LEFT || e.getKeyCode() == KeyEvent.VK_RIGHT) && !getText().isEmpty() && Integer.parseInt(getText().replaceAll("\\D", "")) > 999 )
                        formatText();
                }
            });
        }

        private void formatText() {
            try {
                String text = getText().replaceAll("\\D", "");
                long value = formatter.parse(text).longValue();
                String formattedText = formatter.format(value);
                setText(formattedText);
                if(GUI.equals("addUpgrade"))
                    addUpgradeGUI.updateStats();
            } catch (ParseException ignored) {}
        }
    }
}
