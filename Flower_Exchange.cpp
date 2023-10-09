#include <iostream>
#include <fstream>
#include <sstream>
#include <vector>
#include <string>
#include <algorithm>
#include <chrono>
#include <ctime>
#include <iomanip>

struct Order {
    std::string orderID; // New field for orderID
    std::string clOrdID;
    std::string instrument;
    int quantity;
    int side;
    double price;
};
std::vector<Order> buyTable;
std::vector<Order> sellTable;
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
        int Index=rowIndex + 1;
        // while (Index > 0) {
        //     Index /= 10;
        //     row.orderID = "ORD23" + std::to_string(0);
        // }
        row.orderID = "ORD23" + std::to_string(Index);

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
// Custom comparison function for sorting buy orders
bool CompareBuyOrders(const Order& a, const Order& b) {
    if (a.price != b.price) {
        return a.price > b.price; // Ascending order by price
    } else {
        return a.orderID < b.orderID; // Time priority for orders with the same price
    }
}
// Custom comparison function for sorting sell orders
bool CompareSellOrders(const Order& a, const Order& b) {
    if (a.price != b.price) {
        return a.price < b.price; // Descending order by price
    } else {
        return a.orderID < b.orderID; // Time priority for orders with the same price
    }
}
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
    executionRow.orderID = Order.orderID; // "ORD23" + std::to_string(executionTable.size() + 1); // Generate orderID
    executionRow.instrument = Order.instrument;
    executionRow.side = Order.side;
    executionRow.price = Order.price;
    executionRow.quantity = Order.quantity;
    executionRow.status = status;
    executionRow.reason = reason;
    executionRow.transactionTime = transactionTime;

    executionTable.push_back(executionRow);
}

// Function to delete the first row from a table and rearrange the remaining rows
void DeleteAndRearrangeFirstRow(std::vector<Order>& table) {
    if (!table.empty()) {
        table.erase(table.begin()); // Delete the first row
    }
}

// Function to write the Execution table to a CSV file
void WriteExecutionToCSV(const std::vector<ExecutionRow>& executionTable, const std::string& filepath) {
    std::ofstream file(filepath);

    if (!file.is_open()) {
        std::cerr << "Error: Could not open the file " << filepath << std::endl;
        return;
    }
    // Write the header row
    file << "clOrdID,orderID,instrument,side,price,quantity,Status,Reason,Transaction Time" << std::endl;
    // Write each row of the Execution table
    for (const auto& executionRow : executionTable) {
        file << executionRow.orderID << "," << executionRow.clOrdID << ","
             << executionRow.instrument << "," << executionRow.side << ","
             << executionRow.price << "," << executionRow.quantity << ","
             << executionRow.status << "," << executionRow.reason << ","
             << executionRow.transactionTime << std::endl;
    }
    file.close();
}
std::string measureTransactionTime() {
    auto now = std::chrono::system_clock::now();// Get the current time
    std::time_t currentTime = std::chrono::system_clock::to_time_t(now);// Convert it to a time_t to extract components
    std::tm* timeinfo = std::localtime(&currentTime);// Extract date and time components
    std::ostringstream oss;// Create a string stream to format the output
    oss << std::put_time(timeinfo, "%Y%m%d-%H%M%S");// Format the date and time as per your requirement
    auto milliseconds = std::chrono::duration_cast<std::chrono::milliseconds>(now.time_since_epoch()).count() % 1000;
    oss << '.' << std::setw(3) << std::setfill('0') << milliseconds;
    return oss.str();// Convert the stringstream to a string and return
}

int main() {
    std::vector<Order> data = readCSVFile("order6.csv");
    std::vector<Order> buyTable;
    std::vector<Order> sellTable;
    int rowIndex = 0; // Change this to the desired row index (0-based)
    std::vector<ExecutionRow> executionTable;
    while (rowIndex < data.size()) {
        bool x = false;
        std::string reason;
        Order selectedRow = data[rowIndex];
    if (selectedRow.quantity > 1000 || selectedRow.quantity < 10) {
        reason = "Invalid Quantity";
        x= true;
    } else if (selectedRow.quantity % 10 != 0) {
        reason = "Invalid Quantity";
        x= true;
    } else if (!(selectedRow.price > 0)) {
        reason = "Invalid Price";
        x= true;
    } else if (selectedRow.instrument != "Rose" && selectedRow.instrument != "Lavender" && selectedRow.instrument != "Tulip" && selectedRow.instrument != "Orchid" && selectedRow.instrument != "Lotus") {
        reason = "Invalid Instrument";
        x= true;
    } else if (selectedRow.side != 1 && selectedRow.side != 2) {
        reason = "Invalid Side";
        x= true;
    } 
    // Calculate transaction time using your measureTransactionTime() function
    if (x) {
        std::string transactionTime = measureTransactionTime(); // Assuming measureTransactionTime function is defined
        insertExecutionRow(executionTable, selectedRow, "Reject", reason, transactionTime); // Assuming insertExecutionRow function is defined
    } else if (selectedRow.side == 1) {
          if (!sellTable.empty() && selectedRow.price >= sellTable.front().price) {
            int x=1;
            while (!sellTable.empty() && x != 0 && selectedRow.price >= sellTable.front().price) {
                int Quantity = selectedRow.quantity; // - sellTable.front().quantity;
                if (Quantity > sellTable.front().quantity) {
                    x = 1;
                    int newQuantity = selectedRow.quantity - sellTable.front().quantity;
                    int price=selectedRow.price;
                    selectedRow.quantity=sellTable.front().quantity;
                    selectedRow.price=sellTable.front().price;
                    std::string transactionTime = measureTransactionTime(); // Assuming measureTransactionTime function is defined
                    insertExecutionRow(executionTable, selectedRow, "PFILL", "None", transactionTime);
                    std::string transactionTime1 = measureTransactionTime();
                    insertExecutionRow(executionTable, sellTable.front(), "FILL", "None", transactionTime1);
                    DeleteAndRearrangeFirstRow(sellTable); // Delete the first row of sell table
                    selectedRow.price=price;
                    selectedRow.quantity=newQuantity;
                } else if (Quantity == sellTable.front().quantity) {
                    x = 0;
                    selectedRow.price=sellTable.front().price;
                    std::string transactionTime2 = measureTransactionTime(); // Assuming measureTransactionTime function is defined
                    insertExecutionRow(executionTable, selectedRow, "FILL", "None", transactionTime2);
                    std::string transactionTime3 = measureTransactionTime(); // Assuming measureTransactionTime function is defined
                    insertExecutionRow(executionTable, sellTable.front(), "FILL", "None", transactionTime3);
                    DeleteAndRearrangeFirstRow(sellTable); // Delete the first row of sell table
                } else {
                    x = 0;
                    int newQuantity = sellTable.front().quantity - selectedRow.quantity;
                    int price=selectedRow.price;
                    selectedRow.quantity=sellTable.front().quantity;
                    selectedRow.price=sellTable.front().price;
                    std::string transactionTime4 = measureTransactionTime(); // Assuming measureTransactionTime function is defined
                    insertExecutionRow(executionTable, selectedRow, "FILL", "None", transactionTime4);
                    sellTable.front().quantity=selectedRow.quantity;
                    std::string transactionTime5 = measureTransactionTime(); // Assuming measureTransactionTime function is defined
                    insertExecutionRow(executionTable, sellTable.front(), "PFILL", "None", transactionTime5);
                    DeleteAndRearrangeFirstRow(sellTable); // Delete the first row of sell table
                    selectedRow.price=price;
                    sellTable.front().quantity=newQuantity; // Change quantity of first row of sell table
                    // sellTable.push_back(sellTable.front()); // Add the first row to the end
                    InsertOrder(buyTable, sellTable, sellTable.front());
                }
            }
            if (x == 1) {
                InsertOrder(buyTable, sellTable, selectedRow); // Assuming InsertOrder function is defined
            }
        } else {
            InsertOrder(buyTable, sellTable, selectedRow); // Assuming InsertOrder function is defined
            std::string transactionTime6 = measureTransactionTime();
            insertExecutionRow(executionTable, selectedRow, "NEW", "None", transactionTime6);
        } 
    }
    else{

        if (!buyTable.empty() && selectedRow.price <= buyTable.front().price) {
            int x=1;
            while (!buyTable.empty() && x != 0 && selectedRow.price <= buyTable.front().price) {
                int Quantity = selectedRow.quantity; // - buyTable.front().quantity;
                if (Quantity > buyTable.front().quantity) {
                    x = 1;
                    int newQuantity = selectedRow.quantity - buyTable.front().quantity;
                    int price=selectedRow.price;
                    selectedRow.quantity=buyTable.front().quantity;
                    selectedRow.price=buyTable.front().price;
                    std::string transactionTime = measureTransactionTime(); // Assuming measureTransactionTime function is defined
                    insertExecutionRow(executionTable, selectedRow, "PFILL", "None", transactionTime);
                    std::string transactionTime1 = measureTransactionTime();
                    insertExecutionRow(executionTable, buyTable.front(), "FILL", "None", transactionTime1);
                    DeleteAndRearrangeFirstRow(buyTable); // Delete the first row of sell table
                    selectedRow.price=price;
                    selectedRow.quantity=newQuantity;
                } else if (Quantity == buyTable.front().quantity) {
                    x = 0;
                    selectedRow.price=buyTable.front().price;
                    std::string transactionTime2 = measureTransactionTime(); // Assuming measureTransactionTime function is defined
                    insertExecutionRow(executionTable, selectedRow, "FILL", "None", transactionTime2);
                    std::string transactionTime3 = measureTransactionTime(); // Assuming measureTransactionTime function is defined
                    insertExecutionRow(executionTable, buyTable.front(), "FILL", "None", transactionTime3);
                    DeleteAndRearrangeFirstRow(buyTable); // Delete the first row of sell table
                } else {
                    x = 0;
                    int newQuantity = buyTable.front().quantity - selectedRow.quantity;
                    int price=selectedRow.price;
                    selectedRow.quantity=buyTable.front().quantity;
                    selectedRow.price=buyTable.front().price;
                    std::string transactionTime4 = measureTransactionTime(); // Assuming measureTransactionTime function is defined
                    insertExecutionRow(executionTable, selectedRow, "FILL", "None", transactionTime4);               
                    buyTable.front().quantity=selectedRow.quantity;
                    std::string transactionTime5 = measureTransactionTime(); // Assuming measureTransactionTime function is defined
                    insertExecutionRow(executionTable, buyTable.front(), "PFILL", "None", transactionTime5);
                    DeleteAndRearrangeFirstRow(buyTable); // Delete the first row of sell table
                    selectedRow.price=price;
                    buyTable.front().quantity=newQuantity; // Change quantity of first row of sell table
                    // sellTable.push_back(sellTable.front()); // Add the first row to the end
                    InsertOrder(buyTable, sellTable, buyTable.front());
                }
            }
            if (x == 1) {
                InsertOrder(buyTable, sellTable, selectedRow); // Assuming InsertOrder function is defined
            }
        } else {
            InsertOrder(buyTable, sellTable, selectedRow); // Assuming InsertOrder function is defined
            std::string transactionTime6 = measureTransactionTime();
            insertExecutionRow(executionTable, selectedRow, "NEW", "None", transactionTime6);
        }
    }
    rowIndex++;
    }
WriteExecutionToCSV(executionTable, "C:\\Users\\MSI\\Desktop\\New folder (2)\\execution_rep.csv");
return 0;
}

