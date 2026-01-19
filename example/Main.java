package example;

import java.util.Scanner;

import badWordScanner.BadWordScanner;
import badWordScanner.Response;
import badWordScanner.Sensetivity;

public class Main {
    static String input = "";

    public static void main() {
        System.out.println("Scanner Bereit");
        Scanner scanner = new Scanner(System.in);

        //Is an Example for LM Studios, using the qwen2.5-3b-instruct model (3B is way too weak, it is just for testing!)
        BadWordScanner badWordScanner = new BadWordScanner(Sensetivity.ZERO_TOLERANCE, "http://localhost:1234/v1/chat/completions", "qwen2.5-3b-instruct");

        while (!input.equals("exit")) {
            input = scanner.nextLine();
            Response output = badWordScanner.Check(input);
            if (output.issave) {
                System.out.println("-Text ist gut-");
            } else if (!output.issave) {
                System.out.println(output.message);
            }

        }
        scanner.close();
    }
}