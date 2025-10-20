import java.util.ArrayList;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner teclado = new Scanner(System.in);
        Evaluador evaluador = new Evaluador();

        String nombresFile = "nombres.txt";
        String rubricaFile = "rubrica.txt";

        try {
            ArrayList<String> alumnos = evaluador.leerNombres(nombresFile);

            System.out.println("Alumnos:");
            for (int i = 0; i < alumnos.size(); i++) {
                String a = alumnos.get(i).replace("[", "").replace("]", "");
                System.out.printf("%d. %s%n", i + 1, a);
            }
        } catch (Exception e) {

        }
    }
}