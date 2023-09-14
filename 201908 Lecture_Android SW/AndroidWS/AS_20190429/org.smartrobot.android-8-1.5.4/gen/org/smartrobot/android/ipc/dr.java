/*
 * This file is auto-generated.  DO NOT MODIFY.
 * Original file: C:\\adt-bundle-windows-x86-20140702\\eclipse\\workspace\\org.smartrobot.android-8-1.5.4\\src\\org\\smartrobot\\android\\ipc\\dr.aidl
 */
package org.smartrobot.android.ipc;
/**
 * @author akaii@kw.ac.kr (Kwang-Hyun Park)
 */
public interface dr extends android.os.IInterface
{
/** Local-side IPC implementation stub class. */
public static abstract class Stub extends android.os.Binder implements org.smartrobot.android.ipc.dr
{
private static final java.lang.String DESCRIPTOR = "org.smartrobot.android.ipc.dr";
/** Construct the stub at attach it to the interface. */
public Stub()
{
this.attachInterface(this, DESCRIPTOR);
}
/**
 * Cast an IBinder object into an org.smartrobot.android.ipc.dr interface,
 * generating a proxy if needed.
 */
public static org.smartrobot.android.ipc.dr asInterface(android.os.IBinder obj)
{
if ((obj==null)) {
return null;
}
android.os.IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
if (((iin!=null)&&(iin instanceof org.smartrobot.android.ipc.dr))) {
return ((org.smartrobot.android.ipc.dr)iin);
}
return new org.smartrobot.android.ipc.dr.Stub.Proxy(obj);
}
@Override public android.os.IBinder asBinder()
{
return this;
}
@Override public boolean onTransact(int code, android.os.Parcel data, android.os.Parcel reply, int flags) throws android.os.RemoteException
{
switch (code)
{
case INTERFACE_TRANSACTION:
{
reply.writeString(DESCRIPTOR);
return true;
}
case TRANSACTION_a:
{
data.enforceInterface(DESCRIPTOR);
byte[] _result = this.a();
reply.writeNoException();
reply.writeByteArray(_result);
return true;
}
case TRANSACTION_b:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
int _arg1;
_arg1 = data.readInt();
byte[] _result = this.b(_arg0, _arg1);
reply.writeNoException();
reply.writeByteArray(_result);
return true;
}
}
return super.onTransact(code, data, reply, flags);
}
private static class Proxy implements org.smartrobot.android.ipc.dr
{
private android.os.IBinder mRemote;
Proxy(android.os.IBinder remote)
{
mRemote = remote;
}
@Override public android.os.IBinder asBinder()
{
return mRemote;
}
public java.lang.String getInterfaceDescriptor()
{
return DESCRIPTOR;
}
@Override public byte[] a() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
byte[] _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_a, _data, _reply, 0);
_reply.readException();
_result = _reply.createByteArray();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public byte[] b(int b1, int b2) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
byte[] _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(b1);
_data.writeInt(b2);
mRemote.transact(Stub.TRANSACTION_b, _data, _reply, 0);
_reply.readException();
_result = _reply.createByteArray();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
}
static final int TRANSACTION_a = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
static final int TRANSACTION_b = (android.os.IBinder.FIRST_CALL_TRANSACTION + 1);
}
public byte[] a() throws android.os.RemoteException;
public byte[] b(int b1, int b2) throws android.os.RemoteException;
}
