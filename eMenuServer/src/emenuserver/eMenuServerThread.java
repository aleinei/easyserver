package emenuserver;

import GUI.MainWindow;
import java.io.IOException;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONArray;
import emenuserver.Database.eMenuSQL;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.JOptionPane;
public class eMenuServerThread extends Thread{
    public Socket serverSocket;
    MainWindow callerWindow;
    String DBName;
    int StoreType;
    public String id = "none";
    public String itemsType = "all";
    public eMenuServerThread(Socket socket, MainWindow callerwindow) throws ClassNotFoundException {
        this.serverSocket = socket;
        this.callerWindow = callerwindow;
        callerWindow.logMessage("New Client Connected");
    }

    @Override
    public void run() {
        try {
            PrintWriter out = new PrintWriter(new OutputStreamWriter(serverSocket.getOutputStream(), StandardCharsets.UTF_8), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(serverSocket.getInputStream(), "UTF-8"));
            String inputLine;
            while((inputLine = in.readLine()) != null) {
                 try {
                    JSONObject obj = new JSONObject(inputLine);
                    if(obj.getString("Msg").toLowerCase().equals("view_items")) {
                      eMenuSQL SQL = new eMenuSQL(DBName);
                      JSONArray items = SQL.getItems();
                      if(items.length() > 0) {
                        out.println(items.toString());
                      }
                    } else if(obj.getString("Msg").toLowerCase().equals("all_sections")) {
                        eMenuSQL SQL = new eMenuSQL(obj.getString("dbName"));
                        JSONArray categories = SQL.getSections(callerWindow, StoreType);
                        if(categories.length() > 0) {
                            out.println(categories.toString());
                            callerWindow.logMessage(categories.toString());
                        } else {
                            JSONArray array = new JSONArray();
                            JSONObject msg = new JSONObject();
                            msg.put("Msg", "all_sections");
                            msg.put("info", "no_sections");
                            array.put(msg);
                            out.println(array.toString());
                        }

                    } else if(obj.getString("Msg").toLowerCase().equals("section_categories")) {
                       eMenuSQL SQL = new eMenuSQL(obj.getString("dbName"));
                       JSONArray categories = SQL.getCategories(callerWindow, StoreType);
                       out.println(categories.toString());
                    } else if(obj.getString("Msg").toLowerCase().equals("section_categories_id")) {
                       eMenuSQL SQL = new eMenuSQL(DBName);
                       int id = obj.getInt("section_id");
                       JSONArray categories = SQL.getCategories(callerWindow, id);
                       out.println(categories.toString());
                    }else if(obj.getString("Msg").toLowerCase().equals("new_order")) {
                        JSONObject order_details = obj.getJSONObject("order");
                        eMenuSQL SQL = new eMenuSQL(DBName);
                        int invoiceId;
                        if((invoiceId = SQL.InsertNewOrder(order_details)) != -1) {
                            callerWindow.logMessage("Order Sent");
                            int tableNumber = order_details.getInt("table_num");
                            JSONObject invoice = new JSONObject();
                            invoice.put("Msg", "new_invoice");
                            invoice.put("db", DBName);
                            invoice.put("table_num", tableNumber);
                            invoice.put("invoice_id", invoiceId);
                            for(eMenuServerThread t : callerWindow.clients) {
                               t.SendMessage(invoice.toString());
                            }
                        } else {
                            callerWindow.logMessage("Order Failed");
                        }
                    } else if(obj.getString("Msg").toLowerCase().equals("usernames")) {
                        eMenuSQL SQL = new eMenuSQL(DBName);
                        JSONArray usernames = SQL.GetUsernames();
                       out.println(usernames.toString());
                    } else if(obj.getString("Msg").toLowerCase().equals("verification")) {
                            String type = obj.getString("info");
                            String password = obj.getString("password");
                            eMenuSQL SQL = new eMenuSQL(DBName);
                            JSONArray me = SQL.UserAuthentication(type, password);
                            if(me != null) {
                               out.println(me.toString());
                            } else 
                            {
                                System.out.println("user is null");
                            }
                    } else if(obj.getString("Msg").toLowerCase().equals("category_items")) {
                        eMenuSQL SQL = new eMenuSQL(obj.getString("dbName"));
                        JSONArray items = SQL.getCategoryItems(callerWindow);
                        if(items != null)
                            out.println(items.toString());
                    } else if(obj.getString("Msg").toLowerCase().equals("unpaid_invoices")) {
                        eMenuSQL SQL = new eMenuSQL(DBName);
                        JSONObject invoices = SQL.getUnpaidInvoices(obj.getInt("userID"));
                        if(invoices != null) {
                            out.println(invoices.toString());
                        } else {
                            callerWindow.logMessage("No invoices or id is incorrect");
                        }
                    } else if(obj.getString("Msg").toLowerCase().equals("close_connection")) {
                        Close();
                        break;
                    } else if(obj.getString("Msg").toLowerCase().equals("invoice_details")) {
                        eMenuSQL SQL = new eMenuSQL(DBName);
                        JSONObject items = SQL.getInvoiceDetails(obj.getInt("invoice_id"));
                        if(items != null) {
                            out.println(items.toString());
                        }
                    } else if(obj.getString("Msg").toLowerCase().equals("user_verify")) {
                        eMenuSQL SQL = new eMenuSQL(DBName);
                        String phone = obj.getString("phone");
                        JSONObject user = SQL.verifyCustomer(callerWindow, phone);
                        if(user != null) {
                            out.println(user.toString());
                            System.out.println("Sent" + user);
                        } else {
                            System.out.println("Sent not");
                        }
                    } else if(obj.getString("Msg").toLowerCase().equals("new_user")) {
                            eMenuSQL sql = new eMenuSQL(DBName);
                            String username = obj.getString("username");
                            String password = obj.getString("password");
                            String phone = obj.getString("phone");
                            String email = obj.getString("email");
                            String address1 = obj.getString("address1");
                            String address2 = obj.getString("building");
                            String floor = obj.getString("floor");
                            String apt = obj.getString("apt");
                            double lat = obj.getDouble("lat");
                            double longt = obj.getDouble("long");
                            eMenuSQL SQL = new eMenuSQL(DBName);
                            JSONObject user = SQL.createNewCustomer(callerWindow, -1, username, password, phone, email, address1,address2, floor, apt, lat, longt, false);
                            if(user != null) {
                                out.println(user);
                            }
                    } else if(obj.getString("Msg").toLowerCase().equals("user_created")) {
                        String username = obj.getString("username");
                        String password = obj.getString("password");
                        String phone = obj.getString("phone");
                        String email = obj.getString("email");
                        String address1 = obj.getString("address1");
                        String address2 = obj.getString("building");
                        String floor = obj.getString("floor");
                        String apt = obj.getString("apt");
                        double lat = obj.getDouble("lat");
                        double longt = obj.getDouble("long");
                        int ID = obj.getInt("id");
                        eMenuSQL SQL = new eMenuSQL(DBName);
                        JSONObject user = SQL.createNewCustomer(callerWindow, ID, username, password, phone, email, address1,address2, floor, apt, lat, longt, true);
                        if(user != null) {
                            out.println(user);
                        }
                    } else if(obj.getString("Msg").toLowerCase().equals("new_order_d")) {
                        int cstId = obj.getInt("user_id");
                        boolean isTakeAway = obj.getBoolean("takeaway");
                        double cost = obj.getDouble("cost");
                        DBName = obj.getString("dbName");
                        if(DBName.isEmpty()) {
                            callerWindow.logMessage("Empty db name, not submitting");
                            return;
                        }
                        eMenuSQL SQL = new eMenuSQL(DBName);
                        JSONObject user = SQL.getUser(cstId);
                        callerWindow.logMessage("Delivery order accepted");
                        JSONObject msg = new JSONObject();
                        msg.put("Msg", "print_order");
                        msg.put("items", obj.getJSONArray("items"));
                        Timestamp time = new Timestamp(new Date().getTime());
                        msg.put("time", time.getTime());
                        msg.put("d_time", obj.getString("d_time"));
                        msg.put("takeaway", isTakeAway);
                        msg.put("user", user);
                        if(!sendClientMessage(DBName, msg.toString())) {
                            SQL.saveInvoice(callerWindow, msg.toString());
                        }
                    } else if(obj.getString("Msg").toLowerCase().equals("extra_items")) {
                        eMenuSQL sql = new eMenuSQL(obj.getString("dbName"));
                        JSONArray returned = sql.getExtraitems();
                        if(returned != null) {
                            out.println(returned.toString());
                            callerWindow.logMessage("Query is correct");
                        } else {
                            callerWindow.logMessage("Query is not correct");
                        }
                    } else if(obj.getString("Msg").toLowerCase().equals("choose_items")) {
                         eMenuSQL sql = new eMenuSQL(obj.getString("dbName"));
                        JSONArray returned = sql.getChooseItems();
                        if(returned != null) {
                            out.println(returned.toString());
                            callerWindow.logMessage("Query is correct");
                            
                        } else {
                            callerWindow.logMessage("Query is not correct");
                        }
                    } else if (obj.getString("Msg").toLowerCase().equals("without_items")) {
                        eMenuSQL sql = new eMenuSQL(obj.getString("dbName"));
                        JSONArray returned = sql.getWithoutItems();
                        if(returned != null) {
                            out.println(returned.toString());
                            callerWindow.logMessage("Query is correct");
                            
                        } else {
                            callerWindow.logMessage("Query is not correct");
                        }                       
                    } else if(obj.getString("Msg").toLowerCase().equals("reg_db")) {
                        DBName = obj.getString("db");
                        String storeType = obj.getString("type");
                        if(storeType.equals("storeType"))
                            StoreType = Types.StoreTypes.Store;
                        else if(storeType.equals("storeClient")) {
                            StoreType = Types.StoreTypes.CLIENT;
                            itemsType = obj.getString("items_type");
                            eMenuSQL sql = new eMenuSQL(DBName);
                            sql.updateUsesStocks(callerWindow, DBName, itemsType.equals("stock"));
                        } 
                        else
                            StoreType = Types.StoreTypes.Cafe;
                        id = DBName + storeType + new Random().nextInt(100);
                    } else if(obj.getString("Msg").equals("kitchen_orders")) {
                        
                        eMenuSQL sql = new eMenuSQL(DBName);
                        JSONArray returned = sql.getKitchenOrders();
                        if(returned != null)
                        {
                            out.println(returned.toString());
                            callerWindow.logMessage("Kitchen orders sent");
                        }
                        else
                        {
                            callerWindow.logMessage("Kitchen orders failed");
                        }
                    } else if(obj.getString("Msg").equals("update_user"))
                    {
                        String change_type = obj.getString("change_type");
                        int userID = obj.getInt("user_id");
                        eMenuSQL SQl = new eMenuSQL(DBName);
                        JSONObject msg = new JSONObject();
                        msg.put("Msg", "user_update");
                        String messageString = SQl.UpdateUser(change_type, userID, obj);
                        msg.put("message", messageString);
                        if(!messageString.equals(""))
                        {
                            msg.put("user_updated", true);
                        }
                        else
                        {
                            msg.put("user_updated", false);
                        }
                        
                        out.println(msg.toString());
                    } else if(obj.getString("Msg").equals("d_order_done")) {
                        int invoiceID = obj.getInt("id");
                        double price = obj.getDouble("value");
                        eMenuSQL SQL = new eMenuSQL(DBName);
                        boolean isDone = SQL.insertNewOrderD(invoiceID, callerWindow, price);
                        if(isDone) {
                            callerWindow.logMessage("A new order has been placed for Merchent " + DBName);
                        } else {
                            callerWindow.logMessage("A new order was not placed for Merchent " + DBName);
                        }
                    } else if(obj.getString("Msg").equals("check_orders")) {
                            eMenuSQL sql = new eMenuSQL(DBName);
                            ArrayList<String> savedInvoices = sql.getSavedInvoice(callerWindow);
                            if(savedInvoices.size() > 0) {
                                for(String invoice : savedInvoices) {
                                    out.println(invoice);
                                }
                                sql.removeSavedInvoices(callerWindow);
                            }
                    } else if(obj.getString("Msg").equals("merchant_reg")) {
                        String merchentName = obj.getString("name");
                        String merchentPhone = obj.getString("phone");
                        String merchentCity = obj.getString("city");
                        String storeType = obj.getString("storetype");
                        String storeName = obj.getString("storename");
                        double lat = obj.getDouble("lat");
                        double lng = obj.getDouble("long");
                        eMenuSQL sql = new eMenuSQL(DBName);
                        boolean inserted = sql.insertNewMerchent(callerWindow, merchentName, merchentPhone, merchentCity, storeType, storeName, lat, lng);
                        if(inserted) {
                            String dialog = "A new Merchent has register, merchent info below please confirm with them\n\n";
                            dialog += "Merchent Name: " + merchentName + "\n\n";
                            dialog += "Merchent Phone: " + merchentPhone + "\n\n";
                            dialog += "Merchent Store name: " + storeName + "\n\n";
                            JOptionPane.showMessageDialog(callerWindow, dialog);
                            callerWindow.logMessage("New merchent registered " + merchentName );
                            
                        } 
                    } else if(obj.getString("Msg").equals("get_shops")) {
                        eMenuSQL sql = new eMenuSQL("");
                        JSONArray shops = sql.getAllShops();
                        JSONObject msg = new JSONObject();
                        msg.put("Msg", "registered_shops");
                        msg.put("shops", shops);
                        if(shops.length() > 0) {
                            SendMessage(msg.toString());
                        }
                    }
                    callerWindow.logMessage(inputLine);
                } catch (JSONException ex) {
                    callerWindow.logMessage("Not a json message " + inputLine);
                    callerWindow.logMessage(ex.getMessage());
                } catch (SQLException ex) {
                    Logger.getLogger(eMenuServerThread.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            
        } catch (IOException ex) {
            Logger.getLogger(eMenuServerThread.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    public void Close() {
        try {
            serverSocket.close();
            callerWindow.removeClient(this);
        } catch (IOException ex) {
            Logger.getLogger(eMenuServerThread.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    public void SendMessage(String message) {
        try {
            PrintWriter output = new PrintWriter( new OutputStreamWriter(serverSocket.getOutputStream(), StandardCharsets.UTF_8), true);
            output.println(message);
            callerWindow.logMessage("Sent a message to " + serverSocket.getRemoteSocketAddress());
        } catch (IOException ex) {
            Logger.getLogger(eMenuServerThread.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public class PingServer implements Runnable {

        @Override
        public void run() {
            while(serverSocket.isConnected()) {
                try {
                    PrintWriter output = new PrintWriter( new OutputStreamWriter(serverSocket.getOutputStream(), StandardCharsets.UTF_8), true);
                    output.print("Pinging");
                } catch (IOException ex) {
                    Close();
                    break;
                }
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException ex) {
                    Logger.getLogger(eMenuServerThread.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }
    
    public boolean sendClientMessage(String dbName, String msg) {
        boolean sent = false;
         for(int e = 0; e < callerWindow.clients.size(); e++) {
           if(callerWindow.clients.get(e) == this) continue;
           if(callerWindow.clients.get(e).StoreType == Types.StoreTypes.CLIENT && callerWindow.clients.get(e).DBName.equalsIgnoreCase(dbName)) {
               callerWindow.clients.get(e).SendMessage(msg);
                sent = true;
           }
         }
         return sent;
    }
}