import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner teclado = new Scanner(System.in);

        System.out.println("Introduce nombre del profesor ausente:");
        String ausente = teclado.nextLine();
        System.out.println("Introduce dia de la semana:");
        String dia = teclado.nextLine();
        System.out.println("Introduce hora:");
        String hora = teclado.nextLine();

    }
}