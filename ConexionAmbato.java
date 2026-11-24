/**
 * APE 4 - Fase 2: Conexión Ambato (red social dinámica).
 * Universidad Técnica de Ambato - Estructura de Datos.
 *
 * Representación: multilista (lista enlazada de vértices; cada vértice tiene su
 * propia lista enlazada de aristas). Se usan nodos propios: sin HashMap, HashSet,
 * ArrayList ni LinkedList.
 *
 * AMISTAD: recíproca (2 aristas dirigidas). SEGUIR: dirigida (1 arista).
 * No se permiten lazos ni duplicados del mismo tipo.
 */
public class ConexionAmbato {

    enum TipoRelacion { AMISTAD, SEGUIR }

    static class Persona {
        String usuario, nombre;
        Persona(String usuario, String nombre) { this.usuario = usuario; this.nombre = nombre; }
    }

    static class Vertice {
        Persona persona;
        Arista aristas;
        Vertice sig;
    }

    static class Arista {
        Vertice destino;
        TipoRelacion tipo;
        Arista sig;
    }

    private Vertice cabeza;

    // ---------- auxiliares ----------

    /** Busca un vértice por su identificador normalizado (minúsculas, sin espacios laterales). */
    private Vertice buscar(String usuario) {
        if (usuario == null) return null;
        String clave = usuario.trim().toLowerCase();
        for (Vertice v = cabeza; v != null; v = v.sig)
            if (v.persona.usuario.equals(clave)) return v;
        return null;
    }

    /** Inserta una arista al comienzo de la lista de origen. */
    private void insertar(Vertice origen, Vertice destino, TipoRelacion tipo) {
        Arista a = new Arista();
        a.destino = destino;
        a.tipo = tipo;
        a.sig = origen.aristas;
        origen.aristas = a;
    }

    /** Indica si ya existe una arista de ese tipo de origen a destino. */
    private boolean existe(Vertice origen, Vertice destino, TipoRelacion tipo) {
        for (Arista a = origen.aristas; a != null; a = a.sig)
            if (a.destino == destino && a.tipo == tipo) return true;
        return false;
    }

    /** Quita de 'origen' la arista (destino, tipo). Devuelve true si la quitó. */
    private boolean quitar(Vertice origen, Vertice destino, TipoRelacion tipo) {
        Arista ant = null;
        for (Arista a = origen.aristas; a != null; ant = a, a = a.sig) {
            if (a.destino == destino && a.tipo == tipo) {
                if (ant == null) origen.aristas = a.sig; else ant.sig = a.sig;
                return true;
            }
        }
        return false;
    }

    /** Quita de 'origen' toda arista (de cualquier tipo) que apunte a 'destino'. */
    private void quitarHacia(Vertice origen, Vertice destino) {
        Arista ant = null, a = origen.aristas;
        while (a != null) {
            if (a.destino == destino) {
                if (ant == null) origen.aristas = a.sig; else ant.sig = a.sig;
                a = (ant == null) ? origen.aristas : ant.sig;
            } else {
                ant = a;
                a = a.sig;
            }
        }
    }

    // ---------- personas ----------

    /** Rechaza usuario nulo/vacío, repetido, con espacios o con comas. */
    public boolean registrarPersona(String usuario, String nombre) {
        if (usuario == null || nombre == null) return false;
        String clave = usuario.trim().toLowerCase();
        if (clave.isEmpty() || nombre.trim().isEmpty()) return false;
        if (clave.contains(" ") || clave.contains(",")) return false;
        if (buscar(clave) != null) return false;
        Vertice v = new Vertice();
        v.persona = new Persona(clave, nombre.trim());
        v.sig = cabeza;          // inserción al inicio
        cabeza = v;
        return true;
    }

    /** Elimina el vértice y todas las aristas entrantes (recorre toda la red). */
    public boolean eliminarPersona(String usuario) {
        Vertice objetivo = buscar(usuario);
        if (objetivo == null) return false;
        for (Vertice w = cabeza; w != null; w = w.sig)
            quitarHacia(w, objetivo);
        Vertice anterior = null, actual = cabeza;
        while (actual != objetivo) {
            anterior = actual;
            actual = actual.sig;
        }
        if (anterior == null) cabeza = objetivo.sig;
        else anterior.sig = objetivo.sig;
        return true;
    }

    // ---------- relaciones ----------

    /** La amistad es recíproca: se crean dos aristas. */
    public boolean agregarAmistad(String a, String b) {
        Vertice va = buscar(a), vb = buscar(b);
        if (va == null || vb == null || va == vb ||
            existe(va, vb, TipoRelacion.AMISTAD)) return false;
        insertar(va, vb, TipoRelacion.AMISTAD);
        insertar(vb, va, TipoRelacion.AMISTAD);
        return true;
    }

    /** Seguir es dirigido: se crea una sola arista (a sigue a b). */
    public boolean seguir(String a, String b) {
        Vertice va = buscar(a), vb = buscar(b);
        if (va == null || vb == null || va == vb ||
            existe(va, vb, TipoRelacion.SEGUIR)) return false;
        insertar(va, vb, TipoRelacion.SEGUIR);
        return true;
    }

    public boolean eliminarAmistad(String a, String b) {
        Vertice va = buscar(a), vb = buscar(b);
        if (va == null || vb == null || !existe(va, vb, TipoRelacion.AMISTAD)) return false;
        quitar(va, vb, TipoRelacion.AMISTAD);
        quitar(vb, va, TipoRelacion.AMISTAD);
        return true;
    }

    public boolean dejarDeSeguir(String a, String b) {
        Vertice va = buscar(a), vb = buscar(b);
        if (va == null || vb == null) return false;
        return quitar(va, vb, TipoRelacion.SEGUIR);
    }

    // ---------- consultas ----------

    private String unir(StringBuilder sb) { return sb.length() == 0 ? "(ninguno)" : sb.toString(); }

    /** Lista de nombres de amigos, o null si el usuario no existe. */
    public String listarAmigos(String usuario) {
        Vertice v = buscar(usuario);
        if (v == null) return null;
        StringBuilder sb = new StringBuilder();
        for (Arista a = v.aristas; a != null; a = a.sig)
            if (a.tipo == TipoRelacion.AMISTAD) {
                if (sb.length() > 0) sb.append(", ");
                sb.append(a.destino.persona.nombre);
            }
        return unir(sb);
    }

    /** Personas a quienes sigue el usuario (aristas salientes SEGUIR). */
    public String listarSeguidos(String usuario) {
        Vertice v = buscar(usuario);
        if (v == null) return null;
        StringBuilder sb = new StringBuilder();
        for (Arista a = v.aristas; a != null; a = a.sig)
            if (a.tipo == TipoRelacion.SEGUIR) {
                if (sb.length() > 0) sb.append(", ");
                sb.append(a.destino.persona.nombre);
            }
        return unir(sb);
    }

    /** Personas que siguen al usuario: exige revisar las aristas de toda la red. */
    public String listarSeguidores(String usuario) {
        Vertice v = buscar(usuario);
        if (v == null) return null;
        StringBuilder sb = new StringBuilder();
        for (Vertice w = cabeza; w != null; w = w.sig)
            for (Arista a = w.aristas; a != null; a = a.sig)
                if (a.tipo == TipoRelacion.SEGUIR && a.destino == v) {
                    if (sb.length() > 0) sb.append(", ");
                    sb.append(w.persona.nombre);
                }
        return unir(sb);
    }

    /** Número de amigos, o -1 si el usuario no existe. */
    public int contarAmigos(String usuario) {
        Vertice v = buscar(usuario);
        if (v == null) return -1;
        int n = 0;
        for (Arista a = v.aristas; a != null; a = a.sig)
            if (a.tipo == TipoRelacion.AMISTAD) n++;
        return n;
    }

    /** Texto con la red completa (lista de adyacencia). */
    public String mostrarRed() {
        StringBuilder sb = new StringBuilder();
        for (Vertice v = cabeza; v != null; v = v.sig) {
            sb.append(v.persona.usuario.toUpperCase()).append(" (").append(v.persona.nombre).append(") -> ");
            if (v.aristas == null) sb.append("sin relaciones");
            for (Arista a = v.aristas; a != null; a = a.sig) {
                sb.append("[").append(a.tipo == TipoRelacion.AMISTAD ? "AMISTAD" : "SEGUIR")
                  .append(":").append(a.destino.persona.nombre).append("] ");
            }
            sb.append("\n");
        }
        return sb.toString();
    }

    // ---------- demostración (secuencia de comprobación de la Tabla 3) ----------

    public static void main(String[] args) {
        ConexionAmbato red = new ConexionAmbato();

        System.out.println("=== Paso 1: registrar A01-A06 y las cinco amistades ===");
        String[][] p = {{"A01","Sofía"},{"A02","Mateo"},{"A03","Valeria"},{"A04","Andrés"},{"A05","Camila"},{"A06","Diego"}};
        for (String[] x : p) System.out.println("registrarPersona(" + x[0] + ") -> " + red.registrarPersona(x[0], x[1]));
        System.out.println("agregarAmistad(A01,A02) -> " + red.agregarAmistad("A01","A02"));
        System.out.println("agregarAmistad(A02,A03) -> " + red.agregarAmistad("A02","A03"));
        System.out.println("agregarAmistad(A03,A04) -> " + red.agregarAmistad("A03","A04"));
        System.out.println("agregarAmistad(A04,A01) -> " + red.agregarAmistad("A04","A01"));
        System.out.println("agregarAmistad(A04,A05) -> " + red.agregarAmistad("A04","A05"));
        System.out.print(red.mostrarRed());
        for (String[] x : p) System.out.println("contarAmigos(" + x[0] + ") = " + red.contarAmigos(x[0]));

        System.out.println("\n=== Pasos 2 y 3: seguimientos ===");
        System.out.println("seguir(A02,A01) -> " + red.seguir("A02","A01"));
        System.out.println("seguir(A06,A01) -> " + red.seguir("A06","A01"));
        System.out.println("listarSeguidores(A01) = " + red.listarSeguidores("A01"));
        System.out.println("listarSeguidos(A02)   = " + red.listarSeguidos("A02"));
        System.out.println("listarSeguidos(A06)   = " + red.listarSeguidos("A06"));

        System.out.println("\n=== Paso 4: listarAmigos(A04) ===");
        System.out.println(red.listarAmigos("A04"));

        System.out.println("\n=== Paso 5: eliminarAmistad(A04,A05) ===");
        System.out.println("eliminarAmistad(A04,A05) -> " + red.eliminarAmistad("A04","A05"));
        System.out.println("listarAmigos(A04) = " + red.listarAmigos("A04"));
        System.out.println("listarAmigos(A05) = " + red.listarAmigos("A05"));

        System.out.println("\n=== Paso 6: eliminarPersona(A01) ===");
        System.out.println("eliminarPersona(A01) -> " + red.eliminarPersona("A01"));
        System.out.print(red.mostrarRed());
        System.out.println("listarAmigos(A01) = " + red.listarAmigos("A01"));
        System.out.println("contarAmigos(A01) = " + red.contarAmigos("A01"));

        System.out.println("\n=== Pasos 7 y 8: validaciones ===");
        System.out.println("registrarPersona(A02, Otro) -> " + red.registrarPersona("A02", "Otro"));
        System.out.println("agregarAmistad(A03,A03)     -> " + red.agregarAmistad("A03","A03"));
        System.out.println("dejarDeSeguir(A06,A01)      -> " + red.dejarDeSeguir("A06","A01"));
    }
}
