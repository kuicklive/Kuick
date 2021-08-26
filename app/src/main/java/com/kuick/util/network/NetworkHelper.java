package com.kuick.util.network;

import android.content.Context;

public interface NetworkHelper
{

    boolean isNetworkConnected(Context context);

    NetworkError castToNetworkError(Throwable throwable, Context context);

}
