package com.kuick.interfaces;

import com.kuick.Response.AllStreamerData;
import com.kuick.Response.MostPopularLivesResponse;
import com.kuick.model.FeatureStreamers;
import com.kuick.model.NewStreamers;
import com.kuick.model.RecommendedStreamers;
import com.kuick.model.TrendingStreamers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public interface ClickEventListener {

    void onClickPosition(int position);
    void RefreshAllAdapter();

    void RefreshTrendingFullScreenAdapter(List<TrendingStreamers> trendingStreamersListFullScreen);
    void RefreshFeaturedFullScreenAdapter(List<FeatureStreamers> featureStreamersListFullScreen);
    void RefreshNewStreamerFullScreenAdapter(List<NewStreamers> newStreamersListFullScreen);

    void MostPopularStreamer(List<MostPopularLivesResponse> mostPopularLivesResponses);
    void StreamerDetailScreenLive(List<AllStreamerData> allStreamerData);

}
