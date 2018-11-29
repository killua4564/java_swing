
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;

public class Problem1 {

    public static class ProblemFrame extends JFrame {

        private final JButton addButton;
        private final JButton removeButton;
        private final JButton submitButton;
        private final JComboBox numberComboBox;
        private final JList productList;
        private final JList shoppingList;
        private final String productNameList[] = {
            "product1 $10", "product2 $20", "product3 $30", "product4 $40", "product5 $50"
        };
        private final String numberList[] = {
            "1", "2", "3", "4", "5"
        };
        private ArrayList<String> shoppingArray = new ArrayList<>();

        public ProblemFrame() {
            super("Problem1");
            this.setLayout(new FlowLayout());

            this.productList = new JList(this.productNameList);
            this.productList.setVisibleRowCount(3);
            this.add(new JScrollPane(this.productList));

            this.numberComboBox = new JComboBox(this.numberList);
            this.add(this.numberComboBox);

            this.addButton = new JButton("??�入");
            this.addButton.addActionListener(new addButtonListener());
            this.add(this.addButton);

            this.shoppingList = new JList();
            this.shoppingList.setVisibleRowCount(3);
            this.add(new JScrollPane(this.shoppingList));

            this.removeButton = new JButton("移除");
            this.removeButton.addActionListener(new removeButtonListener());
            this.add(this.removeButton);

            this.submitButton = new JButton("總�??");
            this.submitButton.addActionListener(new submitButtonListener());
            this.add(this.submitButton);
        }

        private class addButtonListener implements ActionListener {

            @Override
            public void actionPerformed(ActionEvent event) {
                int index = productList.getSelectedIndex() + 1;
                String product = (String) productList.getSelectedValue();
                String number = (String) numberComboBox.getSelectedItem();
                shoppingArray.add(String.format("%d,%s,%s", index, product, number));
                shoppingList.setListData(shoppingArray.toArray());
            }
        }

        private class removeButtonListener implements ActionListener {

            @Override
            public void actionPerformed(ActionEvent event) {
                String item = (String) shoppingList.getSelectedValue();
                shoppingArray.remove(item);
                shoppingList.setListData(shoppingArray.toArray());
            }
        }

        private class submitButtonListener implements ActionListener {

            @Override
            public void actionPerformed(ActionEvent event) {
                int total = 0;
                for (String line : shoppingArray) {
                    String string[] = line.split(",");
                    total += Integer.parseInt(string[0]) * Integer.parseInt(string[2]);
                }
                JOptionPane.showMessageDialog(
                        ProblemFrame.this,
                        String.format("Total: %d", total * 10),
                        "Submit",
                        JOptionPane.PLAIN_MESSAGE
                );
            }
        }
    }

    public static void main(String[] args) {
        ProblemFrame problemFrame = new ProblemFrame();
        problemFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        problemFrame.setSize(800, 150);
        problemFrame.setVisible(true);
    }
}
