
/**
* _LibraryServiceStub.java .
* Generated by the IDL-to-Java compiler (portable), version "3.2"
* from LibraryServicesInterface.idl
* Monday, March 18, 2019 3:04:52 PM EDT
*/

public class _LibraryServiceStub extends org.omg.CORBA.portable.ObjectImpl implements LibraryService
{

  public String findItem (String userId, String itemName)
  {
            org.omg.CORBA.portable.InputStream $in = null;
            try {
                org.omg.CORBA.portable.OutputStream $out = _request ("findItem", true);
                $out.write_string (userId);
                $out.write_string (itemName);
                $in = _invoke ($out);
                String $result = $in.read_string ();
                return $result;
            } catch (org.omg.CORBA.portable.ApplicationException $ex) {
                $in = $ex.getInputStream ();
                String _id = $ex.getId ();
                throw new org.omg.CORBA.MARSHAL (_id);
            } catch (org.omg.CORBA.portable.RemarshalException $rm) {
                return findItem (userId, itemName        );
            } finally {
                _releaseReply ($in);
            }
  } // findItem

  public String returnItem (String userId, String itemID)
  {
            org.omg.CORBA.portable.InputStream $in = null;
            try {
                org.omg.CORBA.portable.OutputStream $out = _request ("returnItem", true);
                $out.write_string (userId);
                $out.write_string (itemID);
                $in = _invoke ($out);
                String $result = $in.read_string ();
                return $result;
            } catch (org.omg.CORBA.portable.ApplicationException $ex) {
                $in = $ex.getInputStream ();
                String _id = $ex.getId ();
                throw new org.omg.CORBA.MARSHAL (_id);
            } catch (org.omg.CORBA.portable.RemarshalException $rm) {
                return returnItem (userId, itemID        );
            } finally {
                _releaseReply ($in);
            }
  } // returnItem

  public String borrowItem (String userId, String itemID, int numberOfDays)
  {
            org.omg.CORBA.portable.InputStream $in = null;
            try {
                org.omg.CORBA.portable.OutputStream $out = _request ("borrowItem", true);
                $out.write_string (userId);
                $out.write_string (itemID);
                $out.write_long (numberOfDays);
                $in = _invoke ($out);
                String $result = $in.read_string ();
                return $result;
            } catch (org.omg.CORBA.portable.ApplicationException $ex) {
                $in = $ex.getInputStream ();
                String _id = $ex.getId ();
                throw new org.omg.CORBA.MARSHAL (_id);
            } catch (org.omg.CORBA.portable.RemarshalException $rm) {
                return borrowItem (userId, itemID, numberOfDays        );
            } finally {
                _releaseReply ($in);
            }
  } // borrowItem

  public String addItem (String userId, String itemID, String itemName, int quantity)
  {
            org.omg.CORBA.portable.InputStream $in = null;
            try {
                org.omg.CORBA.portable.OutputStream $out = _request ("addItem", true);
                $out.write_string (userId);
                $out.write_string (itemID);
                $out.write_string (itemName);
                $out.write_long (quantity);
                $in = _invoke ($out);
                String $result = $in.read_string ();
                return $result;
            } catch (org.omg.CORBA.portable.ApplicationException $ex) {
                $in = $ex.getInputStream ();
                String _id = $ex.getId ();
                throw new org.omg.CORBA.MARSHAL (_id);
            } catch (org.omg.CORBA.portable.RemarshalException $rm) {
                return addItem (userId, itemID, itemName, quantity        );
            } finally {
                _releaseReply ($in);
            }
  } // addItem

  public String removeItem (String managerId, String itemId, int quantity)
  {
            org.omg.CORBA.portable.InputStream $in = null;
            try {
                org.omg.CORBA.portable.OutputStream $out = _request ("removeItem", true);
                $out.write_string (managerId);
                $out.write_string (itemId);
                $out.write_long (quantity);
                $in = _invoke ($out);
                String $result = $in.read_string ();
                return $result;
            } catch (org.omg.CORBA.portable.ApplicationException $ex) {
                $in = $ex.getInputStream ();
                String _id = $ex.getId ();
                throw new org.omg.CORBA.MARSHAL (_id);
            } catch (org.omg.CORBA.portable.RemarshalException $rm) {
                return removeItem (managerId, itemId, quantity        );
            } finally {
                _releaseReply ($in);
            }
  } // removeItem

  public String listItem (String managerId)
  {
            org.omg.CORBA.portable.InputStream $in = null;
            try {
                org.omg.CORBA.portable.OutputStream $out = _request ("listItem", true);
                $out.write_string (managerId);
                $in = _invoke ($out);
                String $result = $in.read_string ();
                return $result;
            } catch (org.omg.CORBA.portable.ApplicationException $ex) {
                $in = $ex.getInputStream ();
                String _id = $ex.getId ();
                throw new org.omg.CORBA.MARSHAL (_id);
            } catch (org.omg.CORBA.portable.RemarshalException $rm) {
                return listItem (managerId        );
            } finally {
                _releaseReply ($in);
            }
  } // listItem

  public String addUserInWaitingList (String userId, String ItemId, int numberOfDays)
  {
            org.omg.CORBA.portable.InputStream $in = null;
            try {
                org.omg.CORBA.portable.OutputStream $out = _request ("addUserInWaitingList", true);
                $out.write_string (userId);
                $out.write_string (ItemId);
                $out.write_long (numberOfDays);
                $in = _invoke ($out);
                String $result = $in.read_string ();
                return $result;
            } catch (org.omg.CORBA.portable.ApplicationException $ex) {
                $in = $ex.getInputStream ();
                String _id = $ex.getId ();
                throw new org.omg.CORBA.MARSHAL (_id);
            } catch (org.omg.CORBA.portable.RemarshalException $rm) {
                return addUserInWaitingList (userId, ItemId, numberOfDays        );
            } finally {
                _releaseReply ($in);
            }
  } // addUserInWaitingList

  public String exchangeItem (String userId, String oldItemId, String newItemID)
  {
            org.omg.CORBA.portable.InputStream $in = null;
            try {
                org.omg.CORBA.portable.OutputStream $out = _request ("exchangeItem", true);
                $out.write_string (userId);
                $out.write_string (oldItemId);
                $out.write_string (newItemID);
                $in = _invoke ($out);
                String $result = $in.read_string ();
                return $result;
            } catch (org.omg.CORBA.portable.ApplicationException $ex) {
                $in = $ex.getInputStream ();
                String _id = $ex.getId ();
                throw new org.omg.CORBA.MARSHAL (_id);
            } catch (org.omg.CORBA.portable.RemarshalException $rm) {
                return exchangeItem (userId, oldItemId, newItemID        );
            } finally {
                _releaseReply ($in);
            }
  } // exchangeItem

  public boolean validateUserName (String userId)
  {
            org.omg.CORBA.portable.InputStream $in = null;
            try {
                org.omg.CORBA.portable.OutputStream $out = _request ("validateUserName", true);
                $out.write_string (userId);
                $in = _invoke ($out);
                boolean $result = $in.read_boolean ();
                return $result;
            } catch (org.omg.CORBA.portable.ApplicationException $ex) {
                $in = $ex.getInputStream ();
                String _id = $ex.getId ();
                throw new org.omg.CORBA.MARSHAL (_id);
            } catch (org.omg.CORBA.portable.RemarshalException $rm) {
                return validateUserName (userId        );
            } finally {
                _releaseReply ($in);
            }
  } // validateUserName

  // Type-specific CORBA::Object operations
  private static String[] __ids = {
    "IDL:LibraryService:1.0"};

  public String[] _ids ()
  {
    return (String[])__ids.clone ();
  }

  private void readObject (java.io.ObjectInputStream s) throws java.io.IOException
  {
     String str = s.readUTF ();
     String[] args = null;
     java.util.Properties props = null;
     org.omg.CORBA.ORB orb = org.omg.CORBA.ORB.init (args, props);
   try {
     org.omg.CORBA.Object obj = orb.string_to_object (str);
     org.omg.CORBA.portable.Delegate delegate = ((org.omg.CORBA.portable.ObjectImpl) obj)._get_delegate ();
     _set_delegate (delegate);
   } finally {
     orb.destroy() ;
   }
  }

  private void writeObject (java.io.ObjectOutputStream s) throws java.io.IOException
  {
     String[] args = null;
     java.util.Properties props = null;
     org.omg.CORBA.ORB orb = org.omg.CORBA.ORB.init (args, props);
   try {
     String str = orb.object_to_string (this);
     s.writeUTF (str);
   } finally {
     orb.destroy() ;
   }
  }
} // class _LibraryServiceStub
