/*
 * This file is auto-generated.  DO NOT MODIFY.
 * Original file: C:\\adt-bundle-windows-x86-20140702\\eclipse\\workspace\\org.smartrobot.android-8-1.5.4\\src\\org\\smartrobot\\android\\ipc\\ac.aidl
 */
package org.smartrobot.android.ipc;
/**
 * @author akaii@kw.ac.kr (Kwang-Hyun Park)
 */
public interface ac extends android.os.IInterface
{
/** Local-side IPC implementation stub class. */
public static abstract class Stub extends android.os.Binder implements org.smartrobot.android.ipc.ac
{
private static final java.lang.String DESCRIPTOR = "org.smartrobot.android.ipc.ac";
/** Construct the stub at attach it to the interface. */
public Stub()
{
this.attachInterface(this, DESCRIPTOR);
}
/**
 * Cast an IBinder object into an org.smartrobot.android.ipc.ac interface,
 * generating a proxy if needed.
 */
public static org.smartrobot.android.ipc.ac asInterface(android.os.IBinder obj)
{
if ((obj==null)) {
return null;
}
android.os.IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
if (((iin!=null)&&(iin instanceof org.smartrobot.android.ipc.ac))) {
return ((org.smartrobot.android.ipc.ac)iin);
}
return new org.smartrobot.android.ipc.ac.Stub.Proxy(obj);
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
org.smartrobot.android.ipc.dc _arg0;
_arg0 = org.smartrobot.android.ipc.dc.Stub.asInterface(data.readStrongBinder());
this.a(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_b:
{
data.enforceInterface(DESCRIPTOR);
org.smartrobot.android.ipc.dc _arg0;
_arg0 = org.smartrobot.android.ipc.dc.Stub.asInterface(data.readStrongBinder());
this.b(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_c:
{
data.enforceInterface(DESCRIPTOR);
org.smartrobot.android.ipc.dr _arg0;
_arg0 = org.smartrobot.android.ipc.dr.Stub.asInterface(data.readStrongBinder());
this.c(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_d:
{
data.enforceInterface(DESCRIPTOR);
org.smartrobot.android.ipc.dr _arg0;
_arg0 = org.smartrobot.android.ipc.dr.Stub.asInterface(data.readStrongBinder());
this.d(_arg0);
reply.writeNoException();
return true;
}
}
return super.onTransact(code, data, reply, flags);
}
private static class Proxy implements org.smartrobot.android.ipc.ac
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
@Override public void a(org.smartrobot.android.ipc.dc a1) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeStrongBinder((((a1!=null))?(a1.asBinder()):(null)));
mRemote.transact(Stub.TRANSACTION_a, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public void b(org.smartrobot.android.ipc.dc b1) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeStrongBinder((((b1!=null))?(b1.asBinder()):(null)));
mRemote.transact(Stub.TRANSACTION_b, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public void c(org.smartrobot.android.ipc.dr c1) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeStrongBinder((((c1!=null))?(c1.asBinder()):(null)));
mRemote.transact(Stub.TRANSACTION_c, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public void d(org.smartrobot.android.ipc.dr d1) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeStrongBinder((((d1!=null))?(d1.asBinder()):(null)));
mRemote.transact(Stub.TRANSACTION_d, _data, _reply, 0);
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
static final int TRANSACTION_c = (android.os.IBinder.FIRST_CALL_TRANSACTION + 2);
static final int TRANSACTION_d = (android.os.IBinder.FIRST_CALL_TRANSACTION + 3);
}
public void a(org.smartrobot.android.ipc.dc a1) throws android.os.RemoteException;
public void b(org.smartrobot.android.ipc.dc b1) throws android.os.RemoteException;
public void c(org.smartrobot.android.ipc.dr c1) throws android.os.RemoteException;
public void d(org.smartrobot.android.ipc.dr d1) throws android.os.RemoteException;
}
