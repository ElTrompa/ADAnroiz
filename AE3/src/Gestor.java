import java.util.ArrayList;
import java.util.Scanner;

public class Gestor {
    Scanner teclado = new Scanner(System.in);

    private ArrayList<Alumno> alumnos = new ArrayList<>();
    private ArrayList<Asignaturas> asignaturas = new ArrayList<>();
    private ArrayList<Ausencias> ausencias = new ArrayList<>();

    public void menu() {
        int opcion = 0;

        do {
            System.out.println("\n=== GESTOR DE FALTAS ===");
            System.out.println("1. Ver alumnos");
            System.out.println("2. Agregar alumno");
            System.out.println("3. Ver asignaturas");
            System.out.println("4. Agregar asignatura");
            System.out.println("5. Registrar ausencia");
            System.out.println("6. Ver ausencias");
            System.out.println("0. Salir");
            System.out.print("Selecciona opcion: ");
            opcion = teclado.nextInt();

            switch (opcion) {
                case 1 -> listarAlumnos();
                case 2 -> agregarAlumno();
                case 3 -> listarAsignaturas();
                case 4 -> agregarAsignatura();
                case 5 -> registrarAusencia();
                case 6 -> listarAusencias();
                case 0 -> System.out.println("Saliendo del programa");
                default -> System.out.println("Opcion no valida");
            }
        }while (opcion != 0);
    }

    private void listarAusencias() {
    }

    private void registrarAusencia() {
    }

    private void agregarAsignatura() {
    }

    private void listarAsignaturas() {
    }

    private void agregarAlumno() {
    }

    private void listarAlumnos() {
    }
}
