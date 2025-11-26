
## 🎯 Objetivo de Desenvolvimento Sustentável (ODS)

O projeto **Zero Papel** alinha-se prioritariamente com os seguintes Objetivos de Desenvolvimento Sustentável, devido ao seu foco na eficiência econômica e na redução de resíduos:

* **ODS 8: Trabalho Decente e Crescimento Econômico** 📈
    * **Justificativa:** A solução acelera o ciclo de comprovação e faturamento, garantindo um processo mais rápido e transparente para o pagamento de motoristas terceirizados.
        Isso fomenta o **crescimento das microempresas e o trabalho decente** ao reduzir a burocracia e o tempo de espera por receita.

* **ODS 12: Consumo e Produção Responsáveis** ♻️
    * **Justificativa:** O projeto promove a sustentabilidade ao alcançar a meta de **"Papel Zero"**.
    Ao digitalizar integralmente o comprovante de entrega e o arquivamento (eliminando caixas de papelão e impressão),
    a transportadora reduz o consumo de recursos naturais e a geração de resíduos.

---

## 📝 Descrição da Atividade

O projeto consistiu no diagnóstico de um problema administrativo no setor de transportes terceirizados e no desenvolvimento de uma solução de *back-office* utilizando a linguagem **Java**.

### Fases de Desenvolvimento e Atividades Realizadas:

1.  **Diagnóstico e Levantamento de Requisitos:**
    * Identificação da situação-problema: Ineficiência, alto custo e risco de perda associados ao arquivamento manual de CT-e (Comprovantes de Entrega) e a dependência de métodos não estruturados (fotos via WhatsApp).
    * Definição do escopo: Desenvolver um **Processador de Comprovantes** robusto em Java para automatizar a auditoria e o arquivamento digital.

2.  **Desenvolvimento da Aplicação Java (Zero Papel):**
    * **Criação da Interface Gráfica (GUI):** Utilização da biblioteca **Swing** em Java para construir uma interface desktop intuitiva, composta por uma área de input para o corpo do e-mail e botões de controle.
    * **Implementação da Lógica de Extração (Regex):** Desenvolvimento de expressões regulares (**Regex**) em Java para analisar o texto do e-mail e extrair dados críticos de auditoria (CT-e, Placa, CPF do Recebedor, Coordenadas GPS).
    * **Decodificação Base64:** Implementação da API **`java.util.Base64`** com correções para limpar caracteres inválidos (`replaceAll`) e ajustar o *padding* da string, garantindo a decodificação da assinatura digital em uma imagem.
    * **Lógica de Arquivamento (I/O):** Utilização de APIs de I/O em Java (`FileOutputStream`, `FileWriter`) para salvar o comprovante como dois arquivos padronizados: um **PNG** (a assinatura decodificada) e um **TXT** (o registro dos metadados).

3.  **Testes e Validação:**
    * Testes unitários e de integração utilizando modelos de e-mail simulados para validar a precisão do Regex e a integridade da decodificação Base64.
    * Validação do fluxo de trabalho: Demonstração da ferramenta ao setor financeiro, provando que o processo manual de arquivamento foi substituído por uma operação de "copiar, colar e clicar".

**Explicação Final de Evidência: Zero Papel (MVP e Plano de Implementação Futura)**

O projeto Zero Papel foi concebido e implementado como um Produto Mínimo Viável (MVP), focado em provar a viabilidade técnica da digitalização e automação do processo de arquivamento.
Ele estabelece o motor de auditoria central, sendo intencionalmente incompleto em sua capacidade de coleta de dados para justificar futuras fases de desenvolvimento.

---

## 📋 Conclusões

O projeto **Zero Papel** cumpriu integralmente seus objetivos, resultando em um sistema de processamento de *back-office* eficiente, desenvolvido integralmente em Java.

### Principais Resultados:

* **Automação e Eficiência Financeira:** O tempo gasto na conferência e arquivamento de um comprovante foi reduzido de minutos (processo manual) para segundos (processo automatizado em Java). Isso acelera o ciclo de faturamento e o pagamento dos terceirizados.
* **Segurança da Informação:** A digitalização com extração de CPF e Geolocalização fornece uma trilha de auditoria mais robusta do que o antigo comprovante em papel.
* **Dominância da Linguagem Java:** Demonstrou-se a capacidade de utilizar Java para resolver problemas empresariais de I/O e processamento de dados complexos, aplicando conceitos avançados de **manipulação de strings (Regex)** e **tratamento de formatos binários (Base64)**.

O projeto estabelece uma base tecnológica sólida na transportadora. A próxima etapa natural seria a evolução do módulo Java para se comunicar diretamente com uma caixa de e-mail (via JavaMail API) para eliminar até mesmo a etapa manual de "copiar e colar".
