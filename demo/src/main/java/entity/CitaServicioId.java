package entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import org.hibernate.Hibernate;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class CitaServicioId implements Serializable {
    private static final long serialVersionUID = 5515160622903836230L;
    @Column(name = "citaId", nullable = false)
    private Integer citaId;

    @Column(name = "servicioId", nullable = false)
    private Integer servicioId;

    public CitaServicioId() { // Constructor sin argumentos PÚBLICO
    }

    public CitaServicioId(Integer citaId, Integer servicioId) {
        this.citaId = citaId;
        this.servicioId = servicioId;
    }

    public Integer getCitaId() {
        return citaId;
    }

    public void setCitaId(Integer citaId) {
        this.citaId = citaId;
    }

    public Integer getServicioId() {
        return servicioId;
    }

    public void setServicioId(Integer servicioId) {
        this.servicioId = servicioId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        CitaServicioId entity = (CitaServicioId) o;
        return Objects.equals(this.citaId, entity.citaId) &&
                Objects.equals(this.servicioId, entity.servicioId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(citaId, servicioId);
    }
}