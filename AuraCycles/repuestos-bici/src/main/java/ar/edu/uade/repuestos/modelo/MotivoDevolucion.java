package ar.edu.uade.repuestos.modelo;

public enum MotivoDevolucion {
    MEDIDA_INCORRECTA("Medida incorrecta"),
    NO_COMPATIBLE("No es compatible con mi bicicleta"),
    DEFECTUOSO("Llego defectuoso"),
    DISTINTO_AL_PUBLICADO("Distinto al publicado"),
    ARREPENTIMIENTO("Me arrepenti de la compra");

    private final String etiqueta;

    MotivoDevolucion(String etiqueta) {
        this.etiqueta = etiqueta;
    }

    public String getEtiqueta() {
        return etiqueta;
    }
}
