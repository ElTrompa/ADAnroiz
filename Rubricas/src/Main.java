import java.io.*;
import java.util.*;

public class Main {
    public static void main(String[] args) throws IOException {
        Evaluador evaluador = new Evaluador();
        Scanner teclado = new Scanner(System.in);

        int opcion = 0;
        ArrayList<String> alumnos = evaluador.leerNombres("nombres.txt");
        ArrayList<String> rubrica = evaluador.leerRubica("rubrica.txt");

        do {
            System.out.println("=== SISTEMA DE EVALUACION ===");
            System.out.println("0 - Salir\n1 - Comprobar nota");
            opcion = teclado.nextInt();

            if (opcion != 0) {
                try {
                    System.out.println("Alumnos disponibles:");

                    for (int i = 0; i < alumnos.size(); i++) {
                        System.out.println((i + 1) + ". " + alumnos.get(i));
                    }

                    System.out.print("Seleccione el numero del alumno a evaluar: ");
                    int index = teclado.nextInt();
                    teclado.nextLine();

                    String alumnoLinea = alumnos.get(index - 1);
                    String[] alumno = alumnoLinea.split(";");

                    System.out.println("\nEvaluando a: " + alumno[0] + " " + alumno[1] + " " + alumno[2]);

                    ArrayList<Integer> notas = new ArrayList<>();

                    for (String linea : rubrica) {
                        String[] partes = linea.replace("", "").replace("", "").split(",\\s*");

                        System.out.println("\n" + partes[0]);
                        System.out.println("Opciones de nota: 100, 66, 33, 0");

                        System.out.print("Introduce la nota para esta pregunta: ");
                        int nota = teclado.nextInt();

                        if (nota == 100 || nota == 66 || nota == 33 || nota == 0) {
                            notas.add(nota);
                        }else {
                            boolean parar = false;

                            do {
                                System.out.println("Recuerda opciones de nota: 100, 66, 33, 0");
                                nota = teclado.nextInt();
                                teclado.nextLine();

                                if (nota == 100 || nota == 66 || nota == 33 || nota == 0) {
                                    notas.add(nota);
                                    parar = true;
                                }
                            } while (!parar);
                        }
                    }

                    evaluador.guardarEvaluaciones("nombres.txt", alumno, notas);
                    System.out.println("\nEvaluacion guardada con exito.");

                } catch (IOException e) {
                    System.err.println("Error al leer o escribir archivos: " + e.getMessage());
                } catch (Exception e) {
                    System.err.println("Error general: " + e.getMessage());
                }
            }else {
                alumnos = evaluador.leerNombres("nombres.txt");

                System.out.println("Alumnos y nota");

                for (int i = 0; i < alumnos.size(); i++) {
                    System.out.println((i + 1) + ". " + alumnos.get(i));
                }
            }
        }while (opcion != 0);

        teclado.close();
    }
}
