import java.io.*;
import java.math.BigInteger;
import java.nio.file.Files;
import java.util.Random;
import java.util.Scanner;

public class Hash {//*****************************************************************************************************************************************************************
//вспомогательный блок и блок инициализации

    public static int command = -1;
    public static BigInteger p, A, r, k, n;
    public static Pair Q = new Pair();

    public static BigInteger f(Pair R)
    {
        return R.x;
    }

    static void print() {
        System.out.println("Введите команду:");
        System.out.println("0: Генерация параметров");
        System.out.println("1: Вычислить точку kQ");
        System.out.println("2: Вычислить е");
        System.out.println("3: Найти точку R и выдать результат");
        System.out.print("-1: Выход\nКоманда: ");
        Scanner scan = new Scanner(System.in);
        command = scan.nextInt();
        if (command > 3 || command < -1)
            System.out.println("Неверный ввод");
    }

    public static void main(String[] args) throws IOException {
        do {
            print();
            init();
            if (check()) {
                System.out.println("Ошибка: неверные параметры или не найден файл");
                return;
            }
            switch (command) {
                case -1:
                    break;
                case 1:
                    step1();
                    break;
                case 2:
                    step2();
                    break;
                case 3:
                    step3();
                    break;
                case 0:
                    gengEll();
                    break;
            }

        } while (command != -1);
    }

    public static Pair sum(Pair x1y1, Pair x2y2, BigInteger p, BigInteger A) {
        try {
            if (x1y1 == null) {
                return null;
            }

            BigInteger x1 = x1y1.x, y1 = x1y1.y, x2 = x2y2.x, y2 = x2y2.y, alph;

            if (x1.equals(x2) && y1.equals(y2)) {
                if (y1.equals(BigInteger.ZERO))
                    return null;
                else
                    alph = ((x1.multiply(x1).multiply(BigInteger.valueOf(3)).add(A)).multiply((BigInteger.TWO.multiply(y1)).modInverse(p))).mod(p);
            } else
                alph = ((y2.subtract(y1)).multiply((x2.subtract(x1)).modInverse(p))).mod(p);

            BigInteger x3 = (alph.multiply(alph).subtract(x1).subtract(x2)).mod(p),
                    y3 = ((x1.subtract(x3)).multiply(alph).subtract(y1)).mod(p);

            return new Pair(x3, y3);
        } catch (ArithmeticException e) {
            return null;
        }
    }

    public static Pair mult(Pair point, BigInteger n, BigInteger A, BigInteger p) {
        Pair res = point;
        for (int i = 2; i <= n.intValue(); i++) {
            res = sum(res, point, p, A);
        }
        return res;
    }

    public static void init() throws FileNotFoundException {
        initParam("input.txt");
    }

    public static boolean check() {
        if (A == null || p == null || r == null || Q.x == null || Q.y == null || k == null || n == null)
            return true;
        return false;
    }

    public static void initParam(String path) throws FileNotFoundException {
        try {
            FileReader reader = new FileReader(path);
            Scanner scan = new Scanner(reader);
            n = new BigInteger(scan.nextLine());
            p = new BigInteger(scan.nextLine());
            A = new BigInteger(scan.nextLine());
            String[] help = scan.nextLine().split(" ");
            Q = new Pair(new BigInteger(help[0]), new BigInteger(help[1]));
            r = new BigInteger(scan.nextLine());
            k = new BigInteger(scan.nextLine());
            reader.close();
        } catch (Exception e) {
        }
    }

    /*public static void deleteAll() throws IOException {
        Files.deleteIfExists(new File("k_.txt").toPath());
        Files.deleteIfExists(new File("k.txt").toPath());
        Files.deleteIfExists(new File("R.txt").toPath());
        Files.deleteIfExists(new File("bit.txt").toPath());
        Files.deleteIfExists(new File("KK.txt").toPath());
        Files.deleteIfExists(new File("round.txt").toPath());
    }*/

    public static void gengEll() throws IOException {
        int n;
        Scanner scan = new Scanner(System.in);
        System.out.print("Введите количество бит n: ");
        n = scan.nextInt();

        GengEllipticCurve curve = new GengEllipticCurve(n);
    }
//*******************************************************************************************************************************************************************
//первый шаг: "Вычислить точку kQ"

    public static void step1() throws IOException
    {
        try
        {
            Pair kQ = mult(Q, k, A, p);
            FileWriter out = new FileWriter("kQ.txt");
            out.write(kQ.x + " " + kQ.y);
            out.close();
        }
        catch (IOException e)
        {
            System.out.println("Ошибка: неверные параметры или не найден файл");
            return;
        }
    }

//*******************************************************************************************************************************************************************
//второй шаг: "Подсчет е"

    public static void step2() throws IOException
    {
        try
        {
            FileReader reader = new FileReader("kQ.txt");
            Scanner scan = new Scanner(reader);

            BigInteger e = scan.nextBigInteger();
            e = e.mod(BigInteger.TWO.pow(n.intValue()));
            FileWriter out = new FileWriter("e.txt");
            out.write(e + "");
            out.close();
        }
        catch (IOException ex)
        {
            System.out.println("Ошибка: неверные параметры или не найден файл");
            return;
        }
    }

//*******************************************************************************************************************************************************************
//третий шаг: "Подсчет R и результат xr"

    public static void step3()
    {
        try
        {
            FileReader reader = new FileReader("e.txt");
            Scanner scan = new Scanner(reader);

            BigInteger e = scan.nextBigInteger();
            Pair R = mult(Q, BigInteger.TWO.pow(n.intValue()).multiply(k).add(e), A, p);

            FileWriter out = new FileWriter("R.txt");
            out.write(R.x + " " + R.y);
            out.close();
            System.out.println("Результат: " + R.x);
        }
        catch (IOException ex)
        {
            System.out.println("Ошибка: неверные параметры или не найден файл");
            return;
        }
    }
}