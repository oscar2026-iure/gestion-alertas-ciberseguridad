package ec.monitoreo.alertas;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "alerta")
public class Alerta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "fecha", nullable = false)
    private LocalDateTime fecha;

    @Column(name = "descripcion", nullable = false, length = 1000)
    private String descripcion;

    @Column(name = "severidad", nullable = false, length = 20)
    private String severidad;

    @Column(name = "responsable", nullable = false, length = 150)
    private String responsable;

    @Column(name = "estado", nullable = false, length = 30)
    private String estado;

    @Column(name = "activo", nullable = false)
    private boolean activo;

    @Column(name = "motivo_anulacion", length = 500)
    private String motivoAnulacion;

    // Constructor requerido por JPA.
    public Alerta() {
    }

    // Constructor para registrar una nueva alerta.
    public Alerta(String descripcion, String severidad,
                  String responsable) {
        this.fecha = LocalDateTime.now();
        this.descripcion = descripcion;
        this.severidad = severidad;
        this.responsable = responsable;
        this.estado = "REGISTRADA";
        this.activo = true;
    }

    public Long getId() {
        return id;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getSeveridad() {
        return severidad;
    }

    public void setSeveridad(String severidad) {
        this.severidad = severidad;
    }

    public String getResponsable() {
        return responsable;
    }

    public void setResponsable(String responsable) {
        this.responsable = responsable;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    public String getMotivoAnulacion() {
        return motivoAnulacion;
    }

    public void setMotivoAnulacion(String motivoAnulacion) {
        this.motivoAnulacion = motivoAnulacion;
    }
}