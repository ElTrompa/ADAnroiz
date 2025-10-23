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

        StringBuilder sb = new StringBuilder();
        sb.append(String.join(";", alumno));
        for (int nota : notas) {
            sb.append(";").append(nota);
        }
        sb.append(";").append(totalStr);

        boolean remplazo = false;

        for (int i = 0; i < lineas.size(); i++) {
            if (lineas.get(i).startsWith(alumno[0] + ";" + alumno[1] + ";" + alumno[2])) {
                lineas.set(i, sb.toString());
                remplazo = true;
                break;
            }
        }

        if (!remplazo) {
            lineas.add(sb.toString());
        }

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(ruta))) {
            for (String l : lineas) {
                bw.write(l);
                bw.newLine();
            }
        }
    }
}