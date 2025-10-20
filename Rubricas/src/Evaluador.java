import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class Evaluador {
    public ArrayList<String> leerNombres(String ruta) throws IOException {
        ArrayList<String> lista = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(ruta))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                if(linea.trim().isEmpty()) continue;
                String[] partes = linea.split(";");
                lista.add(Arrays.toString(partes));
            }
        }

        return lista;
    }

    public ArrayList<String> leerRubica(String ruta) throws IOException {
        ArrayList<String> lista = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(ruta))) {
            String linea;
            boolean primera = true;

            while ((linea = br.readLine()) != null) {
                if (linea.trim().isEmpty()) continue;
                if (primera) { // saltar cabecera
                    primera = false;
                    continue;
                }

                String[] partes = linea.split(";");
                lista.add(Arrays.toString(partes));
            }
        }

        return lista;
    }
}
