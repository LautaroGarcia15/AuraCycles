package ar.edu.uade.repuestos.negocio;

import ar.edu.uade.repuestos.datos.ProductoRepository;
import ar.edu.uade.repuestos.datos.ResenaRepository;
import ar.edu.uade.repuestos.datos.UsuarioRepository;
import ar.edu.uade.repuestos.datos.VentaRepository;
import ar.edu.uade.repuestos.modelo.Producto;
import ar.edu.uade.repuestos.modelo.Resena;
import ar.edu.uade.repuestos.modelo.Usuario;
import ar.edu.uade.repuestos.util.ReglaNegocioException;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import java.util.List;

@Stateless
public class ServicioDeResenas {
    @Inject
    private ResenaRepository resenaRepository;

    @Inject
    private VentaRepository ventaRepository;

    @Inject
    private UsuarioRepository usuarioRepository;

    @Inject
    private ProductoRepository productoRepository;

    public Resena escribir(Long usuarioId, Long productoId, int puntaje,
                           String titulo, String texto) {
        if (puntaje < 1 || puntaje > 5) {
            throw new ReglaNegocioException("El puntaje va de 1 a 5 estrellas.");
        }
        if (texto == null || texto.isBlank()) {
            throw new ReglaNegocioException("Escribi el texto de la resena.");
        }
        if (!ventaRepository.usuarioComproProducto(usuarioId, productoId)) {
            throw new ReglaNegocioException("Solo podes resenar repuestos que compraste.");
        }

        Usuario usuario = usuarioRepository.porId(usuarioId)
                .orElseThrow(() -> new ReglaNegocioException("El usuario no existe."));
        Producto producto = productoRepository.porId(productoId)
                .orElseThrow(() -> new ReglaNegocioException("El repuesto no existe."));

        return resenaRepository.guardar(
            new Resena(usuario, producto, puntaje, titulo, texto));
    }

    public List<Resena> publicadasDe(Long productoId) {
        return resenaRepository.aprobadasPorProducto(productoId);
    }

    public boolean puedeResenar(Long usuarioId, Long productoId) {
        return ventaRepository.usuarioComproProducto(usuarioId, productoId);
    }
}
