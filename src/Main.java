import com.formdev.flatlaf.FlatLightLaf;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class Main {
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(new FlatLightLaf());
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Book Inventory");
            frame.setSize(900, 500);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setLocationRelativeTo(null);
            frame.setLayout(new BorderLayout());

            JTextField titleField = new JTextField();
                        JTextField authorField = new JTextField();
            JTextField categoryField = new JTextField();
        JTextField quantityField = new JTextField();
            JTextField priceField = new JTextField();
            JTextField isbnField = new JTextField();

            JPanel formPanel = new JPanel(new GridLayout(3, 4, 10, 10));
            formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

            formPanel.add(new JLabel("Title"));
            formPanel.add(new JLabel("Author"));
            formPanel.add(new JLabel("Category"));
        formPanel.add(new JLabel("Quantity"));
            formPanel.add(titleField);
                        formPanel.add(authorField);
        formPanel.add(categoryField);
            formPanel.add(quantityField);
        formPanel.add(new JLabel("Price"));
            formPanel.add(new JLabel("ISBN"));
            formPanel.add(new JLabel(""));
        formPanel.add(new JLabel(""));
            formPanel.add(priceField);
        formPanel.add(isbnField);
            formPanel.add(new JLabel(""));
            formPanel.add(new JLabel(""));

            DefaultTableModel model = new DefaultTableModel(
                    new String[]{"ID", "Title", "Author", "Category", "Quantity", "Price", "ISBN"}, 0
            ) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };

        JTable table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);

            JPanel buttonPanel = new JPanel();
        JButton addButton = new JButton("Add");
            JButton updateButton = new JButton("Update");
                        JButton deleteButton = new JButton("Delete");
            JButton clearButton = new JButton("Clear");

            buttonPanel.add(addButton);
            buttonPanel.add(updateButton);
            buttonPanel.add(deleteButton);
            buttonPanel.add(clearButton);

            frame.add(formPanel, BorderLayout.NORTH);
            frame.add(scrollPane, BorderLayout.CENTER);
            frame.add(buttonPanel, BorderLayout.SOUTH);

            Connection con = null;

            try {
                con = DriverManager.getConnection("jdbc:mysql://localhost:3306/books", "root", "");
            } catch (Exception e) {
                JOptionPane.showMessageDialog(frame, "Database error: " + e.getMessage());
            }

            Connection connection = con;

            model.setRowCount(0);

            try {
                Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery("SELECT * FROM books");

                while (resultSet.next()) {
                    model.addRow(new Object[]{
                            resultSet.getInt("id"),
                            resultSet.getString("title"),
                            resultSet.getString("author"),
                            resultSet.getString("category"),
                            resultSet.getInt("quantity"),
                            resultSet.getDouble("price"),
                            resultSet.getString("isbn")
                    });
                }

                resultSet.close();
                statement.close();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(frame, "Could not load books: " + e.getMessage());
            }

            table.getSelectionModel().addListSelectionListener(e -> {
                int row = table.getSelectedRow();

                if (row != -1) {
                    titleField.setText(String.valueOf(model.getValueAt(row, 1)));
                    authorField.setText(String.valueOf(model.getValueAt(row, 2)));
                    categoryField.setText(String.valueOf(model.getValueAt(row, 3)));
                    quantityField.setText(String.valueOf(model.getValueAt(row, 4)));
                    priceField.setText(String.valueOf(model.getValueAt(row, 5)));
                    isbnField.setText(String.valueOf(model.getValueAt(row, 6)));
                }
            });

            addButton.addActionListener(e -> {
                try {
                    String sql = "INSERT INTO books (title, author, category, quantity, price, isbn) VALUES (?, ?, ?, ?, ?, ?)";
                    PreparedStatement statement = connection.prepareStatement(sql);
                    statement.setString(1, titleField.getText());
                statement.setString(2, authorField.getText());
                     statement.setString(3, categoryField.getText());
                    statement.setInt(4, Integer.parseInt(quantityField.getText()));
                    statement.setDouble(5, Double.parseDouble(priceField.getText()));
                statement.setString(6, isbnField.getText());
                    statement.executeUpdate();
                    statement.close();

                    model.setRowCount(0);

                    Statement reloadStatement = connection.createStatement();
                    ResultSet reloadResult = reloadStatement.executeQuery("SELECT * FROM books");

                    while (reloadResult.next()) {
                        model.addRow(new Object[]{
                                reloadResult.getInt("id"),                                reloadResult.getString("title"),
                        reloadResult.getString("author"),
                                reloadResult.getString("category"),
                                reloadResult.getInt("quantity"),
                                reloadResult.getDouble("price"),
                            reloadResult.getString("isbn")
                        });
                    }

                    reloadResult.close();
                    reloadStatement.close();

                    titleField.setText("");
                    authorField.setText("");
                    categoryField.setText("");
                    quantityField.setText("");
                    priceField.setText("");
                    isbnField.setText("");
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(frame, "Could not add book: " + ex.getMessage());
                }
            });

            updateButton.addActionListener(e -> {
                int row = table.getSelectedRow();

                if (row == -1) {
                    JOptionPane.showMessageDialog(frame, "Select a book first.");
                    return;
                }

                try {
                    int id = Integer.parseInt(String.valueOf(model.getValueAt(row, 0)));
                    String sql = "UPDATE books SET title=?, author=?, category=?, quantity=?, price=?, isbn=? WHERE id=?";
                    PreparedStatement statement = connection.prepareStatement(sql);
                    statement.setString(1, titleField.getText());
                    statement.setString(2, authorField.getText());
                    statement.setString(3, categoryField.getText());
                    statement.setInt(4, Integer.parseInt(quantityField.getText()));
                    statement.setDouble(5, Double.parseDouble(priceField.getText()));
                    statement.setString(6, isbnField.getText());
                    statement.setInt(7, id);
                    statement.executeUpdate();
                    statement.close();

                    model.setRowCount(0);

                    Statement reloadStatement = connection.createStatement();
                    ResultSet reloadResult = reloadStatement.executeQuery("SELECT * FROM books");

                    while (reloadResult.next()) {
                        model.addRow(new Object[]{
                                reloadResult.getInt("id"),
                                reloadResult.getString("title"),
                                reloadResult.getString("author"),
                                reloadResult.getString("category"),
                                reloadResult.getInt("quantity"),
                                reloadResult.getDouble("price"),
                                reloadResult.getString("isbn")
                        });
                    }

                    reloadResult.close();
                    reloadStatement.close();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(frame, "Could not update book: " + ex.getMessage());
                }
            });

            deleteButton.addActionListener(e -> {
                int row = table.getSelectedRow();

                if (row == -1) {
                    JOptionPane.showMessageDialog(frame, "Select a book first.");
                    return;
                }

                try {
                    int id = Integer.parseInt(String.valueOf(model.getValueAt(row, 0)));
                    String sql = "DELETE FROM books WHERE id=?";
                    PreparedStatement statement = connection.prepareStatement(sql);
                    statement.setInt(1, id);
                    statement.executeUpdate();
                    statement.close();

                    model.setRowCount(0);

                    Statement reloadStatement = connection.createStatement();
                    ResultSet reloadResult = reloadStatement.executeQuery("SELECT * FROM books");

                    while (reloadResult.next()) {
                        model.addRow(new Object[]{
                                reloadResult.getInt("id"),
                                reloadResult.getString("title"),
                                reloadResult.getString("author"),
                                reloadResult.getString("category"),
                                reloadResult.getInt("quantity"),
                                reloadResult.getDouble("price"),
                                reloadResult.getString("isbn")
                        });
                    }

                    reloadResult.close();
                    reloadStatement.close();

                    titleField.setText("");
                    authorField.setText("");
                    categoryField.setText("");
                    quantityField.setText("");
                    priceField.setText("");
                    isbnField.setText("");
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(frame, "Could not delete book: " + ex.getMessage());
                }
            });

            clearButton.addActionListener(e -> {
                titleField.setText("");
                authorField.setText("");
                categoryField.setText("");
                quantityField.setText("");
                priceField.setText("");
                isbnField.setText("");
                table.clearSelection();
            });

            frame.setVisible(true);
        });
    }
}
