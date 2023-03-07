
package com.ejercicioSpring.noticia.repositorios;

import com.ejercicioSpring.noticia.entidades.Noticia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface NoticiaRepositorio extends JpaRepository<Noticia, Long> {

     @Query("SELECT count(*) FROM Noticia n WHERE usuario_id = :id")
    public int cantidadNoticia(@Param("id") Long id);
    
    
}
