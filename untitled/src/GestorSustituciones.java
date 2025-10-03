import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class GestorSustituciones {
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



        return profesorSustitudor;
    }
}
