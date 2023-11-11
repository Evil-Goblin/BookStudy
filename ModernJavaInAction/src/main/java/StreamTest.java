import java.util.List;

public class StreamTest {
    public static void main(String[] args) {
        List<Integer> integers = List.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
//        integers.stream().map(i -> {
        integers.parallelStream().map(i -> {
                    try {
                        System.out.println("[map][before][ " + System.currentTimeMillis() + " ] [ " + Thread.currentThread().getId() + " ] " + "i = " + i);
                        Thread.sleep(100);
                        System.out.println("[map][after][ " + System.currentTimeMillis() + " ] [ " + Thread.currentThread().getId() + " ] " + "i = " + i);
                        return i;
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                })
                .filter(i -> {
                    try {
                        System.out.println("[filter][before][ " + System.currentTimeMillis() + " ] [ " + Thread.currentThread().getId() + " ] " + "i = " + i);
                        Thread.sleep(100);
                        System.out.println("[filter][after][ " + System.currentTimeMillis() + " ] [ " + Thread.currentThread().getId() + " ] " + "i = " + i);
                        return i > 5;
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                })
                .forEach(i -> {
                    try {
                        System.out.println("[forEach][before][ " + System.currentTimeMillis() + " ] [ " + Thread.currentThread().getId() + " ] " + "i = " + i);
                        Thread.sleep(100);
                        System.out.println("[forEach][after][ " + System.currentTimeMillis() + " ] [ " + Thread.currentThread().getId() + " ] " + "i = " + i);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                });
        /* 실행 결과
          [map][before][ 1699717702691 ] [ 21 ] i = 1
          [map][before][ 1699717702691 ] [ 19 ] i = 4
          [map][before][ 1699717702691 ] [ 20 ] i = 5
          [map][before][ 1699717702691 ] [ 16 ] i = 9
          [map][before][ 1699717702690 ] [ 1 ] i = 7
          [map][before][ 1699717702691 ] [ 17 ] i = 2
          [map][before][ 1699717702691 ] [ 15 ] i = 3
          [map][before][ 1699717702691 ] [ 18 ] i = 6
          [map][after][ 1699717702799 ] [ 20 ] i = 5
          [map][after][ 1699717702799 ] [ 21 ] i = 1
          [filter][before][ 1699717702799 ] [ 20 ] i = 5
          [filter][before][ 1699717702799 ] [ 21 ] i = 1
          [map][after][ 1699717702801 ] [ 15 ] i = 3
          [filter][before][ 1699717702801 ] [ 15 ] i = 3
          [map][after][ 1699717702801 ] [ 18 ] i = 6
          [map][after][ 1699717702801 ] [ 19 ] i = 4
          [filter][before][ 1699717702801 ] [ 19 ] i = 4
          [filter][before][ 1699717702801 ] [ 18 ] i = 6
          [map][after][ 1699717702802 ] [ 16 ] i = 9
          [filter][before][ 1699717702802 ] [ 16 ] i = 9
          [map][after][ 1699717702804 ] [ 1 ] i = 7
          [filter][before][ 1699717702804 ] [ 1 ] i = 7
          [map][after][ 1699717702804 ] [ 17 ] i = 2
          [filter][before][ 1699717702804 ] [ 17 ] i = 2
          [filter][after][ 1699717702901 ] [ 21 ] i = 1
          [filter][after][ 1699717702901 ] [ 20 ] i = 5
          [filter][after][ 1699717702901 ] [ 18 ] i = 6
          [map][before][ 1699717702902 ] [ 20 ] i = 10
          [map][before][ 1699717702902 ] [ 21 ] i = 8
          [forEach][before][ 1699717702902 ] [ 18 ] i = 6
          [filter][after][ 1699717702901 ] [ 19 ] i = 4
          [filter][after][ 1699717702902 ] [ 15 ] i = 3
          [filter][after][ 1699717702902 ] [ 16 ] i = 9
          [forEach][before][ 1699717702902 ] [ 16 ] i = 9
          [filter][after][ 1699717702908 ] [ 1 ] i = 7
          [filter][after][ 1699717702908 ] [ 17 ] i = 2
          [forEach][before][ 1699717702908 ] [ 1 ] i = 7
          [map][after][ 1699717703007 ] [ 20 ] i = 10
          [filter][before][ 1699717703007 ] [ 20 ] i = 10
          [map][after][ 1699717703007 ] [ 21 ] i = 8
          [filter][before][ 1699717703007 ] [ 21 ] i = 8
          [forEach][after][ 1699717703007 ] [ 16 ] i = 9
          [forEach][after][ 1699717703007 ] [ 18 ] i = 6
          [forEach][after][ 1699717703012 ] [ 1 ] i = 7
          [filter][after][ 1699717703107 ] [ 21 ] i = 8
          [forEach][before][ 1699717703108 ] [ 21 ] i = 8
          [filter][after][ 1699717703112 ] [ 20 ] i = 10
          [forEach][before][ 1699717703112 ] [ 20 ] i = 10
          [forEach][after][ 1699717703209 ] [ 21 ] i = 8
          [forEach][after][ 1699717703215 ] [ 20 ] i = 10
         */
    }
}
