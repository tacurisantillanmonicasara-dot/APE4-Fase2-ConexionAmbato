/** Pruebas automatizadas (sin dependencias externas) para ConexionAmbato. */
public class PruebasConexionAmbato {
    static int ok = 0, fallos = 0;

    static void verificar(String nombre, Object esperado, Object obtenido) {
        boolean bien = (esperado == null) ? obtenido == null : esperado.equals(obtenido);
        if (bien) ok++; else fallos++;
        System.out.println((bien ? "[OK]    " : "[FALLO] ") + nombre
                + (bien ? "" : "  esperado=" + esperado + " obtenido=" + obtenido));
    }

    static ConexionAmbato redInicial() {
        ConexionAmbato r = new ConexionAmbato();
        String[][] p = {{"A01","Sofía"},{"A02","Mateo"},{"A03","Valeria"},{"A04","Andrés"},{"A05","Camila"},{"A06","Diego"}};
        for (String[] x : p) r.registrarPersona(x[0], x[1]);
        r.agregarAmistad("A01","A02"); r.agregarAmistad("A02","A03");
        r.agregarAmistad("A03","A04"); r.agregarAmistad("A04","A01"); r.agregarAmistad("A04","A05");
        return r;
    }

    public static void main(String[] args) {
        ConexionAmbato r = redInicial();
        verificar("grados iniciales A01..A06", "2,2,2,3,1,0",
                r.contarAmigos("A01")+","+r.contarAmigos("A02")+","+r.contarAmigos("A03")+","+
                r.contarAmigos("A04")+","+r.contarAmigos("A05")+","+r.contarAmigos("A06"));
        verificar("usuario inexistente -> contarAmigos = -1", -1, r.contarAmigos("zz"));
        verificar("usuario inexistente -> listarAmigos null", null, r.listarAmigos("zz"));
        verificar("normaliza a01 == A01", 2, r.contarAmigos("  a01 "));

        verificar("registro duplicado", false, r.registrarPersona("A02", "Otro"));
        verificar("registro duplicado (minúsculas)", false, r.registrarPersona("a02", "Otro"));
        verificar("registro vacío", false, r.registrarPersona("  ", "X"));
        verificar("registro con espacios", false, r.registrarPersona("a 07", "X"));
        verificar("registro con comas", false, r.registrarPersona("a,07", "X"));
        verificar("registro nulo", false, r.registrarPersona(null, "X"));
        verificar("registro válido", true, r.registrarPersona("A07", "Lucía"));

        verificar("lazo de amistad", false, r.agregarAmistad("A03","A03"));
        verificar("amistad duplicada", false, r.agregarAmistad("A01","A02"));
        verificar("amistad duplicada invertida", false, r.agregarAmistad("A02","A01"));
        verificar("amistad con inexistente", false, r.agregarAmistad("A01","zz"));
        verificar("lazo de seguir", false, r.seguir("A01","A01"));

        verificar("seguir A02->A01", true, r.seguir("A02","A01"));
        verificar("seguir duplicado", false, r.seguir("A02","A01"));
        verificar("seguir es dirigido: A01 no sigue a A02", "(ninguno)", r.listarSeguidos("A01"));
        verificar("seguir A06->A01 (sin amigos)", true, r.seguir("A06","A01"));
        verificar("seguidores de A01", "Diego, Mateo", r.listarSeguidores("A01"));
        verificar("seguir no altera amigos de A01", 2, r.contarAmigos("A01"));
        verificar("se puede seguir a un amigo (tipo distinto)", true, r.seguir("A01","A02"));

        verificar("dejarDeSeguir A06->A01", true, r.dejarDeSeguir("A06","A01"));
        verificar("dejarDeSeguir inexistente", false, r.dejarDeSeguir("A06","A01"));
        verificar("dejarDeSeguir no quita amistad", 2, r.contarAmigos("A01"));

        verificar("eliminarAmistad A04-A05", true, r.eliminarAmistad("A04","A05"));
        verificar("A05 sin amigos", 0, r.contarAmigos("A05"));
        verificar("A04 queda con 2", 2, r.contarAmigos("A04"));
        verificar("eliminarAmistad repetida", false, r.eliminarAmistad("A04","A05"));

        verificar("eliminarPersona A01", true, r.eliminarPersona("A01"));
        verificar("A01 ya no existe", -1, r.contarAmigos("A01"));
        verificar("A02 pierde amistad con A01", 1, r.contarAmigos("A02"));
        verificar("A02 ya no sigue a A01 (referencia limpiada)", "(ninguno)", r.listarSeguidos("A02"));
        verificar("A04 pierde amistad con A01", 1, r.contarAmigos("A04"));
        verificar("eliminarPersona inexistente", false, r.eliminarPersona("A01"));
        verificar("eliminar cabeza y cola", true, r.eliminarPersona("A07") && r.eliminarPersona("A06"));
        verificar("se puede re-registrar A01", true, r.registrarPersona("A01", "Nuevo"));

        System.out.println("\nResultado: " + ok + " correctas, " + fallos + " fallidas");
        if (fallos > 0) System.exit(1);
    }
}
