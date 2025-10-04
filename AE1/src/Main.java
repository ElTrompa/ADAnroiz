import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws IOException {
        Scanner teclado = new Scanner(System.in);

        ArrayList<Profesor> lista = new ArrayList<>();
        GestorSustituciones gestor = new GestorSustituciones(lista);

        gestor.cargarHorarios("Horario.csv");

        System.out.println("Introduce nombre del profesor ausente:");
        String ausente = teclado.next();
        System.out.println("Introduce dia de la semana:");
        String dia = teclado.next();
        System.out.println("Introduce hora:");
        int hora = teclado.nextInt();

        gestor.sustituirProfesor(ausente, hora, dia);

        gestor.guardarHorarioConSustituciones();

        System.out.println("Archivo 'HorarioConSustituciones.csv' generado correctamente.");
    }
}