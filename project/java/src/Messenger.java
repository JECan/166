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
import java.lang.Integer;
import java.io.Console;

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

/*
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
   }//end*/
   
   public String executeQueryString (String query) throws SQLException {
		// creates a statement object
		Statement stmt = this._connection.createStatement();

		// issues the query instruction
		ResultSet rs = stmt.executeQuery(query);
		rs.next();
		String retVal = rs.getString(1);
		stmt.close();
		return retVal;
	}
   
   /**
    * Method to execute an input query SQL instruction (i.e. SELECT).  This
    * method issues the query to the DBMS and outputs the results to
    * standard out.
    *
    * @param query the input query string
    * @return the number of rows returned
    * @throws java.sql.SQLException when failed to execute the query
    */
   public int executeQueryAndPrintResult (String query, boolean outputHeader) throws SQLException {
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
			//System.out.println("4. Delete Account");
            System.out.println("9. < EXIT");
            String authorisedUser = null;
            switch (readChoice()){
               case 1: CreateUser(esql); break;
               case 2: authorisedUser = LogIn(esql); break;
               case 3: ChangePassword(esql); break; 
              // case 4: DeleteAccount(esql); break;
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
                System.out.println("8. Delete Account");
                System.out.println(".........................");
                System.out.println("9. Log out");
                switch (readChoice()){
                   case 1: AddToContact(esql, authorisedUser); break;
                   case 2: ListContacts(esql, authorisedUser); break;
                   case 3: RemoveContact(esql, authorisedUser); break;
                   case 4: AddToBlock(esql, authorisedUser); break;
                   case 5: ListBlocks(esql, authorisedUser); break;
                   case 6: RemoveBlocked(esql, authorisedUser); break;
                   //case 7: MessageMenu(esql, authorisedUser); break;
                   case 8: DeleteAccount(esql, authorisedUser); break;
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
         //String password = in.readLine();
         Console cnsl = null;
         cnsl = System.console();
         char[] passString = cnsl.readPassword();
		 String password = new String(passString);
		 
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

   public static void ChangePassword(Messenger esql){
      // Your code goes here.
      // ... CASE 3 in initial menu
      // ...
      try{
      	  System.out.println("\nEnter login:");
      	  String login = in.readLine();
      	  System.out.println("\nEnter old password:");
      	  Console cnsl = null;
          cnsl = System.console();
          char[] passString = cnsl.readPassword();
		  String oldpass = new String(passString);
      	  //String oldpass = in.readLine();
      	  System.out.println("\nEnter new password:");
      	 // String newpass = in.readLine();
      	  Console cnsl1 = null;
	      cnsl1 = System.console();
          char[] passString1 = cnsl1.readPassword();
		  String newpass = new String(passString1);
		  
		  System.out.println(oldpass + " " + newpass + " " + login);
		  
		  
      	  String query = String.format("update usr set password = '%s' where login = '%s' and password = '%s'", newpass, login, oldpass);
      	  esql.executeUpdate(query);
      	  System.out.println("Password changed successfully\n");
      	}
      	catch(Exception e){
      		System.out.println(e.getMessage());
      		}
   }//end

	//FIX ME
   public static void DeleteAccount(Messenger esql, String user){
      // Your code goes here.
      // ... CASE 4 in initial menu
      // ...
       try{
      	  System.out.println("\nYOU ARE ABOUT TO DELETE YOUR ACCOUNT");
      	  System.out.println("\nEnter login:");
      	  String login = in.readLine();
      	  System.out.println("\nEnter password:");
      	  String password = in.readLine();
      	  
      	  String query = String.format("select count(1) from usr where login = '%s' and password = '%s'", login, password);
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
		  System.out.print("\033[H\033[2J");
		  System.out.flush();
      	  String personAdding = user;
      	  System.out.println("Enter Contacts Name\n");
      	  String personAdded = in.readLine();
      	  
		  String check = String.format("select count(1) from usr where login = '%s'", personAdded);
          String ret = esql.executeQueryString(check);
		  System.out.println(ret + personAdded);
		  
		  int i = Integer.parseInt(ret);
		  
          if (i > 0){
                 System.out.println(ret);
      	        String query = String.format("select count(c.list_member) from usr a, user_list b, user_list_contains c where a.contact_list = b.list_id and b.list_id = c.list_id and c.list_member = '%s' and a.login = '%s';", personAdded, personAdding);
      	        String val = esql.executeQueryString(query);
		         System.out.println(val);
		         int c = Integer.parseInt(val);
				if (c < 1){
					String blocked_id = String.format("select contact_list from usr where login = '%s'", personAdding);
					String return_val =  esql.executeQueryString(blocked_id);
					
					String insert = String.format("insert into user_list_contains (list_id, list_member) values(%s, '%s')", return_val, personAdded);
					esql.executeUpdate(insert);
				}
				else{
					System.out.println("User Login Provided is already Added");
				}
		  }
          else {
              System.out.println("User Login Provided Does not Exist");
          }
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
		  System.out.print("\033[H\033[2J");
		  System.out.flush();
      	  String personBrowsing = user;
      	  System.out.println("Listing contacts...\n");
      	  
      	  String query = String.format("select a.list_member as Contact from user_list_contains a, usr b, user_list c where b.contact_list = c.list_id and upper(c.list_type) = 'CONTACT' and  c.list_id = a.list_id and b.login = '%s'", personBrowsing);
      	  int rowCount = esql.executeQueryAndPrintResult(query, false);
		  System.out.println("\n");
		  System.out.println("\n");
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
      
      try{
		  System.out.print("\033[H\033[2J");
		  System.out.flush();
      	  String personRemoving = user;
      	  System.out.println("Enter Contact Name\n");
      	  String personRemoved = in.readLine();
      	  
      	  String check = String.format("select count(a.list_member) from user_list_contains a, usr b where b.contact_list = a.list_id and b.login = '%s' and a.list_member = '%s'", personRemoving, personRemoved);
      	  String val = esql.executeQueryString(check);
      	  
      	  int i = Integer.parseInt(val);
      	  
      	  if (i > 0) {
				String query = String.format("delete from user_list_contains where list_id = (select contact_list from usr where login = '%s') and list_member = '%s'", personRemoving, personRemoved);
				esql.executeUpdate(query);
				String out = String.format(" %s Was Removed", personRemoved);
				System.out.println(out);
      	  }
      	  else{
      	  	  String out = String.format("User %s does not exist in contact list", personRemoved);
      	  	  System.out.println(out);
      	 }
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
		  System.out.print("\033[H\033[2J");
		  System.out.flush();
      	  String personBlocking = user;
      	  System.out.println("Enter User you wish to block\n");
      	  String personBlocked = in.readLine();
      	  
      	  String check = String.format("select count(1) from usr where login = '%s'", personBlocked);
          String ret = esql.executeQueryString(check);
		  System.out.println(ret + personBlocked);
		  
		  int i = Integer.parseInt(ret);
		  
          if (i > 0){
                 System.out.println(ret);
      	        String query = String.format("select count(c.list_member) from usr a, user_list b, user_list_contains c where a.block_list = b.list_id and b.list_id = c.list_id and c.list_member = '%s' and a.login = '%s';", personBlocked, personBlocking);
      	        String val = esql.executeQueryString(query);
		         System.out.println(val);
		         int c = Integer.parseInt(val);
				if (c < 1){
					String blocked_id = String.format("select block_list from usr where login = '%s'", personBlocking);
					String return_val =  esql.executeQueryString(blocked_id);
					
					String insert = String.format("insert into user_list_contains (list_id, list_member) values(%s, '%s')", return_val, personBlocked);
					esql.executeUpdate(insert);
				}
				else{
					System.out.println("User Login Provided is already blocked");
				}
		  }
          else {
              System.out.println("User Login Provided Does not Exist");
          }
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
		  System.out.print("\033[H\033[2J");
		  System.out.flush();
      	  String personBrowsing = user;
      	  System.out.println("Listing blocked contacts...\n");
      	  
      	  String query = String.format("select a.list_member from user_list_contains a, usr b, user_list c where b.block_list = c.list_id and upper(c.list_type) = 'BLOCK' and  c.list_id = a.list_id and b.login = '%s'", personBrowsing);
      	  int rowCount = esql.executeQueryAndPrintResult(query, false);
		  System.out.println("\n");
		  System.out.println("\n");
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
		  System.out.print("\033[H\033[2J");
		  System.out.flush();
      	  String personUnblocking = user;
      	  System.out.println("Enter Contacts Name\n");
      	  String personUnblocked = in.readLine();
      	  
      	  String check = String.format("select count(a.list_member) from user_list_contains a, usr b where b.block_list = a.list_id and b.login = '%s' and a.list_member = '%s'", personUnblocking, personUnblocked);
      	  String val = esql.executeQueryString(check);
      	  int i = Integer.parseInt(val);
      	  if (i > 0) {
				String query = String.format("delete from user_list_contains where list_id = (select block_list from usr where login = '%s') and list_member = '%s'", personUnblocking, personUnblocked);
				esql.executeUpdate(query);
				String out = String.format(" %s Was Unblocked", personUnblocked);
				System.out.println(out);
      	  }
      	  else{
      	  	  
      	  	  String out = String.format("User %s does not exist in blocked list", personUnblocked);
      	  	  System.out.println(out);
      	 }
	  }
	  catch(Exception e){
	  	  System.err.println(e.getMessage());
	  }
   }//end

  /* public static void MessageMenu(Messenger esql, String user){
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

   }//end */
/*
=================================================================================================
=================================================================================================
=================================================================================================
=================================================================================================
END OF IMPLEMENTATION
*/
}//end Messenger
