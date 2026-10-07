import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

// ==========================================
// 1. DATA STRUCTURE: CUSTOM HASH MAP
// ==========================================
class HashEntry {
    String key;
    double value;
    HashEntry next;

    public HashEntry(String key, double value) {
        this.key = key;
        this.value = value;
        this.next = null;
    }
}

class CustomHashMap {
    private HashEntry[] table;
    private int capacity;

    public CustomHashMap(int capacity) {
        this.capacity = capacity;
        this.table = new HashEntry[capacity];
    }

    private int hash(String key) {
        int code = 0;
        for (int i = 0; i < key.length(); i++) {
            code += key.charAt(i);
        }
        return Math.abs(code) % capacity;
    }

    public void put(String key, double value) {
        int idx = hash(key);
        HashEntry head = table[idx];
        while (head != null) {
            if (head.key.equals(key)) {
                head.value = value;
                return;
            }
            head = head.next;
        }
        HashEntry entry = new HashEntry(key, value);
        entry.next = table[idx];
        table[idx] = entry;
    }

    public double get(String key) {
        int idx = hash(key);
        HashEntry head = table[idx];
        while (head != null) {
            if (head.key.equals(key)) return head.value;
            head = head.next;
        }
        return 0.0;
    }
}

// ==========================================
// 2. DATA STRUCTURE: SINGLY LINKED LIST
// ==========================================
class LogNode {
    int id;
    String timestamp;
    double electricity;
    double fuel;
    double transport;
    double total;
    String hotspot;
    LogNode next;

    public LogNode(int id, String timestamp, double e, double f, double t, double total, String hotspot) {
        this.id = id;
        this.timestamp = timestamp;
        this.electricity = e;
        this.fuel = f;
        this.transport = t;
        this.total = total;
        this.hotspot = hotspot;
        this.next = null;
    }
}

class HistoryList {
    LogNode head;
    int count;

    public HistoryList() {
        this.head = null;
        this.count = 0;
    }

    public void insertAtHead(double e, double f, double t, double total, String hotspot) {
        count++;
        String time = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
        LogNode node = new LogNode(count, time, e, f, t, total, hotspot);
        node.next = head;
        head = node;
    }

    public void clear() {
        head = null;
        count = 0;
    }
}

// ==========================================
// 3. FRONT END: JAVA SWING USER INTERFACE
// ==========================================
public class CarbonCalculatorGUI extends JFrame {
    private JTextField txtElectricity, txtFuel, txtTransport;
    private JLabel lblTotal, lblHotspot;
    private DefaultTableModel tableModel;
    private CustomHashMap factorMap;
    private HistoryList historyList;

    public CarbonCalculatorGUI() {
        // Initialize DSA Engine
        factorMap = new CustomHashMap(10);
        factorMap.put("electricity", 0.82);
        factorMap.put("fuel", 2.31);
        factorMap.put("transport", 0.17);

        historyList = new HistoryList();

        // Window Setup
        setTitle("Carbon Footprint Calculator - DSAA Mini Project (UN SDG 13)");
        setSize(780, 560);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        // Top Panel: Header
        JPanel topPanel = new JPanel(new GridLayout(2, 1));
        topPanel.setBackground(new Color(24, 76, 120));
        JLabel titleLabel = new JLabel("Carbon Footprint Calculator", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setForeground(Color.WHITE);
        JLabel subtitleLabel = new JLabel("UN SDG 13: Climate Action | DSA Hash Table & Linked List Engine", JLabel.CENTER);
        subtitleLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        subtitleLabel.setForeground(new Color(220, 235, 252));
        topPanel.add(titleLabel);
        topPanel.add(subtitleLabel);
        add(topPanel, BorderLayout.NORTH);

        // Center Panel: Form + Output + Table
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setBorder(BorderFactory.createEmptyBorder(12, 16, 12, 16));

        // Input Form
        JPanel formPanel = new JPanel(new GridLayout(3, 2, 8, 8));
        formPanel.setBorder(BorderFactory.createTitledBorder("Input Activity Metrics"));
        formPanel.add(new JLabel("Electricity Usage (kWh/month):"));
        txtElectricity = new JTextField();
        formPanel.add(txtElectricity);

        formPanel.add(new JLabel("Fuel Consumed (Petrol/Diesel Liters):"));
        txtFuel = new JTextField();
        formPanel.add(txtFuel);

        formPanel.add(new JLabel("Daily Commute Distance (km):"));
        txtTransport = new JTextField();
        formPanel.add(txtTransport);
        centerPanel.add(formPanel);

        // Buttons Panel
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        JButton btnCalculate = new JButton("Calculate & Prepend to List");
        btnCalculate.setBackground(new Color(34, 139, 34));
        btnCalculate.setForeground(Color.WHITE);

        JButton btnReset = new JButton("Reset Inputs");
        JButton btnClear = new JButton("Clear History");

        btnPanel.add(btnCalculate);
        btnPanel.add(btnReset);
        btnPanel.add(btnClear);
        centerPanel.add(btnPanel);

        // Output Labels
        JPanel outputPanel = new JPanel(new GridLayout(2, 1, 4, 4));
        outputPanel.setBorder(BorderFactory.createTitledBorder("Computation Summary"));
        lblTotal = new JLabel("Total Footprint: -- kg CO2e / month");
        lblTotal.setFont(new Font("Arial", Font.BOLD, 14));
        lblTotal.setForeground(new Color(18, 102, 38));

        lblHotspot = new JLabel("Primary Hotspot: --");
        lblHotspot.setFont(new Font("Arial", Font.PLAIN, 13));
        lblHotspot.setForeground(new Color(178, 34, 34));

        outputPanel.add(lblTotal);
        outputPanel.add(lblHotspot);
        centerPanel.add(outputPanel);

        // Table Visualization for Linked List
        centerPanel.add(Box.createVerticalStrut(10));
        JLabel tableTitle = new JLabel("Singly Linked List Traversal (Node Head = Top Record):");
        tableTitle.setFont(new Font("Arial", Font.BOLD, 12));
        centerPanel.add(tableTitle);

        String[] columns = {"Node ID", "Time", "Elec (kWh)", "Fuel (L)", "Commute (km)", "Total (kg CO2e)", "Primary Hotspot"};
        tableModel = new DefaultTableModel(columns, 0);
        JTable historyTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(historyTable);
        scrollPane.setPreferredSize(new Dimension(740, 160));
        centerPanel.add(scrollPane);

        add(centerPanel, BorderLayout.CENTER);

        // Event Handling
        btnCalculate.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                calculateAndRecord();
            }
        });

        btnReset.addActionListener(e -> {
            txtElectricity.setText("");
            txtFuel.setText("");
            txtTransport.setText("");
            lblTotal.setText("Total Footprint: -- kg CO2e / month");
            lblHotspot.setText("Primary Hotspot: --");
        });

        btnClear.addActionListener(e -> {
            historyList.clear();
            tableModel.setRowCount(0);
            lblTotal.setText("Total Footprint: -- kg CO2e / month");
            lblHotspot.setText("Primary Hotspot: --");
            JOptionPane.showMessageDialog(null, "Linked List history successfully purged.");
        });
    }

    private void calculateAndRecord() {
        try {
            double elec = txtElectricity.getText().trim().isEmpty() ? 0.0 : Double.parseDouble(txtElectricity.getText());
            double fuel = txtFuel.getText().trim().isEmpty() ? 0.0 : Double.parseDouble(txtFuel.getText());
            double trans = txtTransport.getText().trim().isEmpty() ? 0.0 : Double.parseDouble(txtTransport.getText());

            if (elec <= 0 && fuel <= 0 && trans <= 0) {
                JOptionPane.showMessageDialog(this, "Please enter at least one value greater than zero.", "Input Warning", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // O(1) Factor Lookup from Hash Map
            double elecCO2 = elec * factorMap.get("electricity");
            double fuelCO2 = fuel * factorMap.get("fuel");
            double transCO2 = trans * 30 * factorMap.get("transport"); // monthly projection

            double totalCO2 = elecCO2 + fuelCO2 + transCO2;

            // Hotspot detection
            String hotspot = "Electricity";
            double max = elecCO2;
            if (fuelCO2 > max) { max = fuelCO2; hotspot = "Fuel"; }
            if (transCO2 > max) { max = transCO2; hotspot = "Transport"; }

            // O(1) Prepend to Linked List
            historyList.insertAtHead(elec, fuel, trans, totalCO2, hotspot);

            // Update UI Labels
            lblTotal.setText(String.format("Total Footprint: %.2f kg CO2e / month", totalCO2));
            lblHotspot.setText(String.format("Primary Hotspot: %s (%.2f kg CO2e)", hotspot, max));

            // Refresh Table via O(N) Linked List Traversal
            refreshTable();

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Invalid entry. Enter numeric digits only.", "Format Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void refreshTable() {
        tableModel.setRowCount(0);
        LogNode curr = historyList.head;
        while (curr != null) {
            tableModel.addRow(new Object[]{
                    "#" + curr.id,
                    curr.timestamp,
                    curr.electricity,
                    curr.fuel,
                    curr.transport,
                    String.format("%.2f", curr.total),
                    curr.hotspot
            });
            curr = curr.next;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new CarbonCalculatorGUI().setVisible(true));
    }
}