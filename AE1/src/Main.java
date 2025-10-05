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

            if (opcion == 1) {
                System.out.println("Introduce nombre del profesor ausente:");
                String ausente = teclado.next().toLowerCase();
                System.out.println("Introduce dia de la semana:");
                String Insertadodia = teclado.next().toLowerCase();
                String dia = "";
                if (Insertadodia.equals("lunes") ||
                        Insertadodia.equals("martes") ||
                        Insertadodia.equals("miercoles") ||
                        Insertadodia.equals("jueves") ||
                        Insertadodia.equals("viernes")) {
                    dia = Insertadodia;
                }else {
                    System.out.println("Introduce bien el dia bien");
                    continue;
                }

                System.out.println("Introduce hora:");
                int Insetadahora = teclado.nextInt();
                int hora = 0;

                if (Insetadahora >= 1 || Insetadahora <= 6) {
                    hora = Insetadahora;
                }else {
                    System.out.println("Introduce bien la hora");
                    continue;
                }

                String sustituto = gestor.sustituirProfesor(ausente, hora, dia);

                if (sustituto != null) {
                    gestor.guardarHorarioConSustituciones();
                } else {
                    System.out.println("No se ha realizado ninguna sustitución. No se guardará el archivo.");
                }
            }else if (opcion == 0) {
                System.out.println("Adios");
            }else {
                System.out.println("Introduce solo los numeros 0 y 1");
            }
        }while (opcion != 0);
    }
}