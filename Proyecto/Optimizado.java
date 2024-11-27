package Proyecto;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.EventQueue;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.table.DefaultTableModel; 

public class Optimizado extends JFrame {

    private static final long serialVersionUID = 1L;
    private JPanel contentPane;
    private JTextArea textArea;

    // Lista para guardar las palabras válidas
    private ArrayList<String> listaPalabrasValidas = new ArrayList<>();
    private JTable tableDatos;
    private JTable tableErrores;
    private String textoOptimizadoGlobal = ""; // Variable para almacenar el texto optimizado


    // Mapa para almacenar los tipos de datos de las variables
    private static HashMap<String, String> tiposDeVariables = new HashMap<>();
    private JTable tablaTriplo;

    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    Optimizado frame = new Optimizado();
                    frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
    public Optimizado() {
    	
    	
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 1130, 704);
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);
        contentPane.setLayout(null);

        JPanel panel = new JPanel();
        panel.setForeground(new Color(128, 128, 255));
        panel.setBounds(0, 0, 1106, 680);
        contentPane.add(panel);
        panel.setLayout(null);

        JLabel lblNewLabel = new JLabel("Compilador en java");
        lblNewLabel.setFont(new Font("Tahoma", Font.PLAIN, 60));
        lblNewLabel.setBounds(147, 10, 537, 140);
        panel.add(lblNewLabel);

        JButton btnNewButton = new JButton("Agregar a la tabla");
        btnNewButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String textoIngresado = textoOptimizadoGlobal;
                if (!textoIngresado.isEmpty()) {
                    // Limpia las tablas antes de agregar nuevos datos
                    DefaultTableModel modelDatos = (DefaultTableModel) tableDatos.getModel();
                    modelDatos.setRowCount(0);

                    DefaultTableModel modelErrores = (DefaultTableModel) tableErrores.getModel();
                    modelErrores.setRowCount(0);

                    // Procesa el texto ingresado
                    String[] lineas = textoIngresado.split("\n");
                    Set<String> addedValues = new HashSet<>(); // Para evitar duplicados en la tabla

                    // Contador para generar los tokens de error ERN1, ERN2, etc.
                    int contadorErrores = 1;

                    // Procesa cada línea para detectar errores y agregar datos a la tabla
                    for (int numLinea = 0; numLinea < lineas.length; numLinea++) {
                        String linea = lineas[numLinea].trim();
                        /*if(linea.contains("for")){
                        	continue;
                        }*/
                        String[] tokens = dividirEnTokens(linea);
                        for (int i = 0; i < tokens.length; i++) {
                            String token = tokens[i].trim();

                            // Ignorar tokens vacíos
                            if (token.isEmpty()) {
                                continue;
                            }

                            // Si encontramos un "=", el siguiente token es el valor asignado
                            if (token.equals("=") && i + 1 < tokens.length) {
                                String valorAsignado = tokens[i + 1].trim();
                                String tipoValorAsignado = determinarTipoDato(valorAsignado);

                                // Actualizar el tipo de la variable a la izquierda
                                String variable = tokens[i - 1].trim();

                                // Actualizar el tipo en el mapa
                                tiposDeVariables.put(variable, tipoValorAsignado);

                                // Agregar la variable con su tipo a la tabla
                                if (addedValues.add(variable)) {
                                    modelDatos.addRow(new Object[]{variable, tipoValorAsignado});
                                } else {
                                    actualizarTipoDatoEnTabla(variable, tipoValorAsignado, modelDatos);
                                }

                                // Agregar el "=" como operador
                                if (addedValues.add(token)) {
                                    modelDatos.addRow(new Object[]{token, ""}); // El "=" no tiene un tipo de dato
                                }

                                // Agregar el valor asignado
                                if (addedValues.add(valorAsignado)) {
                                    modelDatos.addRow(new Object[]{valorAsignado, tipoValorAsignado});
                                }

                                // Saltar el valor ya procesado
                                i++;
                                continue;
                            }

                            // Determinar el tipo de dato para otros tokens
                            String tipoDato = determinarTipoDato(token);

                            // Solo agregar a la tabla si es necesario
                            if (token.startsWith("$")) {
                                // Verificar si la variable ya tiene un tipo asignado
                                if (tiposDeVariables.containsKey(token)) {
                                    tipoDato = tiposDeVariables.get(token);
                                } else {
                                    // Agregar la variable como "desconocido" si aún no ha sido asignada
                                    tiposDeVariables.put(token, tipoDato);
                                }
                            }

                            // Agregar el token y su tipo de dato a la tabla si no ha sido agregado antes
                            if (addedValues.add(token)) {
                                modelDatos.addRow(new Object[]{token, tipoDato});
                            }
                        }
                        // Detectar errores en la línea actual
                        detectarErrores(tokens, numLinea + 1, modelErrores, contadorErrores);
                        // Actualizar contador de errores
                        contadorErrores = modelErrores.getRowCount() + 1;
                    }
                    System.out.println("Lista de palabras válidas: " + listaPalabrasValidas);
                }
            }
        });
     // Dentro del método public claseDos() después de la creación de otros botones:

        JButton btnOptimizar = new JButton("Optimizar");
        btnOptimizar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String textoIngresado = textArea.getText().trim();

                if (!textoIngresado.isEmpty()) {
                    // Aplicar la optimización completa
                	textoOptimizadoGlobal = eliminarRedundancia1(textoIngresado);
                	textoOptimizadoGlobal = eliminarRedundancia0(textoOptimizadoGlobal);
                	textoOptimizadoGlobal = eliminarRedundanciaMas0(textoOptimizadoGlobal);
                	textoOptimizadoGlobal = eliminarEspacios(textoOptimizadoGlobal);
                	List<String> Almacen1 = almacenarVariables1(textoOptimizadoGlobal);
                	List<String> Almacen0 = almacenarVariables0(textoOptimizadoGlobal);
                	textoOptimizadoGlobal= optimizarCodigoVariables(textoOptimizadoGlobal, Almacen1, Almacen0);
                	textoOptimizadoGlobal=eliminarOperadoresIzquierdaInvalidos(textoOptimizadoGlobal);
                   
                    // Mostrar el texto optimizado en una nueva ventana
                    JFrame ventanaOptimizada = new JFrame("Texto Optimizado");
                    ventanaOptimizada.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                    ventanaOptimizada.setBounds(100, 100, 400, 400);

                    JPanel panelOptimizado = new JPanel();
                    panelOptimizado.setBorder(new EmptyBorder(5, 5, 5, 5));
                    ventanaOptimizada.setContentPane(panelOptimizado);
                    panelOptimizado.setLayout(null);

                    JLabel lblTextoOptimizado = new JLabel("Texto Optimizado:");
                    lblTextoOptimizado.setBounds(10, 10, 200, 25);
                    panelOptimizado.add(lblTextoOptimizado);

                    JScrollPane scrollPaneOptimizado = new JScrollPane();
                    scrollPaneOptimizado.setBounds(10, 40, 360, 300);
                    panelOptimizado.add(scrollPaneOptimizado);

                    JTextArea textAreaOptimizado = new JTextArea();
                    textAreaOptimizado.setText(textoOptimizadoGlobal);
                    textAreaOptimizado.setLineWrap(true);
                    textAreaOptimizado.setWrapStyleWord(true);
                    textAreaOptimizado.setEditable(false);
                    scrollPaneOptimizado.setViewportView(textAreaOptimizado);
                    

                    ventanaOptimizada.setVisible(true);
                } else {
                    JOptionPane.showMessageDialog(null, "No hay texto para optimizar.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });



        btnOptimizar.setBounds(31, 600, 187, 23); // Ubicado debajo de los botones existentes
        panel.add(btnOptimizar);

        btnNewButton.setBounds(31, 562, 187, 23);
        panel.add(btnNewButton);
        JScrollPane scrollPaneTextArea = new JScrollPane();
        scrollPaneTextArea.setBounds(31, 160, 187, 347);
        panel.add(scrollPaneTextArea);
        
                textArea = new JTextArea();
                scrollPaneTextArea.setViewportView(textArea);
                textArea.setLineWrap(true);
                textArea.setWrapStyleWord(true);

        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setBounds(233, 160, 220, 347);
        panel.add(scrollPane);

        tableDatos = new JTable();
        tableDatos.setEnabled(false);
        tableDatos.setModel(new DefaultTableModel(
            new Object[][] {},
            new String[] {
                "Lexema", "Tipo de datos"
            }
        ));
        scrollPane.setViewportView(tableDatos);

        JScrollPane scrollPane_1 = new JScrollPane();
        scrollPane_1.setBounds(463, 160, 595, 232);
        panel.add(scrollPane_1);

        tableErrores = new JTable();
        tableErrores.setModel(new DefaultTableModel(
            new Object[][] {},
            new String[] {
                "Token", "Lexema", "Renglon", "Descripción"
            }
        ));
        scrollPane_1.setViewportView(tableErrores);
        
        JScrollPane scrollPane_2 = new JScrollPane();
        scrollPane_2.setBounds(463, 402, 595, 253);
        panel.add(scrollPane_2);
        
        tablaTriplo = new JTable();
        tablaTriplo.setToolTipText("");
        tablaTriplo.setModel(new DefaultTableModel(
        	new Object[][] {
        	},
        	new String[] {
        		"Numero", "Dato Objeto", "Dato fuente", "Operador"
        	}
        ));
        scrollPane_2.setViewportView(tablaTriplo);
        
        JButton btnNewButton_1 = new JButton("Analizar");
        btnNewButton_1.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	Triplos2 triplos = new Triplos2(tablaTriplo);
            	// Limpia la tabla de triplos antes de agregar nuevos datos
                DefaultTableModel modelTriplo = (DefaultTableModel) tablaTriplo.getModel();
                modelTriplo.setRowCount(0);

                // Obtiene el texto ingresado por el usuario
                String textoIngresado = textoOptimizadoGlobal;
                if (!textoIngresado.isEmpty()) {
                    // Instancia de la clase Triplos para llenar la tabla de triplos
                	triplos.llenarTriplo(textoIngresado);                    }
                }
        });
        btnNewButton_1.setBounds(233, 563, 220, 21);
        panel.add(btnNewButton_1);
        
    }
    

    // Aquí se asegura que el * se considere como un operador de multiplicación
    /*public static String[] dividirEnTokens(String linea) {
        return linea.split("\\s+|(?=[+\\-/=()])|(?<=[+\\-/=()])");
    }*/
    public static String[] dividirEnTokens(String linea) {
        return linea.split("\\s+|(?=[+\\-/=<])|(?<=[+\\-/=<])|(?=[{}();])|(?<=[{}();])");
    }

    public static String determinarTipoDato(String valor) {
        if (tiposDeVariables.containsKey(valor)) {
            return tiposDeVariables.get(valor);
        }
        if (valor.startsWith("\"") && valor.endsWith("\"")) {
            return "cadena";
        }
        try {
            Double.parseDouble(valor);
            if (valor.contains(".")) {
                return "real";
            } else {
                return "int";
            }
        } catch (NumberFormatException e) {
            if ("+-*/=".contains(valor)) {
                return ""; // Operadores sin tipo de dato definido
            }
            return "";
        }
    }

    public static void actualizarTipoDatoEnTabla(String variable, String tipoDato, DefaultTableModel model) {
        for (int i = 0; i < model.getRowCount(); i++) {
            String lexema = (String) model.getValueAt(i, 0);
            if (lexema.equals(variable)) {
                model.setValueAt(tipoDato, i, 1);
            }
        }
    }

    public void detectarErrores(String[] tokens, int lineaNumero, DefaultTableModel modeloErrores, int contadorErrores) {
        String variableAsignada = null; // Variable que se está asignando (a la izquierda de "=")
        boolean errorDetectadoEnLinea = false; // Bandera para detener la detección tras el primer error

        for (int i = 0; i < tokens.length; i++) {
            String token = tokens[i].trim();

            // Detectar si es una asignación para capturar la variable a la izquierda del "="
            if (token.equals("=") && i > 0) {
                variableAsignada = tokens[i - 1].trim(); // Guardamos la variable a la izquierda del "="
            }

            if (token.startsWith("$")) {
                if (!tiposDeVariables.containsKey(token)) {
                    if (!errorDetectadoEnLinea) { // Solo agregar un error por línea
                        String errorToken = "ERN" + contadorErrores;
                        modeloErrores.addRow(new Object[]{errorToken, token, lineaNumero, "Variable indefinida"});
                        contadorErrores++;
                        errorDetectadoEnLinea = true; // Se detectó un error, no buscar más en esta línea
                    }
                    break; // Salir del bucle tras detectar el error
                }
            }
            // Detectar incompatibilidad de tipos, pero permitir int y real
            if ((token.equals("+") || token.equals("-") || token.equals("*") || token.equals("/")) && !errorDetectadoEnLinea) {
                if (i > 0 && i < tokens.length - 1) {
                    String operandoIzquierdo = tokens[i - 1].trim();
                    String operandoDerecho = tokens[i + 1].trim();
                    String tipoIzquierdo = determinarTipoDato(operandoIzquierdo);
                    String tipoDerecho = determinarTipoDato(operandoDerecho);

                    // Solo marcar como error si no son compatibles (int y real son compatibles)
                    if (!sonCompatibles(tipoIzquierdo, tipoDerecho)) {
                        String errorToken = "ERN" + contadorErrores;

                        // Usar la variable asignada en la descripción del error
                        String descripcionError = "Incompatibilidad de tipos, " + variableAsignada;

                        modeloErrores.addRow(new Object[]{errorToken, operandoIzquierdo, lineaNumero, descripcionError});
                        contadorErrores++;
                        errorDetectadoEnLinea = true; // Se detectó un error, no buscar más en esta línea
                        break; // Salir del bucle tras detectar el error
                    }
                }
            }
        }
    }
    // Función para verificar si dos tipos son compatibles
    public boolean sonCompatibles(String tipo1, String tipo2) {
        // int y real son compatibles
        if ((tipo1.equals("int") && tipo2.equals("real")) || (tipo1.equals("real") && tipo2.equals("int"))) {
            return true;
        }
        // Si ambos tipos son iguales, también son compatibles
        return tipo1.equals(tipo2);
    } 
    public String eliminarRedundancia1(String texto) {
        // Divide el texto por líneas
        String[] lineas = texto.split("\n");
        StringBuilder textoOptimizado = new StringBuilder();

        for (String linea : lineas) {
            // Reemplazar patrones redundantes como "*1" o "/1"
            String lineaOptimizada = linea.replaceAll("\\*1\\b", "").replaceAll("/1\\b", "");
            textoOptimizado.append(lineaOptimizada).append("\n");
        }

        return textoOptimizado.toString().trim(); // Elimina espacios extra al final
    }
    public String eliminarRedundancia0(String texto) {
        // Divide el texto por líneas
        String[] lineas = texto.split("\n");
        StringBuilder textoOptimizado = new StringBuilder();

        for (String linea : lineas) {
            // Reemplazar patrones redundantes como "*1" o "/1"
            String lineaOptimizada = linea.replaceAll("\\*0\\b", "").replaceAll("/0\\b", "");
            textoOptimizado.append(lineaOptimizada).append("\n");
        }        

        return textoOptimizado.toString().trim(); // Elimina espacios extra al final
    }
    public String eliminarRedundanciaMas0(String texto) {
        // Divide el texto por líneas
        String[] lineas = texto.split("\n");
        StringBuilder textoOptimizado = new StringBuilder();

        for (String linea : lineas) {
            // Reemplazar patrones redundantes como "*1" o "/1"
            String lineaOptimizada = linea.replaceAll("\\+0\\b", "").replaceAll("/0\\b", "");
            textoOptimizado.append(lineaOptimizada).append("\n");
        }        

        return textoOptimizado.toString().trim(); // Elimina espacios extra al final
    }
    public String eliminarEspacios(String texto) {
        StringBuilder textoOptimizado = new StringBuilder();
        String[] lineas = texto.split("\n");
        for (String linea : lineas) {
            linea = linea.trim(); // Elimina espacios al inicio y final de la línea
            if (!linea.isEmpty()) {
                textoOptimizado.append(linea.replaceAll("\\s+", "")).append("\n");
            }
        }
        return textoOptimizado.toString().trim();
    }
    // Método que procesa el texto ingresado y retorna las variables optimizadas
    public List<String> almacenarVariables0(String textoIngresado) {
        List<String> variables = new ArrayList<>();
        
        // Dividimos el texto en líneas
        String[] lineas = textoIngresado.split("\\n");
        
        // Recorremos cada línea
        for (String linea : lineas) {
            // Eliminamos espacios adicionales
            linea = linea.trim();
            
            // Dividimos por el signo '='
            String[] partes = linea.split("=");

            // Verificamos que tenga exactamente dos partes
            if (partes.length == 2) {
                String variable = partes[0].trim();
                String valor = partes[1].trim();

                // Verificamos que el valor sea "1" o "0"
                if (valor.equals("1")) {
                    variables.add(variable);
                    // Imprimir en consola la variable almacenada
                    System.out.println("Variable almacenada: " + variable);
                }
            }
        }
        return variables;
    }
    public List<String> almacenarVariables1(String textoIngresado) {
        List<String> variables = new ArrayList<>();
        
        // Dividimos el texto en líneas
        String[] lineas = textoIngresado.split("\\n");
        
        // Recorremos cada línea
        for (String linea : lineas) {
            // Eliminamos espacios adicionales
            linea = linea.trim();
            
            // Dividimos por el signo '='
            String[] partes = linea.split("=");

            // Verificamos que tenga exactamente dos partes
            if (partes.length == 2) {
                String variable = partes[0].trim();
                String valor = partes[1].trim();

                // Verificamos que el valor sea "1" o "0"
                if (valor.equals("0")) {
                    variables.add(variable);
                    // Imprimir en consola la variable almacenada
                    System.out.println("Variable almacenada: " + variable);
                }
            }
        }
        return variables;
    }

    public String optimizarCodigoVariables(String textoIngresado, List<String> variables1, List<String> variables0) {
        // Crear HashSets para optimizar las búsquedas
        Set<String> setVariables1 = new HashSet<>(variables1);
        Set<String> setVariables0 = new HashSet<>(variables0);

        // Dividir el texto en líneas
        String[] lineas = textoIngresado.split("\n");
        StringBuilder textoOptimizado = new StringBuilder();

        // Procesar cada línea
        for (String linea : lineas) {
            // Guardar la línea original antes de cualquier modificación
            String lineaOriginal = linea;

            boolean lineaModificada = false;

            // Verificar si la línea contiene operadores aritméticos
            if (linea.contains("*") || linea.contains("/") || linea.contains("%") ||
                linea.contains("+") || linea.contains("-")) {

                // Procesar variables1: eliminar solo si el operador lógico a la izquierda NO es + o -
                for (String variable : setVariables1) {
                    int index = linea.indexOf(variable);
                    if (index > 0) {
                        char operadorIzquierda = linea.charAt(index - 1);
                        if (operadorIzquierda != '+' && operadorIzquierda != '-') {
                            linea = linea.replace(variable, "");
                            lineaModificada = true;
                        }
                    }
                }

                // Procesar variables0: eliminar solo si el operador lógico a la izquierda es + o -
                for (String variable : setVariables0) {
                    int index = linea.indexOf(variable);
                    if (index > 0) {
                        char operadorIzquierda = linea.charAt(index - 1);
                        if (operadorIzquierda == '*' || operadorIzquierda == '/') {
                            linea = linea.replace(variable, "");
                            lineaModificada = true;
                        }
                    }
                }

                // Si la línea se modificó, pero se eliminó una variable1 con un operador + o -, revertir cambios
                if (lineaModificada) {
                    for (String variable : setVariables1) {
                        int index = lineaOriginal.indexOf(variable);
                        if (index > 0) {
                            char operadorIzquierda = lineaOriginal.charAt(index - 1);
                            if (operadorIzquierda == '+' || operadorIzquierda == '-') {
                                // Revertir a la línea original
                                linea = lineaOriginal;
                                break;
                            }
                        }
                    }
                }
                // para 0

                // Procesar variables1: eliminar solo si el operador lógico a la izquierda NO es + o -
                for (String variable : setVariables1) {
                    int index = linea.indexOf(variable);
                    if (index > 0) {
                        char operadorIzquierda = linea.charAt(index - 1);
                        if (operadorIzquierda != '*' && operadorIzquierda != '/') {
                            linea = linea.replace(variable, "");
                            lineaModificada = true;
                        }
                    }
                }

                // Procesar variables0: eliminar solo si el operador lógico a la izquierda es + o -
                for (String variable : setVariables0) {
                    int index = linea.indexOf(variable);
                    if (index > 0) {
                        char operadorIzquierda = linea.charAt(index - 1);
                        if (operadorIzquierda == '*' || operadorIzquierda == '/') {
                            linea = linea.replace(variable, "");
                            lineaModificada = true;
                        }
                    }
                }

                // Si la línea se modificó, pero se eliminó una variable1 con un operador + o -, revertir cambios
                if (lineaModificada) {
                    for (String variable : setVariables1) {
                        int index = lineaOriginal.indexOf(variable);
                        if (index > 0) {
                            char operadorIzquierda = lineaOriginal.charAt(index - 1);
                            if (operadorIzquierda == '*' || operadorIzquierda == '/') {
                                // Revertir a la línea original
                                linea = lineaOriginal;
                                break;
                            }
                        }
                    }
                }
            }
           

            // Agregar la línea (original o modificada) al resultado
            textoOptimizado.append(linea).append("\n");
        }

        return textoOptimizado.toString().trim(); // Retorna el texto optimizado
    }

    public String eliminarOperadoresIzquierdaInvalidos(String textoIngresado) {
        // Expresión regular para encontrar operadores seguidos por otro operador o nada
        String regex = "(\\*|/|%|\\+|-)\\s*(\\+|-|$|\\s)";

        // Reemplazar el operador de la izquierda, conservando el de la derecha o eliminándolo si es vacío
        textoIngresado = textoIngresado.replaceAll(regex, "$2");

        return textoIngresado.trim(); // Retorna el texto sin operadores inválidos
    }
}
