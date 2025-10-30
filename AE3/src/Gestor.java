import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;


public class Gestor {
    Scanner teclado = new Scanner(System.in);

    private String rutaAlumnoXml = "alumnos.xml";
    private String rutaAsignaturasXml = "asignaturas.xml";
    private static String rutaAusenciasXml = "ausencias.xml";

    public void menu() {

        int opcion;

        do {
            System.out.println("GESTOR DE FALTAS");
            System.out.println("1. Ver alumnos");
            System.out.println("2. Agregar alumno");
            System.out.println("3. Ver asignaturas");
            System.out.println("4. Agregar asignatura");
            System.out.println("5. Registrar ausencia");
            System.out.println("6. Ver ausencias");
            System.out.println("0. Salir");
            System.out.println("Selecciona opcion: ");
            opcion = Integer.parseInt(teclado.next());

            if (opcion == 1) {//leer alumnos

            } else if (opcion == 2) {//agregar alumnos

            }else if (opcion == 3) {//leer asignaturas

            }else if (opcion == 4) {//crear asignaturas

            }else if (opcion == 5) { // Registrar ausencias
                System.out.println("Dime la fecha:");
                String fecha = teclado.next();
                System.out.println("Dime la hora de inicio:");
                String horaInicio = teclado.next();
                System.out.println("Dime la hora de fin:");
                String horaFin = teclado.next();
                System.out.println("Dime la asignatura:");
                String asignatura = teclado.next();
                System.out.println("Dime el nombre del alumno:");
                String nombreAlumno = teclado.next();
                System.out.println("Dime si esta justificada:");
                Boolean justificada = teclado.nextBoolean();
                System.out.println("Dime el curso:");
                String curso = teclado.next();

                Ausencias nueva = new Ausencias(fecha,horaInicio,horaFin,asignatura,nombreAlumno,justificada,curso);

                ArrayList<Ausencias> lista = new ArrayList<>();
                lista.add(nueva);

                guardarAusenciasXML(lista);

                System.out.println("Nueva ausencia creada y guardada en ausencias.xml");

            }else if (opcion == 6) { // Leer ausencias
                ArrayList<Ausencias> leidas = leerAusenciasXML();

                System.out.println("\nAusencias leídas desde el XML:");
                for (Ausencias a : leidas) {
                    System.out.println(a);
                }
            }else if (opcion == 0) {

            }else {
                System.err.println("Error en opcion");
            }

        } while (opcion != 0);
    }

    private static String getTagValue(Element parent, String tagName) {
        NodeList nodes = parent.getElementsByTagName(tagName);
        return (nodes.getLength() > 0) ? nodes.item(0).getTextContent().trim() : "";
    }

    public static ArrayList<Ausencias> leerAusenciasXML () {
        ArrayList<Ausencias> lista = new ArrayList<>();

        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(new File(rutaAusenciasXml));
            doc.getDocumentElement().normalize();

            NodeList nodos = doc.getElementsByTagName("ausencia");

            for (int i = 0; i < nodos.getLength(); i++) {
                Element e = (Element) nodos.item(i);

                Ausencias a = new Ausencias(
                        getTagValue(e, "fecha"),
                        getTagValue(e, "hora_inicio"),
                        getTagValue(e, "hora_fin"),
                        getTagValue(e, "asignatura"),
                        getTagValue(e, "alumno"),
                        Boolean.parseBoolean(getTagValue(e, "justificada")),
                        getTagValue(e, "curso")
                );

                lista.add(a);
            }
        }catch (Exception e) {
            System.err.println("Error leer");
        }

        return lista;
    }

    public static void guardarAusenciasXML (ArrayList<Ausencias> lista) {
        try (FileWriter writer = new FileWriter(rutaAusenciasXml)) {
            writer.write("<ausencias>\n");

            for (Ausencias a : lista) {
                writer.write("  <ausencia>\n");
                writer.write("    <fecha>" + a.getFecha() + "</fecha>\n");
                writer.write("    <hora_inicio>" + a.getHoraInicio() + "</hora_inicio>\n");
                writer.write("    <hora_fin>" + a.getHoraFin() + "</hora_fin>\n");
                writer.write("    <asignatura>" + a.getAsignatura() + "</asignatura>\n");
                writer.write("    <alumno>" + a.getAlumno() + "</alumno>\n");
                writer.write("    <justificada>" + a.isJustificada() + "</justificada>\n");
                writer.write("    <curso>" + a.getCurso() + "</curso>\n");
                writer.write("  </ausencia>\n");
            }

            writer.write("</ausencias>\n");
            System.out.println("Archivo XML guardado correctamente en: " + rutaAusenciasXml);

        } catch (IOException e) {
            System.out.println("Error guardar");
        }
        }
    }

