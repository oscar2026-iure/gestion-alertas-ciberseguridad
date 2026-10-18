package ec.monitoreo.alertas;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class Gestion_alertas extends JFrame {

    private final EntityManagerFactory fabrica;

    private final JTextField txtId = new JTextField();

    private final JTextArea txtDescripcion =
            new JTextArea(3, 30);

    private final JComboBox<String> cmbResponsable =
            new JComboBox<>(new String[]{
                "Oscar Leg",
                "Hernán Val",
                "Luis Lima",
                "Técnico Vacante"
            });

    private final JComboBox<String> cmbSeveridad =
            new JComboBox<>(new String[]{
                "BAJA",
                "MEDIA",
                "ALTA",
                "CRITICA"
            });

    private final JComboBox<String> cmbEstado =
            new JComboBox<>(new String[]{
                "REGISTRADA",
                "EN_ANALISIS",
                "EN_TRATAMIENTO",
                "CERRADA"
            });

    private final JCheckBox chkAnuladas =
            new JCheckBox("Mostrar también registros anulados");

    private final JLabel lblInformacion =
            new JLabel("Seleccione una alerta o registre una nueva.");

    private final DefaultTableModel modelo =
            new DefaultTableModel(
                    new String[]{
                        "ID", "Fecha", "Descripción", "Severidad",
                        "Técnico responsable", "Estado",
                        "Activa", "Motivo de anulación"
                    }, 0) {

        @Override
        public boolean isCellEditable(int fila, int columna) {
            return false;
        }
    };

    private final JTable tabla = new JTable(modelo);

    private List<Alerta> registros = new ArrayList<>();

    public Gestion_alertas(EntityManagerFactory fabrica) {

        super("Gestión y seguimiento de alertas de ciberseguridad");

        this.fabrica = fabrica;

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1200, 700);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        txtId.setEditable(false);
        txtDescripcion.setLineWrap(true);
        txtDescripcion.setWrapStyleWord(true);

        cmbResponsable.setEditable(false);

        JPanel formulario =
                new JPanel(new GridLayout(0, 2, 8, 8));

        formulario.setBorder(
                BorderFactory.createTitledBorder("Datos de la alerta")
        );

        formulario.add(new JLabel("ID seleccionado:"));
        formulario.add(txtId);

        formulario.add(new JLabel("Descripción:"));
        formulario.add(new JScrollPane(txtDescripcion));

        formulario.add(new JLabel("Severidad:"));
        formulario.add(cmbSeveridad);

        formulario.add(new JLabel("Técnico responsable:"));
        formulario.add(cmbResponsable);

        formulario.add(new JLabel("Estado:"));
        formulario.add(cmbEstado);

        JPanel botones =
                new JPanel(new FlowLayout(FlowLayout.LEFT));

        agregarBoton(botones, "Nuevo / Limpiar", this::limpiar);
        agregarBoton(botones, "Registrar", this::registrar);
        agregarBoton(
                botones, "Consultar / Refrescar", this::consultar
        );
        agregarBoton(botones, "Actualizar", this::actualizar);
        agregarBoton(botones, "Anular", this::anular);

        botones.add(chkAnuladas);

        chkAnuladas.setToolTipText(
                "Muestra también los registros anulados. "
                + "No modifica los datos."
        );

        chkAnuladas.addActionListener(e -> consultar());

        JPanel superior = new JPanel(new BorderLayout());
        superior.add(formulario, BorderLayout.CENTER);
        superior.add(botones, BorderLayout.SOUTH);

        add(superior, BorderLayout.NORTH);
        add(new JScrollPane(tabla), BorderLayout.CENTER);
        add(lblInformacion, BorderLayout.SOUTH);

        tabla.setSelectionMode(
                ListSelectionModel.SINGLE_SELECTION
        );
        tabla.setRowHeight(25);

        tabla.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                cargarSeleccion();
            }
        });

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                if (fabrica.isOpen()) {
                    fabrica.close();
                }
            }
        });

        limpiar();
        consultar();
    }

    private void agregarBoton(
            JPanel panel, String texto, Runnable accion) {

        JButton boton = new JButton(texto);
        boton.addActionListener(e -> accion.run());
        panel.add(boton);
    }

    private void limpiar() {

        tabla.clearSelection();
        txtId.setText("");
        txtDescripcion.setText("");

        cmbResponsable.setSelectedItem("Técnico Vacante");
        cmbSeveridad.setSelectedItem("MEDIA");
        cmbEstado.setSelectedItem("REGISTRADA");

        lblInformacion.setText(
                "Formulario listo para registrar una nueva alerta."
        );
    }

    private String responsableSeleccionado() {

        Object seleccionado = cmbResponsable.getSelectedItem();

        return seleccionado == null
                ? ""
                : seleccionado.toString();
    }

    private boolean validarFormulario() {

        String descripcion = txtDescripcion.getText().trim();
        String responsable = responsableSeleccionado();

        if (descripcion.isBlank()) {
            mensaje("Escriba la descripción de la alerta.");
            return false;
        }

        if (descripcion.length() > 1000) {
            mensaje("La descripción admite hasta 1000 caracteres.");
            return false;
        }

        // Verificar que se haya elegido uno de los cuatro técnicos.
        boolean tecnicoValido = false;

        for (int i = 0; i < cmbResponsable.getItemCount(); i++) {
            if (cmbResponsable.getItemAt(i).equals(responsable)) {
                tecnicoValido = true;
                break;
            }
        }

        if (!tecnicoValido) {
            mensaje("Seleccione un técnico del listado.");
            return false;
        }

        return true;
    }

    private Long obtenerId() {

        if (txtId.getText().isBlank()) {
            mensaje("Seleccione primero una alerta de la tabla.");
            return null;
        }

        return Long.valueOf(txtId.getText());
    }

    private void registrar() {

        if (!txtId.getText().isBlank()) {
            mensaje(
                    "Pulse Nuevo / Limpiar para registrar otra alerta."
            );
            return;
        }

        if (!validarFormulario()) {
            return;
        }

        boolean guardado = ejecutarTransaccion(gestor -> {

            Alerta alerta = new Alerta(
                    txtDescripcion.getText().trim(),
                    cmbSeveridad.getSelectedItem().toString(),
                    responsableSeleccionado()
            );

            // Toda alerta nueva inicia como REGISTRADA.
            gestor.persist(alerta);
        });

        if (guardado) {
            consultar();
            mensaje("Alerta registrada correctamente.");
        }
    }

    private void consultar() {

        try (EntityManager gestor = fabrica.createEntityManager()) {

            String consulta = "SELECT a FROM Alerta a";

            if (!chkAnuladas.isSelected()) {
                consulta += " WHERE a.activo = true";
            }

            consulta += " ORDER BY a.id DESC";

            List<Alerta> resultado = gestor.createQuery(
                    consulta, Alerta.class
            ).setMaxResults(200).getResultList();

            limpiar();
            modelo.setRowCount(0);
            registros = resultado;

            for (Alerta alerta : registros) {
                modelo.addRow(new Object[]{
                    alerta.getId(),
                    alerta.getFecha(),
                    alerta.getDescripcion(),
                    alerta.getSeveridad(),
                    alerta.getResponsable(),
                    alerta.getEstado(),
                    alerta.isActivo() ? "SÍ" : "NO",
                    alerta.getMotivoAnulacion()
                });
            }

            String filtro = chkAnuladas.isSelected()
                    ? "Vigentes y anuladas"
                    : "Solo vigentes, incluidas las cerradas";

            lblInformacion.setText(
                    filtro
                    + " | Registros mostrados: " + registros.size()
                    + " | Máximo: 200."
            );

        } catch (RuntimeException error) {
            mostrarError(error);
        }
    }

    private void cargarSeleccion() {

        int fila = tabla.getSelectedRow();

        if (fila < 0 || fila >= registros.size()) {
            return;
        }

        Alerta alerta = registros.get(fila);

        txtId.setText(alerta.getId().toString());
        txtDescripcion.setText(alerta.getDescripcion());

        // Conserva visualmente el responsable de registros anteriores.
        // Al actualizar, se exigirá elegir uno del listado actual.
        cmbResponsable.setSelectedItem(alerta.getResponsable());

        cmbSeveridad.setSelectedItem(alerta.getSeveridad());
        cmbEstado.setSelectedItem(alerta.getEstado());

        lblInformacion.setText(
                alerta.isActivo()
                ? "Alerta seleccionada: " + alerta.getId()
                : "Registro anulado: solo consulta. Motivo: "
                  + alerta.getMotivoAnulacion()
        );
    }

    private void actualizar() {

        Long id = obtenerId();

        if (id == null || !validarFormulario()) {
            return;
        }

        boolean actualizado = ejecutarTransaccion(gestor -> {

            Alerta alerta = buscarActiva(gestor, id);

            alerta.setDescripcion(
                    txtDescripcion.getText().trim()
            );
            alerta.setSeveridad(
                    cmbSeveridad.getSelectedItem().toString()
            );
            alerta.setResponsable(responsableSeleccionado());
            alerta.setEstado(
                    cmbEstado.getSelectedItem().toString()
            );
        });

        if (actualizado) {
            consultar();
            mensaje("Alerta actualizada correctamente.");
        }
    }

    private void anular() {

        Long id = obtenerId();

        if (id == null) {
            return;
        }

        String motivo = JOptionPane.showInputDialog(
                this,
                "Indique por qué se anula este registro.\n"
                + "Ejemplo: duplicado o ingresado por error.\n"
                + "El registro permanecerá almacenado.",
                "Anular alerta " + id,
                JOptionPane.QUESTION_MESSAGE
        );

        if (motivo == null) {
            return;
        }

        motivo = motivo.trim();

        if (motivo.isBlank() || motivo.length() > 500) {
            mensaje(
                    "El motivo debe tener entre 1 y 500 caracteres."
            );
            return;
        }

        int confirmacion = JOptionPane.showConfirmDialog(
                this,
                "¿Desea anular la alerta " + id + "?\n"
                + "Podrá consultarla marcando "
                + "\"Mostrar también registros anulados\".",
                "Confirmar anulación",
                JOptionPane.YES_NO_OPTION
        );

        if (confirmacion != JOptionPane.YES_OPTION) {
            return;
        }

        final String motivoFinal = motivo;

        boolean anulado = ejecutarTransaccion(gestor -> {

            Alerta alerta = buscarActiva(gestor, id);

            alerta.setActivo(false);
            alerta.setMotivoAnulacion(motivoFinal);
        });

        if (anulado) {
            consultar();
            mensaje(
                    "Alerta anulada. El registro se conserva.\n"
                    + "Para verlo, marque "
                    + "\"Mostrar también registros anulados\"."
            );
        }
    }

    private Alerta buscarActiva(
            EntityManager gestor, Long id) {

        Alerta alerta = gestor.find(Alerta.class, id);

        if (alerta == null) {
            throw new IllegalStateException(
                    "La alerta no existe."
            );
        }

        if (!alerta.isActivo()) {
            throw new IllegalStateException(
                    "La alerta está anulada y no puede modificarse."
            );
        }

        return alerta;
    }

    private boolean ejecutarTransaccion(
            Consumer<EntityManager> accion) {

        try (EntityManager gestor = fabrica.createEntityManager()) {

            EntityTransaction transaccion =
                    gestor.getTransaction();

            try {
                transaccion.begin();
                accion.accept(gestor);
                transaccion.commit();
                return true;

            } catch (RuntimeException error) {

                if (transaccion.isActive()) {
                    transaccion.rollback();
                }

                throw error;
            }

        } catch (RuntimeException error) {
            mostrarError(error);
            return false;
        }
    }

    private void mensaje(String texto) {
        JOptionPane.showMessageDialog(this, texto);
    }

    private void mostrarError(RuntimeException error) {

        error.printStackTrace();

        JOptionPane.showMessageDialog(
                this,
                "No se pudo completar la operación.\n"
                + "Detalle: " + error.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE
        );
    }

    public static void main(String[] args) {

        try {
            EntityManagerFactory fabrica =
                    Persistence.createEntityManagerFactory(
                            "AlertasPU"
                    );

            SwingUtilities.invokeLater(() -> {
                new Gestion_alertas(fabrica).setVisible(true);
            });

        } catch (RuntimeException error) {

            error.printStackTrace();

            JOptionPane.showMessageDialog(
                    null,
                    "No se pudo conectar con PostgreSQL.\n"
                    + "Revise persistence.xml y la salida de NetBeans.",
                    "Error de conexión",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }
}