package ar.edu.uade.repuestos.util;

// Utilidad de consola para generar los hashes de schema.sql. No se despliega.
public final class GeneradorHash {
    private GeneradorHash() {
    }

    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Uso: GeneradorHash <password>");
            return;
        }
        System.out.println(PasswordHash.hashear(args[0]));
    }
}
