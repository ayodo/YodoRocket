/*
 * This file is auto-generated.  DO NOT MODIFY.
 * Original file: D:\\Project\\DualScreenMessager-Lib\\DSC\\DS_Lib\\src\\main\\aidl\\com\\sunmi\\aidl\\SendService.aidl
 */
package com.sunmi.aidl;
public interface SendService extends android.os.IInterface
{
/** Local-side IPC implementation stub class. */
public static abstract class Stub extends android.os.Binder implements com.sunmi.aidl.SendService
{
private static final java.lang.String DESCRIPTOR = "com.sunmi.aidl.SendService";
/** Construct the stub at attach it to the interface. */
public Stub()
{
this.attachInterface(this, DESCRIPTOR);
}
/**
 * Cast an IBinder object into an com.sunmi.aidl.SendService interface,
 * generating a proxy if needed.
 */
public static com.sunmi.aidl.SendService asInterface(android.os.IBinder obj)
{
if ((obj==null)) {
return null;
}
android.os.IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
if (((iin!=null)&&(iin instanceof com.sunmi.aidl.SendService))) {
return ((com.sunmi.aidl.SendService)iin);
}
return new com.sunmi.aidl.SendService.Stub.Proxy(obj);
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
case TRANSACTION_sendFileToFile:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
java.lang.String _arg1;
_arg1 = data.readString();
boolean _arg2;
_arg2 = (0!=data.readInt());
long _arg3;
_arg3 = data.readLong();
com.sunmi.aidl.SendServiceCallback _arg4;
_arg4 = com.sunmi.aidl.SendServiceCallback.Stub.asInterface(data.readStrongBinder());
int _result = this.sendFileToFile(_arg0, _arg1, _arg2, _arg3, _arg4);
reply.writeNoException();
reply.writeInt(_result);
return true;
}
case TRANSACTION_sendByteToMemory:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
byte[] _arg1;
_arg1 = data.createByteArray();
com.sunmi.aidl.SendServiceCallback _arg2;
_arg2 = com.sunmi.aidl.SendServiceCallback.Stub.asInterface(data.readStrongBinder());
int _result = this.sendByteToMemory(_arg0, _arg1, _arg2);
reply.writeNoException();
reply.writeInt(_result);
return true;
}
}
return super.onTransact(code, data, reply, flags);
}
private static class Proxy implements com.sunmi.aidl.SendService
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
@Override public int sendFileToFile(java.lang.String recvPackageName, java.lang.String path, boolean isReport, long userFlag, com.sunmi.aidl.SendServiceCallback callback) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(recvPackageName);
_data.writeString(path);
_data.writeInt(((isReport)?(1):(0)));
_data.writeLong(userFlag);
_data.writeStrongBinder((((callback!=null))?(callback.asBinder()):(null)));
mRemote.transact(Stub.TRANSACTION_sendFileToFile, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public int sendByteToMemory(java.lang.String recvPackageName, byte[] data, com.sunmi.aidl.SendServiceCallback callback) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(recvPackageName);
_data.writeByteArray(data);
_data.writeStrongBinder((((callback!=null))?(callback.asBinder()):(null)));
mRemote.transact(Stub.TRANSACTION_sendByteToMemory, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
}
static final int TRANSACTION_sendFileToFile = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
static final int TRANSACTION_sendByteToMemory = (android.os.IBinder.FIRST_CALL_TRANSACTION + 1);
}
public int sendFileToFile(java.lang.String recvPackageName, java.lang.String path, boolean isReport, long userFlag, com.sunmi.aidl.SendServiceCallback callback) throws android.os.RemoteException;
public int sendByteToMemory(java.lang.String recvPackageName, byte[] data, com.sunmi.aidl.SendServiceCallback callback) throws android.os.RemoteException;
}
