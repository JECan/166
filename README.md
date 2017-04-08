Online Messaging Database System
====
This project is a terminal based messenger system based on Facebook Messenger implemented with Java and SQL.To initialize database go to `project/sql/scripts` directory and initialize the database with 
```
$ ./create_db.sh
```
To compile go to `/project/java/scripts` and compile the messenger with
```
$ ./compile.sh 
```
Implementation of the messaging application uses terminal input. Upon starting the executable the user is prompted to enter options 1-3 which correspond to creating an account, logging in and changing the user's password.  Once logged in the user will see the following...
```
MAIN MENU
---------
1. Add to contact list
2. Browse contact list
3. Remove contact
4. Add to block list
5. Browse block list
6. Remove blocked contact
7. Message Menu
8. Delete Account
.........................
9. Log out
Please make your choice: 7
```

From here the user sees the mainmenu of 8 options and a logout option. Below is a compressed version of the menu:
```
Message Main Menu
_________________
1. View My Chats
2. Create Chat
9. Back to main menu
Please make your choice: 1
```

Option 7 puts the user into the chat/messages menu. Here there are 3 options to view/create chat and to go back to the main menu. The following is an example of a message menu.

```
**********************************************************************************************************
*  #  *  Chat Type *     Last Sender      *              Last Message                *       Date        *
**********************************************************************************************************
  0       group        Javon_Ondricka         consectetur nesciunt eaque rem...          08/10/07 11:58   
  1       group        Kira.Weimann           labore doloribus asperiores cu...          11/10/07 02:52   
  2       group        Declan.Howell          dolorem corrupti sequi qui con...          02/15/08 11:41   
  3       group        Junius.Anderson        magnam modi in est qui repudia...          05/06/09 07:02   
  4       group        Simeon_Nienow          libero accusamus quidem dolor...           02/14/12 04:40   
  5       group        Emmanuelle_Cartwrigh   sit commodi perferendis aliqui...          02/25/12 11:39   
  6       group        Joannie_Predovic       dolorem vero iusto et qui aut...           04/05/14 08:00   
Please Select a Choice

_________________

V. View Chat Messages
A. Add Members to Chat
D. Delete Chat
9. Back to Message Menu
Please make your choice: 
```

The user can now View messages, chat members, add members to chat, and delete chat.
