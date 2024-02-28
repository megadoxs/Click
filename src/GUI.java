import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Scanner;

public class GUI extends JFrame{
    private final Building[] buildings;
    private final String[] groups10 = new String[]{"mineur", "fermier", "alchimiste"};
    private final String[] groups50 = new String[]{"humanoide", "gelé", "eau", "foret"};
    private ArrayList<Upgrade> upgrades;
    private JTextField[] levels;
    private JTextField[] prod;
    private JTextField[] unitprod;
    private JLabel[] statsValue;
    private static AddUpgradeGUI addUpgradeGUI;

    public GUI(Building[] buildings, ArrayList<Upgrade> upgrades){
        this.buildings = buildings;
        this.upgrades = upgrades;
        setGUI();
    }

    public GUI(Building[] buildings) throws HeadlessException {
        this.buildings = buildings;
        setGUI();
    }

    public void setGUI(){
        setTitle("Clicker");
        setVisible(true);
        setLayout(new GridBagLayout());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);;
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
            header[j].setFont(new Font(header[j].getFont().getName(), Font.PLAIN, 20));
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
            addButtons[i].setMargin(new Insets(0, 0, 0, 0));
            delButtons[i].setMargin(new Insets(0, 0, 0, 0));
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
        stats.setFont(new Font(stats.getFont().getName(), Font.PLAIN, 20));
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
        JLabel options = new JLabel("Options");
        options.setFont(new Font(options.getFont().getName(), Font.PLAIN, 20));
        add(options, gbc);

        gbc.gridy++;
        purchaseRecommended purchaseListener = new purchaseRecommended();
        BetterButton purchaseRecommended = new BetterButton("Purchase Recommended");
        purchaseRecommended.setMargin(new Insets(0, 0, 0, 0));
        purchaseRecommended.addActionListener(purchaseListener);
        add(purchaseRecommended, gbc);

        gbc.gridy++;
        addUpgrade addUpgradeListener = new addUpgrade();
        JButton addUpgrade = new JButton("Add Upgrade");
        addUpgrade.setMargin(new Insets(0, 0, 0, 0));
        addUpgrade.addActionListener(addUpgradeListener);
        add(addUpgrade, gbc);

        gbc.gridy++;
        JButton upgradeList = new JButton("Upgrade List");
        upgradeList.setMargin(new Insets(0, 0, 0, 0));
        add(upgradeList, gbc);

        gbc.gridy++;
        JButton showbuildingUpgrades = new JButton("See Buildings's Upgrades");
        showbuildingUpgrades.setMargin(new Insets(0, 0, 0, 0));
        add(showbuildingUpgrades, gbc);

        gbc.gridy++;
        JButton reset = new JButton("Reset Data");
        reset.setMargin(new Insets(0, 0, 0, 0));
        add(reset, gbc);

        //pack
        pack();
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

    private void updateBuildings(ActionEvent evt){

        //updates target building
        GUI.this.levels[((BetterButton) evt.getSource()).getId()].setText(GUI.this.buildings[((BetterButton) evt.getSource()).getId()].getLevel() + "");
        GUI.this.buildings[((BetterButton) evt.getSource()).getId()].calculateUnitProduction();
        GUI.this.unitprod[((BetterButton) evt.getSource()).getId()].setText(GUI.this.buildings[((BetterButton) evt.getSource()).getId()].getFormattedUnitProduction());
        GUI.this.prod[((BetterButton) evt.getSource()).getId()].setText(GUI.this.buildings[((BetterButton) evt.getSource()).getId()].getFormattedProduction());

        //updates next building
        GUI.this.buildings[((BetterButton) evt.getSource()).getId()+1].calculateUnitProduction();
        GUI.this.unitprod[((BetterButton) evt.getSource()).getId()+1].setText(GUI.this.buildings[((BetterButton) evt.getSource()).getId()+1].getFormattedUnitProduction());
        GUI.this.prod[((BetterButton) evt.getSource()).getId()+1].setText(GUI.this.buildings[((BetterButton) evt.getSource()).getId()+1].getFormattedProduction());

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
        if (Building.getBestPurchase(buildings).getValue() > Upgrade.getBestPurchase(upgrades).getValue()){
            bestPurchase[0] = GUI.this.buildings[Building.getBestPurchase(buildings).getIndex()].getName();
            bestPurchase[1] = "Building";
            bestPurchase[2] = Building.getFormattedUpgradeBoost(buildings[Building.getBestPurchase(buildings).getIndex()]);
            bestPurchase[3] = Building.getFormattedNewProd(buildings, GUI.this.buildings[Building.getBestPurchase(buildings).getIndex()]);
            bestPurchase[4] = GUI.this.buildings[Building.getBestPurchase(buildings).getIndex()].getFormattedUpgradePrice();
        }else {
            bestPurchase[0] = GUI.this.upgrades.get(Upgrade.getBestPurchase(upgrades).getIndex()).getName();
            bestPurchase[1] = "Upgrade";
            bestPurchase[2] = GUI.this.upgrades.get(Upgrade.getBestPurchase(upgrades).getIndex()).getFormattedUpgradeBoost();
            bestPurchase[3] = GUI.this.upgrades.get(Upgrade.getBestPurchase(upgrades).getIndex()).getFormattedNewProd(buildings);
            bestPurchase[4] = GUI.this.upgrades.get(Upgrade.getBestPurchase(upgrades).getIndex()).getFormattedPrice();
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
                try {
                    File iFile = new File("./data/upgrades.txt");
                    Scanner inputFile = new Scanner(iFile);
                    File OFile = new File("temp.txt");
                    PrintWriter outputFile = new PrintWriter(OFile);
                    int UpgradeIndex = Upgrade.getBestPurchase(upgrades).getIndex();

                    while (inputFile.hasNextLine()) {
                        String upgrade = inputFile.nextLine();
                        if(!upgrade.equals(upgrades.get(UpgradeIndex).getName()+ "|" + upgrades.get(UpgradeIndex).getType() + "|" + upgrades.get(UpgradeIndex).getTargetName() + "|" + upgrades.get(UpgradeIndex).getPrice())){
                            outputFile.println(upgrade);
                        }
                    }

                    inputFile.close();
                    outputFile.close();
                    iFile.delete();
                    OFile.renameTo(new File("./data/upgrades.txt"));
                } catch (FileNotFoundException e) {
                    throw new RuntimeException(e);
                }
                upgrades.get(Upgrade.getBestPurchase(upgrades).getIndex()).applyUpgrade();
                if(upgrades.get(Upgrade.getBestPurchase(upgrades).getIndex()).getGroup() != null){
                    for (int i = 0; i < upgrades.get(Upgrade.getBestPurchase(upgrades).getIndex()).getGroup().length; i++){
                        ((BetterButton) evt.getSource()).setId(Building.getBuildingByName(buildings, upgrades.get(Upgrade.getBestPurchase(upgrades).getIndex()).getGroup()[i].getName()).getIndex());
                        updateBuildings(evt);
                    }
                }
                else{
                    ((BetterButton) evt.getSource()).setId(Building.getBuildingByName(buildings, upgrades.get(Upgrade.getBestPurchase(upgrades).getIndex()).getBuilding().getName()).getIndex());
                    updateBuildings(evt);
                }
            }
            updateStats();
        }
    }

    private class addUpgrade implements ActionListener {
        public void actionPerformed(ActionEvent evt) {
            if (addUpgradeGUI == null) {
                addUpgradeGUI = new AddUpgradeGUI();
            } else {
                addUpgradeGUI.toFront();
            }
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
            setVisible(true);
            setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            addClosingListener();
            setLayout(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.insets = new Insets(5, 5, 5, 5);

            //page body
            gbc.gridx = 0;
            gbc.gridy = 0;
            gbc.gridwidth = 2;
            JLabel addUpgrade = new JLabel("New Upgrade");
            addUpgrade.setFont(new Font(addUpgrade.getFont().getName(), Font.PLAIN, 20));
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
            selectType.setSelectedItem(null);
            add(selectType, gbc);


            gbc.gridy++;
            selectTarget = new JComboBox<>();
            add(selectTarget, gbc);

            gbc.gridy++;
            enterPrice = new NumericTextField();
            add(enterPrice, gbc);

            gbc.gridy++;
            gbc.gridx = 0;
            gbc.gridwidth = 2;
            JButton createUpgrade = new JButton("add Upgrade");
            createUpgrade.setHorizontalAlignment(JTextField.CENTER);
            add(createUpgrade, gbc);

            //stats
            gbc.insets = new Insets(5, 30, 5, 5);
            gbc.gridy = 0;
            gbc.gridx = 2;
            JLabel stats = new JLabel("Stats");
            stats.setFont(new Font(addUpgrade.getFont().getName(), Font.PLAIN, 20));
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


            gbc.insets = new Insets(5, 5, 5, 5);
            gbc.gridy = 1;
            gbc.gridx = 3;
            JLabel totalProdValue = new JLabel(Building.getFormattedTotalProd(GUI.this.buildings));
            add(totalProdValue, gbc);

            gbc.insets = new Insets(2, 30, 2, 5);
            gbc.gridx = 4;
            gbc.gridy = 0;
            JLabel options = new JLabel("Options");
            options.setFont(new Font(options.getFont().getName(), Font.PLAIN, 20));
            add(options, gbc);

            gbc.gridy++;
            JButton upgradeList = new JButton("Upgrade List");
            upgradeList.setMargin(new Insets(0, 0, 0, 0));
            add(upgradeList, gbc);

            gbc.gridy++;
            JButton showbuildingUpgrades = new JButton("See Buildings's Upgrades");
            showbuildingUpgrades.setMargin(new Insets(0, 0, 0, 0));
            add(showbuildingUpgrades, gbc);

            //listeners

            class updateUpgrade implements ActionListener {
                @Override
                public void actionPerformed(ActionEvent evt) {
                    updateStats();
                }
            }

            DocumentListener textFieldListener = new DocumentListener() {
                @Override
                public void insertUpdate(DocumentEvent e) {
                    updateStats();
                }

                @Override
                public void removeUpdate(DocumentEvent e) {
                    updateStats();
                }

                @Override
                public void changedUpdate(DocumentEvent e) {}
            };

            updateUpgrade updateUpgradeListener = new updateUpgrade();
            enterName.getDocument().addDocumentListener(textFieldListener);
            selectType.addActionListener(updateUpgradeListener);
            selectTarget.addActionListener(updateUpgradeListener);


            selectType.addActionListener(new ActionListener() { // need to remove the possibility to select an a target that already has that upgrade
                public void actionPerformed(ActionEvent e) {
                    if (selectType.getSelectedItem() != null && selectType.getSelectedItem().toString().equals("10% group")){
                        selectTarget.removeAllItems();
                        for (int i = 0; i < GUI.this.groups10.length; i++){
                            if(!Building.getGroup(buildings, GUI.this.groups10[i], false)[0].hasUpgrade("10% group", GUI.this.groups10[i]) && !Upgrade.upgradeExist(GUI.this.upgrades, GUI.this.groups10[i], selectType.getSelectedItem().toString()))
                                selectTarget.addItem(GUI.this.groups10[i]);
                        }
                        selectTarget.setSelectedItem(null);
                    }
                    else if (selectType.getSelectedItem() != null && selectType.getSelectedItem().toString().equals("50% group")){
                        selectTarget.removeAllItems();
                        for (int i = 0; i < GUI.this.groups50.length; i++){
                            if(!Building.getGroup(buildings, GUI.this.groups50[i], true)[0].hasUpgrade("50% group", GUI.this.groups50[i]) && !Upgrade.upgradeExist(GUI.this.upgrades, GUI.this.groups50[i], selectType.getSelectedItem().toString()))
                                selectTarget.addItem(GUI.this.groups50[i]);
                        }
                        selectTarget.setSelectedItem(null);
                    }
                    else if (selectType.getSelectedItem() != null && selectType.getSelectedItem().toString().equals("200%")){
                        selectTarget.removeAllItems();
                        for (int i = 0; i < GUI.this.buildings.length; i++){
                            if(GUI.this.buildings[i].getLevel() > 0 && !GUI.this.buildings[i].hasUpgrade("200%") && GUI.this.buildings[i].hasUpgrade("100%") && !Upgrade.upgradeExist(GUI.this.upgrades, buildings[i].getName(), selectType.getSelectedItem().toString()))
                                selectTarget.addItem(GUI.this.buildings[i].getName());
                        }
                        selectTarget.setSelectedItem(null);
                    }
                    else if (selectType.getSelectedItem() != null) {
                        selectTarget.removeAllItems();
                        for (int i = 0; i < GUI.this.buildings.length; i++){
                            if(GUI.this.buildings[i].getLevel() > 0 && !GUI.this.buildings[i].hasUpgrade(selectType.getSelectedItem().toString()) && !Upgrade.upgradeExist(GUI.this.upgrades, buildings[i].getName(), selectType.getSelectedItem().toString()))
                                selectTarget.addItem(GUI.this.buildings[i].getName());
                        }
                        selectTarget.setSelectedItem(null);
                    }
                    else {
                        selectTarget.removeAllItems();
                    }
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
                            else if(selectType.getSelectedItem().toString().equals("10% group")){
                                GUI.this.upgrades.add(new Upgrade(enterName.getText(), selectType.getSelectedItem().toString(), selectTarget.getSelectedItem().toString(), Building.getGroup(GUI.this.buildings, selectTarget.getSelectedItem().toString(), false), Integer.parseInt(enterPrice.getText().replaceAll("[^0-9]", ""))));
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
                    }
                }
            });

            //pack
            pack();
        }

        public void updateStats(){
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.insets = new Insets(5, 5, 5, 5);
            gbc.gridy = 2;
            gbc.gridx = 3;
            // upgrade boost & New prod
            if(selectType.getSelectedItem() != null && selectTarget.getSelectedItem() != null){
                if(selectType.getSelectedItem().toString().equals("50% group")){
                    AddUpgradeGUI.this.prodBoostValue.setText((new Upgrade("", selectType.getSelectedItem().toString(), selectTarget.getSelectedItem().toString(), Building.getGroup(GUI.this.buildings, selectTarget.getSelectedItem().toString(), true), 0)).getFormattedUpgradeBoost());
                    AddUpgradeGUI.this.newTotalProd.setText((new Upgrade("", selectType.getSelectedItem().toString(), selectTarget.getSelectedItem().toString(), Building.getGroup(GUI.this.buildings, selectTarget.getSelectedItem().toString(), true), 0)).getFormattedNewProd(GUI.this.buildings));
                }
                else if(selectType.getSelectedItem().toString().equals("10% group")){
                    AddUpgradeGUI.this.prodBoostValue.setText((new Upgrade("", selectType.getSelectedItem().toString(), selectTarget.getSelectedItem().toString(), Building.getGroup(GUI.this.buildings, selectTarget.getSelectedItem().toString(), false), 0)).getFormattedUpgradeBoost());
                    AddUpgradeGUI.this.newTotalProd.setText((new Upgrade("", selectType.getSelectedItem().toString(), selectTarget.getSelectedItem().toString(), Building.getGroup(GUI.this.buildings, selectTarget.getSelectedItem().toString(), false), 0)).getFormattedNewProd(GUI.this.buildings));
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
                else if(selectType.getSelectedItem().toString().equals("10% group")){
                    AddUpgradeGUI.this.isBestPurchase.setText(Boolean.toString((new Upgrade("", selectType.getSelectedItem().toString(), selectTarget.getSelectedItem().toString(), Building.getGroup(GUI.this.buildings, selectTarget.getSelectedItem().toString(), false), Integer.parseInt(enterPrice.getText().replaceAll("[^0-9]", "")))).isBestPurchase(GUI.this.upgrades, GUI.this.buildings)));
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

        public class NumericTextField extends JTextField {
            private DecimalFormat formatter;

            public NumericTextField() {
                super();

                formatter = new DecimalFormat("#,###");
                formatter.setGroupingSize(3);
                formatter.setParseIntegerOnly(true);

                addKeyListener(new KeyListener() {
                    @Override
                    public void keyTyped(KeyEvent e) {
                        char c = e.getKeyChar();
                        if (!(Character.isDigit(c) || c == KeyEvent.VK_BACK_SPACE || c == KeyEvent.VK_DELETE)) {
                            e.consume();
                        }
                    }

                    @Override
                    public void keyPressed(KeyEvent e) {
                    }

                    @Override
                    public void keyReleased(KeyEvent e) {
                        formatText();
                        AddUpgradeGUI.this.updateStats();
                    }
                });
            }

            private void formatText() {
                try {
                    String text = getText().replaceAll("\\s", "");
                    long value = formatter.parse(text).longValue();
                    String formattedText = formatter.format(value);
                    setText(formattedText);
                    AddUpgradeGUI.this.updateStats();
                } catch (ParseException e) {}
            }
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
}
