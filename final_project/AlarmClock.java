
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.Timer;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;
import javax.swing.text.AttributeSet;

public class AlarmClock {

    private static class ClockFrame extends JFrame {

        private final JMenu fileMenu;
        private final JMenu aboutMenu;
        private final JMenuBar menuBar;
        private final JMenuItem addMenuItem;
        private final JMenuItem exitMenuItem;
        private final JMenuItem aboutMenuItem;
        private final JPanel alarmClockPanel;
        private final ClockPanel clockPanel;
        private final AlarmClockItemPostgreSQL alarmClockItemPostgreSQL;

        private class ClockPanel extends JPanel {

            private int hour;
            private int minute;
            private int second;
            private final Timer timer;
            private final int radius = 150;
            private final double centerX = 250;
            private final double centerY = 200;
            private final int clockBlock = 12 * 60 * 60;
            private final Point2D.Double[] pointList = new Point2D.Double[this.clockBlock];

            public ClockPanel() {
                this.timer = new Timer(1000, new TimerHandler());
                this.hour = Calendar.getInstance().get(Calendar.HOUR);
                this.minute = Calendar.getInstance().get(Calendar.MINUTE);
                this.second = Calendar.getInstance().get(Calendar.SECOND);
                for (int i = 0; i < this.clockBlock; i++) {
                    double x = Math.cos(Math.PI * 2 * i / this.clockBlock) * this.radius;
                    double y = Math.sin(Math.PI * 2 * i / this.clockBlock) * this.radius;
                    this.pointList[i] = new Point2D.Double(x, y);
                }
                this.timer.start();
            }

            private double reduce(double a, double b, double r) {
                return a + b * r;
            }

            @Override
            public void paintComponent(Graphics graphics) {
                super.paintComponent(graphics);
                Graphics2D graphics2D = (Graphics2D) graphics;
                for (int minuteNumber = 1; minuteNumber <= 60; minuteNumber++) {
                    if (minuteNumber % 5 == 0) {
                        int hourNumber = minuteNumber / 5;
                        int numberIndex = (hourNumber * 60 * 60 + 3 * this.clockBlock / 4) % this.clockBlock;
                        Point2D.Double numberPoint = this.pointList[numberIndex];
                        graphics2D.drawString(String.valueOf(hourNumber), (float) this.reduce(this.centerX, numberPoint.x, .9), (float) this.reduce(this.centerY, numberPoint.y, .9));
                        graphics2D.draw(new Line2D.Double(this.centerX + numberPoint.x, this.centerY + numberPoint.y, this.reduce(this.centerX, numberPoint.x, .95), this.reduce(this.centerY, numberPoint.y, .95)));
                    } else {
                        int numberIndex = (minuteNumber * 12 * 60 + 3 * this.clockBlock / 4) % this.clockBlock;
                        Point2D.Double numberPoint = this.pointList[numberIndex];
                        graphics2D.draw(new Line2D.Double(this.centerX + numberPoint.x, this.centerY + numberPoint.y, this.reduce(this.centerX, numberPoint.x, .98), this.reduce(this.centerY, numberPoint.y, .98)));
                    }
                }
                int hourIndex = (this.hour * 60 * 60 + this.minute * 60 + this.second + 3 * this.clockBlock / 4) % this.clockBlock;
                int minuteIndex = (12 * this.minute * 60 + 12 * this.second + 3 * this.clockBlock / 4) % this.clockBlock;
                int secondIndex = (12 * 60 * this.second + 3 * this.clockBlock / 4) % this.clockBlock;
                Point2D.Double hourPoint = this.pointList[hourIndex];
                Point2D.Double minutePoint = this.pointList[minuteIndex];
                Point2D.Double secondPoint = this.pointList[secondIndex];
                graphics2D.draw(new Ellipse2D.Double(this.centerX - this.radius, this.centerY - this.radius, 2 * this.radius, 2 * this.radius));
                graphics2D.draw(new Line2D.Double(this.centerX, this.centerY, this.reduce(this.centerX, hourPoint.x, .4), this.reduce(this.centerY, hourPoint.y, .4)));
                graphics2D.draw(new Line2D.Double(this.centerX, this.centerY, this.reduce(this.centerX, minutePoint.x, .6), this.reduce(this.centerY, minutePoint.y, .6)));
                graphics2D.draw(new Line2D.Double(this.centerX, this.centerY, this.reduce(this.centerX, secondPoint.x, .8), this.reduce(this.centerY, secondPoint.y, .8)));
            }

            private class TimerHandler implements ActionListener {

                @Override
                public void actionPerformed(ActionEvent e) {
                    try {
                        hour = Calendar.getInstance().get(Calendar.HOUR);
                        minute = Calendar.getInstance().get(Calendar.MINUTE);
                        second = Calendar.getInstance().get(Calendar.SECOND);
                        repaint();
                        checkAlarmClockItem();
                    } catch (SQLException ex) {
                        System.out.println(ex.toString());
                    }
                }
            }
        }

        private class AlarmClockItem {

            private int ID;
            private int hour;
            private int minute;
            private String name;
            private boolean flag = true;

            public AlarmClockItem(int ID, String name, int hour, int minute) {
                this.ID = ID;
                this.name = name;
                this.hour = hour;
                this.minute = minute;
            }

            public int getID() {
                return this.ID;
            }

            public void setID(int ID) {
                this.ID = ID;
            }

            public int getHour() {
                return this.hour;
            }

            public void setHour(int hour) {
                this.hour = hour;
            }

            public int getMinute() {
                return this.minute;
            }

            public void setMinute(int minute) {
                this.minute = minute;
            }

            public String getName() {
                return this.name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public boolean getFlag() {
                return this.flag;
            }

            public void setFlag(boolean flag) {
                this.flag = flag;
            }
        }

        private class AlarmClockItemPanel extends JPanel {

            private final int ID;
            private final JLabel nameLabel;
            private final JLabel timeLabel;
            private final JButton deleteButton;
            private final JCheckBox flagCheckBox;
            private final AlarmClockItem alarmClockItem;

            public AlarmClockItemPanel(int ID, AlarmClockItem alarmClockItem) {
                this.setSize(400, 100);
                this.setBorder(BorderFactory.createEtchedBorder());

                this.ID = ID;
                this.alarmClockItem = alarmClockItem;

                this.flagCheckBox = new JCheckBox();
                this.flagCheckBox.setSelected(this.alarmClockItem.flag);
                this.flagCheckBox.addActionListener(new flagCheckBoxListener());
                this.add(this.flagCheckBox);

                this.nameLabel = new JLabel(String.format("Name: %-30s", this.alarmClockItem.name));
                this.add(this.nameLabel);

                this.timeLabel = new JLabel(String.format("Time: %02d:%02d", this.alarmClockItem.hour, this.alarmClockItem.minute));
                this.add(this.timeLabel);

                this.deleteButton = new JButton("Delete");
                this.deleteButton.addActionListener(new deleteButtonListener());
                this.add(this.deleteButton);
            }

            private class flagCheckBoxListener implements ActionListener {

                @Override
                public void actionPerformed(ActionEvent event) {
                    try {
                        alarmClockItemPostgreSQL.updateAlarmClockItem(ID, flagCheckBox.isSelected());
                    } catch (SQLException ex) {
                        System.out.println(ex.toString());
                    }
                }
            }

            private class deleteButtonListener implements ActionListener {

                @Override
                public void actionPerformed(ActionEvent event) {
                    try {
                        alarmClockItemPostgreSQL.deleteAlarmClockItem(ID);
                        resetAlarmClockItemPanel();
                    } catch (SQLException ex) {
                        System.out.println(ex.toString());
                    }
                }
            }
        }

        private class AlarmClockItemThread extends Thread {

            private final String name;

            public AlarmClockItemThread(String name) {
                this.name = name;
            }

            @Override
            public void run() {
                try {
                    AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File("ringing.wav"));
                    Clip clip = AudioSystem.getClip();
                    clip.open(audioInputStream);
                    clip.start();
                    JOptionPane.showMessageDialog(
                            ClockFrame.this,
                            String.format("Alarm Clock %s ringing", this.name),
                            "Alarm Clock",
                            JOptionPane.PLAIN_MESSAGE
                    );
                } catch (IOException | LineUnavailableException | UnsupportedAudioFileException ex) {
                    System.out.println(ex.toString());
                }
            }
        }

        private class AlarmClockItemPostgreSQL {

            private final Connection connection;
            private final String DRIVER = "org.postgresql.Driver";
            private final String URL = "jdbc:postgresql://localhost:5432/AlarmClock";
            private final String USERNAME = "Killua4564";
            private final String PASSWORD = "";

            public AlarmClockItemPostgreSQL() throws ClassNotFoundException, SQLException {
                Class.forName(this.DRIVER);
                this.connection = DriverManager.getConnection(this.URL, this.USERNAME, this.PASSWORD);
            }

            public int insertAlarmClockItem(String name, int hour, int minute) throws SQLException {
                PreparedStatement preparedStatement = connection.prepareStatement(
                        "INSERT INTO \"AlarmClockItem\""
                        + "(\"AlarmClockItemHour\", \"AlarmClockItemMinute\", \"AlarmClockItemName\")"
                        + "VALUES (?, ?, ?);"
                );
                preparedStatement.setInt(1, minute);
                preparedStatement.setInt(2, hour);
                preparedStatement.setString(3, name);
                return preparedStatement.executeUpdate();
            }

            public ArrayList<AlarmClockItem> getAllAlarmClockItem() throws SQLException {
                ArrayList<AlarmClockItem> alarmClockItemList = new ArrayList<>();
                PreparedStatement preparedStatement = this.connection.prepareStatement(
                        "SELECT * FROM \"AlarmClockItem\";"
                );
                ResultSet resultSet = preparedStatement.executeQuery();
                while (resultSet.next()) {
                    alarmClockItemList.add(new AlarmClockItem(
                            resultSet.getInt("AlarmClockItemID"),
                            resultSet.getString("AlarmClockItemName"),
                            resultSet.getInt("AlarmClockItemHour"),
                            resultSet.getInt("AlarmClockItemMinute")
                    ));
                }
                return alarmClockItemList;
            }

            public int updateAlarmClockItem(int ID, boolean flag) throws SQLException {
                PreparedStatement preparedStatement = this.connection.prepareStatement(
                        "UPDATE \"AlarmClockItem\" SET \"AlarmClockItemFlag\" = ?"
                        + "WHERE \"AlarmClockItemID\" = ?;"
                );
                preparedStatement.setBoolean(1, flag);
                preparedStatement.setInt(2, ID);
                return preparedStatement.executeUpdate();
            }

            public int deleteAlarmClockItem(int ID) throws SQLException {
                PreparedStatement preparedStatement = this.connection.prepareStatement(
                        "DELETE FROM \"AlarmClockItem\" WHERE \"AlarmClockItemID\" = ?;"
                );
                preparedStatement.setInt(1, ID);
                return preparedStatement.executeUpdate();
            }
        }

        public ClockFrame() throws ClassNotFoundException, SQLException {
            super("ClockFrame");
            this.setLayout(new GridLayout(2, 1));

            this.menuBar = new JMenuBar();
            this.fileMenu = new JMenu("File");
            this.aboutMenu = new JMenu("About");
            this.addMenuItem = new JMenuItem("Add alarm");
            this.aboutMenuItem = new JMenuItem("About");
            this.exitMenuItem = new JMenuItem("Exit");

            this.fileMenu.setMnemonic('F');
            this.aboutMenu.setMnemonic('A');
            this.addMenuItem.setMnemonic('N');
            this.exitMenuItem.setMnemonic('X');
            this.aboutMenuItem.setMnemonic('A');

            this.addMenuItem.addActionListener(new AddListener());
            this.exitMenuItem.addActionListener(new ExitListener());
            this.aboutMenuItem.addActionListener(new AboutListener());

            this.fileMenu.add(this.addMenuItem);
            this.fileMenu.add(this.exitMenuItem);
            this.aboutMenu.add(this.aboutMenuItem);
            this.menuBar.add(this.fileMenu);
            this.menuBar.add(this.aboutMenu);
            this.setJMenuBar(this.menuBar);

            this.clockPanel = new ClockPanel();
            this.add(this.clockPanel);

            this.alarmClockPanel = new JPanel();
            this.alarmClockPanel.setBorder(BorderFactory.createTitledBorder("Alarm Clock"));
            this.add(this.alarmClockPanel);

            this.alarmClockItemPostgreSQL = new AlarmClockItemPostgreSQL();
            resetAlarmClockItemPanel();
        }

        private void resetAlarmClockItemPanel() throws SQLException {
            ArrayList<AlarmClockItem> alarmClockItems = this.alarmClockItemPostgreSQL.getAllAlarmClockItem();
            for (Component component : this.alarmClockPanel.getComponents()) {
                if (component.getClass().equals(AlarmClockItemPanel.class)) {
                    this.alarmClockPanel.remove(component);
                }
            }
            for (int index = 0; index < alarmClockItems.size(); index++) {
                AlarmClockItem alarmClockItem = alarmClockItems.get(index);
                AlarmClockItemPanel alarmClockItemPanel = new AlarmClockItemPanel(alarmClockItem.getID(), alarmClockItem);
                this.alarmClockPanel.add(alarmClockItemPanel);
            }
            this.alarmClockPanel.revalidate();
            this.alarmClockPanel.repaint();
        }

        private void checkAlarmClockItem() throws SQLException {
            int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
            int minute = Calendar.getInstance().get(Calendar.MINUTE);
            if (Calendar.getInstance().get(Calendar.SECOND) == 0) {
                for (AlarmClockItem alarmClockItem : alarmClockItemPostgreSQL.getAllAlarmClockItem()) {
                    if (alarmClockItem.getFlag() && alarmClockItem.hour == hour && alarmClockItem.minute == minute) {
                        new AlarmClockItemThread(alarmClockItem.name).start();
                    }
                }
            }
        }

        private class AddListener implements ActionListener {

            private AddClockInternalFrame addClockInternalFrame;

            private class AddClockInternalFrame extends JInternalFrame {

                private final JButton confirmButton;
                private final JComboBox hourComboBox;
                private final JComboBox minuteComboBox;
                private final JLabel nameLabel;
                private final JLabel timeLabel;
                private final JLabel colonLabel;
                private final JPanel panel;
                private final JTextField nameTextField;
                private final String hourList[] = {
                    "00", "01", "02", "03", "04", "05", "06", "07", "08", "09",
                    "10", "11", "12", "13", "14", "15", "16", "17", "18", "19",
                    "20", "21", "22", "23"
                };
                private final String minuteList[] = {
                    "00", "01", "02", "03", "04", "05", "06", "07", "08", "09",
                    "10", "11", "12", "13", "14", "15", "16", "17", "18", "19",
                    "20", "21", "22", "23", "24", "25", "26", "27", "28", "29",
                    "30", "31", "32", "33", "34", "35", "36", "37", "38", "39",
                    "40", "41", "42", "43", "44", "45", "46", "47", "48", "49",
                    "50", "51", "52", "53", "54", "55", "56", "57", "58", "59"
                };

                public AddClockInternalFrame() {
                    super("Add Alarm Clock", true, true, true, true);

                    this.panel = new JPanel();

                    this.nameLabel = new JLabel("Name: ");
                    this.panel.add(this.nameLabel);

                    this.nameTextField = new JTextField();
                    this.nameTextField.setDocument(new JTextFieldLimit(15));
                    this.nameTextField.setText("Alarm Message");
                    this.panel.add(this.nameTextField);

                    this.timeLabel = new JLabel("Time: ");
                    this.panel.add(this.timeLabel);

                    this.hourComboBox = new JComboBox(this.hourList);
                    this.panel.add(this.hourComboBox);

                    this.colonLabel = new JLabel(" : ");
                    this.panel.add(this.colonLabel);

                    this.minuteComboBox = new JComboBox(this.minuteList);
                    this.panel.add(this.minuteComboBox);

                    this.confirmButton = new JButton("Confirm");
                    this.confirmButton.addActionListener(new ConfirmButtonListener());
                    this.panel.add(this.confirmButton);

                    this.add(this.panel);
                }

                private class JTextFieldLimit extends PlainDocument {

                    private final int limit;

                    public JTextFieldLimit(int limit) {
                        super();
                        this.limit = limit;
                    }

                    @Override
                    public void insertString(int offset, String string, AttributeSet attr) throws BadLocationException {
                        if (string != null) {
                            if ((this.getLength() + string.length()) <= this.limit) {
                                super.insertString(offset, string, attr);
                            }
                        }
                    }
                }

                private class ConfirmButtonListener implements ActionListener {

                    private int hour;
                    private int minute;
                    private String name;

                    @Override
                    public void actionPerformed(ActionEvent event) {
                        try {
                            this.name = nameTextField.getText();
                            this.hour = Integer.parseInt((String) hourComboBox.getSelectedItem());
                            this.minute = Integer.parseInt((String) minuteComboBox.getSelectedItem());
                            alarmClockItemPostgreSQL.insertAlarmClockItem(this.name, this.hour, this.minute);
                            resetAlarmClockItemPanel();
                            clockPanel.remove(addClockInternalFrame);
                        } catch (SQLException ex) {
                            System.out.println(ex.toString());
                        }
                    }
                }
            }

            @Override
            public void actionPerformed(ActionEvent event) {
                this.addClockInternalFrame = new AddClockInternalFrame();
                this.addClockInternalFrame.setVisible(true);
                this.addClockInternalFrame.pack();
                clockPanel.add(this.addClockInternalFrame);
            }
        }

        private class ExitListener implements ActionListener {

            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        }

        private class AboutListener implements ActionListener {

            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(
                        ClockFrame.this,
                        "Hi, this alarm clock is made by Jellyfish owo.",
                        "About",
                        JOptionPane.PLAIN_MESSAGE
                );
            }
        }
    }

    public static void main(String[] args) throws ClassNotFoundException, SQLException {
        ClockFrame clockFrame = new ClockFrame();
        clockFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        clockFrame.setSize(500, 800);
        clockFrame.setResizable(false);
        clockFrame.setVisible(true);
    }
}