
/**
* LibraryServiceHolder.java .
* Generated by the IDL-to-Java compiler (portable), version "3.2"
* from LibraryServicesInterface.idl
* Monday, March 18, 2019 3:25:15 PM EDT
*/

public final class LibraryServiceHolder implements org.omg.CORBA.portable.Streamable
{
  public LibraryService value = null;

  public LibraryServiceHolder ()
  {
  }

  public LibraryServiceHolder (LibraryService initialValue)
  {
    value = initialValue;
  }

  public void _read (org.omg.CORBA.portable.InputStream i)
  {
    value = LibraryServiceHelper.read (i);
  }

  public void _write (org.omg.CORBA.portable.OutputStream o)
  {
    LibraryServiceHelper.write (o, value);
  }

  public org.omg.CORBA.TypeCode _type ()
  {
    return LibraryServiceHelper.type ();
  }

}
