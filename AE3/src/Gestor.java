import org.w3c.dom.*;
import javax.xml.parsers.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Gestor {
    private List<Alumno> alumnos = new ArrayList<>();
    private List<Asignaturas> asignaturas = new ArrayList<>();
    private List<Ausencias> ausencias = new ArrayList<>();

    private static final String RUTA_ALUMNOS = "C:/Users/Andreu/git/ADAnroiz/AE3/alumnos.xml";
    private static final String RUTA_ASIGNATURAS = "C:/Users/Andreu/git/ADAnroiz/AE3/asignaturas.xml";
    private static final String RUTA_AUSENCIAS = "C:/Users/Andreu/git/ADAnroiz/AE3/ausencias.xml";

    private Scanner sc = new Scanner(System.in);

    public void menu() {
        cargarDatos();

        int opcion;
        do {
            System.out.println("\n=== GESTOR DE FALTAS ===");
            System.out.println("1. Ver alumnos");
            System.out.println("2. Agregar alumno");
            System.out.println("3. Ver asignaturas");
            System.out.println("4. Agregar asignatura");
            System.out.println("5. Registrar ausencia");
            System.out.println("6. Ver ausencias");
            System.out.println("7. Guardar todo");
            System.out.println("0. Salir");
            System.out.print("\nSelecciona opcion: ");
            opcion = Integer.parseInt(sc.nextLine());

            switch (opcion) {
                case 1 -> verAlumnos();
                case 2 -> agregarAlumno();
                case 3 -> verAsignaturas();
                case 4 -> agregarAsignatura();
                case 5 -> registrarAusencia();
                case 6 -> verAusencias();
                case 7 -> System.out.println("Guardado (simulado)");
                case 0 -> System.out.println("Saliendo...");
                default -> System.out.println("Opcion no valida.");
            }
        } while (opcion != 0);
    }

    public void cargarDatos() {
        cargarAlumnosDesdeXML();
        cargarAsignaturasDesdeXML();
        cargarAusenciasDesdeXML();
    }

    private String getTag(Element e, String... posibles) {
        for (String t : posibles) {
            NodeList n = e.getElementsByTagName(t);
            if (n != null && n.getLength() > 0 && n.item(0) != null)
                return n.item(0).getTextContent();
        }
        return "";
    }

    private void cargarAlumnosDesdeXML() {
        try {
            File f = new File(RUTA_ALUMNOS);
            DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document doc = db.parse(f);
            NodeList lista = doc.getElementsByTagName("alumno");

            for (int i = 0; i < lista.getLength(); i++) {
                Element e = (Element) lista.item(i);
                Alumno a = new Alumno(
                        getTag(e, "nombre_madre", "nombreMadre"),
                        getTag(e, "nombre_padre", "nombrePadre"),
                        getTag(e, "correo_padres", "correoPadres"),
                        getTag(e, "fecha_nacimiento", "fechaNacimiento"),
                        Integer.parseInt(getTag(e, "DNI", "dni")),
                        getTag(e, "curso"),
                        getTag(e, "nombre")
                );
                alumnos.add(a);
            }
            System.out.println("Cargados " + alumnos.size() + " alumnos.");
        } catch (Exception ex) {
            System.out.println("Error cargando alumnos: " + ex.getMessage());
        }
    }

    private void cargarAsignaturasDesdeXML() {
        try {
            File f = new File(RUTA_ASIGNATURAS);
            DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document doc = db.parse(f);
            NodeList lista = doc.getElementsByTagName("asignatura");

            for (int i = 0; i < lista.getLength(); i++) {
                Element e = (Element) lista.item(i);
                Asignaturas a = new Asignaturas(
                        getTag(e, "nombre"),
                        getTag(e, "cursos"),
                        Integer.parseInt(getTag(e, "horas")),
                        getTag(e, "profe_imparte", "profeImparte")
                );
                asignaturas.add(a);
            }
            System.out.println("Cargadas " + asignaturas.size() + " asignaturas.");
        } catch (Exception ex) {
            System.out.println("Error cargando asignaturas: " + ex.getMessage());
        }
    }

    private void cargarAusenciasDesdeXML() {
        try {
            File f = new File(RUTA_AUSENCIAS);
            DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document doc = db.parse(f);
            NodeList lista = doc.getElementsByTagName("ausencia");

            for (int i = 0; i < lista.getLength(); i++) {
                Element e = (Element) lista.item(i);
                Element t = (Element) e.getElementsByTagName("Timespand").item(0);

                Ausencias a = new Ausencias();
                if (t != null) {
                    a.setFecha(getTag(t, "fecha"));
                    a.setHoraInicio(getTag(t, "hora_inicio", "horaInicio"));
                    a.setHoraFin(getTag(t, "hora_fin", "horaFin"));
                }
                a.setAsignatura(getTag(e, "asignatura"));
                a.setNombre(getTag(e, "alumno"));
                a.setJustificado(Boolean.parseBoolean(getTag(e, "justificada")));
                a.setCurso(getTag(e, "curso"));

                ausencias.add(a);
            }
            System.out.println("Cargadas " + ausencias.size() + " ausencias.");
        } catch (Exception ex) {
            System.out.println("Error cargando ausencias: " + ex.getMessage());
        }
    }

    private void verAlumnos() {
        if (alumnos.isEmpty()) {
            System.out.println("No hay datos para mostrar.");
            return;
        }
        System.out.println("\n--- Lista de alumnos ---");
        for (Alumno a : alumnos) {
            System.out.println(a.getNombre() + " - " + a.getCurso() + " - DNI:" + a.getDni());
        }
    }

    private void verAsignaturas() {
        if (asignaturas.isEmpty()) {
            System.out.println("No hay asignaturas para mostrar.");
            return;
        }
        System.out.println("\n--- Lista de asignaturas ---");
        for (Asignaturas a : asignaturas) {
            System.out.println(a.getNombre() + " (" + a.getCursos() + ") - Horas: " + a.getHoras());
        }
    }

    private void verAusencias() {
        if (ausencias.isEmpty()) {
            System.out.println("No hay ausencias registradas.");
            return;
        }
        System.out.println("\n--- Lista de ausencias ---");
        for (Ausencias a : ausencias) {
            System.out.println(a.getNombre() + " - " + a.getAsignatura() + " - " + a.getFecha() +
                    " [" + a.getHoraInicio() + "-" + a.getHoraFin() + "] Justificada: " + a.isJustificado());
        }
    }

    private void agregarAlumno() {
        System.out.print("Nombre: ");
        String nombre = sc.nextLine();
        System.out.print("Curso: ");
        String curso = sc.nextLine();
        System.out.print("DNI: ");
        int dni = Integer.parseInt(sc.nextLine());
        System.out.print("Fecha nacimiento: ");
        String fecha = sc.nextLine();
        System.out.print("Correo padres: ");
        String correo = sc.nextLine();
        System.out.print("Nombre padre: ");
        String padre = sc.nextLine();
        System.out.print("Nombre madre: ");
        String madre = sc.nextLine();

        Alumno a = new Alumno(madre, padre, correo, fecha, dni, curso, nombre);
        alumnos.add(a);
        System.out.println("Alumno agregado.");
    }

    private void agregarAsignatura() {
        System.out.print("Nombre: ");
        String nombre = sc.nextLine();
        System.out.print("Cursos: ");
        String cursos = sc.nextLine();
        System.out.print("Horas: ");
        int horas = Integer.parseInt(sc.nextLine());
        System.out.print("Profesor: ");
        String profe = sc.nextLine();

        asignaturas.add(new Asignaturas(nombre, cursos, horas, profe));
        System.out.println("Asignatura agregada.");
    }

    private void registrarAusencia() {
        System.out.print("Alumno: ");
        String alumno = sc.nextLine();
        System.out.print("Asignatura: ");
        String asignatura = sc.nextLine();
        System.out.print("Curso: ");
        String curso = sc.nextLine();
        System.out.print("Fecha (YYYY-MM-DD): ");
        String fecha = sc.nextLine();
        System.out.print("Hora inicio: ");
        String inicio = sc.nextLine();
        System.out.print("Hora fin: ");
        String fin = sc.nextLine();
        System.out.print("Justificada (true/false): ");
        boolean justificada = Boolean.parseBoolean(sc.nextLine());

        Ausencias a = new Ausencias();
        a.setNombre(alumno);
        a.setAsignatura(asignatura);
        a.setCurso(curso);
        a.setFecha(fecha);
        a.setHoraInicio(inicio);
        a.setHoraFin(fin);
        a.setJustificado(justificada);

        ausencias.add(a);
        System.out.println("Ausencia registrada.");
    }
}
