# The Collector

The Collector is a collection manager Android application.  Its purpose is to to make it easier to keep track of items in any kind of collection.  The Collector is a crowd-sourced application, so you can find and use existing collections made by other users.  All data is stored on a remote server, and is accessed through a web API.

## Activities

### Register User Activity

This activity allows the user to create an account to start managing their collections.  A new user will enter their name, email, and password to make a new account.

### Login Activity

This activity sends authentication information (email and password) to the web API which allows the user to login.

### Main Activity

This activity is a hub that allows the user to go to his/her collections, find a collection to add, or create a collection.  The login activity is also accessible from the menu at the top of the screen.

### Collections Activity

This activity lists all of the collections associated with the user.  Clicking on any collection will bring the user to the items page for that collection.

### Items Activity

This activity shows the different items contained in a collection. Each item has 1-3 buttons that can be clicked to determine the item's "state".  Each item can also be deleted for that user by clicking the `Delete` button, or you can search for the item on eBay by clicking the `Buy It`button.  A user can add an item to his/her version of the collection by clicking the `Add Item` button.  All that is needed to add an item is the name of the new item.  Items added on this page will only be added to the current user's version of the collection; they will not be visible to other users if they use that collection.

### Find Activity

This activity allows the user to search for existing collections made by other users.  After searching, clicking any collection will bring him/her to the items page for that collection where he/she can add that collection to their own.

### Create Collection Activity

This activity allows the user to create a new collection to be used by them as well as any other user.  A collection needs a name and labels for buttons.  Only one button is required, but the user can add up to three.  After creating the collection, the user can add items to it just like on the items page, but the item will be visible to all users who have the collection.
