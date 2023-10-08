#include <iostream>
#include <fstream>
#include <sstream>
#include <vector>
#include <string>

#include <algorithm>

#include <chrono>

std::vector<Order> buyTable; // Assume this is your buy table
std::vector<Order> sellTable; // Assume this is your sell table
std::vector<ExecutionRow> executionTable; // Assume this is your execution table

struct Order {
    std::string orderID; // New field for orderID
    std::string clOrdID;
    std::string instrument;
    int side;
    int quantity;
    double price;
};
struct Order {
    std::string orderID;
    std::string clOrdID;
    std::string instrument;
    int side;
    int quantity;
    double price;

};

std::vector<Order> readCSVFile(const std::string& filename) {
    std::vector<Order> rows;
    std::ifstream file(filename);

    if (!file.is_open()) {
        std::cerr << "Error: Could not open the file " << filename << std::endl;
        return rows;
    }

    std::string line;
    std::getline(file, line); // Read and discard the header line

    int rowIndex = 0; // Initialize the row index

    while (std::getline(file, line)) {
        std::istringstream iss(line);
        Order row;
        std::string field;

        // Calculate the orderID based on the row index
        row.orderID = "ORD23" + std::to_string(rowIndex + 1);

        if (std::getline(iss, field, ',')) {
            row.clOrdID = field;
        }
        if (std::getline(iss, field, ',')) {
            row.instrument = field;
        }
        if (std::getline(iss, field, ',')) {
            row.side = std::stoi(field);
        }
        if (std::getline(iss, field, ',')) {
            row.quantity = std::stoi(field);
        }
        if (std::getline(iss, field, ',')) {
            row.price = std::stod(field);
        }

        rows.push_back(row);
        rowIndex++; // Increment the row index
    }

    file.close();
    return rows;
}
//--------------------------------------------------------------------//




// Custom comparison function for sorting buy orders
bool CompareBuyOrders(const Order& a, const Order& b) {
    if (a.price != b.price) {
        return a.price > b.price; // Ascending order by price
    } else {
        return a.clOrdID < b.clOrdID; // Time priority for orders with the same price
    }
}

// Custom comparison function for sorting sell orders
bool CompareSellOrders(const Order& a, const Order& b) {
    if (a.price != b.price) {
        return a.price < b.price; // Descending order by price
    } else {
        return a.clOrdID < b.clOrdID; // Time priority for orders with the same price
    }
}

// Function to insert an order into the appropriate table based on the "side" field
// void InsertOrder(std::vector<Order>& buyTable, std::vector<Order>& sellTable, const Order& csvRow) {
//     Order order;
//     order.orderID = csvRow.orderID;
//     order.clOrdID = csvRow.clOrdID;
//     order.instrument = csvRow.instrument;
//     order.side = csvRow.side;
//     order.quantity = csvRow.quantity;
//     order.price = csvRow.price;

//     if (csvRow.side == 1) {
//         // Insert into the buy table and sort it
//         buyTable.push_back(order);
//         std::sort(buyTable.begin(), buyTable.end(), CompareBuyOrders);
//     } else if (csvRow.side == 2) {
//         // Insert into the sell table and sort it
//         sellTable.push_back(order);
//         std::sort(sellTable.begin(), sellTable.end(), CompareSellOrders);
//     } else {
//         std::cerr << "Invalid side value: " << csvRow.side << std::endl;
//     }
// }

void InsertOrder(std::vector<Order>& buyTable, std::vector<Order>& sellTable, const Order& csvRow) {
    if (csvRow.side == 1) {
        // Insert into the buy table
        buyTable.push_back(csvRow);
        // Sort the buyTable
        std::sort(buyTable.begin(), buyTable.end(), CompareBuyOrders);
    } else if (csvRow.side == 2) {
        // Insert into the sell table
        sellTable.push_back(csvRow);
        // Sort the sellTable
        std::sort(sellTable.begin(), sellTable.end(), CompareSellOrders);
    } else {
        std::cerr << "Invalid side value: " << csvRow.side << std::endl;
    }
}



//.....................................................................//
struct ExecutionRow {
    std::string clOrdID;
    std::string orderID;
    std::string instrument;
    int side;
    double price;
    int quantity;
    std::string status;
    std::string reason;
    std::string transactionTime;
};

void insertExecutionRow(std::vector<ExecutionRow>& executionTable, const Order& Order, const std::string& status, const std::string& reason, const std::string& transactionTime) {
    ExecutionRow executionRow;
    executionRow.clOrdID = Order.clOrdID;
    executionRow.orderID = "ORD23" + std::to_string(executionTable.size() + 1); // Generate orderID
    executionRow.instrument = Order.instrument;
    executionRow.side = Order.side;
    executionRow.price = Order.price;
    executionRow.quantity = Order.quantity;
    executionRow.status = status;
    executionRow.reason = reason;
    executionRow.transactionTime = transactionTime;

    executionTable.push_back(executionRow);
}
//......................................................................//
// Function to delete the first row from a table and rearrange the remaining rows
void DeleteAndRearrangeFirstRow(std::vector<Order>& table) {
    if (!table.empty()) {
        table.erase(table.begin()); // Delete the first row

        // // No need to rearrange if there is only one row left
        // if (table.size() > 1) {
        //     // Sort the table based on the order type (buy or sell)
        //     if (table[0].price > table[1].price) {
        //         std::sort(table.begin(), table.end(), CompareSellOrders);
        //     } else {
        //         std::sort(table.begin(), table.end(), CompareBuyOrders);
        //     }
        // }
    }
}
//.........................................................................//
// Function to change the quantity size of a selected row
// void ChangeQuantity(Order& selectedRow, int newQuantity) {
//     selectedRow.setQuantity(newQuantity);
// }
//...........................................................//

// Function to write the Execution table to a CSV file
void WriteExecutionToCSV(const std::vector<ExecutionRow>& executionTable, const std::string& filename) {
    std::ofstream file(filename);

    if (!file.is_open()) {
        std::cerr << "Error: Could not open the file " << filename << std::endl;
        return;
    }

    // Write the header row
    file << "clOrdID,orderID,instrument,side,price,quantity,Status,Reason,Transaction Time" << std::endl;

    // Write each row of the Execution table
    for (const auto& executionRow : executionTable) {
        file << executionRow.clOrdID << "," << executionRow.orderID << ","
             << executionRow.instrument << "," << executionRow.side << ","
             << executionRow.price << "," << executionRow.quantity << ","
             << executionRow.status << "," << executionRow.reason << ","
             << executionRow.transactionTime << std::endl;
    }

    file.close();
}
//...........................................................//
// Define a custom function to measure transaction time
void measureTransactionTime() {
    // Record the start time
    auto start = std::chrono::high_resolution_clock::now();

    // Place your code or operations here
    // ...

    // Record the end time
    auto end = std::chrono::high_resolution_clock::now();

    // Calculate the duration in microseconds
    auto duration = std::chrono::duration_cast<std::chrono::microseconds>(end - start);

    // Print the transaction time in microseconds
    std::cout << "Transaction time: " << duration.count() << " microseconds" << std::endl;
}
//....................................................................//


// Define your pre-existing functions here:
// measureTransactionTime()
// insertExecutionRow()
// InsertOrder()
// DeleteAndRearrangeFirstRow()


//.....................................................///


int main() {
    std::vector<Order> data = readCSVFile("order5.csv");

    
    std::vector<Order> buyTable;
    std::vector<Order> sellTable;
    int rowIndex = 0; // Change this to the desired row index (0-based)


    //..............................................................//

    

    // // Example CSVRow objects (you can replace these with your data)
    // // CSVRow row1 = {"A123", "XYZ", 1, 100, 50.0};
    // // CSVRow row2 = {"B456", "ABC", 2, 200, 55.0};
    // // CSVRow row3 = {"C789", "XYZ", 1, 150, 55.0};
    // // CSVRow row4 = {"D012", "ABC", 2, 300, 48.0};
    // CSVRow selectedRow = data[rowIndex];
    // // Insert CSVRows into the appropriate tables
    // InsertOrder(buyTable, sellTable, selectedRow);
    // // InsertOrder(buyTable, sellTable, row2);
    // // InsertOrder(buyTable, sellTable, row3);
    // // InsertOrder(buyTable, sellTable, row4);

    // // Print the buy and sell tables
    // std::cout << "Buy Table:" << std::endl;
    // for (const auto& order : buyTable) {
    //     std::cout << "Row " << rowIndex << " Data: " << order.orderID << ", " << order.clOrdID << ", " << order.instrument << ", " << order.side << ", " << order.quantity << ", " << order.price << std::endl;

    // }

    // std::cout << "\nSell Table:" << std::endl;
    // for (const auto& order : sellTable) {
    //     std::cout << order.clOrdID << ", " << order.instrument << ", " << order.quantity << ", " << order.price << std::endl;
    // }
    // std::vector<ExecutionRow> executionTable;
    // insertExecutionRow(executionTable, selectedRow, "Executed", "Fulfilled", "2023-10-08 15:30:00");
    //.................................................................//
    // Example buy and sell tables (you can replace these with your data)
    // std::vector<Order> buyTable = {
    //     {"ORD231", "aa13", "Rose", 1, 100, 55},
    //     {"ORD231", "aa13", "Rose", 1, 100, 59},
    //     {"ORD231", "aa13", "Rose", 1, 100, 53}
    // };

    // std::vector<Order> sellTable = {
    //     {"ORD231", "aa13", "Rose", 1, 100, 55},
    //     {"ORD231", "aa13", "Rose", 1, 100, 55},
    //     {"ORD231", "aa13", "Rose", 1, 100, 55}
    // };

    // // Delete and rearrange the first row of the buy table
    // DeleteAndRearrangeFirstRow(buyTable);

    // // Delete and rearrange the first row of the sell table
    // DeleteAndRearrangeFirstRow(sellTable);

    // // Print the updated buy and sell tables
    // std::cout << "Buy Table:" << std::endl;
    // for (const auto& order : buyTable) {
    //     std::cout << order.clOrdID << ", " << order.instrument << ", " << order.quantity << ", " << order.price << std::endl;
    // }

    // int rowIndex1 = 0; // Change this to the desired row index (0-based)
    
    // if (rowIndex1 >= 0 && rowIndex1 < buyTable.size()) {
    //     Order buyTable1 = buyTable[rowIndex1];
    //     std::cout << "Row " << rowIndex1 << " Data: " << buyTable1.orderID << ", " << buyTable1.clOrdID << ", " << buyTable1.instrument << ", " << buyTable1.side << ", " << buyTable1.quantity << ", " << buyTable1.price << std::endl;
    // }
    // std::cout << "\nSell Table:" << std::endl;
    // for (const auto& order : sellTable) {
    //     std::cout << order.clOrdID << ", " << order.instrument << ", " << order.quantity << ", " << order.price << std::endl;
    // }
    //..............................................................//

    int rowindex = 0;
    while (rowindex < data.size()) {
        bool x = false;
    std::string reason;
    Order selectedRow = data[rowIndex];
    std::vector<ExecutionRow> executionTable;
 

    if (selectedRow.quantity > 1000 || selectedRow.quantity < 10 || selectedRow.quantity % 10 != 0 ||
        !(selectedRow.price > 0) || (selectedRow.instrument != "Rose" && selectedRow.instrument != "Lavender" &&
                                       selectedRow.instrument != "Tulip" && selectedRow.instrument != "Orchid" &&
                                       selectedRow.instrument != "Lotus") ||
        (selectedRow.side != 1 && selectedRow.side != 2)) {
        reason = "Invalid Quantity";
        x = true;
    }

    if (x) {
        std::string transactionTime = measureTransactionTime(); // Assuming measureTransactionTime function is defined
        // ExecutionRow executionRow;
        // executionRow.clOrdID = selectedRow.clOrdID;
        // executionRow.orderID = "ORD23" + std::to_string(executionTable.size() + 1); // Generate orderID
        // executionRow.instrument = selectedRow.instrument;
        // executionRow.side = selectedRow.side;
        // executionRow.price = selectedRow.price;
        // executionRow.quantity = selectedRow.quantity;
        // executionRow.status = "Reject";
        // executionRow.reason = reason;
        // executionRow.transactionTime = transactionTime;

        insertExecutionRow(executionTable, selectedRow, "Reject", reason, transactionTime); // Assuming insertExecutionRow function is defined
    } else if (selectedRow.side == 1) {
        int x=1;
        while (!sellTable.empty() && x != 0 && selectedRow.price >= sellTable.front().price) {
            int Quantity = selectedRow.quantity - sellTable.front().quantity;

            if (Quantity > sellTable.front().quantity) {
                x = 1;
                int newQuantity = selectedRow.quantity - sellTable.front().quantity;
                selectedRow.quantity=sellTable.front().quantity;
                std::string transactionTime = measureTransactionTime(); // Assuming measureTransactionTime function is defined
                
                //ChangeQuantity(selectedRow, sellTable.front().quantity); // Change quantity of selectedRow
                // ExecutionRow executionRowPFILL = selectedRow;
                // executionRowPFILL.status = "PFILL";
                // executionRowPFILL.reason = "None";
                // executionRowPFILL.transactionTime = transactionTime;

                // insertExecutionRow(executionTable, executionRowPFILL); // Insert the "PFILL" row
                insertExecutionRow(executionTable, selectedRow, "PFILL", "None", transactionTime);
                std::string transactionTime = measureTransactionTime();
                insertExecutionRow(executionTable, sellTable.front(), "FILL", "None", transactionTime);

                // ExecutionRow executionRowFILL = sellTable.front();
                // executionRowFILL.status = "FILL";
                // executionRowFILL.reason = "None";
                // executionRowFILL.transactionTime = transactionTime;

                // insertExecutionRow(executionTable, executionRowFILL); // Insert the "FILL" row
                // insertExecutionRow(executionTable, selectedRow, "PFILL", "None", transactionTime);

                DeleteAndRearrangeFirstRow(sellTable); // Delete the first row of sell table
                selectedRow.quantity=newQuantity;
            } else if (Quantity == sellTable.front().quantity) {
                x = 0;
                std::string transactionTime = measureTransactionTime(); // Assuming measureTransactionTime function is defined
                insertExecutionRow(executionTable, selectedRow, "FILL", "None", transactionTime);

                // ExecutionRow executionRowFILL = selectedRow;
                // executionRowFILL.status = "FILL";
                // executionRowFILL.reason = "None";
                // executionRowFILL.transactionTime = transactionTime;

                // insertExecutionRow(executionTable, executionRowFILL); // Insert the "FILL" row

                // ExecutionRow executionRowFILL2 = sellTable.front();
                // executionRowFILL2.status = "FILL";
                // executionRowFILL2.reason = "None";
                // executionRowFILL2.transactionTime = transactionTime;
                std::string transactionTime = measureTransactionTime(); // Assuming measureTransactionTime function is defined
                insertExecutionRow(executionTable, sellTable.front(), "FILL", "None", transactionTime);

                // insertExecutionRow(executionTable, executionRowFILL2); // Insert the second "FILL" row

                DeleteAndRearrangeFirstRow(sellTable); // Delete the first row of sell table
            } else {
                x = 0;
                int newQuantity = sellTable.front().quantity - selectedRow.quantity;
                selectedRow.quantity=sellTable.front().quantity;
                std::string transactionTime = measureTransactionTime(); // Assuming measureTransactionTime function is defined

                // ChangeQuantity(selectedRow, sellTable.front().quantity); // Change quantity of selectedRow
                // ExecutionRow executionRowFILL = selectedRow;
                // executionRowFILL.status = "FILL";
                // executionRowFILL.reason = "None";
                // executionRowFILL.transactionTime = transactionTime;
                insertExecutionRow(executionTable, selectedRow, "FILL", "None", transactionTime);
                
                sellTable.front().quantity=selectedRow.quantity;
                std::string transactionTime = measureTransactionTime(); // Assuming measureTransactionTime function is defined
                insertExecutionRow(executionTable, sellTable.front(), "PFILL", "None", transactionTime);

                // insertExecutionRow(executionTable, executionRowFILL); // Insert the "FILL" row

                // sellTable.front().setQuantity(newQuantity); // Change quantity of first row of sell table
                // ExecutionRow executionRowPFILL2 = sellTable.front();
                // executionRowPFILL2.status = "PFILL";
                // executionRowPFILL2.transactionTime = transactionTime;

                // insertExecutionRow(executionTable, executionRowPFILL2); // Insert the "PFILL" row for the first row of sell table

                DeleteAndRearrangeFirstRow(sellTable); // Delete the first row of sell table
                sellTable.front().quantity=newQuantity; // Change quantity of first row of sell table
                sellTable.push_back(sellTable.front()); // Add the first row to the end
            }
        }

        if (x == 1) {
            InsertOrder(buyTable, selectedRow); // Assuming InsertOrder function is defined
        }
    } else {
        InsertOrder(buyTable, selectedRow); // Assuming InsertOrder function is defined
    }
        rowindex++;
    return 0;


    //...............................................................//
}
