/*
 * Template JAVA User Interface
 * =============================
 *
 * Database Management Systems
 * Department of Computer Science &amp; Engineering
 * University of California - Riverside
 *
 * Target DBMS: 'Postgres'
 *
 */


import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.io.File;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;
import java.util.ArrayList;
import java.io.IOException;

/**
 * This class defines a simple embedded SQL utility class that is designed to
 * work with PostgreSQL JDBC drivers.
 *
 */
public class Messenger {

   // reference to physical database connection.
   private Connection _connection = null;

   // handling the keyboard inputs through a BufferedReader
   // This variable can be global for convenience.
   static BufferedReader in = new BufferedReader(
                                new InputStreamReader(System.in));

   /**
    * Creates a new instance of Messenger
    *
    * @param hostname the MySQL or PostgreSQL server hostname
    * @param database the name of the database
    * @param username the user name used to login to the database
    * @param password the user login password
    * @throws java.sql.SQLException when failed to make a connection.
    */
   public Messenger (String dbname, String dbport, String user, String passwd) throws SQLException {

      System.out.print("Connecting to database...");
      try{
         // constructs the connection URL
         String url = "jdbc:postgresql://localhost:" + dbport + "/" + dbname;
         System.out.println ("Connection URL: " + url + "\n");

         // obtain a physical connection
         this._connection = DriverManager.getConnection(url, user, passwd);
         System.out.println("Done");
      }catch (Exception e){
         System.err.println("Error - Unable to Connect to Database: " + e.getMessage() );
         System.out.println("Make sure you started postgres on this machine");
         System.exit(-1);
      }//end catch
   }//end Messenger

   /**
    * Method to execute an update SQL statement.  Update SQL instructions
    * includes CREATE, INSERT, UPDATE, DELETE, and DROP.
    *
    * @param sql the input SQL string
    * @throws java.sql.SQLException when update failed
    */
   public void executeUpdate (String sql) throws SQLException {
      // creates a statement object
      Statement stmt = this._connection.createStatement ();

      // issues the update instruction
      stmt.executeUpdate (sql);

      // close the instruction
      stmt.close ();
   }//end executeUpdate

   /**
    * Method to execute an input query SQL instruction (i.e. SELECT).  This
    * method issues the query to the DBMS and outputs the results to
    * standard out.
    *
    * @param query the input query string
    * @return the number of rows returned
    * @throws java.sql.SQLException when failed to execute the query
    */
   public int executeQueryAndPrintResult (String query) throws SQLException {
      // creates a statement object
      Statement stmt = this._connection.createStatement ();

      // issues the query instruction
      ResultSet rs = stmt.executeQuery (query);

      /*
       ** obtains the metadata object for the returned result set.  The metadata
       ** contains row and column info.
       */
      ResultSetMetaData rsmd = rs.getMetaData ();
      int numCol = rsmd.getColumnCount ();
      int rowCount = 0;

      // iterates through the result set and output them to standard out.
      boolean outputHeader = true;
      while (rs.next()){
	 if(outputHeader){
	    for(int i = 1; i <= numCol; i++){
		System.out.print(rsmd.getColumnName(i) + "\t");
	    }
	    System.out.println();
	    outputHeader = false;
	 }
         for (int i=1; i<=numCol; ++i)
            System.out.print (rs.getString (i) + "\t");
         System.out.println ();
         ++rowCount;
      }//end while
      stmt.close ();
      return rowCount;
   }//end executeQuery

   /**
    * Method to execute an input query SQL instruction (i.e. SELECT).  This
    * method issues the query to the DBMS and returns the results as
    * a list of records. Each record in turn is a list of attribute values
    *
    * @param query the input query string
    * @return the query result as a list of records
    * @throws java.sql.SQLException when failed to execute the query
    */
   public List<List<String>> executeQueryAndReturnResult (String query) throws SQLException { 
      // creates a statement object 
      Statement stmt = this._connection.createStatement (); 
 
      // issues the query instruction 
      ResultSet rs = stmt.executeQuery (query); 
 
      /* 
       ** obtains the metadata object for the returned result set.  The metadata 
       ** contains row and column info. 
       */ 
      ResultSetMetaData rsmd = rs.getMetaData (); 
      int numCol = rsmd.getColumnCount (); 
      int rowCount = 0; 
 
      // iterates through the result set and saves the data returned by the query. 
      boolean outputHeader = false;
      List<List<String>> result  = new ArrayList<List<String>>(); 
      while (rs.next()){
          List<String> record = new ArrayList<String>(); 
         for (int i=1; i<=numCol; ++i) 
            record.add(rs.getString (i)); 
         result.add(record); 
      }//end while 
      stmt.close (); 
      return result; 
   }//end executeQueryAndReturnResult

   /**
    * Method to execute an input query SQL instruction (i.e. SELECT).  This
    * method issues the query to the DBMS and returns the number of results
    *
    * @param query the input query string
    * @return the number of rows returned
    * @throws java.sql.SQLException when failed to execute the query
    */
   public int executeQuery (String query) throws SQLException {
       // creates a statement object
       Statement stmt = this._connection.createStatement ();

       // issues the query instruction
       ResultSet rs = stmt.executeQuery (query);

       int rowCount = 0;

       // iterates through the result set and count nuber of results.
       if(rs.next()){
          rowCount++;
       }//end while
       stmt.close ();
       return rowCount;
   }

   /**
    * Method to fetch the last value from sequence. This
    * method issues the query to the DBMS and returns the current 
    * value of sequence used for autogenerated keys
    *
    * @param sequence name of the DB sequence
    * @return current value of a sequence
    * @throws java.sql.SQLException when failed to execute the query
    */
   public int getCurrSeqVal(String sequence) throws SQLException {
	Statement stmt = this._connection.createStatement ();
	
	ResultSet rs = stmt.executeQuery (String.format("Select currval('%s')", sequence));
	if (rs.next())
		return rs.getInt(1);
	return -1;
   }

   /**
    * Method to close the physical connection if it is open.
    */
   public void cleanup(){
      try{
         if (this._connection != null){
            this._connection.close ();
         }//end if
      }catch (SQLException e){
         // ignored.
      }//end try
   }//end cleanup

   /**
    * The main execution method
    *
    * @param args the command line arguments this inclues the <mysql|pgsql> <login file>
    */
   public static void main (String[] args) {
      if (args.length != 3) {
         System.err.println (
            "Usage: " +
            "java [-classpath <classpath>] " +
            Messenger.class.getName () +
            " <dbname> <port> <user>");
         return;
      }//end if
      
      Greeting();
      Messenger esql = null;
      try{
         // use postgres JDBC driver.
         Class.forName ("org.postgresql.Driver").newInstance ();
         // instantiate the Messenger object and creates a physical
         // connection.
         String dbname = args[0];
         String dbport = args[1];
         String user = args[2];
         esql = new Messenger (dbname, dbport, user, "");

         boolean keepon = true;
         while(keepon) {
            // These are sample SQL statements
            System.out.println("MAIN MENU");
            System.out.println("---------");
            System.out.println("1. Create user");
            System.out.println("2. Log in");
            System.out.println("3. Change Password");
			System.out.println("4. Delete Account");
            System.out.println("9. < EXIT");
            String authorisedUser = null;
            switch (readChoice()){
               case 1: CreateUser(esql); break;
               case 2: authorisedUser = LogIn(esql); break;
               case 3: ChangePassword(esql); break; 
               case 4: DeleteAccount(esql); break;
               case 9: keepon = false; break;
               default : System.out.println("Unrecognized choice!"); break;
            }//end switch
            if (authorisedUser != null) {
              boolean usermenu = true;
              while(usermenu) {
                System.out.println("MAIN MENU");
                System.out.println("---------");
                System.out.println("1. Add to contact list");
                System.out.println("2. Browse contact list");
                System.out.println("3. Remove contact");
                System.out.println("4. Add to block list");
                System.out.println("5. Browse block list");
				System.out.println("6. Remove blocked contact");
                System.out.println("7. Messages Menu");
                System.out.println(".........................");
                System.out.println("9. Log out");
                switch (readChoice()){
                   case 1: AddToContact(esql, authorisedUser); break;
                   case 2: ListContacts(esql, authorisedUser); break;
                   case 3: RemoveContact(esql, authorisedUser); break;
                   case 4: AddToBlock(esql, authorisedUser); break;
                   case 5: ListBlocks(esql, authorisedUser); break;
                   case 6: RemoveBlocked(esql, authorisedUser); break;
                   case 7: MessageMenu(esql, authorisedUser); break;
                   //case 3: NewMessage(esql); break;
                   case 9: usermenu = false; break;
                   default : System.out.println("Unrecognized choice!"); break;
                }
              }
            }
         }//end while
      }catch(Exception e) {
         System.err.println (e.getMessage ());
      }finally{
         // make sure to cleanup the created table and close the connection.
         try{
            if(esql != null) {
               System.out.print("Disconnecting from database...");
               esql.cleanup ();
               System.out.println("Done\n\nBye !");
            }//end if
         }catch (Exception e) {
            // ignored.
         }//end try
      }//end try
   }//end main
  
   public static void Greeting(){
      System.out.println(
         "\n\n*******************************************************\n" +
         "              User Interface      	               \n" +
         "*******************************************************\n");
   }//end Greeting

   /*
    * Reads the users choice given from the keyboard
    * @int
    **/
   public static int readChoice() {
      int input;
      // returns only if a correct value is given.
      do {
         System.out.print("Please make your choice: ");
         try { // read the integer, parse it and break.
            input = Integer.parseInt(in.readLine());
            break;
         }catch (Exception e) {
            System.out.println("Your input is invalid!");
            continue;
         }//end try
      }while (true);
      return input;
   }//end readChoice

   /*
    * Creates a new user with privided login, passowrd and phoneNum
    * An empty block and contact list would be generated and associated with a user
    **/
   public static void CreateUser(Messenger esql){
      try{
         System.out.print("\tEnter user login: ");
         String login = in.readLine();
         System.out.print("\tEnter user password: ");
         String password = in.readLine();
         System.out.print("\tEnter user phone: ");
         String phone = in.readLine();

	 //Creating empty contact\block lists for a user
	 esql.executeUpdate("INSERT INTO USER_LIST(list_type) VALUES ('block')");
	 int block_id = esql.getCurrSeqVal("user_list_list_id_seq");
         esql.executeUpdate("INSERT INTO USER_LIST(list_type) VALUES ('contact')");
	 int contact_id = esql.getCurrSeqVal("user_list_list_id_seq");
         
	 String query = String.format("INSERT INTO USR (phoneNum, login, password, block_list, contact_list) VALUES ('%s','%s','%s',%s,%s)", phone, login, password, block_id, contact_id);

         esql.executeUpdate(query);
         System.out.println ("User successfully created!");
      }catch(Exception e){
         System.err.println (e.getMessage ());
      }
   }//end
   
   /*
    * Check log in credentials for an existing user
    * @return User login or null is the user does not exist
    **/
   public static String LogIn(Messenger esql){
      try{
         System.out.print("\tEnter user login: ");
         String login = in.readLine();
         System.out.print("\tEnter user password: ");
         String password = in.readLine();

         String query = String.format("SELECT * FROM Usr WHERE login = '%s' AND password = '%s'", login, password);
         int userNum = esql.executeQuery(query);
	 if (userNum > 0)
		return login;
         return null;
      }catch(Exception e){
         System.err.println (e.getMessage ());
         return null;
      }
   }//end

/*
=================================================================================================
=================================================================================================
=================================================================================================
=================================================================================================
BEGINNING OF IMPLEMENTATION
*/

   public String executeQueryString(String query) throws SQLException{
      // Your code goes here.
      // ... just a string returning verision of executeQuery 
      // ...
      Statement mystatement = this._connection.createStatement();
      ResultSet myresultset = mystatement.executeQuery(query);
      myresultset.next();
      String returnValue = myresultset.getString("returnValue");
      mystatement.close();
      return returnValue;
   }//end

   public static void ChangePassword(Messenger esql){
      // Your code goes here.
      // ... CASE 3 in initial menu
      // ...
      try{
      	  System.out.println("\nEnter login:");
      	  String login = in.readLine();
      	  System.out.println("\nEnter old password:");
      	  String oldpass = in.readLine();
      	  System.out.println("\nEnter new password:");
      	  String newpass = in.readLine();
      	  
      	  String query = String.format("SELECT ", login, oldpass, newpass);
      	  String returnval = esql.executeQueryString(query);
      	  
      	  if(returnval.isEmpty()){
      	  	  System.out.println("Password changed successfully\n");
      	  }
      	  
      	  else
      	  	  System.out.println(returnval);
      	}
      	catch(Exception e){
      		System.out.println(e.getMessage());
      		}
   }//end


   public static void DeleteAccount(Messenger esql){
      // Your code goes here.
      // ... CASE 4 in initial menu
      // ...
       try{
      	  System.out.println("\nYOU ARE ABOUT TO DELETE YOUR ACCOUNT");
      	  System.out.println("\nEnter login:");
      	  String login = in.readLine();
      	  System.out.println("\nEnter password:");
      	  String password = in.readLine();
      	  
      	  String query = String.format("SELECT ", login, password);
      	  String returnval = esql.executeQueryString(query);
      	  
      	  if(returnval.isEmpty()){
      	  	  System.out.println("ARE YOU SURE YOU WANT TO DELETE THIS ACCOUNT ('yes' or 'no')\n");
      	  	  String action = in.readLine();
      	  	  String yes = "yes";
      	  	  if(action == yes){
				System.out.println("DELETING ACCOUNT\n");
				String delQuery = String.format("DELETE FROM USR WHERE login = '%s'");
      	  	  }
      	  	  else
      	  	  	  System.out.println("Account was not deleted...Returning to main menu");
      	  }
      	  else
      	  	  System.out.println(returnval);
      	}
      	catch(Exception e){
      		System.out.println(e.getMessage());
      		}
 
   }//end

   public static void AddToContact(Messenger esql, String user){
      // Your code goes here.
      // ... CASE 1 in login menu
      // ...
      try{
      	  String personAdding = user;
      	  System.out.println("Enter Contacts Name\n");
      	  String personAdded = in.readLine();
      	  
      	  String query = String.format("SELECT ", personAdding, personAdded);
      	  String returnval = esql.executeQueryString(query);
      	  
      	  if(returnval.isEmpty()){
      	  	  System.out.println("Request Sent");
      	  }
      	  else
      	  	  System.out.println(returnval);
	  }
	  catch(Exception e){
	  	  System.err.println(e.getMessage());
	  }
   }//end

   public static void ListContacts(Messenger esql, String user){
      // Your code goes here.
      // ... CASE 2 in login menu
      // ...
      try{
      	  String personBrowsing = user;
      	  System.out.println("Listing contacts...\n");
      	  
      	  String query = String.format("SELECT ", personBrowsing);
      	  int rowCount = esql.executeQueryAndPrintResult(query);

      	  if(rowCount == 0){
      	  	  System.out.println("No contacts\n");
      	  }
      }
	  catch(Exception e){
	  	  System.err.println(e.getMessage());
	  }
   }//end

   public static void RemoveContact(Messenger esql, String user){
      // Your code goes here.
      // ... CASE 3 in login menu
      // ...
      System.out.println("Enter User you wish to remove\n");
      try{
      	  String personRemoving = user;
      	  System.out.println("Enter Contacts Name\n");
      	  String personRemoved = in.readLine();
      	  
      	  String query = String.format("SELECT ", personRemoving, personRemoved);
      	  String returnval = esql.executeQueryString(query);
      	  
      	  if(returnval.isEmtpy()){
      	  	  System outputmsg = String.format("Removed %s", personRemoved);
      	  	  System.out.println(outputmsg);
      	  }
      	  else
      	  	  System.out.println(returnval);
	  }
	  catch(Exception e){
	  	  System.err.println(e.getMessage());
	  }
   }//end

   public static void AddToBlock(Messenger esql, String user){
      // Your code goes here.
      // ... CASE 4 in login menu
      // ...
      try{
      	  String personBlocking = user;
      	  System.out.println("Enter User you wish to block\n");
      	  String personBlocked = in.readLine();
      	  
      	  String query = String.format("SELECT ", personBlocking, personBlocked);
      	  String returnval = esql.executeQueryString(query);
      	  
      	  if(returnval.isEmtpy()){
      	  	  System.out.println("Request Sent");
      	  }
      	  else
      	  	  System.out.println(returnval);
	  }
	  catch(Exception e){
	  	  System.err.println(e.getMessage());
	  }
   }//end

   public static void ListBlocks(Messenger esql, String user){
      // Your code goes here.
      // ... CASE 5 in login menu
      // ...
      try{
      	  String personBrowsing = user;
      	  System.out.println("Listing blocked contacts...\n");
      	  
      	  String query = String.format("SELECT ", personBrowsing);
      	  int rowCount = esql.executeQueryAndPrintResult(query);

      	  if(rowCount == 0){
      	  	  System.out.println("No contacts\n");
      	  }
      }
	  catch(Exception e){
	  	  System.err.println(e.getMessage());
	  }
   }//end

   public static void RemoveBlocked(Messenger esql, String user){
      // Your code goes here.
      // ... CASE 6 in login menu
      // ...
      System.out.println("Enter User you wish to remove\n");
      try{
      	  String personUnblocking = user;
      	  System.out.println("Enter Contacts Name\n");
      	  String personUnblocked = in.readLine();
      	  
      	  String query = String.format("SELECT ", personUnblocking, personUnblocked);
      	  String returnval = esql.executeQueryString(query);
      	  
      	  if(returnval.isEmtpy()){
      	  	  System.out.println("Unblocked");
      	  }
      	  else
      	  	  System.out.println(returnval);
	  }
	  catch(Exception e){
	  	  System.err.println(e.getMessage());
	  }
   }//end

   public static void MessageMenu(Messenger esql, String user){
      // Your code goes here.
      // ... CASE 7 in login menu
      // ...
      try{
      	  boolean stillView = true;
      	  String personViewing = user; 
      	  while(stillView){
      	  	  System.out.println("Message Main Menu\n");
      	  	  System.out.println("_________________\n");
      	  	  System.out.println("1. Write New Message");
      	  	  System.out.println("2. Edit Messages");
      	  	  System.out.println("3. Delete Messages");
      	  	  System.out.println("4. View my Chats");
      	  	  System.out.println("9. Back to main menu");
      	  	  
      	  	  switch(readChoice()){
      	  	  	  case 1: break;
      	  	  	  case 2: break;
      	  	  	  case 3: break;
      	  	  	  case 9: stillView = false; break;
      	  	  	  default : System.out.println("Unrecognized choice!"); break;
      	  	  	  }
      	 }
   	  }
	  catch(Exception e){
	  	  System.err.println(e.getMessage());
	  }

   }//end


   public static void NewMessage(Messenger esql, String user){
      // Your code goes here.
      // ... CASE 1 in message menu
      // ...
      try{
      	  String author = user;
      	  System.out.println("Enter recipiants");
      	  String recipiants = in.readLine();
      	  //make recipiants with a ; delimiter in query? and as user is entering
      	  //if they want multiple recipiants?
      	  System.out.println("Enter your message... ");
      	  String text;
      	  String totaltext;

      	  boolean takeInput = true;
      	  while(takeInput == true){
      	  	  text = in.readLine();
      	  	  totaltext = totaltext + text + "\n";
      	  	  if(text.equals("exit")){
      	  	  	  takeInput = false;
      	  	  }
      	  }
      	  
      	  String query = String.format("Select ", author, recipiants, totaltext);
      	  int rowCount = executeQuery(query);
      	  System.out.println("Message sent");
      	  if(rowCount > 0){
      	  	  System.out.println("Error sending message");
      	  }

      }
	  catch(Exception e){
	  	  System.err.println(e.getMessage());
	  }
   }//end 

   public static void EditMessage(Messenger esql, String user){
      // Your code goes here.
      // ... CASE 2 in message menu
      // ...
      try{
      	  String editer = user;
      	  System.out.println("Edit last message in which chat...\n");
      	  //user enters the chat id of the chat where he wants to edit latest of his messages
      	  String whichchat = in.readLine();
      	  System.out.println("Edit your message:");
      	  //user enters new message which will be edited from scratch
      	  String newmsg = in.readLine();

      	  System.out.println("Changing message...");
      	  String query = String.format("DELETE ", deleter, whichchat, newmsg);
      	  String editquery = esql.executeQueryString(query);
      }
	  catch(Exception e){
	  	  System.err.println(e.getMessage());
	  }

   }//end 

   public static void DeleteMessage(Messenger esql, String user){
      // Your code goes here.
      // ... CASE 3 in message menu
      // ...
      try{
      	  String deleter = user;
      	  System.out.println("Delete last message in which chat...\n");
      	  //user enters the chat id of the chat where he wants to delete his last message
      	  String whichchat = in.readLine();

      	  System.out.println("Deleting message...");
      	  String query = String.format("DELETE ", deleter, whichchat);
      	  String delquery = esql.executeQueryString(query);
      }
	  catch(Exception e){
	  	  System.err.println(e.getMessage());
	  }
   }//end 

   public static void ViewChats(Messenger esql, String user){
      // Your code goes here.
      // ... CASE 4 in message menu
      // ...
      try{
      	  //printing last 10 messages from each chat
      	  String chatviewer = user;
      	  String query = String.format("SELECT");
      	  int last10 = esql.executeQueryAndPrintResult(query);
      	  if(last10 == 0){
      	  	  System.out.println("None\n");
      	  }
      }
	  catch(Exception e){
	  	  System.err.println(e.getMessage());
	  }

   }//end 
/*
=================================================================================================
=================================================================================================
=================================================================================================
=================================================================================================
END OF IMPLEMENTATION
*/
}//end Messenger
