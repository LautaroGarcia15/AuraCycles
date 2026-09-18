package ar.edu.uade.repuestos.negocio;

import ar.edu.uade.repuestos.datos.ProductoRepository;
import ar.edu.uade.repuestos.datos.ResenaRepository;
import ar.edu.uade.repuestos.modelo.Producto;
import ar.edu.uade.repuestos.util.ReglaNegocioException;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import java.util.List;

@Stateless
public class ServicioDeProductos {
    @Inject
    private ProductoRepository productoRepository;

    @Inject
    private ResenaRepository resenaRepository;

    public List<Producto> listar() {
        return productoRepository.todos();
    }

    public List<Producto> buscar(String texto) {
        return productoRepository.buscar(texto);
    }

    public Producto porId(Long id) {
        return productoRepository.porId(id)
                .orElseThrow(() -> new ReglaNegocioException("El repuesto no existe."));
    }

    public double promedioPuntaje(Long productoId) {
        return resenaRepository.promedioPuntaje(productoId);
    }

    public int cantidadResenas(Long productoId) {
        return resenaRepository.aprobadasPorProducto(productoId).size();
    }
}
