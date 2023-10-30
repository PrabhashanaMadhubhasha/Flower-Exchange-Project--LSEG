/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package codes;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Date;
import java.text.SimpleDateFormat;
public class process{
public static class Order {
    String orderID;
    String clOrdID;
    String instrument;
    int quantity;
    int side;
    double price;
}

public static class ExecutionRow {
    String clOrdID;
    String orderID;
    String instrument;
    int side;
    double price;
    int quantity;
    String status;
    String reason;
    String transactionTime;
}


    public static List<Order> readCSVFile(String filename) {
        List<Order> rows = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            br.readLine(); // Read and discard the header line
            int rowIndex = 0;
            while ((line = br.readLine()) != null) {
                String[] fields = line.split(",");
                Order row = new Order();
                int Index = rowIndex + 1;
                row.orderID = "ORD23" + Index;
                row.clOrdID = fields[0];
                row.instrument = fields[1];
                row.side = Integer.parseInt(fields[2]);
                row.quantity = Integer.parseInt(fields[3]);
                row.price = Double.parseDouble(fields[4]);
                rows.add(row);
                rowIndex++;
            }
        } catch (IOException e) {
            System.err.println("Error: Could not open the file " + filename);
        }
        return rows;
    }



    public static void compareBuyOrders(List<Order> buyTable) {
        Collections.sort(buyTable, (a, b) -> {
            if (a.price != b.price) {
                return Double.compare(b.price, a.price);
            } else {
                return a.orderID.compareTo(b.orderID);
            }
        });
    }

    public static void compareSellOrders(List<Order> sellTable) {
        Collections.sort(sellTable, (a, b) -> {
            if (a.price != b.price) {
                return Double.compare(a.price, b.price);
            } else {
                return a.orderID.compareTo(b.orderID);
            }
        });
    }

    public static void insertOrder(List<Order> buyTable, List<Order> sellTable, Order csvRow) {
    switch (csvRow.side) {
        case 1 -> {
            buyTable.add(csvRow);
            compareBuyOrders(buyTable);
        }
        case 2 -> {
            sellTable.add(csvRow);
            compareSellOrders(sellTable);
        }
        default -> System.err.println("Invalid side value: " + csvRow.side);
    }
    }

    public static void deleteAndRearrangeFirstRow(List<Order> table) {
        if (!table.isEmpty()) {
            table.remove(0);
        }
    }

    public static String measureTransactionTime() {
        Date now = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd-HHmmss.SSS");
        return dateFormat.format(now);
    }

    public static void insertExecutionRow(List<ExecutionRow> executionTable, Order order, String status, String reason, String transactionTime) {
        ExecutionRow executionRow = new ExecutionRow();
        executionRow.clOrdID = order.clOrdID;
        executionRow.orderID = order.orderID;
        executionRow.instrument = order.instrument;
        executionRow.side = order.side;
        executionRow.price = order.price;
        executionRow.quantity = order.quantity;
        executionRow.status = status;
        executionRow.reason = reason;
        executionRow.transactionTime = transactionTime;
        executionTable.add(executionRow);
    }

    public static void writeExecutionToCSV(List<ExecutionRow> executionTable, String filepath) {
        try (FileWriter file = new FileWriter(filepath)) {
            file.write("clOrdID,orderID,instrument,side,price,quantity,Status,Reason,Transaction Time\n");
            for (ExecutionRow executionRow : executionTable) {
                file.write(executionRow.orderID + "," + executionRow.clOrdID + ","
                        + executionRow.instrument + "," + executionRow.side + ","
                        + executionRow.price + "," + executionRow.quantity + ","
                        + executionRow.status + "," + executionRow.reason + ","
                        + executionRow.transactionTime + "\n");
            }
        } catch (IOException e) {
            System.err.println("Error: Could not open the file " + filepath);
        }
    }

    public static void exchanger() {
        List<Order> buyTable = new ArrayList<>();
        List<Order> sellTable = new ArrayList<>();

        List<Order> data = readCSVFile("C:\\Users\\MSI\\Documents\\NetBeansProjects\\project1\\src\\codes\\orders.csv");
        List<ExecutionRow> executionTable = new ArrayList<>();
        int rowIndex = 0;

        while (rowIndex < data.size()) {
            boolean x = false;
            String reason = "";
            Order selectedRow = data.get(rowIndex);

            if (selectedRow.quantity > 1000 || selectedRow.quantity < 10) {
                reason = "Invalid Quantity";
                x = true;
            } else if (selectedRow.quantity % 10 != 0) {
                reason = "Invalid Quantity";
                x = true;
            } else if (!(selectedRow.price > 0)) {
                reason = "Invalid Price";
                x = true;
            } else if (!("Rose".equals(selectedRow.instrument) || "Lavender".equals(selectedRow.instrument) ||
                    "Tulip".equals(selectedRow.instrument) || "Orchid".equals(selectedRow.instrument) ||
                    "Lotus".equals(selectedRow.instrument))) {
                reason = "Invalid Instrument";
                x = true;
            } else if (selectedRow.side != 1 && selectedRow.side != 2) {
                reason = "Invalid Side";
                x = true;
            }

            if (x) {
                String transactionTime = measureTransactionTime();
                insertExecutionRow(executionTable, selectedRow, "Reject", reason, transactionTime);
            } else if (selectedRow.side == 1) {
                if (!sellTable.isEmpty() && selectedRow.price >= sellTable.get(0).price) {
                    int xValue = 1;
                    while (!sellTable.isEmpty() && xValue != 0 && selectedRow.price >= sellTable.get(0).price) {
                        int Quantity = selectedRow.quantity;
                        if (Quantity > sellTable.get(0).quantity) {
                            xValue = 1;
                            int newQuantity = selectedRow.quantity - sellTable.get(0).quantity;
                            int price = (int) selectedRow.price;
                            selectedRow.quantity = sellTable.get(0).quantity;
                            selectedRow.price = sellTable.get(0).price;
                            String transactionTime = measureTransactionTime();
                            insertExecutionRow(executionTable, selectedRow, "PFILL", "None", transactionTime);
                            String transactionTime1 = measureTransactionTime();
                            insertExecutionRow(executionTable, sellTable.get(0), "FILL", "None", transactionTime1);
                            deleteAndRearrangeFirstRow(sellTable);
                            selectedRow.price = price;
                            selectedRow.quantity = newQuantity;
                        } else if (Quantity == sellTable.get(0).quantity) {
                            xValue = 0;
                            selectedRow.price = sellTable.get(0).price;
                            String transactionTime2 = measureTransactionTime();
                            insertExecutionRow(executionTable, selectedRow, "FILL", "None", transactionTime2);
                            String transactionTime3 = measureTransactionTime();
                            insertExecutionRow(executionTable, sellTable.get(0), "FILL", "None", transactionTime3);
                            deleteAndRearrangeFirstRow(sellTable);
                        } else {
                            xValue = 0;
                            int newQuantity = sellTable.get(0).quantity - selectedRow.quantity;
                            int price = (int) selectedRow.price;
                            selectedRow.quantity = sellTable.get(0).quantity;
                            selectedRow.price = sellTable.get(0).price;
                            String transactionTime4 = measureTransactionTime();
                            insertExecutionRow(executionTable, selectedRow, "FILL", "None", transactionTime4);
                            sellTable.get(0).quantity = selectedRow.quantity;
                            String transactionTime5 = measureTransactionTime();
                            insertExecutionRow(executionTable, sellTable.get(0), "PFILL", "None", transactionTime5);
                            deleteAndRearrangeFirstRow(sellTable);
                            selectedRow.price = price;
                            sellTable.get(0).quantity = newQuantity;
                            insertOrder(buyTable, sellTable, sellTable.get(0));
                        }
                    }
                    if (xValue == 1) {
                        insertOrder(buyTable, sellTable, selectedRow);
                    }
                } else {
                    insertOrder(buyTable, sellTable, selectedRow);
                    String transactionTime6 = measureTransactionTime();
                    insertExecutionRow(executionTable, selectedRow, "NEW", "None", transactionTime6);
                }
            } else {
                if (!buyTable.isEmpty() && selectedRow.price <= buyTable.get(0).price) {
                    int xValue = 1;
                    while (!buyTable.isEmpty() && xValue != 0 && selectedRow.price <= buyTable.get(0).price) {
                        int Quantity = selectedRow.quantity;
                        if (Quantity > buyTable.get(0).quantity) {
                            xValue = 1;
                            int newQuantity = selectedRow.quantity - buyTable.get(0).quantity;
                            int price = (int) selectedRow.price;
                            selectedRow.quantity = buyTable.get(0).quantity;
                            selectedRow.price = buyTable.get(0).price;
                            String transactionTime = measureTransactionTime();
                            insertExecutionRow(executionTable, selectedRow, "PFILL", "None", transactionTime);
                            String transactionTime1 = measureTransactionTime();
                            insertExecutionRow(executionTable, buyTable.get(0), "FILL", "None", transactionTime1);
                            deleteAndRearrangeFirstRow(buyTable);
                            selectedRow.price = price;
                            selectedRow.quantity = newQuantity;
                        } else if (Quantity == buyTable.get(0).quantity) {
                            xValue = 0;
                            selectedRow.price = buyTable.get(0).price;
                            String transactionTime2 = measureTransactionTime();
                            insertExecutionRow(executionTable, selectedRow, "FILL", "None", transactionTime2);
                            String transactionTime3 = measureTransactionTime();
                            insertExecutionRow(executionTable, buyTable.get(0), "FILL", "None", transactionTime3);
                            deleteAndRearrangeFirstRow(buyTable);
                        } else {
                            xValue = 0;
                            int newQuantity = buyTable.get(0).quantity - selectedRow.quantity;
                            int price = (int) selectedRow.price;
                            selectedRow.quantity = buyTable.get(0).quantity;
                            selectedRow.price = buyTable.get(0).price;
                            String transactionTime4 = measureTransactionTime();
                            insertExecutionRow(executionTable, selectedRow, "FILL", "None", transactionTime4);
                            buyTable.get(0).quantity = selectedRow.quantity;
                            String transactionTime5 = measureTransactionTime();
                            insertExecutionRow(executionTable, buyTable.get(0), "PFILL", "None", transactionTime5);
                            deleteAndRearrangeFirstRow(buyTable);
                            selectedRow.price = price;
                            buyTable.get(0).quantity = newQuantity;
                            insertOrder(buyTable, sellTable, buyTable.get(0));
                        }
                    }
                    if (xValue == 1) {
                        insertOrder(buyTable, sellTable, selectedRow);
                    }
                } else {
                    insertOrder(buyTable, sellTable, selectedRow);
                    String transactionTime6 = measureTransactionTime();
                    insertExecutionRow(executionTable, selectedRow, "NEW", "None", transactionTime6);
                }
            }
            rowIndex++;
        }
        writeExecutionToCSV(executionTable, "C:\\Users\\MSI\\Documents\\NetBeansProjects\\project1\\src\\codes\\exchanged.csv");
    }
//    public static void main(String[] args) {
//    main1();
//    }
}

