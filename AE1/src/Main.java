import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) throws IOException {
        Scanner teclado = new Scanner(System.in);
        ArrayList<Profesor> lista = new ArrayList<>();
        GestorSustituciones gestor = new GestorSustituciones(lista);

        gestor.cargarHorarios("horario.csv");

        int opcion = 0;
        do {
            System.out.println("Quieres sustituir a alguien?\n0.Salir\n1.Sustituir");
            opcion = teclado.nextInt();
            teclado.nextLine();

            if (opcion == 1) {
                gestor.realizarSustitucion();
            } else if (opcion == 0) {
                System.out.println("Adios");
            } else {
                System.out.println("Introduce solo los numeros 0 y 1");
            }
        } while (opcion != 0);
    }
}