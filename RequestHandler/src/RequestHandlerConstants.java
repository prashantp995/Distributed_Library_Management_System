public class RequestHandlerConstants {

  static final String RES_WAIT_LIST_POSSIBLE = "waitList";
  static final String METHOD_ADD_ITEM = "addItem";
  static final String METHOD_REMOVE_ITEM = "removeItem";
  static final String METHOD_LIST_ITEM = "listItem";
  static final String METHOD_BORROW_ITEM = "borrowItem";
  static final String METHOD_EXCHANGE_ITEM = "exchangeItem";
  static final String METHOD_ADD_USER_IN_WAITLIST = "addUserInWaitList";
  static final String METHOD_RETURN_ITEM = "returnItem";
  static final String METHOD_VALIDATE_USER_NAME = "validateUser";
  static final String METHOD_FIND_ITEM = "findITem";
  static final String METHOD_SIMULATE_SOFTWARE_BUG = "sfBug";
  static final String SUCCESS = "Success";
  static final String FAIL = "Fail";
  static final String TRUE = "True";
  static final String BUGGY = "sending buggy result";
  static final String CORRECT = "sending correct result";
  static final String RES_TRUE_SUCCESS = "true:success";
  static final String RES_FALSE_FAILURE = "false:failure";
  static final String RES_APPEND_SUCCESS = ":success";
  static final String RES_APPEND_FAILURE = ":failure";
  static final String RES_ITEM_NOT_EROOR = "item not found:failure";
  static final String RES_ITEM_NOT_BORROWED = "item not borrowed:failure";
  static final String RES_ITEM_ALREADY_BORROWED = "item already borrowed:failure";
  static final String RES_ALREADY_IN_WAIT_LIST = "already in wait list:failure";
  static final String RES_WAITLIST_POSSIBLE = "waitlist:success";
  static final String RES_FOREIGN_LIB_ERROR = "can not borrow two books from foreign library:failure";
  static final String RES_ITEMID_NOT_VALID = "item id not valid:failure";
  static final String RES_ITEM_NAME_ERROR = "item id and item name does not match:failure";
  static final String RES_INCORRECT_QUANTITY_ERROR = "incorrect quantity:failure";

}
