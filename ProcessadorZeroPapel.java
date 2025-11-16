import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.util.Base64;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ProcessadorZeroPapel extends JFrame {

    // ==========================================================
    // CONFIGURAÇÕES CRÍTICAS
    // ==========================================================
    // 1. Defina o caminho onde os arquivos (PNG e TXT) serão salvos:
    // ATENÇÃO: Verifique se este caminho existe e se o Java tem permissão para escrever nele.
    private static final String PASTA_ARQUIVAMENTO = "Escreva Aqui o caminho onde vai armazenar os arquivos gerados \\"; 
    
    // 2. Regex para extrair a Base64 - Busca o código após o cabeçalho.
    private static final Pattern BASE64_PATTERN = Pattern.compile(
        "--- CÓDIGO DA ASSINATURA .*?\\s*(data:image/png;base64,[^\n]+)", Pattern.DOTALL
    );
    
    // ==========================================================
    // VARIÁVEIS DA INTERFACE
    // ==========================================================
    private JTextArea inputArea;
    private JLabel lblStatus; 

    // --- CONSTRUTOR ---
    public ProcessadorZeroPapel() {
        super("Zero Papel - Processador de Comprovantes (Java)");
        
        // Configuração Inicial da Janela
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(700, 500);
        setLayout(new BorderLayout(10, 10));
        
        // Garante que a pasta de arquivamento exista
        new File(PASTA_ARQUIVAMENTO).mkdirs();

        // ------------------ ÁREA CENTRAL (INPUT) ------------------
        inputArea = new JTextArea("1. Cole o corpo COMPLETO do e-mail (enviado pelo motorista) aqui...");
        inputArea.setLineWrap(true);
        add(new JScrollPane(inputArea), BorderLayout.CENTER);
        
        // ------------------ ÁREA INFERIOR (CONTROLE) ------------------
        JPanel controlPanel = new JPanel();
        controlPanel.setLayout(new BoxLayout(controlPanel, BoxLayout.Y_AXIS)); 
        
        // Botão de Ação
        JButton btnProcessar = new JButton("2. Processar E-mail e Arquivar Comprovante");
        btnProcessar.setFont(new Font("Arial", Font.BOLD, 14));
        btnProcessar.setAlignmentX(Component.CENTER_ALIGNMENT); 
        btnProcessar.addActionListener(new ProcessarListener());
        
        // Label de Status
        lblStatus = new JLabel("Status: Pronto para processar o conteúdo colado.", SwingConstants.CENTER);
        lblStatus.setFont(new Font("Arial", Font.ITALIC, 12));
        lblStatus.setAlignmentX(Component.CENTER_ALIGNMENT); 
        
        // Adiciona os componentes ao painel de controle
        controlPanel.add(Box.createVerticalStrut(10)); 
        controlPanel.add(btnProcessar);
        controlPanel.add(Box.createVerticalStrut(5));  
        controlPanel.add(lblStatus);
        controlPanel.add(Box.createVerticalStrut(10)); 
        
        // Adiciona o Painel de Controle ao rodapé da janela principal
        add(controlPanel, BorderLayout.SOUTH); 
        
        setLocationRelativeTo(null); 
        setVisible(true);
    }

    // ==========================================================
    // LÓGICA DE PROCESSAMENTO (REGEX E DECODIFICAÇÃO)
    // ==========================================================
    private class ProcessarListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String emailContent = inputArea.getText();
            
            // 1. Extração de Dados Essenciais
            String cte = extractData(emailContent, "CT-e / NF-e:\\s*(\\S+)");
            String placa = extractData(emailContent, "Placa do Veículo \\(Logado\\):\\s*(\\S+)");
            String recebedor = extractData(emailContent, "Recebedor:\\s*(.*)"); 
            String cpf = extractData(emailContent, "CPF do Recebedor:\\s*(\\S+)");
            String geoData = extractData(emailContent, "Localização GPS no momento da assinatura:\\s*(.*)");
            String base64Data = extractBase64(emailContent);

            if (base64Data == null || cte == null || placa == null || cpf == null) {
                lblStatus.setText("Status: ERRO - Falha na extração de dados vitais. Tente novamente.");
                JOptionPane.showMessageDialog(ProcessadorZeroPapel.this,
                    "Erro: Falha ao extrair dados vitais. O Regex não encontrou o CT-e, Placa, CPF ou a Assinatura. Cole o conteúdo COMPLETO do e-mail.", 
                    "Erro de Extração", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            String cpfPadronizado = cpf.replaceAll("[^0-9]", "");
            
            // Cria a data formatada (Corrige o aviso de toLocaleString())
            String dataRegistro = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault()).format(new Date());
            String timestampArquivo = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());


            try {
                // 2. Geração do Nome do Arquivo Padronizado
                String nomeBase = String.format("CTe-%s_Placa-%s_CPF-%s_%s", cte, placa, cpfPadronizado, timestampArquivo);
                
                // --- Processamento da Imagem (CORREÇÃO BASE64) ---
                String base64Image = base64Data.split(",")[1]; 
                
                // CORREÇÃO CRÍTICA (Limpeza e PADDING): 
                String base64Limpa = base64Image
                    .replaceAll("\\s+", "") // 1. Remove espaços, tabs e novas linhas (Resolve o erro 'incorrect ending byte' da cópia)
                    .replaceAll("[^A-Za-z0-9+/=]", ""); // 2. Remove caracteres não-Base64 válidos (Resolve falhas de codificação)

                // 3. Adiciona PADDING = (Resolve o erro 'wrong 4-byte ending unit')
                int padding = 4 - (base64Limpa.length() % 4);
                if (padding != 4 && padding != 0) {
                    for (int i = 0; i < padding; i++) {
                        base64Limpa += "=";
                    }
                }
                
                // 4. Decodifica a string Base64 limpa e PADRONIZADA
                byte[] imagemBytes = Base64.getDecoder().decode(base64Limpa); 
                
                String nomeImagem = nomeBase + ".png";
                File arquivoImagem = new File(PASTA_ARQUIVAMENTO, nomeImagem);
                
                try (FileOutputStream fos = new FileOutputStream(arquivoImagem)) {
                    fos.write(imagemBytes); // Salva a imagem PNG
                }
                
                // --- Salva os Metadados (Registro para Auditoria) ---
                String nomeMetadados = nomeBase + ".txt";
                File arquivoMetadados = new File(PASTA_ARQUIVAMENTO, nomeMetadados);
                
                try (FileWriter writer = new FileWriter(arquivoMetadados)) {
                    writer.write("Registro Processado pelo Sistema JAVA 'Zero Papel'\n\n");
                    writer.write("CT-e: " + cte + "\n");
                    writer.write("Placa Logada: " + placa + "\n");
                    writer.write("Recebedor: " + recebedor + "\n");
                    writer.write("CPF: " + cpf + "\n");
                    writer.write("Localização GPS: " + geoData + "\n");
                    writer.write("Data Processamento: " + dataRegistro + "\n"); 
                    writer.write("--------------------------------\n");
                    writer.write("Assinatura salva em: " + nomeImagem + "\n");
                }

                // 4. Sucesso e Limpeza
                JOptionPane.showMessageDialog(ProcessadorZeroPapel.this, 
                    "Sucesso! Comprovante arquivado como:\n" + nomeImagem, 
                    "Processamento JAVA Concluído", JOptionPane.INFORMATION_MESSAGE);
                
                lblStatus.setText("Status: Arquivo " + nomeImagem + " salvo! Pronto para o próximo.");
                inputArea.setText("Cole o corpo COMPLETO do PRÓXIMO e-mail de comprovante aqui...");

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(ProcessadorZeroPapel.this, 
                    "Erro Crítico de Processamento: " + ex.getMessage(), "Erro JAVA", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
                lblStatus.setText("Status: Falha crítica. Verifique o console e permissões.");
            }
        }
    }
    
    // --- Métodos Auxiliares de Extração (REGEX) ---
    
    private String extractData(String content, String regex) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(content);
        if (matcher.find()) {
            return matcher.group(1).trim();
        }
        return null;
    }
    
    private String extractBase64(String content) {
        Matcher matcher = BASE64_PATTERN.matcher(content);
        if (matcher.find()) {
            return matcher.group(1).trim(); 
        }
        return null;
    }

    // --- Método Main (Ponto de entrada) ---
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ProcessadorZeroPapel());
    }
}