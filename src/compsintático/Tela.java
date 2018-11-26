/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package compsintático;

import java.awt.EventQueue;
import java.util.List;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 *
 * @author Gome
 */
public class Tela {

    private JFrame frame;

    // Inicia a aplicação
    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            try {
                Tela window = new Tela();
                window.frame.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public Tela() {
        initialize();
    }

    // Inicializa componentes da aplicação
    private void initialize() {
        /*frame = new JFrame("Analisador léxico");
        frame.setBounds(100, 100, 655, 425);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().setLayout(null);
        frame.setResizable(false);

        JPanel panel = new JPanel();
        panel.setBounds(10, 11, 629, 375);
        frame.getContentPane().add(panel);
        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));*/

        JFileChooser chooser = new JFileChooser(".");
        FileNameExtensionFilter filter = new FileNameExtensionFilter(
                "Código-fonte BJ", "bj", "text");
        chooser.setFileFilter(filter);

        /*// Colunas da tabela
        String col[] = {"Lexema", "Valor"};

        // Tabela para exibição do resultado
        DefaultTableModel tableModel = new DefaultTableModel(col, 0);
        JTable table = new JTable(tableModel);

        JScrollPane scrollPane = new JScrollPane(table);
        panel.add(scrollPane);*/
        // Processa arquivo selecionado
        if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
            /*List<Token> tokens = AnLexico.execute(chooser.getSelectedFile().getAbsolutePath());

            for (Token token : tokens) {
                Object[] objs = {token.getLexema(), token.getValor()};
                tableModel.addRow(objs);
            }
        } else {
            JOptionPane.showMessageDialog(null, "Arquivo não selecionado!");
            System.exit(1);
        }*/
            List<Token> tokens = AnLexico.execute(chooser.getSelectedFile().getAbsolutePath());
            /*
			 * for (Token token : tokens) { Object[] objs = { token.getToken(),
			 * token.getLexema(), token.getTipo() }; tableModel.addRow(objs); }
             */

            // Executa a analise sintatica
            AnSintatico analisadorSintatico = new AnSintatico();

            JOptionPane.showMessageDialog(null, analisadorSintatico.programaDiretorSintatico(tokens));

        } else {
            JOptionPane.showMessageDialog(null, "Arquivo não selecionado");
            System.exit(1);
        }
    }

}
