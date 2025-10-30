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

    private static String rutaAlumnoXml = "alumnos.xml";
    private static String rutaAsignaturasXml = "asignaturas.xml";
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
                ArrayList<Alumno> leidos = leerAlumnosXML();

                System.out.println("\nAlumnos leidos desde el XML:");
                for (Alumno a : leidos) {
                    System.out.println(a);
                }
            } else if (opcion == 2) {//agregar alumnos
                System.out.println("Dime el nombre:");
                String nombre = teclado.next();
                System.out.println("Dime el curso:");
                String curso = teclado.next();
                System.out.println("Dime el dni:");
                int DNI = teclado.nextInt();
                System.out.println("Dime la fecha de nacimiento:");
                String fecha_nacimiento = teclado.next();
                System.out.println("Dime el correo de los padres:");
                String correo_padres = teclado.next();
                System.out.println("Dime el nombre del padre:");
                String nombre_padre = teclado.next();
                System.out.println("Dime el nombre de la madre:");
                String nombre_madre = teclado.next();

                Alumno nuevo = new Alumno(nombre, curso, DNI, fecha_nacimiento, correo_padres, nombre_padre, nombre_madre);

                ArrayList<Alumno> lista = new ArrayList<>();
                lista.add(nuevo);

                guardarAlumnosXML(lista);

                System.out.println("Nueva ausencia creada y guardada en ausencias.xml");
            }else if (opcion == 3) {//leer asignaturas
                ArrayList<Asignaturas> leidas = leerAsignaturasXML();

                System.out.println("\nAsignaturas leídas desde el XML:");
                for (Asignaturas a : leidas) {
                    System.out.println(a);
                }

            }else if (opcion == 4) {//crear asignaturas
                System.out.println("Dime el nombre:");
                String nombre = teclado.next();
                System.out.println("Dime los cursos:");
                String cursos = teclado.next();
                System.out.println("Dime las horas:");
                int horas = teclado.nextInt();
                System.out.println("Dime el profesor que imparte:");
                String profe_imparte = teclado.next();

                Asignaturas nueva = new Asignaturas(nombre, cursos, horas, profe_imparte);

                ArrayList<Asignaturas> lista = new ArrayList<>();
                lista.add(nueva);

                guardarAsignaturasXML(lista);

                System.out.println("Nueva asignatura creada y guardada en asignaturas.xml");
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
                System.out.println("Dime si esta justificada: (true/false)");
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
                System.out.println("Saliendo...");
            }else {
                System.err.println("Error en opcion");
            }

        } while (opcion != 0);
    }

    //getTagValue
    private static String getTagValue(Element parent, String tagName) {
        NodeList nodes = parent.getElementsByTagName(tagName);
        return (nodes.getLength() > 0) ? nodes.item(0).getTextContent().trim() : "";
    }

    //Ausencias
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

    //Alumnos
    public static ArrayList<Alumno> leerAlumnosXML () {
        ArrayList<Alumno> lista = new ArrayList<>();

        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(new File(rutaAlumnoXml));
            doc.getDocumentElement().normalize();

            NodeList nodos = doc.getElementsByTagName("alumno");

            for (int i = 0; i < nodos.getLength(); i++) {
                Element e = (Element) nodos.item(i);

                Alumno a = new Alumno(
                        getTagValue(e, "nombre"),
                        getTagValue(e, "curso"),
                        Integer.parseInt(getTagValue(e, "DNI")),
                        getTagValue(e, "fecha_nacimiento"),
                        getTagValue(e, "correo_padres"),
                        getTagValue(e, "nombre_padre"),
                        getTagValue(e, "nombre_madre")
                );

                lista.add(a);
            }
        }catch (Exception e) {
            System.err.println("Error leer");
        }

        return lista;
    }

    public static void guardarAlumnosXML (ArrayList<Alumno> lista) {
        try (FileWriter writer = new FileWriter(rutaAlumnoXml)) {
            writer.write("<alumnos>\n");

            for (Alumno a : lista) {
                writer.write("  <alumno>\n");
                writer.write("    <nombre>" + a.getAlumno() + "</nombre>\n");
                writer.write("    <curso>" + a.getCurso() + "</curso>\n");
                writer.write("    <DNI>" + a.getDni() + "</DNI>\n");
                writer.write("    <fecha_nacimiento>" + a.getFecha() + "</fecha_nacimiento>\n");
                writer.write("    <correo_padres>" + a.getCorreoPadres() + "</correo_padres>\n");
                writer.write("    <nombre_padre>" + a.getNombrePadre() + "</nombre_padre>\n");
                writer.write("    <nombre_madre>" + a.getNombreMadre() + "</nombre_madre>\n");
                writer.write("  </alumno>\n");
            }

            writer.write("</alumnos>\n");
            System.out.println("Archivo XML guardado correctamente en: " + rutaAlumnoXml);

        } catch (IOException e) {
            System.out.println("Error guardar");
        }
    }

    //Asignaturas
    public static ArrayList<Asignaturas> leerAsignaturasXML () {
        ArrayList<Asignaturas> lista = new ArrayList<>();

        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(new File(rutaAsignaturasXml));
            doc.getDocumentElement().normalize();

            NodeList nodos = doc.getElementsByTagName("asignatura");

            for (int i = 0; i < nodos.getLength(); i++) {
                Element e = (Element) nodos.item(i);

                Asignaturas a = new Asignaturas(
                        getTagValue(e, "nombre"),
                        getTagValue(e, "cursos"),
                        Integer.parseInt(getTagValue(e, "horas")),
                        getTagValue(e, "profe_imparte")
                );

                lista.add(a);
            }
        }catch (Exception e) {
            System.err.println("Error leer");
        }

        return lista;
    }

    public static void guardarAsignaturasXML (ArrayList<Asignaturas> lista) {
        try (FileWriter writer = new FileWriter(rutaAsignaturasXml)) {
            writer.write("<asignaturas>\n");

            for (Asignaturas a : lista) {
                writer.write("  <asignatura>\n");
                writer.write("    <nombre>" + a.getNombreAsignatura() + "</nombre>\n");
                writer.write("    <cursos>" + a.getCursos() + "</cursos>\n");
                writer.write("    <horas>" + a.getHoras() + "</horas>\n");
                writer.write("    <profe_imparte>" + a.getProfesor() + "</profe_imparte>\n");
                writer.write("  </asignatura>\n");
            }

            writer.write("</asignaturas>\n");
            System.out.println("Archivo XML guardado correctamente en: " + rutaAsignaturasXml);

        } catch (IOException e) {
            System.out.println("Error guardar");
        }
    }
    }

