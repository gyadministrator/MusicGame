package utils;

import bean.RecommendMusic;

/**
 * Created by Administrator on 2018/2/3.
 */

public class CurrentMusicUtils {
    private static RecommendMusic recommendMusic;

    public static RecommendMusic getRecommendMusic() {
        return recommendMusic;
    }

    public static void setRecommendMusic(RecommendMusic recommendMusic) {
        CurrentMusicUtils.recommendMusic = recommendMusic;
    }
}
