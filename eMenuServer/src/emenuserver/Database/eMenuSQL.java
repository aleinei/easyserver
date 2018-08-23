package emenuserver.Database;

import GUI.MainWindow;
import emenuserver.PrintOrder;
import java.io.IOException;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.Date;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import net.sf.dynamicreports.report.datasource.DRDataSource;

public class eMenuSQL {
    
    String dbName;
    String defaultDBName = "ESISoft";
    public eMenuSQL(String name) {
        this.dbName = name;
    }
    public Connection Connect() throws SQLException {
        //Connection con = DriverManager.getConnection("jdbc:sqlserver://localhost:1433;databaseName="+ dbName +";user=sa;password=AbdulRahman02^InsafMousa02^");
        Connection con = DriverManager.getConnection("jdbc:sqlserver://localhost:1433;databaseName="+ dbName +";user=sa;password=AbdulRahman02^");
        if(con != null) {
            return con;
        } else {
            return null;
        }
    }
    
    public Connection ConnectToMain() throws SQLException
    {
        //Connection con = DriverManager.getConnection("jdbc:sqlserver://localhost:1433;databaseName=ESISoft;user=sa;password=AbdulRahman02^InsafMousa02^");
        Connection con = DriverManager.getConnection("jdbc:sqlserver://localhost:1433;databaseName=ESISoft;user=sa;password=AbdulRahman02^");
        if(con != null) {
            return con;
        } else {
            return null;
        }
    }
            
    public JSONArray getItems() throws SQLException, JSONException, IOException {
        JSONArray jArray = new JSONArray();
        jArray.put(new JSONObject().put("Msg", "all_items").put("info", "null"));
        try {
        Connection con = this.Connect();
        if(con != null) {
            String query = "SELECT * FROM `items`";
            try (Statement stmt = con.createStatement()) {
                ResultSet rs = stmt.executeQuery(query);
                while(rs.next()) {
                    JSONObject json = new JSONObject();
                    json.put("item_id", rs.getInt("item_id"));
                    json.put("item_name", rs.getString("item_name"));
                    int id = rs.getInt("item_id");
                    String fileExt;
                    switch(id) {
                        case 12:
                            fileExt = "jpg";
                            break;
                        case 15:
                            fileExt = "jpg";
                            break;
                        case 24:
                            fileExt = "jpeg";
                            break;
                        case 30:
                            fileExt = "jpeg";
                            break;
                        default:
                            fileExt = "jpg";
                            break;
                    }
                    String itemURL = "/images/"+ id + "." + fileExt;
                    json.put("item_image", itemURL);
                    json.put("item_desc", rs.getString("item_desc"));
                    json.put("item_price", rs.getInt("item_price"));
                    jArray.put(json);
                }
                con.close();
            }
        }
        } catch (SQLException e) {
            System.out.println("Error in getting data from database");
        }
        
        return jArray;
    }
    
    public JSONArray getSections(int type) throws JSONException {
          JSONArray jArray = new JSONArray();
        jArray.put(new JSONObject().put("Msg", "all_sections").put("info", "null"));
        try {
        Connection con = this.Connect();
        if(con != null) {
            String query = "SELECT DISTINCT TOP (100) PERCENT dbo.[Drug Section].Id, dbo.[Drug Section].SortOrder, dbo.[Drug Section].Name FROM dbo.[Trade Names] INNER JOIN dbo.[Drug Section] ON dbo.[Trade Names].Section = dbo.[Drug Section].Id INNER JOIN dbo.Categories ON dbo.[Trade Names].Category = dbo.Categories.Id WHERE ((dbo.[Drug Section].Name IS Not NULL) OR (dbo.[Drug Section].Name<>N'')) AND (dbo.[Trade Names].Flag in(1,2,3,5,6)) AND ((dbo.[Trade Names].BarCode IS NULL) OR (dbo.[Trade Names].BarCode=N'')) AND (dbo.[Drug Section].Id<>0) Order By dbo.[Drug Section].SortOrder";
            try (Statement stmt = con.createStatement()) {
                ResultSet rs = stmt.executeQuery(query);
                while(rs.next()) {
                    JSONObject json = new JSONObject();
                    json.put("section_name", rs.getString("name"));
                    json.put("section_id", rs.getInt("Id"));
                    jArray.put(json);
                }
                con.close();
            }
        }
        } catch (SQLException e) {
            System.out.println("Error in getting data from database " + e );
        }
        return jArray;
        
    }
    
    public JSONArray getCategories(int type) throws JSONException, SQLException {
        JSONArray jArray = new JSONArray();
        jArray.put(new JSONObject().put("Msg", "section_categories").put("info", ""));
        try {
            Connection con = this.Connect();
            String query = "";
            if(type == emenuserver.Types.StoreTypes.Cafe)
                query = "SELECT DISTINCT TOP (100) PERCENT dbo.Categories.Id, dbo.[Trade Names].Section, dbo.Categories.SortOrder, dbo.Categories.Name FROM dbo.[Trade Names] INNER JOIN dbo.[Drug Section] ON dbo.[Trade Names].Section = dbo.[Drug Section].Id INNER JOIN dbo.Categories ON dbo.[Trade Names].Category = dbo.Categories.Id WHERE (dbo.[Trade Names].Flag in(2,3,5,6)) And ((dbo.[Categories].[Name] Is Not Null) Or  (dbo.[Categories].[Name]<>N'' ) ) AND (dbo.[Trade Names].BarCode IS NULL OR dbo.[Trade Names].BarCode = N'') AND (dbo.Categories.Id<>0) ORDER BY dbo.Categories.SortOrder";
            else
                query = "SELECT DISTINCT TOP (100) PERCENT dbo.[Trade Names].Section, Categories.Id, Categories.SortOrder, Categories.Name FROM [Trade Names]  INNER JOIN [Drug Section] ON [Trade Names].Section = [Drug Section].Id INNER JOIN Categories  ON [Trade Names].Category = Categories.Id WHERE ((Categories.Name IS NOT NULL) OR (Categories.Name <> N'')) AND (Categories.Id<>0) ORDER BY Categories.SortOrder";
            System.out.println(type + "");
            try(Statement stmt = con.createStatement()) {
                ResultSet rs = stmt.executeQuery(query);
                while(rs.next()) {
                    int category_id = rs.getInt("Id");
                    JSONObject category_object = new JSONObject();
                    category_object.put("section_id", rs.getInt("Section"));
                    category_object.put("Id", category_id);
                    category_object.put("Name", rs.getString("Name"));
                    jArray.put(category_object);
                }
            }
        } catch (SQLException e ) {
            System.out.println("Error in getting data " + e);
        }
        
        return jArray;
    }
    
    public JSONArray getCategoryItems(int type) throws JSONException {
        JSONArray jArray = new JSONArray();
        jArray.put(new JSONObject().put("Msg", "category_items").put("info", "section_Id"));
        try {
        Connection con = this.Connect();
        if(con != null) {
            String query = "";
            if(type == emenuserver.Types.StoreTypes.Cafe)
                query = "SELECT TOP (100) PERCENT dbo.[Trade Names].Source, dbo.[Trade Names].Unit, dbo.[Trade Names].Section , dbo.[Trade Names].Category , dbo.[Trade Names].MaxChildItems, dbo.[Trade Names].Id, dbo.[Trade Names].SortOrder, dbo.[Trade Names].Name, convert(float, dbo.[Trade Names].Price) AS Price FROM dbo.[Trade Names] INNER JOIN dbo.[Drug Section] ON dbo.[Trade Names].Section = dbo.[Drug Section].Id INNER JOIN dbo.Categories ON dbo.[Trade Names].Category = dbo.Categories.Id WHERE (dbo.[Trade Names].Flag in(2,3,5,6)) And ((dbo.[Trade Names].Name IS NOT NULL) OR (dbo.[Trade Names].Name <> N'')) And ((dbo.[Trade Names].BarCode IS NULL) OR (dbo.[Trade Names].BarCode = N'')) And (dbo.[Trade Names].id<>0) Order By dbo.[Trade Names].SortOrder";
            else
                query = "SELECT [Trade Names].Section, [Trade Names].Category, dbo.[Trade Names].Unit , dbo.[Trade Names].Source, [Trade Names].SortOrder, [Trade Names].Id, dbo.[Trade Names].MaxChildItems, [Trade Names].Name, [Trade Names].Price FROM [Trade Names] INNER JOIN [Drug Section] ON  [Trade Names].Section = [Drug Section].Id INNER JOIN Categories ON [Trade Names].Category =  Categories.Id WHERE ([Trade Names].Name  IS NOT NULL OR [Trade Names].Name <> N'') And ([Trade Names].id<>0) Order By [Trade Names].SortOrder";
            System.out.println(type + "");
            try (Statement stmt = con.createStatement()) {
                ResultSet rs = stmt.executeQuery(query);
                JSONArray items = new JSONArray();
                while(rs.next()) {
                    JSONObject json = new JSONObject();
                    json.put("section_id", rs.getInt("Section"));
                    json.put("category_id", rs.getInt("Category"));
                    json.put("Id", rs.getInt("Id"));
                    json.put("Name", rs.getString("Name"));
                    json.put("Price", rs.getFloat("Price"));
                    json.put("maxChild", rs.getInt("MaxChildItems"));
                    json.put("unit", rs.getInt("Unit"));
                    String query2 = "SELECT Name FROM dbo.[Sources] WHERE Id = " + rs.getInt("Source") + "";
                    try(Statement stmt2 = con.createStatement())
                    {
                        ResultSet rs2 = stmt2.executeQuery(query2);
                        if(rs2.next())
                        {
                            if(rs2.getString("Name") != null){
                                json.put("source", rs2.getString("Name"));
                                System.out.println(rs2.getString("Name"));
                            }
                        }
                    }
                    items.put(json);
                }
                jArray.put(items);
            }
            con.close();
        }
        } catch (SQLException e) {
            System.out.println("Error in getting data from database");
            e.printStackTrace();
        }
        
        return jArray;
    }
    
      
    public JSONArray getCategories2(int section_id) throws JSONException, SQLException {
        JSONArray jArray = new JSONArray();
        jArray.put(new JSONObject().put("Msg", "section_categories").put("info", ""));
        try {
            Connection con = this.Connect();
            String query = "SELECT DISTINCT TOP (100) PERCENT dbo.Categories.Id, dbo.[Trade Names].Section, dbo.Categories.SortOrder, dbo.Categories.Name FROM dbo.[Trade Names] INNER JOIN dbo.[Drug Section] ON dbo.[Trade Names].Section = dbo.[Drug Section].Id INNER JOIN dbo.Categories ON dbo.[Trade Names].Category = dbo.Categories.Id WHERE (dbo.[Trade Names].Section = '" + section_id +"') And(dbo.[Trade Names].Flag in(2,3,5,6)) And ((dbo.[Categories].[Name] Is Not Null) Or  (dbo.[Categories].[Name]<>N'' ) ) AND (dbo.[Trade Names].BarCode IS NULL OR dbo.[Trade Names].BarCode = N'') AND (dbo.Categories.Id<>0) ORDER BY dbo.Categories.SortOrder";
            try(Statement stmt = con.createStatement()) {
                ResultSet rs = stmt.executeQuery(query);
                while(rs.next()) {
                    int category_id = rs.getInt("Id");
                    JSONObject category_object = new JSONObject();
                    category_object.put("section_id", rs.getInt("Section"));
                    category_object.put("Id", category_id);
                    category_object.put("Name", rs.getString("Name"));
                    jArray.put(category_object);
                }
            }
        } catch (SQLException e ) {
            System.out.println("Error in getting data " + e);
        }
        
        return jArray;
    }
    
    public void InsertNewOrder(String item_name, int count) throws SQLException {
            Connection con = this.Connect();
            String query = "INSERT INTO `orders` (`item_name`, `item_count`) VALUES ('"+item_name+"','"+count+"')";
            Statement stmt = con.createStatement();
            stmt.execute(query);
              
    }
    
    public JSONArray GetUsernames() throws JSONException, SQLException {
        JSONArray jArray = new JSONArray();
        jArray.put(new JSONObject().put("Msg", "usernames").put("info", "null"));
        Connection con = this.Connect();
        if(con != null) {
            String query = "SELECT Name FROM Users WHERE (Name IS NOT NULL) AND (Password IS NOT NULL)";
            try(Statement stmt = con.createStatement()) {
                ResultSet rs = stmt.executeQuery(query);
                JSONArray usersArray = new JSONArray();
                while(rs.next()) {
                    JSONObject object = new JSONObject();
                    object.put("Name", rs.getString("Name"));
                    usersArray.put(object);
                }
                jArray.put(usersArray);
                stmt.close();
            }
            query = "SELECT Name FROM SalesPersons WHERE (Name IS NOT NULL) AND (myPassword IS NOT NULL)";
            try(Statement stmt = con.createStatement()) {
                ResultSet rs = stmt.executeQuery(query);
                JSONArray waitersArray = new JSONArray();
                while(rs.next()) {
                    JSONObject object = new JSONObject();
                    object.put("Name", rs.getString("Name"));
                    waitersArray.put(object);
                }
                jArray.put(waitersArray);
                stmt.close();
            }
            con.close();
        }  
        return jArray;
    }
    
    public JSONArray UserAuthentication(String type, String password) throws SQLException, JSONException {
        JSONArray jArray = new JSONArray();
        try {
            Connection con = this.Connect();
            if(con != null) {
                if(type.equals("cashier")) {
                    String query = "SELECT Name, Id, Position FROM Users WHERE Password = '" + password + "'";
                    try(Statement stmt = con.createStatement()) {
                        ResultSet rs = stmt.executeQuery(query);
                        if(rs.next()) {
                             jArray.put(new JSONObject().put("Msg", "user_verified"));
                             JSONObject object = new JSONObject();
                             object.put("name", rs.getString("Name"));
                             object.put("id", rs.getInt("Id"));
                             object.put("position", rs.getFloat("Position"));
                             jArray.put(object);
                        } else {
                             jArray.put(new JSONObject().put("Msg", "user_not_verified"));
                        }
                        return jArray;
                    }
                } else if(type.equals("captain")) {
                    String query = "SELECT Name, Id FROM SalesPersons WHERE myPassword = '" + password + "'";
                    try(Statement stmt = con.createStatement()) {
                        ResultSet rs = stmt.executeQuery(query);
                           if(rs.next()) {
                             jArray.put(new JSONObject().put("Msg", "user_verified"));
                             JSONObject object = new JSONObject();
                             object.put("name", rs.getString("Name"));
                             object.put("id", rs.getInt("Id"));
                             jArray.put(object);
                            } else {
                                 return null;
                            }
                            return jArray;
                        }
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(eMenuSQL.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
    public JSONObject getUnpaidInvoices(int userID) throws SQLException, JSONException {
         JSONObject jArray = new JSONObject();
         jArray.put("Msg", "unpaid_invoices");
         try { 
         Connection con = this.Connect();
            if(con != null) {
                String query = "Select * from Invoice_Order where [myUser] = '"+ userID +"' And [Type]= 1 And Total = 0  And [Store] = 5  And IT=2 And Status < 2 And Id>0 Order by IDate, CO";
                try(Statement stmt = con.createStatement()) {
                    ResultSet rs = stmt.executeQuery(query);
                    JSONArray invoices = new JSONArray();
                    while(rs.next()) {
                        JSONObject invoice = new JSONObject();
                        invoice.put("invoice_id", rs.getInt("ID"));
                        invoice.put("table_num", rs.getInt("myTable"));
                        invoices.put(invoice);
                    }
                    jArray.put("invoices",invoices);
                    return jArray;
                }
            } else {
                return null;
            }
         } catch (SQLException ex) {
            Logger.getLogger(eMenuSQL.class.getName()).log(Level.SEVERE, null, ex);
        }
         return null;
    }
    
    
    public JSONObject getInvoiceDetails(int invoiceID) throws JSONException {
         JSONObject jArray = new JSONObject();
         jArray.put("Msg", "invoice_details");
         try { 
         Connection con = this.Connect();
            if(con != null) {
                String query = "SELECT * FROM Invoice_Order_Details WHERE Invoice_Order_ID = '" + invoiceID +"'";
                try(Statement stmt = con.createStatement()) {
                    ResultSet rs = stmt.executeQuery(query);
                    JSONArray items = new JSONArray();
                    while(rs.next()) {
                        JSONObject item = new JSONObject();
                        item.put("itemID", rs.getInt("DrugID"));
                        item.put("inovice_id", rs.getInt("Invoice_order_ID"));
                        item.put("itemName", rs.getString("ModifiedName"));
                        item.put("itemPrice", rs.getString("Price"));
                        item.put("qty", rs.getInt("Quantity"));
                        try(Statement stmt2 = con.createStatement())
                        {
                            String query2 = "SELECT * FROM dbo.[Trade Names] WHERE Id = " + rs.getInt("DrugID");
                            ResultSet rs2 = stmt2.executeQuery(query2);
                            if(rs2.next())
                            {
                                int src = rs2.getInt("Source");
                                String query3 = "SELECT Name FROM dbo.[Sources] WHERE Id = " + src + "";
                                try(Statement stmt3 = con.createStatement())
                                {
                                    ResultSet rs3 = stmt3.executeQuery(query3);
                                    if(rs3.next())
                                    {
                                        if(rs2.getString("Name") != null){
                                            item.put("source", rs3.getString("Name"));
                                        }
                                    }
                                }
                            }
                        }
                        items.put(item);
                    }
                    jArray.put("items",items);
                    return jArray;
                }
            } else {
                return null;
            }
         } catch (SQLException ex) {
            Logger.getLogger(eMenuSQL.class.getName()).log(Level.SEVERE, null, ex);
        }
         return null;
    }
    
    public JSONObject verifyCustomer(String username, String password) {
        JSONObject user = new JSONObject();
        try {
            Connection con = this.Connect();
            if(con != null) {
                String query = "SELECT * FROM Customers WHERE Name = '" + username + "' AND Password = '" + password + "'";
                try (Statement stmt = con.createStatement()) {
                    ResultSet rs = stmt.executeQuery(query);
                    if(rs.next()) {
                        try {
                            user.put("Msg", "user_verified");
                            user.put("Id", rs.getInt("ID"));
                            user.put("address", rs.getString("Address1"));
                            user.put("tele", rs.getString("Telephone"));
                            user.put("email", rs.getString("E-mail"));
                            user.put("lat", rs.getDouble("Latitude"));
                            user.put("long", rs.getDouble("Longitude"));
                            user.put("pass", rs.getString("Password"));
                            try(Statement stmt2 = con.createStatement())
                            {
                                String query2 = "SELECT ID, Total FROM Invoice_Order WHERE Cust = " + rs.getInt("ID") + " ORDER BY IDate";
                                JSONArray invoices = new JSONArray();
                                ResultSet rs2 = stmt2.executeQuery(query2);
                                while(rs2.next())
                                {
                                    JSONObject invoice = new JSONObject();
                                    int id = rs2.getInt("ID");
                                    double total = rs2.getDouble("Total");
                                    invoice.put("id", id);
                                    invoice.put("viewed", false);
                                    if(total > 0)
                                        invoice.put("delievered", true);
                                    else
                                        invoice.put("delievered", false);
                                    try(Statement stmt3 = con.createStatement())
                                    {
                                        String query3 = "SELECT ModifiedName, Price, Quantity FROM Invoice_Order_Details WHERE Invoice_Order_ID = " + id;
                                        JSONArray items = new JSONArray();
                                        ResultSet rs3 = stmt3.executeQuery(query3);
                                        while(rs3.next())
                                        {
                                            String name = rs3.getString("ModifiedName");
                                            double price = rs3.getDouble("Price");
                                            double qty = rs3.getDouble("Quantity");
                                            JSONObject item = new JSONObject();
                                            item.put("name", name);
                                            item.put("price", price);
                                            item.put("qty", qty);
                                            items.put(item);
                                        }
                                        invoice.put("items", items);
                                    }
                                    invoices.put(invoice);
                                }
                                user.put("orders", invoices);
                            }
                        } catch (JSONException ex) {
                            Logger.getLogger(eMenuSQL.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    } else {
                        try {
                            user.put("Msg", "user_not_exist");
                        } catch (JSONException ex) {
                            Logger.getLogger(eMenuSQL.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                }
                return user;
            }
        } catch (SQLException ex) {
            Logger.getLogger(eMenuSQL.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public JSONObject createNewCustomer(String username, String password, String phone, String email, String address1, String address2, String floor, String apt, double lat, double longt) {
        JSONObject user = new JSONObject();
        try {
            Connection con = this.Connect();
            if(con != null) {
                if(UserExists(username))
                {
                    try {
                        user.put("Msg", "user_exists");
                    } catch (JSONException ex) {
                        Logger.getLogger(eMenuSQL.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    return user;
                }
                String completeAddress = "Building number " + address2 + " Floor Number " + floor + ", Apt Number " + apt + " , " + address1; 
                int Id = getNextId("Customers");
                String values = "'" + Id + "',";
                values += "'1',";
                values += "'" + username + "',";
                values += "'" + password + "',";
                values += "'" + phone + "',";
                values += "'" + email + "',";
                values += "'" + completeAddress + "',";
                values += "" + lat + ",";
                values += "" + longt + "";
                String query = "INSERT INTO Customers (Id, Type, Name, Password, Telephone, [E-mail], Address1, Latitude, Longitude) VALUES (" + values + ")";
                try(Statement stmt = con.createStatement()) {
                    int rowsaffected = stmt.executeUpdate(query);
                    if(rowsaffected != 0) {
                        user.put("Msg", "user_created");
                    } else {
                        user.put("Msg", "user_failed");
                    }
                    return user;
                } catch (JSONException ex) {
                    Logger.getLogger(eMenuSQL.class.getName()).log(Level.SEVERE, null, ex);
                } 
            }
        } catch (SQLException ex) {
            Logger.getLogger(eMenuSQL.class.getName()).log(Level.SEVERE, null, ex);
        }
            
        return null;
    }
    
    public int InsertNewOrder(JSONObject order) {
        try {
            Connection con = this.Connect();
            if(con != null) {
                int invoice_id = order.getInt("invoice_id");
                int tableNum = order.getInt("table_num");
                 SimpleDateFormat forma = new SimpleDateFormat("yyyy/MM/dd hh:mm");
                 String date = forma.format(new Date());
                if(InvoiceExists(invoice_id)) {
                    int invoiceCO = getInvoiceCO(invoice_id);
                    JSONArray order_items = order.getJSONArray("order_items");
                    DRDataSource dataSource = new DRDataSource("name", "qty");
                    for(int i = 0; i < order_items.length(); i++) {
                        JSONObject item = order_items.getJSONObject(i);
                        int Id = getNextId("Invoice_Order_Details");
                        String values = "'" + Id + "',";
                        values += "'" + invoice_id + "',";
                        values += "'0',";
                        values += "'" + item.getInt("itemId") + "',";
                        values += "'" + item.getString("itemName") + "',";
                        values += "'" + item.getDouble("itemPrice") + "',";
                        values += "'" + item.getInt("qty") * -1 + "',";
                        values += "'2',";
                        values += "'5',";
                        values += "'2',";
                        values += "'"+order.getInt("cashierID")+"',";
                        values += "'" + date + "'" ;
                        String query = "INSERT INTO Invoice_Order_Details "
                                + "(ID, Invoice_Order_ID, Parent, DrugID, ModifiedName, Price, Quantity, Supplier, Store, myStat, myUser, Ordered) VALUES (" + values + ")";
                        dataSource.add(item.getString("itemName"), item.getInt("qty"));
                        try(Statement stmt = con.createStatement()) {
                            int rows = stmt.executeUpdate(query);
                            if(rows <= 0) {
                                return -1;
                            }                                                               
                            boolean hasExtra = item.getBoolean("hasextra");
                            boolean hasadd = item.getBoolean("hasadd");
                            boolean hasWithout = item.getBoolean("haswithout");
                            if(hasExtra) {
                                JSONArray extraItems = item.getJSONArray("extraitems");
                                for(int e = 0; e < extraItems.length(); e++) {
                                    JSONObject ei = extraItems.getJSONObject(e);                                    
                                    int eID = getNextId("Invoice_Order_Details");                                    
                                    String queryValues = "'" + eID + "',";                                    
                                    queryValues += "'" + invoice_id + "',";                                    
                                    queryValues += "'" + Id + "',";                                    
                                    queryValues += "'" + ei.getInt("id") + "',";                                    
                                    queryValues += "'" + ei.getString("name") + "',";                                    
                                    queryValues += "'" + ei.getDouble("price") + "',";                                    
                                    queryValues += "'" + ei.getDouble("qty") + "',";                                    
                                    queryValues += "'2',";                                    
                                    queryValues += "'5',";                                    
                                    queryValues += "'1',";                                    
                                    queryValues += "'" + order.getInt("cashierID") + "',";                                    
                                    queryValues += "'"+emenuserver.Types.ChildItemType.EXTRA+"'";
                                    String extraQuery = "INSERT INTO Invoice_Order_Details "                                    
                                            + "(ID, Invoice_Order_ID, Parent, DrugID, ModifiedName, Price, Quantity, Supplier, Store, myStat, myUser, ComponentType) VALUES (" + queryValues + ")";                                            
                                    try(Statement stmt3 = con.createStatement()) {                                    
                                        stmt3.execute(extraQuery);                                        
                                        stmt3.close();                                        
                                    }                                    
                                }                                
                            }                            
                            if(hasadd) {                            
                                JSONArray extraItems = item.getJSONArray("addableitems");                                
                                for(int e = 0; e < extraItems.length(); e++) {                                
                                    JSONObject ei = extraItems.getJSONObject(e);                                    
                                    int eID = getNextId("Invoice_Order_Details");                                    
                                    String queryValues = "'" + eID + "',";                                    
                                    queryValues += "'" + invoice_id + "',";                                    
                                    queryValues += "'" + Id + "',";                                    
                                    queryValues += "'" + ei.getInt("id") + "',";                                    
                                    queryValues += "'" + ei.getString("name") + "',";                                    
                                    queryValues += "'" + ei.getDouble("price") + "',";                                    
                                    queryValues += "'" + ei.getDouble("qty") + "',";                                    
                                    queryValues += "'2',";                                   
                                    queryValues += "'5',";                                    
                                    queryValues += "'1',";
                                    queryValues += "'" + order.getInt("cashierID") + "',";
                                    queryValues += "'"+emenuserver.Types.ChildItemType.OPTIONAL+"'";
                                    String extraQuery = "INSERT INTO Invoice_Order_Details "
                                            + "(ID, Invoice_Order_ID, Parent, DrugID, ModifiedName, Price, Quantity, Supplier, Store, myStat, myUser, ComponentType) VALUES (" + queryValues + ")";
                                    try(Statement stmt3 = con.createStatement()) {
                                        stmt3.execute(extraQuery);
                                        stmt3.close();   
                                    }   
                                }   
                            }
                            if(hasWithout) {
                                JSONArray extraItems = item.getJSONArray("withoutitems");
                                for(int e = 0; e < extraItems.length(); e++) {
                                    JSONObject ei = extraItems.getJSONObject(e);
                                    int eID = getNextId("Invoice_Order_Details");
                                    String queryValues = "'" + eID + "',";
                                    queryValues += "'" + invoice_id + "',";
                                    queryValues += "'" + Id + "',";
                                    queryValues += "'" + ei.getInt("id") + "',";
                                    queryValues += "'" + ei.getString("name") + "',";
                                    queryValues += "'" + ei.getDouble("price") + "',";
                                    queryValues += "'" + ei.getDouble("qty") + "',";
                                    queryValues += "'2',";
                                    queryValues += "'5',";
                                    queryValues += "'1',";
                                    queryValues += "'" + order.getInt("cashierID") + "',";
                                     queryValues += "'"+emenuserver.Types.ChildItemType.WITHOUT+"'";
                                     String extraQuery = "INSERT INTO Invoice_Order_Details "
                                               + "(ID, Invoice_Order_ID, Parent, DrugID, ModifiedName, Price, Quantity, Supplier, Store, myStat, myUser, ComponentType) VALUES (" + queryValues + ")";
                                            try(Statement stmt3 = con.createStatement()) {
                                                stmt3.execute(extraQuery);
                                                stmt3.close();
                                            }
                                        }
                             }
                            System.out.println("Updated order item");
                        }
                    }
                    System.out.println(order_items.getJSONObject(0).toString());
                    PrintOrder.PrintWorkOrder(order_items, getCashierName(order.getInt("cashierID")), getCaptainName(order.getInt("captainID")), invoice_id, tableNum, invoiceCO, this);
                    return invoice_id;
                } else {
                    int invoiceId = getNextId("Invoice_Order");
                    int nextCO = getNextCO(2);
                    String invoiceValues = "'" + invoiceId + "',";
                    invoiceValues += "'"+ nextCO + "',";
                    invoiceValues += "'0',";
                    invoiceValues += "'1',";
                    invoiceValues += "'"+order.getInt("cashierID")+"',";
                    invoiceValues += "'0',";
                    invoiceValues += "'5',";
                    invoiceValues += "'"+ tableNum +"',";
                    invoiceValues += "'2',";
                    invoiceValues += "'"+order.getInt("captainID")+"',";
                    invoiceValues += "'" + date + "'";
                    String query1 = "INSERT INTO Invoice_Order (ID, CO, Cust, Type, myUser, Status, Store, myTable, IT, SP, Ordered) VALUES (" + invoiceValues + ")";
                    try(Statement stmt = con.createStatement()) {
                        int rows = stmt.executeUpdate(query1);
                        if(rows > 0) {
                            JSONArray order_items = order.getJSONArray("order_items");
                            DRDataSource dataSource = new DRDataSource("name", "qty");
                            for(int i = 0; i < order_items.length(); i++) {
                                JSONObject item = order_items.getJSONObject(i);
                                int Id = getNextId("Invoice_Order_Details");
                                String values = "'" + Id + "',";
                                values += "'" + invoiceId + "',";
                                values += "'0',";
                                values += "'" + item.getInt("itemId") + "',";
                                values += "'" + item.getString("itemName") + "',";
                                values += "'" + item.getDouble("itemPrice") + "',";
                                values += "'" + item.getInt("qty") * -1 + "',";
                                values += "'2',";
                                values += "'5',";
                                values += "'2',";
                                values += "'"+order.getInt("cashierID")+"',";
                                values += "'" + date + "'";
                                String query = "INSERT INTO Invoice_Order_Details "
                                        + "(ID, Invoice_Order_ID, Parent, DrugID, ModifiedName, Price, Quantity, Supplier, Store, myStat, myUser, Ordered) VALUES (" + values + ")";
                                dataSource.add(item.getString("itemName"), item.getInt("qty"));
                                try(Statement stmt2 = con.createStatement()) {
                                    int rows2 = stmt2.executeUpdate(query);
                                    if(rows2 <= 0) {
                                        return -1;
                                    }
                                    boolean hasExtra = item.getBoolean("hasextra");
                                    boolean hasadd = item.getBoolean("hasadd");
                                    boolean hasWithout = item.getBoolean("haswithout");
                                    if(hasExtra) {
                                        JSONArray extraItems = item.getJSONArray("extraitems");
                                        for(int e = 0; e < extraItems.length(); e++) {
                                            JSONObject ei = extraItems.getJSONObject(e);
                                            int eID = getNextId("Invoice_Order_Details");
                                            String queryValues = "'" + eID + "',";
                                            queryValues += "'" + invoiceId + "',";
                                            queryValues += "'" + Id + "',";
                                            queryValues += "'" + ei.getInt("id") + "',";
                                            queryValues += "'" + ei.getString("name") + "',";
                                            queryValues += "'" + ei.getDouble("price") + "',";
                                            queryValues += "'" + ei.getDouble("qty") + "',";
                                            queryValues += "'2',";
                                            queryValues += "'5',";
                                            queryValues += "'1',";
                                            queryValues += "'" + order.getInt("cashierID") + "',";
                                            queryValues += "'"+emenuserver.Types.ChildItemType.EXTRA+"'";
                                            String extraQuery = "INSERT INTO Invoice_Order_Details "
                                                    + "(ID, Invoice_Order_ID, Parent, DrugID, ModifiedName, Price, Quantity, Supplier, Store, myStat, myUser, ComponentType) VALUES (" + queryValues + ")";
                                            try(Statement stmt3 = con.createStatement()) {
                                                stmt3.execute(extraQuery);
                                                stmt3.close();
                                            }
                                        }
                                    }
                                    if(hasadd) {
                                        JSONArray extraItems = item.getJSONArray("addableitems");
                                        for(int e = 0; e < extraItems.length(); e++) {
                                            JSONObject ei = extraItems.getJSONObject(e);
                                            int eID = getNextId("Invoice_Order_Details");
                                            String queryValues = "'" + eID + "',";
                                            queryValues += "'" + invoiceId + "',";
                                            queryValues += "'" + Id + "',";
                                            queryValues += "'" + ei.getInt("id") + "',";
                                            queryValues += "'" + ei.getString("name") + "',";
                                            queryValues += "'" + ei.getDouble("price") + "',";
                                            queryValues += "'" + ei.getDouble("qty") + "',";
                                            queryValues += "'2',";
                                            queryValues += "'5',";
                                            queryValues += "'1',";
                                            queryValues += "'" + order.getInt("cashierID") + "',";
                                            queryValues += "'"+emenuserver.Types.ChildItemType.OPTIONAL+"'";
                                            String extraQuery = "INSERT INTO Invoice_Order_Details "
                                                    + "(ID, Invoice_Order_ID, Parent, DrugID, ModifiedName, Price, Quantity, Supplier, Store, myStat, myUser, ComponentType) VALUES (" + queryValues + ")";
                                            try(Statement stmt3 = con.createStatement()) {
                                                stmt3.execute(extraQuery);
                                                stmt3.close();
                                            }
                                        }
                                    }
                                    if(hasWithout) {
                                        JSONArray extraItems = item.getJSONArray("withoutitems");
                                        for(int e = 0; e < extraItems.length(); e++) {
                                            JSONObject ei = extraItems.getJSONObject(e);
                                            int eID = getNextId("Invoice_Order_Details");
                                            String queryValues = "'" + eID + "',";
                                            queryValues += "'" + invoiceId + "',";
                                            queryValues += "'" + Id + "',";
                                            queryValues += "'" + ei.getInt("id") + "',";
                                            queryValues += "'" + ei.getString("name") + "',";
                                            queryValues += "'" + ei.getDouble("price") + "',";
                                            queryValues += "'" + ei.getDouble("qty") + "',";
                                            queryValues += "'2',";
                                            queryValues += "'5',";
                                            queryValues += "'1',";
                                            queryValues += "'" + order.getInt("cashierID") + "',";
                                            queryValues += "'"+emenuserver.Types.ChildItemType.WITHOUT+"'";
                                            String extraQuery = "INSERT INTO Invoice_Order_Details "
                                                    + "(ID, Invoice_Order_ID, Parent, DrugID, ModifiedName, Price, Quantity, Supplier, Store, myStat, myUser, ComponentType) VALUES (" + queryValues + ")";
                                            try(Statement stmt3 = con.createStatement()) {
                                                stmt3.execute(extraQuery);
                                                stmt3.close();
                                            }
                                        }
                                    }
                                    System.out.println("Updated order with new invoice " + invoiceId);
                                }
                            }
                            System.out.println(order_items.getJSONObject(0).toString());
                            PrintOrder.PrintWorkOrder(order_items, getCashierName(order.getInt("cashierID")), getCaptainName(order.getInt("captainID")), invoiceId, tableNum, nextCO, this);
                            return invoiceId;
                        } else {
                            return -1;
                        }
                    }
                }
            }
        } catch (SQLException | JSONException ex) {
            Logger.getLogger(eMenuSQL.class.getName()).log(Level.SEVERE, null, ex);
        }
        return -1;
    }
    
    public boolean insertNewOrderD(int cstId, JSONArray items, MainWindow caller, boolean isTakeAway, double cost) {
        try {
                    Connection con = this.ConnectToMain();
                    int invoiceId = getNextIdMain("Invoice_Order");
                    int cstID = getCstIDWithName(dbName);
                    String invoiceValues = "'" + invoiceId + "',";
                    if(isTakeAway)
                        invoiceValues += "'"+ getNextCO(0) +"',";
                    else
                        invoiceValues += "'"+ getNextCO(1) +"',";
                    invoiceValues += "'"+cstId+"',";
                    invoiceValues += "'1',";
                    invoiceValues += "'0',";
                    invoiceValues += "'1',";
                    invoiceValues += "'5',";
                    invoiceValues += "'0',";
                    if(isTakeAway)
                        invoiceValues += "'0',";
                    else
                        invoiceValues += "'1',";
                    invoiceValues += "'1',";
                    invoiceValues +="'" + cost + "',";
                    invoiceValues +="'" + cstID + "'";
                    String query1 = "INSERT INTO Invoice_Order (ID, CO, Cust, Type, myUser, Status, Store, myTable, IT, SP, Total, ourCust) VALUES (" + invoiceValues + ")";
                    try(Statement stmt = con.createStatement()) {
                        int rows = stmt.executeUpdate(query1);
                        if(rows > 0) {
                            for(int i = 0; i < items.length(); i++) {
                                JSONObject item = items.getJSONObject(i);
                                int Id = getNextIdMain("Invoice_Order_Details");
                                String values = "'" + Id + "',";
                                values += "'" + invoiceId + "',";
                                values += "'0',";
                                values += "'" + item.getInt("itemId") + "',";
                                values += "'" + item.getString("itemName") + "',";
                                values += "'" + item.getDouble("itemPrice") + "',";
                                values += "'" + item.getInt("qty") * -1 + "',";
                                values += "'2',";
                                values += "'5',";
                                values += "'1'";
                                String query = "INSERT INTO Invoice_Order_Details "
                                        + "(ID, Invoice_Order_ID, Parent, DrugID, ModifiedName, Price, Quantity, Supplier, Store, myStat) VALUES (" + values + ")";
                                try(Statement stmt2 = con.createStatement()) {
                                    int rows2 = stmt2.executeUpdate(query);
                                    if(rows2 <= 0) {
                                        System.out.println("rows2 was not done");
                                        return false;
                                    }
                                    boolean hasExtra = item.getBoolean("hasextra");
                                    boolean hasadd = item.getBoolean("hasadd");
                                    boolean hasWithout = item.getBoolean("haswithout");
                                    if(hasExtra) {
                                        JSONArray extraItems = item.getJSONArray("extraitems");
                                        for(int e = 0; e < extraItems.length(); e++) {
                                            JSONObject ei = extraItems.getJSONObject(e);
                                            int eID = getNextId("Invoice_Order_Details");
                                            String queryValues = "'" + eID + "',";
                                            queryValues += "'" + invoiceId + "',";
                                            queryValues += "'" + Id + "',";
                                            queryValues += "'" + ei.getInt("id") + "',";
                                            queryValues += "'" + ei.getString("name") + "',";
                                            queryValues += "'" + ei.getDouble("price") + "',";
                                            queryValues += "'" + ei.getDouble("qty") + "',";
                                            queryValues += "'2',";
                                            queryValues += "'5',";
                                            queryValues += "'1',";
                                            queryValues += "'"+emenuserver.Types.ChildItemType.EXTRA+"'";
                                            String extraQuery = "INSERT INTO Invoice_Order_Details "
                                                    + "(ID, Invoice_Order_ID, Parent, DrugID, ModifiedName, Price, Quantity, Supplier, Store, myStat, ComponentType) VALUES (" + queryValues + ")";
                                            try(Statement stmt3 = con.createStatement()) {
                                                stmt3.execute(extraQuery);
                                                stmt3.close();
                                            }
                                        }
                                    }
                                    if(hasadd) {
                                        JSONArray extraItems = item.getJSONArray("addableitems");
                                        for(int e = 0; e < extraItems.length(); e++) {
                                            JSONObject ei = extraItems.getJSONObject(e);
                                            int eID = getNextIdMain("Invoice_Order_Details");
                                            String queryValues = "'" + eID + "',";
                                            queryValues += "'" + invoiceId + "',";
                                            queryValues += "'" + Id + "',";
                                            queryValues += "'" + ei.getInt("id") + "',";
                                            queryValues += "'" + ei.getString("name") + "',";
                                            queryValues += "'" + ei.getDouble("price") + "',";
                                            queryValues += "'" + ei.getDouble("qty") + "',";
                                            queryValues += "'2',";
                                            queryValues += "'5',";
                                            queryValues += "'1',";
                                            queryValues += "'"+emenuserver.Types.ChildItemType.OPTIONAL+"'";
                                            String extraQuery = "INSERT INTO Invoice_Order_Details "
                                                    + "(ID, Invoice_Order_ID, Parent, DrugID, ModifiedName, Price, Quantity, Supplier, Store, myStat, ComponentType) VALUES (" + queryValues + ")";
                                            try(Statement stmt3 = con.createStatement()) {
                                                stmt3.execute(extraQuery);
                                                stmt3.close();
                                            }
                                        }
                                    }
                                    if(hasWithout) {
                                        JSONArray extraItems = item.getJSONArray("withoutitems");
                                        for(int e = 0; e < extraItems.length(); e++) {
                                            JSONObject ei = extraItems.getJSONObject(e);
                                            int eID = getNextIdMain("Invoice_Order_Details");
                                            String queryValues = "'" + eID + "',";
                                            queryValues += "'" + invoiceId + "',";
                                            queryValues += "'" + Id + "',";
                                            queryValues += "'" + ei.getInt("id") + "',";
                                            queryValues += "'" + ei.getString("name") + "',";
                                            queryValues += "'" + ei.getDouble("price") + "',";
                                            queryValues += "'" + ei.getDouble("qty") + "',";
                                            queryValues += "'2',";
                                            queryValues += "'5',";
                                            queryValues += "'1',";
                                            queryValues += "'"+emenuserver.Types.ChildItemType.WITHOUT+"'";
                                            String extraQuery = "INSERT INTO Invoice_Order_Details "
                                                    + "(ID, Invoice_Order_ID, Parent, DrugID, ModifiedName, Price, Quantity, Supplier, Store, myStat, ComponentType) VALUES (" + queryValues + ")";
                                            try(Statement stmt3 = con.createStatement()) {
                                                stmt3.execute(extraQuery);
                                                stmt3.close();
                                            }
                                        }
                                    }
                                }
                            }
                            return true;
                        } else {
                            System.out.println("rows was not done");
                            return false;
                        }
                    } catch (JSONException ex) {
                        Logger.getLogger(eMenuSQL.class.getName()).log(Level.SEVERE, null, ex);
                    }
        } catch (SQLException ex) {
            Logger.getLogger(eMenuSQL.class.getName()).log(Level.SEVERE, null, ex);
        }
       return false;
    }
    
    public JSONArray getExtraitems() {
      JSONArray returnedArray = new JSONArray();
        try {
            Connection con = this.Connect();
            returnedArray.put(new JSONObject().put("Msg", "extra_items").put("info", ""));
            if(con != null) {
                String query = "SELECT * FROM CombinedItems WHERE ChildType = " + emenuserver.Types.ChildItemType.EXTRA;
                //Sting query = "SELECT * FROM CombinedItems WHERE ChildType = 2";
                try(Statement stmt = con.createStatement()) {
                    ResultSet rs = stmt.executeQuery(query);
                    JSONArray extraItems = new JSONArray();
                    while(rs.next()) {
                        JSONObject item = new JSONObject();
                        item.put("itemId", rs.getInt("MainItem"));
                        item.put("extraId", rs.getInt("ChildItem"));
                        item.put("effectsPrice", rs.getInt("AfectsPrice"));
                        item.put("qty", rs.getDouble("Qty"));
                        String query2 = "SELECT Name, Price FROM dbo.[Trade Names] WHERE Id = '" + rs.getInt("ChildItem") +"'";
                        try(Statement stmt2 = con.createStatement()) {
                            ResultSet rs2 = stmt2.executeQuery(query2);
                            if(rs2.next()) {
                                item.put("extraName", rs2.getString("Name"));
                                item.put("extraPrice", rs2.getDouble("Price"));
                            }
                            stmt2.close();
                        }
                        extraItems.put(item);
                    }
                    returnedArray.put(extraItems);
                    return returnedArray;
                }
            }
        } catch (SQLException | JSONException ex) {
            Logger.getLogger(eMenuSQL.class.getName()).log(Level.SEVERE, null, ex);
        }
      return null;
    }
    
    public JSONArray getChooseItems() {
              JSONArray returnedArray = new JSONArray();
        try {
            Connection con = this.Connect();
            returnedArray.put(new JSONObject().put("Msg", "choose_items").put("info", ""));
            if(con != null) {
                String query = "SELECT * FROM CombinedItems WHERE ChildType = " + emenuserver.Types.ChildItemType.OPTIONAL;
                try(Statement stmt = con.createStatement()) {
                    ResultSet rs = stmt.executeQuery(query);
                    JSONArray extraItems = new JSONArray();
                    while(rs.next()) {
                        JSONObject item = new JSONObject();
                        item.put("itemId", rs.getInt("MainItem"));
                        item.put("extraId", rs.getInt("ChildItem"));
                        item.put("effectsPrice", rs.getInt("AfectsPrice"));
                        item.put("qty", rs.getDouble("Qty"));
                        String query2 = "SELECT Name, Price FROM dbo.[Trade Names] WHERE Id = '" + rs.getInt("ChildItem") +"'";
                        try(Statement stmt2 = con.createStatement()) {
                            ResultSet rs2 = stmt2.executeQuery(query2);
                            if(rs2.next()) {
                                item.put("extraName", rs2.getString("Name"));
                                item.put("extraPrice", rs2.getDouble("Price"));
                            }
                            stmt2.close();
                        }
                        extraItems.put(item);
                    }
                    returnedArray.put(extraItems);
                    return returnedArray;
                }
            }
        } catch (SQLException | JSONException ex) {
            Logger.getLogger(eMenuSQL.class.getName()).log(Level.SEVERE, null, ex);
        }
      return null;
    }
    public JSONArray getWithoutItems() {
        JSONArray returnedArray = new JSONArray();
        try {
            Connection con = this.Connect();
            returnedArray.put(new JSONObject().put("Msg", "without_items").put("info", ""));
            if(con != null) {
                String query = "SELECT * FROM CombinedItems WHERE ChildType = " + emenuserver.Types.ChildItemType.WITHOUT;
                try(Statement stmt = con.createStatement()) {
                    ResultSet rs = stmt.executeQuery(query);
                    JSONArray extraItems = new JSONArray();
                    while(rs.next()) {
                        JSONObject item = new JSONObject();
                        item.put("itemId", rs.getInt("MainItem"));
                        item.put("extraId", rs.getInt("ChildItem"));
                        item.put("effectsPrice", rs.getInt("AfectsPrice"));
                        item.put("qty", rs.getDouble("Qty"));
                        String query2 = "SELECT Name, Price FROM dbo.[Trade Names] WHERE Id = '" + rs.getInt("ChildItem") +"'";
                        try(Statement stmt2 = con.createStatement()) {
                            ResultSet rs2 = stmt2.executeQuery(query2);
                            if(rs2.next()) {
                                item.put("extraName", rs2.getString("Name"));
                                item.put("extraPrice", rs2.getDouble("Price"));
                            }
                            stmt2.close();
                        }
                        extraItems.put(item);
                    }
                    returnedArray.put(extraItems);
                    return returnedArray;
                }
            }
        } catch (SQLException | JSONException ex) {
            Logger.getLogger(eMenuSQL.class.getName()).log(Level.SEVERE, null, ex);
        }
      return null;
    }
    private int getNextId(String tableName) {
        try {
            Connection con = this.Connect();
            if(con != null) {
                String query = "SELECT max(Id) as Id FROM  " + tableName;
                try(Statement stmt = con.createStatement()) {
                    ResultSet rs = stmt.executeQuery(query);
                    if(rs.next()) {
                        return rs.getInt("Id") + 1;
                    }
                }
            }
 
        } catch (SQLException ex) {
            Logger.getLogger(eMenuSQL.class.getName()).log(Level.SEVERE, null, ex);
        }
        return -1;
    }
        private int getNextIdMain(String tableName) {
        try {
            Connection con = this.ConnectToMain();
            if(con != null) {
                String query = "SELECT max(Id) as Id FROM  " + tableName;
                try(Statement stmt = con.createStatement()) {
                    ResultSet rs = stmt.executeQuery(query);
                    if(rs.next()) {
                        return rs.getInt("Id") + 1;
                    }
                }
            }
 
        } catch (SQLException ex) {
            Logger.getLogger(eMenuSQL.class.getName()).log(Level.SEVERE, null, ex);
        }
        return -1;
    }
    private boolean InvoiceExists(int invoiceId) {
        try {
            Connection con = this.Connect();
            if(con != null) {
                String query = "SELECT ID FROM Invoice_Order WHERE ID = '" + invoiceId + "'";
                try(Statement stmt = con.createStatement()) {
                    ResultSet rs = stmt.executeQuery(query);
                    if(rs.next()) return true;
                    else return false;
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(eMenuSQL.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }
    
    
    private int getNextCO(int IT)
    {
        try {
            Connection con = this.Connect();
            if(con != null) {
                String query = "SELECT max(CO) as CO FROM Invoice_Order WHERE Type = 1 AND Store = 5 AND IT = '" + IT + "'" ;
                try(Statement stmt = con.createStatement()) {
                    ResultSet rs = stmt.executeQuery(query);
                    if(rs.next()) {
                        return rs.getInt("CO") + 1;
                    }
                }
            }
 
        } catch (SQLException ex) {
            Logger.getLogger(eMenuSQL.class.getName()).log(Level.SEVERE, null, ex);
        }
        return 1;
    }
    
    
    public JSONObject GetCstWithId(int cst)
    {
        try {
            Connection con = this.Connect();
            if(con != null)
            {
                String query = "SELECT * FROM Customers WHERE ID = '" + cst + "'";
                try(Statement stmt = con.createStatement())
                {
                    ResultSet rs = stmt.executeQuery(query);
                    if(rs.next())
                    {
                        JSONObject user = new JSONObject();
                        try {
                            user.put("name", rs.getString("Name"));
                            user.put("phone", rs.getString("Telephone"));
                            user.put("id", cst);
                            return user;
                        } catch (JSONException ex) {
                            Logger.getLogger(eMenuSQL.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(eMenuSQL.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return null;
    }
    
    public String getCashierName(int id)
    {
        try {
            Connection con = this.Connect();
            if(con != null)
            {
                String query = "SELECT Name from dbo.Users WHERE Id = " + id;
                try(Statement stmt = con.createStatement())
                {
                    ResultSet rs = stmt.executeQuery(query);
                    if(rs.next())
                    {
                        return rs.getString("Name");
                    }
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(eMenuSQL.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return "";
    }
    
    public String getCaptainName(int id)
    {
        try {
            Connection con = this.Connect();
            if(con != null)
            {
                String query = "SELECT Name from dbo.SalesPersons WHERE Id = " + id;
                try(Statement stmt = con.createStatement())
                {
                    ResultSet rs = stmt.executeQuery(query);
                    if(rs.next())
                    {
                        return rs.getString("Name");
                    }
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(eMenuSQL.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return "";
    }
    
    public int getInvoiceCO(int invoiceID)
    {
        try {
            Connection con = this.Connect();
            if(con != null)
            {
                String query = "SELECT CO from dbo.Invoice_Order WHERE Id = " + invoiceID;
                try(Statement stmt = con.createStatement())
                {
                    ResultSet rs = stmt.executeQuery(query);
                    if(rs.next())
                    {
                        return rs.getInt("CO");
                    }
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(eMenuSQL.class.getName()).log(Level.SEVERE, null, ex);
        }
        return -1;
    }
    
    public JSONArray getKitchenOrders()
    {
        try {
            Connection con = this.Connect();
            if(con != null)
            {
                JSONArray returnedArray = new JSONArray();
                returnedArray.put(new JSONObject().put("Msg", "kitchen_orders").put("extra" , ""));
                String query = "SELECT DISTINCT dbo.NewGridInvoiceDetails.Invoice_Order_Id as Id, dbo.Invoice_Order.CO as myCO, dbo.Invoice_Order.[myTable]," +
                                "dbo.Invoice_Order.[It] FROM dbo.NewGridInvoiceDetails INNER JOIN dbo.Invoice_Order ON dbo.NewGridInvoiceDetails.Invoice_Order_Id = dbo.Invoice_Order.Id" +
                                "Where (dbo.NewGridInvoiceDetails.Parent = 0)" +
                                "And (dbo.NewGridInvoiceDetails.Kitchen Is Null And Not dbo.NewGridInvoiceDetails.Ordered Is Null)" +
                                "AND  (NOT (dbo.NewGridInvoiceDetails.Ordered IS NULL)) AND  (dbo.NewGridInvoiceDetails.SourceId=1)";
                try(Statement stmt = con.createStatement())
                {
                    ResultSet rs = stmt.executeQuery(query);
                    JSONArray orders = new JSONArray();
                    while(rs.next())
                    {
                        JSONObject order = new JSONObject();
                        order.put("Id", rs.getInt("Id"));
                        order.put("CO", rs.getInt("myCO"));
                        order.put("tableNum", rs.getInt("myTable"));
                        order.put("IT", rs.getInt("it"));
                        orders.put(order);
                    }
                    returnedArray.put(orders);
                    return returnedArray;
                } 
            }
        } catch (SQLException | JSONException ex) {
            Logger.getLogger(eMenuSQL.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
    public boolean UserExists(String username)
    {
        try {
            Connection con = this.Connect();
            if(con != null)
            {
                String query = "SELECT * FROM Customers WHERE Name = '" + username + "'";
                try(Statement stmt = con.createStatement())
                {
                    ResultSet rs = stmt.executeQuery(query);
                    return rs.next();
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(eMenuSQL.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }
    
    public String GetPrinterNameFromSource(String source)
    {
        try {
            Connection con = this.Connect();
            if(con != null)
            {
                String query = "SELECT ReportID From Sources WHERE Name = '" + source + "'";
                try(Statement stmt = con.createStatement())
                {
                    ResultSet rs = stmt.executeQuery(query);
                    if(rs.next())
                    {
                        int reportID = rs.getInt("ReportID");
                        String query2 = "SELECT Printer FROM ESIReports WHERE Id = " + reportID;
                        try(Statement stmt2 = con.createStatement())
                        {
                            ResultSet rs2 = stmt2.executeQuery(query2);
                            if(rs2.next())
                                return rs2.getString("Printer");
                        }
                    }
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(eMenuSQL.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "";
    }
    
    public String UpdateUser(String updateType, int userID, JSONObject message)
    {
        try {
            Connection con = this.Connect();
            if(con != null)
            {
                String query = "";
                switch(updateType)
                {
                    case "password":
                        String password = message.getString("new_password");
                        query = "UPDATE Customers SET Password = '" + password + "' WHERE ID = " + userID;
                        break;
                    case "email":
                        String email = message.getString("email");
                        query = "UPDATE Customers SET [E-mail] = '" + email + "' WHERE ID = "+ userID;
                        break;
                    case "username":
                        String username = message.getString("username");
                        if(UserExists(username))
                        {
                            return "Username already exists, please choose a different one";
                        }
                        query = "UPDATE Customers SET Name = '" + username + "' WHERE ID = " + userID;
                        break;
                    case "usernamenemail":
                        String username2 = message.getString("username");
                        String email2 = message.getString("email");
                        if(UserExists(username2))
                        {
                            return "Username already exists, please choose a different one";
                        }
                        query = "UPDATE Customers SET Name = '" + username2 + "' , [E-mail] = '" + email2 + "' WHERE ID = " + userID;
                        break;
                    case "address":
                        String address = message.getString("address");
                        double lat = message.getDouble("lat");
                        double longt = message.getDouble("long");
                        query = "UPDATE Customers SET Address1 = '" + address + "' , Latitude = '" + lat + "' , Longitude = '" + longt + "' WHERE ID = " + userID;
                        break;
                    case "phone":
                        String phone = message.getString("phone");
                        query = "UPDATE Customers SET Telephone = '" + phone + "' WHERE ID = " + userID;
                        break;
                }
                if(!query.equals(""))
                {
                    try(Statement stmt = con.createStatement())
                    {
                        int rows = stmt.executeUpdate(query);
                        if(rows > 0)
                            return "Profile Successfully updated";
                        else
                            return "Profile Update failed";
                    }
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(eMenuSQL.class.getName()).log(Level.SEVERE, null, ex);
        } catch (JSONException ex) {
            Logger.getLogger(eMenuSQL.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return "";
    }
    
    public int getCstIDWithName(String name)
    {
        try {
            Connection con = this.ConnectToMain();
            if(con != null)
            {
                String query = "SELECT ID FROM Customers WHERE Name = '" + name+ "'";
                try(Statement stmt = con.createStatement())
                {
                    ResultSet rs = stmt.executeQuery(query);
                    if(rs.next())
                        return rs.getInt("ID");
                    else
                        return -1;
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(eMenuSQL.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return -1;
    }
}