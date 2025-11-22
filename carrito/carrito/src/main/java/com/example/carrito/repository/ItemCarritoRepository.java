package com.example.carrito.repository;

import com.example.carrito.model.ItemCarrito;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ItemCarritoRepository extends JpaRepository<ItemCarrito, Long> {
    
    List<ItemCarrito> findByUsuarioId(Long usuarioId);
    
    Optional<ItemCarrito> findByUsuarioIdAndProductoId(Long usuarioId, Long productoId);
    
    void deleteByUsuarioId(Long usuarioId);
    
    @Query("SELECT SUM(i.productoPrecio * i.cantidad) FROM ItemCarrito i WHERE i.usuarioId = :usuarioId")
    Double calcularTotal(Long usuarioId);
}