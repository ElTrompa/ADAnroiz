import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

public class GestorSustituciones {
    Scanner teclado = new Scanner(System.in);

    private ArrayList<Profesor> profesores = new ArrayList<>();

    public GestorSustituciones(ArrayList<Profesor> profesores) {
        this.profesores = profesores;
    }

    public void cargarHorarios(String ficheroHorario) throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(ficheroHorario))) {
            String linea;
            while((linea = br.readLine()) != null) {
                if (linea.trim().isEmpty()) continue;
                String[] datos = linea.split(",");
                if (datos.length < 4) continue;

                String nombre = datos[0];
                String dia = datos[1];
                int hora = Integer.parseInt(datos[2]);
                String clase = datos[3];

                Profesor pr = new Profesor(nombre, hora, clase, dia);
                profesores.add(pr);
            }
        }
    }

    public String sustituirProfesor(String nombre, int hora, String dia) {
        System.out.println("PROFESORES DISPONIBLES:");

        ArrayList<Profesor> disponibles = new ArrayList<>();

        for (Profesor p : profesores) {
            if (!nombre.equalsIgnoreCase(p.getNombre()) &&
                    hora == p.getHora() &&
                    dia.equalsIgnoreCase(p.getDia()) &&
                    p.getClase().equalsIgnoreCase("Libre")) {
                System.out.println("- " + p.getNombre());
                disponibles.add(p);
            }
        }

        if (disponibles.isEmpty()) {
            System.out.println("No hay profesores disponibles para ese día y hora.");
            return "ERROR";
        }

        System.out.println("¿Qué profesor quieres elegir?");
        String profesorSustituto = teclado.next();

        boolean encontrado = false;

        for (Profesor p : disponibles) {
            if (profesorSustituto.equalsIgnoreCase(p.getNombre())) {
                System.out.println("Profesor elegido para sustituir a " + nombre + " es " + profesorSustituto);
                encontrado = true;
                break;
            }
        }

        if (!encontrado) {
            System.out.println("El nombre introducido no coincide con ningún profesor disponible.");
            profesorSustituto = "ERROR";
        }

        return profesorSustituto;
    }

    public void guardarHorarioConSustituciones() throws IOException {
        String nombreArchivo = "HorarioConSustituciones.csv";
        File archivo = new File(nombreArchivo);

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(archivo))) {
            bw.write("Nombre,Dia,Hora,Clase");
            bw.newLine();

            for (Profesor p : profesores) {
                bw.write(p.getNombre() + "," + p.getDia() + "," + p.getHora() + "," + p.getClase());
                bw.newLine();
            }
        }

        System.out.println("Horario con sustituciones guardado en '" + nombreArchivo + "'.");
    }
}
