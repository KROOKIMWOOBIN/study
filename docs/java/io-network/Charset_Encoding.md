## 문자 집합 조회
```java
public class Main {

    public static void main(String[] args) {
        // 이용 가능한 모든 CharSet [Java, OS]
        SortedMap<String, Charset> charsets = Charset.availableCharsets();
        for (String charsetName : charsets.keySet()) {
            System.out.println("charsetName = " + charsetName);
        }
        System.out.println("=====");
        // 문자로 조회 (대소문자 구분 X)
        Charset charset1 = Charset.forName("MS949");
        System.out.println("charset1 = " + charset1);
        System.out.println("=====");
        // 별칭 조회
        Set<String> aliases = charset1.aliases();
        for (String alias : aliases) {
            System.out.println("alias = " + alias);

        }
        System.out.println("=====");
        // UTF-8 문자로 조회
        Charset charset2 = Charset.forName("UTF-8");
        System.out.println("charset2 = " + charset2);
        System.out.println("=====");
        // UTF-8 상수로 조회
        Charset charset3 = StandardCharsets.UTF_8;
        System.out.println("charset3 = " + charset3);
        System.out.println("=====");
        // 시스템의 기본 Charset 조회
        Charset defaultCharset = Charset.defaultCharset();
        System.out.println("defaultCharset = " + defaultCharset);
    }

}
```

## 문자 인코딩/디코딩
```java
public class Main {

    private static final Charset EUC_KR = Charset.forName("EUC-KR");
    private static final Charset MS_949 = Charset.forName("MS949");

    public static void main(String[] args) {
        System.out.println("== 영문 ASCII 인코딩 ==");
        test("A", US_ASCII, US_ASCII);
        test("A", US_ASCII, ISO_8859_1);
        test("A", US_ASCII, EUC_KR);
        test("A", US_ASCII, MS_949);
        test("A", US_ASCII, UTF_8);
        test("A", US_ASCII, UTF_16BE);

        System.out.println("== 한글 인코딩 - 기본 ==");
        test("가", US_ASCII, US_ASCII);
        test("가", ISO_8859_1, ISO_8859_1);
        test("가", EUC_KR, EUC_KR);
        test("가", MS_949, MS_949);
        test("가", UTF_8, UTF_8);
        test("가", UTF_16BE, UTF_16BE);

        System.out.println("== 한글 인코딩 - 복잡한 문자 ==");
        test("쀍", US_ASCII, US_ASCII);
        test("쀍", ISO_8859_1, ISO_8859_1);
        test("쀍", EUC_KR, EUC_KR);
        test("쀍", MS_949, MS_949);
        test("쀍", UTF_8, UTF_8);
        test("쀍", UTF_16BE, UTF_16BE);

        System.out.println("== 한글 인코딩 - 디코딩이 다른 경우 ==");
        test("가", EUC_KR, MS_949);
        test("쀍", MS_949, EUC_KR);
        test("가", EUC_KR, UTF_8);
        test("가", MS_949, UTF_8);
        test("가", UTF_8, MS_949);

        System.out.println("== 영문 인코딩 - 디코딩이 다른 경우 ==");
        test("A", EUC_KR, UTF_8);
        test("A", MS_949, UTF_8);
        test("A", UTF_8, MS_949);
        test("A", UTF_8, UTF_16BE);
    }

    private static void test(String text, Charset encodingCharset, Charset decondigCharset) {
        byte[] encoded = text.getBytes(encodingCharset);
        String decoded = new String(encoded, decondigCharset);
        System.out.printf("%s -> [%s] 인코딩 -> %s %sbyte -> [%s] 디코딩 -> %s\n",
                text, encodingCharset, Arrays.toString(encoded), encoded.length,
                decondigCharset, decoded);
    }

}
```