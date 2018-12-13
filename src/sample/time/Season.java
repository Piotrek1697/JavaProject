package sample.time;

import java.time.LocalDate;

/**
 * Enum class that represents all of seasons.
 */
public enum Season {
    SPRING,SUMMER,AUTUMN,WINTER;

    /**
     * Method that calculates what season is in specific day.
     * @param localDate - date for which you want check season
     * @return Season name
     */
    public static Season of(LocalDate localDate){
        int [] spring = new int[]{83,174};
        int [] summer = new int[]{175,267};
        int [] autumn = new int[]{268,354};

        Season season;
        int dayOfYear = localDate.getDayOfYear();

        if (dayOfYear >= spring[0] && dayOfYear <= spring[1]){
            season = Season.SPRING;
        }else if(dayOfYear >= summer[0] && dayOfYear <= summer[1]){
            season = Season.SUMMER;
        }else if (dayOfYear >= autumn[0] && dayOfYear <= autumn[1]){
            season = Season.AUTUMN;
        }else {
            season = Season.WINTER;
        }

        return season;
    }
}
