import java.util.Scanner;

public class Main {

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);

        String email = scanner.nextLine();

        AsyncEmailValidator.validateAsync(email)
                .thenAccept(result -> {

                    System.out.println(result);

                    if(result.isValid())
                        System.out.println("Email is valid");
                    else
                        System.out.println("Invalid email");

                });
    }
}