package com.mt.helper.utility;

import com.mt.helper.pojo.SumTotal;
import java.util.Random;
import java.util.UUID;
import org.springframework.http.HttpMethod;
import org.springframework.lang.Nullable;

public class RandomUtility {
    private static final Random random = new Random();

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
     *
     * @param url      url to combine
     * @param sumTotal sum total
     * @param pageSize page size
     * @param <T>      type
     * @return combined url
     */
    public static <T> String pickRandomPage(String url, SumTotal<T> sumTotal,
                                            @Nullable Integer pageSize) {
        Integer totalItemCount = sumTotal.getTotalItemCount();
        if (pageSize == null) {
            pageSize = random.nextInt(totalItemCount) + 1;//avoid 0
        }
        int i = pickRandomPage(sumTotal, pageSize);
        return HttpUtility.appendQuery(url, HttpUtility.getPageQuery(i, pageSize));
    }

    public static String randomStringWithNum() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }

    public static String randomStringWithNumNullable() {
        int i = random.nextInt(2);
        if (i == 0) {
            return null;
        }
        return UUID.randomUUID().toString().replaceAll("-", "");
    }
    //TODO make sure random string length is stable
    public static String randomStringNoNum() {
        return UUID.randomUUID().toString().replaceAll("-", "").replaceAll("\\d", "");
    }
    public static String randomHttpPath(){
        return "test/" + RandomUtility.randomStringNoNum()
            +
            "/abc";
    }
    public static Long randomLong() {
        return random.nextLong();
    }

    public static Integer randomInt() {
        return random.nextInt();
    }

    public static boolean randomBoolean() {
        int i = random.nextInt(2);
        return i == 0 ? Boolean.TRUE : Boolean.FALSE;
    }

    public static <E extends Enum<E>> E randomEnum(Enum<E>[] e) {
        int i = pickRandomFromList(e.length);
        return (E) e[i];
    }

    public static String randomHttpMethod() {
        return randomEnum(HttpMethod.values()).name();
    }

    public static String randomLocalHostUrl() {
        return "http://localhost:" + random.nextInt(10000);
    }

    public static String randomEmail() {
        return RandomUtility.randomStringWithNum() + "@gmail.com";
    }

    public static String randomPassword() {
        return "P1!" + UUID.randomUUID().toString().replaceAll("-", "").substring(0, 10);
    }
}
