package Proyecto;

import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

public class Triplos2{
	
	
	public  JTable tablaTriplo = new JTable(new DefaultTableModel(new Object[]{"", "Dato Obj", "Dato Fuente", "Operator"},0));
	public  String incremento = "";
	public  int inicio_For;
	
	public Triplos2(JTable tablaTriplo) {
		this.tablaTriplo=tablaTriplo;
	}
	
	public String[] dividirEnTokens(String linea) {
        return linea.split("\\s+|(?=[+\\-/=*])|(?<=[+\\-/=*])");
    }

	public void  llenarTriplo(String codigo) {
	    DefaultTableModel modelTriplo = (DefaultTableModel) tablaTriplo.getModel();
	    modelTriplo.setRowCount(0); // Limpiar tabla antes de llenarla

	    String textoIngresado = codigo;
	    String[] lineas = textoIngresado.split("\n");
	    
	    for (int i = 0; i < lineas.length; i++) {
	    	
	    	if (lineas[i].contains("for")){
	    		analizar_for(lineas[i]);
	    	} else if (lineas[i].contains("}")) {
	    		incremento_jump();
	    		if (i == lineas.length - 1) {
	    			modelTriplo.addRow(new Object[]{modelTriplo.getRowCount() + 1, "...", "...", "..."});
		    	}
	    	} else {
	    		triplo_aritmetico(lineas[i]);
	    	}
	    }
	}    	
	    	
	public void triplo_aritmetico(String linea) {
	        int tempCounter = 1;
	        String[] tokens = dividirEnTokens(linea);
	        
	        DefaultTableModel modelTriplo = (DefaultTableModel) tablaTriplo.getModel();
	        
	        Stack<String> operandos = new Stack<>();
	        Stack<String> operadores = new Stack<>();

	        String variableAsignada = tokens[0].trim(); // La variable en el lado izquierdo de "="
	        
	        // Procesamos los tokens después del "="
	        for (int i = 2; i < tokens.length; i++) {
	            String token = tokens[i].trim();

	            if (token.equals("*") || token.equals("/")) {
	                // Operadores de mayor precedencia
	                while (!operadores.isEmpty() && (operadores.peek().equals("*") || operadores.peek().equals("/"))) {
	                    generarTriplo(operandos, operadores, tempCounter);
	                }
	                
	                if (!operadores.isEmpty() && (operadores.peek().equals("+") || operadores.peek().equals("-"))) {
	                	String temp = "T" + tempCounter++;
	                	 String dato_fuente= operandos.pop();
	                    modelTriplo.addRow(new Object[]{modelTriplo.getRowCount() + 1, temp, dato_fuente, "="});
	                    operadores.push(token);
	                    operandos.push(temp);
	                } else {
	                	operadores.push(token);
	                }
	            } else if (token.equals("+") || token.equals("-")) {
	                // Operadores de menor precedencia
	                while (!operadores.isEmpty()) {
	                    generarTriplo(operandos, operadores, tempCounter);
	                }
	                operadores.push(token);
	            } else {
	                // Si es un operando (constante o variable), asignarle un temporal antes de operaciones
	                if (i == 2){
	                    String temp = "T" + tempCounter++;
	                    modelTriplo.addRow(new Object[]{modelTriplo.getRowCount() + 1, temp, token, "="});
	                    operandos.push(temp);
	                } else {
	                    operandos.push(token);
	                }  
	            }
	        }

	        // Procesa cualquier operador restante en la pila
	        while (!operadores.isEmpty()) {
	            generarTriplo(operandos, operadores, tempCounter);
	        }

	        // Asignación final a la variable en el lado izquierdo
	        String tempFinal = operandos.pop();
	        modelTriplo.addRow(new Object[]{modelTriplo.getRowCount() + 1, variableAsignada, tempFinal, "="});
	    }
	
// Método auxiliar para generar un triplo basado en las pilas de operandos y operadores
	public void generarTriplo(Stack<String> operandos, Stack<String> operadores, int tempCounter) {
		    String operador = operadores.pop();
		    String valorDerecho = operandos.pop();
		    String valorIzquierdo = operandos.pop();
		    
		    DefaultTableModel modelTriplo = (DefaultTableModel) tablaTriplo.getModel();
		    
		    modelTriplo.addRow(new Object[]{modelTriplo.getRowCount() + 1, valorIzquierdo, valorDerecho, operador});
		    operandos.push(valorIzquierdo); // El resultado temporal se usa en operaciones posteriores
		}
	
	public void analizar_for(String linea) {
		 
		DefaultTableModel modelTriplo = (DefaultTableModel) tablaTriplo.getModel();
		Pattern pattern = Pattern.compile("\\(([^)]+)\\)");
        Matcher matcher = pattern.matcher(linea);
        
        String dentro_for = "";
        
        while (matcher.find()) {
            dentro_for = matcher.group(1);
        }
        
        String[] partes_for = dentro_for.split(";");
        
        // Primera parte asignacion
		String[] tokens_1 = dividirEnTokens(partes_for[0]);
		generarTriplo_For(tokens_1);
		inicio_For = modelTriplo.getRowCount() + 1;
		
		// Segunda parte condicion
		if (partes_for[1].contains("&&")) {
			String[] condicionales = partes_for[1].split("&&");
			for (String condicional : condicionales) {
				String[] tokens_2 = condicional.split("(?<=[\\w])(?=[<>=!]=?|\\b)|(?<=[<>=!]=?)(?=\\w|\\d)|\\s+");
				generarTriplo_For(tokens_2);
				modelTriplo.addRow(new Object[]{modelTriplo.getRowCount() + 1, "Tr1", "True", modelTriplo.getRowCount() + 3});
		        modelTriplo.addRow(new Object[]{modelTriplo.getRowCount() + 1, "Tr1", "False", null});
			}	
		} else if (partes_for[1].contains("||")) {
			String[] condicionales = partes_for[1].split("\\|\\|");
			for (int i = 0; i < condicionales.length; i++) {
				String[] tokens_2 = condicionales[i].split("(?<=[\\w])(?=[<>=!]=?|\\b)|(?<=[<>=!]=?)(?=\\w|\\d)|\\s+");
				generarTriplo_For(tokens_2);
				if (i == 0) {
					modelTriplo.addRow(new Object[]{modelTriplo.getRowCount() + 1, "Tr1", "True", modelTriplo.getRowCount() + 7});
			        modelTriplo.addRow(new Object[]{modelTriplo.getRowCount() + 1, "Tr1", "False", modelTriplo.getRowCount() + 2});
				} else {
					modelTriplo.addRow(new Object[]{modelTriplo.getRowCount() + 1, "Tr1", "True", modelTriplo.getRowCount() + 3});
			        modelTriplo.addRow(new Object[]{modelTriplo.getRowCount() + 1, "Tr1", "False", null});
				}
			}	
		} else {
			String[] tokens_2 = partes_for[1].split("(?<=[\\w])(?=[<>=!]=?|\\b)|(?<=[<>=!]=?)(?=\\w|\\d)|\\s+");
			generarTriplo_For(tokens_2);
			modelTriplo.addRow(new Object[]{modelTriplo.getRowCount() + 1, "Tr1", "True", modelTriplo.getRowCount() + 3});
	        modelTriplo.addRow(new Object[]{modelTriplo.getRowCount() + 1, "Tr1", "False", null});
		}
		
		//Tercera parte incremento
		incremento = partes_for[2];
	}
	
	public void generarTriplo_For(String[] tokens) {
		
		int tempCounter = 1;
        DefaultTableModel modelTriplo = (DefaultTableModel) tablaTriplo.getModel();
		String temp = "T" + tempCounter++;
		
		if ( tokens[1].equals("=")) {
			modelTriplo.addRow(new Object[]{modelTriplo.getRowCount() + 1, temp, tokens[2], "="});
	        modelTriplo.addRow(new Object[]{modelTriplo.getRowCount() + 1, tokens[0], temp, tokens[1]});
		} else {
			modelTriplo.addRow(new Object[]{modelTriplo.getRowCount() + 1, temp, tokens[0], "="});
	        modelTriplo.addRow(new Object[]{modelTriplo.getRowCount() + 1, temp, tokens[2], tokens[1]});
		} 
	}
	
	public void incremento_jump(){
		triplo_aritmetico(incremento);
		
		DefaultTableModel modelTriplo = (DefaultTableModel) tablaTriplo.getModel();
		modelTriplo.addRow(new Object[]{modelTriplo.getRowCount() + 1, null, inicio_For, "JMP"});
		
		for (int i = modelTriplo.getRowCount() - 1; i >= 0; i--) {
			if (modelTriplo.getValueAt(i, 2).equals("False") && modelTriplo.getValueAt(i, 3) == null) {
				modelTriplo.setValueAt(modelTriplo.getRowCount() + 1, i, 3);
			}
		}
	}

	 /*public static void main(String[] args) {

	        String codigo = "$Va1=8/5+4*3\r\n"
	        		+ "$Va2=2+6*3\r\n"
	        		+ "for($Va3=1;$Var3>9||$Var3<=20;$Var3=$Var3+1){\r\n"
	        		+ "$Id1=$Va3/5+4\r\n"
	        		+ "$Id2=5+$Va2*3\r\n"
	        		+ "}\r\n"
	        		+ "for($Va6=1;$Var6>9&&$Var6<=20;$Var6=$Var6+1){\r\n"
	        		+ "$Id4=$Va5/5+4\r\n"
	        		+ "$Id6=5+$Va9*3\r\n"
	        		+ "}\r\n"
	        		+ "$Va1=8907\r\n";
	        llenarTriplo(codigo);
	        
	        DefaultTableModel model = (DefaultTableModel) tablaTriplo.getModel();
	        System.out.printf("%-10s%-10s%-10s%-10s%n", "", "Dato Obj", "Dato F", "Operador");
	        System.out.println("------------------------------------");
	        
	        for (int i = 0; i < model.getRowCount(); i++) {
	            System.out.printf("%-10s%-10s%-10s%-10s%n", 
	                    model.getValueAt(i, 0), 
	                    model.getValueAt(i, 1), 
	                    model.getValueAt(i, 2), 
	                    model.getValueAt(i, 3));
	        } 
	 }*/
}