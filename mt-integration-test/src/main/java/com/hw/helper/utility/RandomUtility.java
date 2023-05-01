package com.hw.helper.utility;

import com.hw.helper.SumTotal;
import java.util.Random;
import org.springframework.lang.Nullable;

public class RandomUtility {
    public static final Random random = new Random();

    /**
     * pick random index from list, return -1 if list is empty
     * <p>e.g. list size =1 then value range from [0,1)</p>
     * <p>e.g. list size =2 then value range from [0,1,2)</p>
     * <p>e.g. list size =3 then value range from [0,1,2,3)</p>
     *
     * @param listSize list size
     * @return index 0,1,2,3...
     */
    public static int pickRandomFromList(int listSize) {
        if (listSize == 0) {
            return -1;
        }
        return random.nextInt(listSize);
    }

    /**
     * pick paginated page from lists
     *
     * @param sumTotal sum total
     * @param pageSize page size
     * @param <T>      type
     * @return page index
     */
    public static <T> int pickRandomPage(SumTotal<T> sumTotal, int pageSize) {
        Integer totalItemCount = sumTotal.getTotalItemCount();
        int totalPage = Math.floorDiv(totalItemCount, pageSize) + 1;
        return pickRandomFromList(totalPage);
    }

    /**
     * pick random page
     * @param url url to combine
     * @param sumTotal sum total
     * @param pageSize page size
     * @return combined url
     * @param <T> type
     */
    public static <T> String pickRandomPage(String url, SumTotal<T> sumTotal,
                                            @Nullable Integer pageSize) {
        Integer totalItemCount = sumTotal.getTotalItemCount();
        if (pageSize == null) {
            pageSize = random.nextInt(totalItemCount) + 1;//avoid 0
        }
        int i = pickRandomPage(sumTotal, pageSize);
        return UrlUtility.appendQuery(url, UrlUtility.getPageQuery(i, pageSize));
    }

}
