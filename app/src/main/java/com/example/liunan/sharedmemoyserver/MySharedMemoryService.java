package com.example.liunan.sharedmemoyserver;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.SharedMemory;
import android.support.annotation.Nullable;
import android.system.ErrnoException;
import android.system.OsConstants;
import android.util.Log;

import java.nio.ByteBuffer;

public class MySharedMemoryService extends Service {

    private final String TAG = MySharedMemoryService.class.getSimpleName();
    private final int BUFFER_MAX_SIZE = 100;
    private SharedMemory memory;
    private ByteBuffer buffer;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        MyMemoryBinder binder = new MyMemoryBinder();
        try {
            memory = SharedMemory.create("MySharedMemoryService", BUFFER_MAX_SIZE);
            buffer = memory.mapReadWrite();
            memory.setProtect(OsConstants.PROT_READ);
        } catch (ErrnoException e) {
            Log.e(TAG, e.toString());
        }
        return binder;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        memory.close();
    }

    private class MyMemoryBinder extends IMyMemoryService.Stub {

        @Override
        public void writeByte(int offset, byte num) {
            if (offset >= BUFFER_MAX_SIZE) {
                Log.e(TAG, "memory's size is " + BUFFER_MAX_SIZE);
            }
            buffer.put(offset, num);
        }

        @Override
        public SharedMemory getMemory() {
            return memory;
        }
    }
}
