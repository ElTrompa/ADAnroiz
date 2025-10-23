import java.io.*;
import java.util.*;

public class Main {
    public static void main(String[] args) {
        Evaluador evaluador = new Evaluador();
        Scanner sc = new Scanner(System.in);

        try {
            // Leer alumnos y rúbrica
            ArrayList<String> alumnos = evaluador.leerNombres("nombres.txt");
            ArrayList<String> rubrica = evaluador.leerRubica("rubrica.txt");

            System.out.println("=== SISTEMA DE EVALUACIÓN ===");
            System.out.println("Alumnos disponibles:");

            for (int i = 0; i < alumnos.size(); i++) {
                System.out.println((i + 1) + ". " + alumnos.get(i));
            }

            System.out.print("Seleccione el número del alumno a evaluar: ");
            int index = sc.nextInt();
            sc.nextLine(); // limpiar buffer

            String alumnoLinea = alumnos.get(index - 1);
            String[] alumno = alumnoLinea.replace("[", "").replace("]", "").split(",\\s*");

            System.out.println("\nEvaluando a: " + alumno[0] + " " + alumno[1] + " " + alumno[2]);

            ArrayList<Integer> notas = new ArrayList<>();

            // Iterar sobre las preguntas de la rúbrica
            for (String linea : rubrica) {
                String[] partes = linea.replace("[", "").replace("]", "").split(",\\s*");

                System.out.println("\n" + partes[0]); // Pregunta
                System.out.println("Opciones de nota: 100, 66, 33, 0");

                System.out.print("Introduce la nota para esta pregunta: ");
                int nota = sc.nextInt();
                sc.nextLine();

                notas.add(nota);
            }

            // Guardar resultados
            evaluador.guardarEvaluaciones("nombres.txt", alumno, notas);
            System.out.println("\n✅ Evaluación guardada con éxito.");

        } catch (IOException e) {
            System.err.println("Error al leer o escribir archivos: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Error general: " + e.getMessage());
        }

        sc.close();
    }
}
