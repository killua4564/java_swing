
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

class GUIFrame extends JFrame {

    private final ButtonGroup genderButtonGroup;
    private final ButtonGroup bloodButtonGroup;
    private final JButton confirmJButton;
    private final JComboBox itemsComboBox;
    private final JLabel bloodLabel;
    private final JLabel genderLabel;
    private final JLabel itemLabel;
    private final JLabel nameLabel;
    private final JLabel newItemLabel;
    private final JPanel radioButtonPanel;
    private final JRadioButton maleRadioButton;
    private final JRadioButton femaleRadioButton;
    private final JRadioButton bloodARadioButton;
    private final JRadioButton bloodBRadioButton;
    private final JRadioButton bloodABRadioButton;
    private final JRadioButton bloodORadioButton;
    private final JTextField nameTextField;

    private final String comboBoxName[] = {
        "Andy, Male, O",
        "Allen, Male, AB",
        "Sandy, Female, B",
        "Windy, Female, A",
        "Rose, Female, A"
    };

    public GUIFrame() {
        super("Homework");

        this.radioButtonPanel = new JPanel();
        this.radioButtonPanel.setLayout(null);
        
        this.itemsComboBox = new JComboBox(this.comboBoxName);
        this.itemsComboBox.setBounds(50, 50, 200, 50);
        this.itemsComboBox.addItemListener(new ComboBoxListener());
        this.radioButtonPanel.add(this.itemsComboBox);

        this.newItemLabel = new JLabel();
        this.newItemLabel.setText("New Item");
        this.newItemLabel.setBounds(300, 50, 100, 50);
        this.radioButtonPanel.add(this.newItemLabel);

        this.confirmJButton = new JButton("confirm");
        this.confirmJButton.setBounds(400, 50, 200, 40);
        this.confirmJButton.addActionListener(new ConfirmHandler());
        this.radioButtonPanel.add(this.confirmJButton);

        this.itemLabel = new JLabel();
        this.itemLabel.setText("Your choice is: [Andy, Male, O]");
        this.itemLabel.setBounds(30, 100, 250, 50);
        this.radioButtonPanel.add(this.itemLabel);

        this.nameLabel = new JLabel();
        this.nameLabel.setText("Name");
        this.nameLabel.setBounds(300, 120, 100, 25);
        this.radioButtonPanel.add(this.nameLabel);

        this.genderLabel = new JLabel();
        this.genderLabel.setText("Gender");
        this.genderLabel.setBounds(400, 120, 100, 25);
        this.radioButtonPanel.add(this.genderLabel);

        this.bloodLabel = new JLabel();
        this.bloodLabel.setText("Blood Type");
        this.bloodLabel.setBounds(500, 120, 100, 25);
        this.radioButtonPanel.add(this.bloodLabel);

        this.nameTextField = new JTextField("");
        this.nameTextField.setBounds(300, 150, 100, 25);
        this.radioButtonPanel.add(this.nameTextField);

        this.genderButtonGroup = new ButtonGroup();
        this.maleRadioButton = new JRadioButton("Male", true);
        this.maleRadioButton.setBounds(400, 150, 100, 25);
        this.femaleRadioButton = new JRadioButton("Female", false);
        this.femaleRadioButton.setBounds(400, 175, 100, 25);
        
        this.radioButtonPanel.add(this.maleRadioButton);
        this.radioButtonPanel.add(this.femaleRadioButton);
        this.genderButtonGroup.add(this.maleRadioButton);
        this.genderButtonGroup.add(this.femaleRadioButton);

        this.bloodButtonGroup = new ButtonGroup();
        this.bloodARadioButton = new JRadioButton("A", true);
        this.bloodARadioButton.setBounds(500, 150, 100, 25);
        this.bloodBRadioButton = new JRadioButton("B", false);
        this.bloodBRadioButton.setBounds(500, 175, 100, 25);
        this.bloodABRadioButton = new JRadioButton("AB", false);
        this.bloodABRadioButton.setBounds(500, 200, 100, 25);
        this.bloodORadioButton = new JRadioButton("O", false);
        this.bloodORadioButton.setBounds(500, 225, 100, 25);
        
        this.radioButtonPanel.add(this.bloodARadioButton);
        this.radioButtonPanel.add(this.bloodBRadioButton);
        this.radioButtonPanel.add(this.bloodABRadioButton);
        this.radioButtonPanel.add(this.bloodORadioButton);
        
        this.bloodButtonGroup.add(this.bloodARadioButton);
        this.bloodButtonGroup.add(this.bloodBRadioButton);
        this.bloodButtonGroup.add(this.bloodABRadioButton);
        this.bloodButtonGroup.add(this.bloodORadioButton);
        
        this.add(this.radioButtonPanel);
    }

    private class ConfirmHandler implements ActionListener {

        private String name;
        private String gender;
        private String blood;

        @Override
        public void actionPerformed(ActionEvent event) {
            this.name = nameTextField.getText();
            this.gender = (maleRadioButton.isSelected()) ? "Male" : "Female";
            this.blood = (bloodARadioButton.isSelected()) ? "A" : (bloodBRadioButton.isSelected()) ? "B" : (bloodABRadioButton.isSelected()) ? "AB" : "O";
            nameTextField.setText("");
            maleRadioButton.setSelected(true);
            bloodARadioButton.setSelected(true);
            itemsComboBox.addItem(String.format("%s, %s, %s", this.name, this.gender, this.blood));
            itemLabel.setText(String.format("Your input is: [%s, %s, %s]", this.name, this.gender, this.blood));
        }
    }

    private class ComboBoxListener implements ItemListener {

        @Override
        public void itemStateChanged(ItemEvent event) {
            itemLabel.setText(String.format("Your choice is: [%s]", (String) event.getItem()));
        }
    }
}

public class GUI {

    public static void main(String[] args) {
        GUIFrame guiFrame = new GUIFrame();
        guiFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        guiFrame.setSize(700, 350);
        guiFrame.setVisible(true);
    }
}
