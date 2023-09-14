/*
 * This file is auto-generated.  DO NOT MODIFY.
 * Original file: C:\\adt-bundle-windows-x86-20140702\\eclipse\\workspace\\org.smartrobot.android-8-1.5.4\\src\\org\\smartrobot\\android\\ipc\\dc.aidl
 */
package org.smartrobot.android.ipc;
/**
 * @author akaii@kw.ac.kr (Kwang-Hyun Park)
 */
public interface dc extends android.os.IInterface
{
/** Local-side IPC implementation stub class. */
public static abstract class Stub extends android.os.Binder implements org.smartrobot.android.ipc.dc
{
private static final java.lang.String DESCRIPTOR = "org.smartrobot.android.ipc.dc";
/** Construct the stub at attach it to the interface. */
public Stub()
{
this.attachInterface(this, DESCRIPTOR);
}
/**
 * Cast an IBinder object into an org.smartrobot.android.ipc.dc interface,
 * generating a proxy if needed.
 */
public static org.smartrobot.android.ipc.dc asInterface(android.os.IBinder obj)
{
if ((obj==null)) {
return null;
}
android.os.IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
if (((iin!=null)&&(iin instanceof org.smartrobot.android.ipc.dc))) {
return ((org.smartrobot.android.ipc.dc)iin);
}
return new org.smartrobot.android.ipc.dc.Stub.Proxy(obj);
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
byte[] _arg0;
_arg0 = data.createByteArray();
long _arg1;
_arg1 = data.readLong();
this.a(_arg0, _arg1);
reply.writeNoException();
return true;
}
case TRANSACTION_b:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
int _arg1;
_arg1 = data.readInt();
byte[] _arg2;
_arg2 = data.createByteArray();
long _arg3;
_arg3 = data.readLong();
this.b(_arg0, _arg1, _arg2, _arg3);
reply.writeNoException();
return true;
}
}
return super.onTransact(code, data, reply, flags);
}
private static class Proxy implements org.smartrobot.android.ipc.dc
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
@Override public void a(byte[] a1, long a2) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeByteArray(a1);
_data.writeLong(a2);
mRemote.transact(Stub.TRANSACTION_a, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public void b(int b1, int b2, byte[] b3, long b4) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(b1);
_data.writeInt(b2);
_data.writeByteArray(b3);
_data.writeLong(b4);
mRemote.transact(Stub.TRANSACTION_b, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
}
static final int TRANSACTION_a = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
static final int TRANSACTION_b = (android.os.IBinder.FIRST_CALL_TRANSACTION + 1);
}
public void a(byte[] a1, long a2) throws android.os.RemoteException;
public void b(int b1, int b2, byte[] b3, long b4) throws android.os.RemoteException;
}
