import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;

public class Evaluador {
    public ArrayList<String> leerNombres(String ruta) throws IOException {
        ArrayList<String> lista = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(ruta))) {
            String linea;
            boolean primera = true;

            while ((linea = br.readLine()) != null) {
                if (linea.trim().isEmpty()) continue;

                if (primera) {
                    primera = false;
                    continue;
                }

                lista.add(linea);
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
                lista.add(linea);
            }
        }

        return lista;
    }

    public void guardarEvaluaciones(String ruta, String[] alumno, ArrayList<Integer> notas) throws IOException {
        ArrayList<String> lineas = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(ruta))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                lineas.add(linea);
            }
        }

        int suma = 0;
        for (int nota : notas) {
            suma += nota;
        }
        int total = Math.round((float) suma / notas.size());
        String totalStr = "Total:" + total;

        String nuevaLinea = alumno[0] + ";" + alumno[1] + ";" + alumno[2];
        for (int nota : notas) {
            nuevaLinea += ";" + nota;
        }
        nuevaLinea += ";" + totalStr;

        boolean reemplazo = false;

        for (int i = 0; i < lineas.size(); i++) {
            if (lineas.get(i).startsWith(alumno[0] + ";" + alumno[1] + ";" + alumno[2])) {
                lineas.set(i, nuevaLinea);
                reemplazo = true;
                break;
            }
        }

        if (!reemplazo) {
            lineas.add(nuevaLinea);
        }

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(ruta))) {
            for (String l : lineas) {
                bw.write(l);
                bw.newLine();
            }
        }
    }
}