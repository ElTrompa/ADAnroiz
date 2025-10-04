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
        String profesorSustitudor = "";

        System.out.println("PROFESORES DISPONIBLE:");

        for(int i = 0; i < profesores.size(); i++) {
            if(!nombre.equals(profesores.get(i).getNombre()) &&
                    hora == profesores.get(i).getHora() &&
                    dia.equals(profesores.get(i).getDia()) &&
                    profesores.get(i).getClase().equals("Libre")) {
                System.out.println("- " + profesores.get(i).getNombre());
            }
        }

        System.out.println("¿Que profesor quieres elegir?");
        profesorSustitudor = teclado.next();

        for(int i = 0; i < profesores.size(); i++) {
            if (profesorSustitudor.equals(profesores.get(i).getNombre()) && hora == profesores.get(i).getHora() && dia.equals(profesores.get(i).getHora()) && profesores.get(i).getClase().equals("Libre")) {
                System.out.println("Profesor elegido para sustituir a " + nombre + " es " + profesorSustitudor);
            } else {
                System.out.println("Algo a salido mal");
                profesorSustitudor = "ERROR";
            }
        }

        return profesorSustitudor;
    }

    public void guardarHorarioConSustituciones() throws IOException {
        String nombreArchivo = "HorarioConSustituciones.csv";
        File archivo = new File(nombreArchivo);

        if (!archivo.exists()) {
            boolean creado = archivo.createNewFile();
            if (creado) {
                System.out.println("Archivo '" + nombreArchivo + "' creado por primera vez.");
            } else {
                System.out.println("No se pudo crear el archivo '" + nombreArchivo + "'.");
            }
        } else {
            System.out.println("El archivo '" + nombreArchivo + "' ya existe. Se sobrescribirá con los nuevos datos.");
        }

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(archivo))) {
            bw.write("Nombre,Dia,Hora,Clase");
            bw.newLine();

            for (Profesor p : profesores) {
                bw.write(p.getNombre() + "," + p.getDia() + "," + p.getHora() + "," + p.getClase());
                bw.newLine();
            }
        }

        System.out.println("Archivo '" + nombreArchivo + "' guardado correctamente.");
    }
}
