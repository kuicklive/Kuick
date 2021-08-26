package com.kuick.common;

import android.content.Context;

import com.kuick.BuildConfig;
import com.kuick.Remote.EndPoints;

import java.net.URISyntaxException;
import java.util.concurrent.TimeUnit;

import io.socket.client.IO;
import io.socket.client.Socket;
import okhttp3.OkHttpClient;

public class SocketSingleObject
{

    public static SocketSingleObject instance;

    private Socket mSocket;
    private Context context;

    public SocketSingleObject(Context context) {
        this.context = context;
        this.mSocket = getServerSocket();
    }

    public static SocketSingleObject get(Context context){
        if(instance == null){
            instance = getSync(context);
        }
        instance.context = context;
        return instance;
    }

    private static synchronized SocketSingleObject getSync(Context context) {
        if(instance == null){
            instance = new SocketSingleObject(context);
        }
        return instance;
    }

    public Socket getSocket(){
        return this.mSocket;
    }

    public Socket getServerSocket() {
        try
        {
//                IO.Options opts = new IO.Options();
//                opts.forceNew = true;
//                opts.reconnection = true;
            mSocket = IO.socket(BuildConfig.SOCKET_ADDRESS);//, opts

            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .connectTimeout(60, TimeUnit.SECONDS)
                    .writeTimeout(1, TimeUnit.MINUTES)
                    .readTimeout(1, TimeUnit.MINUTES)
                    .build();
            IO.setDefaultOkHttpCallFactory(okHttpClient);
            IO.setDefaultOkHttpWebSocketFactory(okHttpClient);

            return mSocket;
        }
        catch (URISyntaxException e)
        {
            throw new RuntimeException(e);
        }
    }
}
