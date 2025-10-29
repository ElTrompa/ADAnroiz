import org.w3c.dom.*;
import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.util.ArrayList;
import java.util.Scanner;

public class Gestor {
    Scanner teclado = new Scanner(System.in);

    private ArrayList<Alumno> alumnos = new ArrayList<>();
    private ArrayList<Asignaturas> asignaturas = new ArrayList<>();
    private ArrayList<Ausencias> ausencias = new ArrayList<>();

    private final String RUTA_ALUMNOS = "alumnos.xml";
    private final String RUTA_ASIGNATURAS = "asignaturas.xml";
    private final String RUTA_AUSENCIAS = "ausencias.xml";

    public void menu() {
        int opcion = 0;

        do {
            System.out.println("\n=== GESTOR DE FALTAS ===");
            System.out.println("1. Ver alumnos");
            System.out.println("2. Agregar alumno");
            System.out.println("3. Ver asignaturas");
            System.out.println("4. Agregar asignatura");
            System.out.println("5. Registrar ausencia");
            System.out.println("6. Ver ausencias");
            System.out.println("0. Salir");
            System.out.print("Selecciona opcion: ");
            opcion = teclado.nextInt();

            switch (opcion) {
                case 1 -> listarAlumnos();
                case 2 -> agregarAlumno();
                case 3 -> listarAsignaturas();
                case 4 -> agregarAsignatura();
                case 5 -> registrarAusencia();
                case 6 -> listarAusencias();
                case 0 -> System.out.println("Saliendo del programa");
                default -> System.out.println("Opcion no valida");
            }
        }while (opcion != 0);
    }

    private void cargarAlumnosDesdeXML() {
        try {
            File file = new File(RUTA_ALUMNOS);
            if (!file.exists()) {
                System.out.println("No existe alumnos.xml, se creara al guardar.");
                return;
            }

            Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(file);
            doc.getDocumentElement().normalize();
            NodeList lista = doc.getElementsByTagName("alumno");

            for (int i = 0; i < lista.getLength(); i++) {
                Element e = (Element) lista.item(i);
                Alumno a = new Alumno(
                        e.getElementsByTagName("nombre").item(0).getTextContent(),
                        e.getElementsByTagName("curso").item(0).getTextContent(),
                        Integer.parseInt(e.getElementsByTagName("dni").item(0).getTextContent()),
                        e.getElementsByTagName("fechaNacimiento").item(0).getTextContent(),
                        e.getElementsByTagName("correoPadres").item(0).getTextContent(),
                        e.getElementsByTagName("nombrePadre").item(0).getTextContent(),
                        e.getElementsByTagName("nombreMadre").item(0).getTextContent()
                );
                alumnos.add(a);
            }

            System.out.println("Alumnos cargados correctamente.");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void guardarAlumnosEnXML() {
        try {
            Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
            Element root = doc.createElement("alumnos");
            doc.appendChild(root);

            for (Alumno a : alumnos) {
                Element eAlumno = doc.createElement("alumno");

                Element eNombre = doc.createElement("nombre");
                eNombre.appendChild(doc.createTextNode(a.getNombre()));
                eAlumno.appendChild(eNombre);

                Element eCurso = doc.createElement("curso");
                eCurso.appendChild(doc.createTextNode(a.getCurso()));
                eAlumno.appendChild(eCurso);

                Element eDni = doc.createElement("dni");
                eDni.appendChild(doc.createTextNode(String.valueOf(a.getDni())));
                eAlumno.appendChild(eDni);

                Element eFecha = doc.createElement("fechaNacimiento");
                eFecha.appendChild(doc.createTextNode(a.getFechaNacimiento()));
                eAlumno.appendChild(eFecha);

                Element eCorreo = doc.createElement("correoPadres");
                eCorreo.appendChild(doc.createTextNode(a.getCorreoPadres()));
                eAlumno.appendChild(eCorreo);

                Element ePadre = doc.createElement("nombrePadre");
                ePadre.appendChild(doc.createTextNode(a.getNombrePadre()));
                eAlumno.appendChild(ePadre);

                Element eMadre = doc.createElement("nombreMadre");
                eMadre.appendChild(doc.createTextNode(a.getNombreMadre()));
                eAlumno.appendChild(eMadre);

                root.appendChild(eAlumno);
            }

            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.transform(new DOMSource(doc), new StreamResult(new File(RUTA_ALUMNOS)));

            System.out.println("Alumnos guardados correctamente.");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void cargarAsignaturasDesdeXML() {
        try {
            File file = new File(RUTA_ASIGNATURAS);
            if (!file.exists()) {
                System.out.println("No existe asignaturas.xml, se creara al guardar.");
                return;
            }

            Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(file);
            doc.getDocumentElement().normalize();
            NodeList lista = doc.getElementsByTagName("asignatura");

            for (int i = 0; i < lista.getLength(); i++) {
                Element e = (Element) lista.item(i);
                Asignaturas as = new Asignaturas(
                        e.getElementsByTagName("nombre").item(0).getTextContent(),
                        e.getElementsByTagName("cursos").item(0).getTextContent(),
                        Integer.parseInt(e.getElementsByTagName("horas").item(0).getTextContent()),
                        e.getElementsByTagName("profeImparte").item(0).getTextContent()
                );
                asignaturas.add(as);
            }

            System.out.println("Asignaturas cargadas correctamente.");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void listarAusencias() {
        if (alumnos.isEmpty()) {
            System.out.println("No hay alumnos registrados.");
        } else {
            System.out.println("\n--- Lista de alumnos ---");
            alumnos.forEach(System.out::println);
        }
    }

    private void registrarAusencia() {
        System.out.print("Nombre alumno: ");
        String nombre = teclado.next();
        System.out.print("Asignatura: ");
        String asig = teclado.next();
        System.out.print("fecha 2025-10-25 08:30-09:30): ");
        String fecha = teclado.next();
        System.out.print("Hora inicio 08:30: ");
        String horaInicio = teclado.next();
        System.out.print("Hora final 09:30:");
        String horaFinal = teclado.next();
        System.out.print("Curso: ");
        String curso = teclado.next();
        System.out.print("Justificada (true/false): ");
        boolean justificada = teclado.nextBoolean();

        Ausencias au = new Ausencias(fecha, horaInicio, horaFinal, asig, nombre, justificada, curso);
        ausencias.add(au);
        System.out.println("Registrado");
    }

    private void agregarAsignatura() {
        System.out.println("Nombre: ");
        String nombre = teclado.next();
        System.out.print("Curso: ");
        String curso = teclado.next();
        System.out.print("DNI: ");
        int dni = teclado.nextInt();
        System.out.print("Fecha nacimiento (yyyy-mm-dd): ");
        String fecha = teclado.next();
        System.out.print("Correo padres: ");
        String correo = teclado.next();
        System.out.print("Nombre padre: ");
        String padre = teclado.next();
        System.out.print("Nombre madre: ");
        String madre = teclado.next();


        Alumno a = new Alumno(madre, padre, correo, fecha, dni, curso, nombre);
        alumnos.add(a);
        System.out.println("Agregado");
    }

    private void listarAsignaturas() {
        if (asignaturas.isEmpty()) {
            System.out.println("No hay asignaturas registradas.");
        } else {
            System.out.println("\n--- Lista de asignaturas ---");
            asignaturas.forEach(System.out::println);
        }
    }

    private void agregarAlumno() {
        System.out.print("Nombre: ");
        String nombre = teclado.next();
        System.out.print("Curso: ");
        String curso = teclado.next();
        System.out.print("Horas semanales: ");
        int horas = teclado.nextInt();
        System.out.print("Profesor que imparte: ");
        String profesor = teclado.next();

        Asignaturas as = new Asignaturas(nombre, curso, horas, profesor);
        asignaturas.add(as);
        System.out.println("Agregado");
    }

    private void listarAlumnos() {
        if (ausencias.isEmpty()) {
        System.out.println("No hay ausencias registradas.");
    } else {
        System.out.println("\n--- Lista de ausencias ---");
        ausencias.forEach(System.out::println);
    }
    }

    private void guardarAsignaturasEnXML() {
        try {
            Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
            Element root = doc.createElement("asignaturas");
            doc.appendChild(root);

            for (Asignaturas a : asignaturas) {
                Element eAsig = doc.createElement("asignatura");

                Element eNombre = doc.createElement("nombre");
                eNombre.appendChild(doc.createTextNode(a.getNombre()));
                eAsig.appendChild(eNombre);

                Element eCursos = doc.createElement("cursos");
                eCursos.appendChild(doc.createTextNode(a.getCursos()));
                eAsig.appendChild(eCursos);

                Element eHoras = doc.createElement("horas");
                eHoras.appendChild(doc.createTextNode(String.valueOf(a.getHoras())));
                eAsig.appendChild(eHoras);

                Element eProfe = doc.createElement("profeImparte");
                eProfe.appendChild(doc.createTextNode(a.getProfeImparte()));
                eAsig.appendChild(eProfe);

                root.appendChild(eAsig);
            }

            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.transform(new DOMSource(doc), new StreamResult(new File(RUTA_ASIGNATURAS)));

            System.out.println("Asignaturas guardadas correctamente.");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void cargarAusenciasDesdeXML() {
        try {
            File file = new File(RUTA_AUSENCIAS);
            if (!file.exists()) {
                System.out.println("No existe ausencias.xml, se creara al guardar.");
                return;
            }

            Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(file);
            doc.getDocumentElement().normalize();
            NodeList lista = doc.getElementsByTagName("ausencia");

            for (int i = 0; i < lista.getLength(); i++) {
                Element e = (Element) lista.item(i);
                Ausencias a = new Ausencias(
                        e.getElementsByTagName("Timespand").item(0).getTextContent(),
                        e.getElementsByTagName("asignatura").item(0).getTextContent(),
                        e.getElementsByTagName("alumno").item(0).getTextContent(),
                        Boolean.parseBoolean(e.getElementsByTagName("justificada").item(0).getTextContent()),
                        e.getElementsByTagName("curso").item(0).getTextContent()
                );
                ausencias.add(a);
            }

            System.out.println("Ausencias cargadas correctamente.");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void guardarAusenciasEnXML() {
        try {
            Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
            Element root = doc.createElement("ausencias");
            doc.appendChild(root);

            for (Ausencias a : ausencias) {
                Element eAus = doc.createElement("ausencia");

                Element eT = doc.createElement("Timespand");
                eT.appendChild(doc.createTextNode(a.getFecha()));
                eAus.appendChild(eT);

                Element eA = doc.createElement("asignatura");
                eA.appendChild(doc.createTextNode(a.getAsignatura()));
                eAus.appendChild(eA);

                Element eAl = doc.createElement("alumno");
                eAl.appendChild(doc.createTextNode(a.getNombre()));
                eAus.appendChild(eAl);

                Element eJ = doc.createElement("justificada");
                eJ.appendChild(doc.createTextNode(String.valueOf(a.isJustificado())));
                eAus.appendChild(eJ);

                Element eC = doc.createElement("curso");
                eC.appendChild(doc.createTextNode(a.getCurso()));
                eAus.appendChild(eC);

                root.appendChild(eAus);
            }

            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.transform(new DOMSource(doc), new StreamResult(new File(RUTA_AUSENCIAS)));

            System.out.println("Ausencias guardadas correctamente.");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
