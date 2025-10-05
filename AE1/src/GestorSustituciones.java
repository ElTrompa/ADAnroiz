import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

public class GestorSustituciones {
    Scanner teclado = new Scanner(System.in);

    private ArrayList<Profesor> profesores = new ArrayList<>();

    public GestorSustituciones(ArrayList<Profesor> profesores) {
        this.profesores = profesores;
    }

    public void realizarSustitucion() throws IOException {
        System.out.println("Introduce nombre del profesor ausente:");
        String ausente = teclado.nextLine().toLowerCase();

        System.out.println("Introduce día de la semana:");
        String dia = teclado.nextLine().toLowerCase();

        if (!dia.equals("lunes") && !dia.equals("martes") && !dia.equals("miercoles") &&
                !dia.equals("jueves") && !dia.equals("viernes")) {
            System.out.println("Día no válido. Debe ser de lunes a viernes.");
            return;
        }

        System.out.println("Introduce hora (1 a 6):");
        int hora = teclado.nextInt();
        teclado.nextLine();

        if (hora < 1 || hora > 6) {
            System.out.println("Hora no válida. Debe estar entre 1 a 6.");
            return;
        }

        String sustituto = sustituirProfesor(ausente, hora, dia);

        if (sustituto != null) {
            guardarHorarioConSustituciones();
        } else {
            System.out.println("No se ha realizado ninguna sustitución. No se guardará el archivo.");
        }
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
            return null;
        }

        System.out.println("¿Qué profesor quieres elegir?");
        String profesorSustituto = teclado.next();

        for (Profesor p : disponibles) {
            if (profesorSustituto.equalsIgnoreCase(p.getNombre())) {
                System.out.println("Profesor elegido para sustituir a " + nombre + " es " + profesorSustituto);
                return profesorSustituto;
            }
        }

        System.out.println("El nombre introducido no coincide con ningún profesor disponible.");
        return null;
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
