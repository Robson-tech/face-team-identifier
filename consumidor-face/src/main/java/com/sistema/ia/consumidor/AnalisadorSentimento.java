package com.sistema.ia.consumidor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Random;

public class AnalisadorSentimento {
    
    private static final Logger logger = LoggerFactory.getLogger(AnalisadorSentimento.class);
    private final Random random;
    
    public AnalisadorSentimento() {
        this.random = new Random();
        logger.info("Analisador de sentimento inicializado com algoritmo baseado em características");
    }
    
    public ResultadoAnalise analisarSentimento(MensagemImagem mensagem) {
        // Extrair características completas da imagem
        double[] caracteristicas = extrairCaracteristicas(mensagem.getDados());
        
        // Simular tempo de processamento (para que a fila encha)
        try {
            Thread.sleep(2000 + random.nextInt(3000)); // 2-5 segundos
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        // Fazer predição avançada baseada nas características
        String sentimento = classificarSentimentoAvancado(caracteristicas);
        double confianca = calcularConfianca(caracteristicas, sentimento);
        
        return new ResultadoAnalise(mensagem.getId(), sentimento, confianca, caracteristicas);
    }
    
    private String classificarSentimentoAvancado(double[] caracteristicas) {
        // Lista completa de sentimentos detectáveis
        String[] sentimentos = {
            "ALEGRIA", "TRISTEZA", "RAIVA", "MEDO", "SURPRESA", "NOJO", "NEUTRO",
            "CONTEMPLATIVO", "MELANCOLIA", "EUFORIA", "ANSIEDADE", "SERENIDADE",
            "CONFUSO", "DETERMINADO", "CANSADO", "ANIMADO", "PREOCUPADO", "SATISFEITO"
        };
        
        // Algoritmo avançado de classificação baseado em múltiplas características
        double[] scores = new double[sentimentos.length];
        
        // Análise baseada em brilho e contraste
        double brilho = caracteristicas[26]; // Brilho médio
        double contraste = caracteristicas[27]; // Contraste
        double energia = caracteristicas[31]; // Energia
        
        // ALEGRIA: Alto brilho, alto contraste, alta energia
        scores[0] = (brilho * 0.4) + (contraste * 0.3) + (energia * 0.3);
        
        // TRISTEZA: Baixo brilho, baixo contraste
        scores[1] = (1.0 - brilho) * 0.5 + (1.0 - contraste) * 0.3 + (1.0 - energia) * 0.2;
        
        // RAIVA: Alto contraste, alta detecção de bordas
        scores[2] = (contraste * 0.4) + (caracteristicas[33] * 0.3) + (caracteristicas[35] * 0.3);
        
        // MEDO: Características de textura irregulares
        scores[3] = (caracteristicas[32] * 0.3) + (caracteristicas[30] * 0.4) + (1.0 - caracteristicas[31] * 0.3);
        
        // SURPRESA: Alta variação, muitas bordas
        scores[4] = (caracteristicas[29] * 0.4) + (caracteristicas[33] * 0.3) + (caracteristicas[36] * 0.3);
        
        // NOJO: Características de textura específicas
        scores[5] = (caracteristicas[32] * 0.5) + ((1.0 - brilho) * 0.3) + (caracteristicas[30] * 0.2);
        
        // NEUTRO: Características equilibradas
        scores[6] = 1.0 - Math.abs(brilho - 0.5) - Math.abs(contraste - 0.5);
        
        // CONTEMPLATIVO: Baixa energia, baixo contraste, médio brilho
        scores[7] = (Math.abs(brilho - 0.5) < 0.2 ? 0.4 : 0.0) + 
                   ((1.0 - energia) * 0.3) + ((1.0 - contraste) * 0.3);
        
        // MELANCOLIA: Muito baixo brilho, baixa energia
        scores[8] = ((1.0 - brilho) * 0.6) + ((1.0 - energia) * 0.4);
        
        // EUFORIA: Muito alto brilho, muito alta energia
        scores[9] = (brilho > 0.7 ? brilho * 0.5 : 0.0) + (energia > 0.6 ? energia * 0.5 : 0.0);
        
        // ANSIEDADE: Alta rugosidade, muitas transições
        scores[10] = (caracteristicas[32] * 0.4) + (caracteristicas[25] * 0.3) + (caracteristicas[35] * 0.3);
        
        // SERENIDADE: Baixa rugosidade, alta homogeneidade
        scores[11] = (caracteristicas[30] * 0.5) + ((1.0 - caracteristicas[32]) * 0.3) + 
                    ((1.0 - caracteristicas[25]) * 0.2);
        
        // CONFUSO: Características inconsistentes
        scores[12] = Math.abs(brilho - contraste) + Math.abs(energia - caracteristicas[30]);
        
        // DETERMINADO: Alto contraste, bordas definidas
        scores[13] = (contraste * 0.4) + (caracteristicas[33] * 0.4) + (energia * 0.2);
        
        // CANSADO: Baixa energia, baixo contraste
        scores[14] = ((1.0 - energia) * 0.5) + ((1.0 - contraste) * 0.3) + ((1.0 - brilho) * 0.2);
        
        // ANIMADO: Alta energia, alto brilho
        scores[15] = (energia * 0.5) + (brilho * 0.3) + (contraste * 0.2);
        
        // PREOCUPADO: Características de ansiedade + baixo brilho
        scores[16] = (caracteristicas[32] * 0.3) + ((1.0 - brilho) * 0.4) + (caracteristicas[25] * 0.3);
        
        // SATISFEITO: Características equilibradas com tendência positiva
        scores[17] = (brilho * 0.3) + (caracteristicas[30] * 0.4) + ((1.0 - caracteristicas[32]) * 0.3);
        
        // Encontrar o sentimento com maior score
        int melhorIndice = 0;
        double melhorScore = scores[0];
        
        for (int i = 1; i < scores.length; i++) {
            if (scores[i] > melhorScore) {
                melhorScore = scores[i];
                melhorIndice = i;
            }
        }
        
        return sentimentos[melhorIndice];
    }
    
    private double calcularConfianca(double[] caracteristicas, String sentimento) {
        // Calcular confiança baseada na consistência das características
        double confiancaBase = 0.6; // Base mínima
        
        // Adicionar confiança baseada na clareza das características
        double brilho = caracteristicas[26];
        double contraste = caracteristicas[27];
        double energia = caracteristicas[31];
        
        // Características claras aumentam a confiança
        if (contraste > 0.6) confiancaBase += 0.15;
        if (energia > 0.5 || energia < 0.3) confiancaBase += 0.1; // Extremos são mais confiáveis
        if (brilho > 0.7 || brilho < 0.3) confiancaBase += 0.1;
        
        // Adicionar variação aleatória pequena
        confiancaBase += random.nextDouble() * 0.1;
        
        return Math.min(confiancaBase, 0.98); // Máximo 98%
    }
    
    private double[] extrairCaracteristicas(byte[] imagemDados) {
        // Extrair TODAS as características visuais possíveis da imagem
        double[] caracteristicas = new double[50]; // Expandido para 50 características
        int index = 0;
        
        // ========== CARACTERÍSTICAS BÁSICAS ==========
        caracteristicas[index++] = imagemDados.length / 1024.0; // Tamanho em KB
        caracteristicas[index++] = detectarFormato(imagemDados); // 0=JPEG, 1=PNG, 2=GIF, 3=Outros
        
        // ========== ANÁLISE DE CORES DETALHADA ==========
        double[][] histogramRGB = calcularHistogramaRGB(imagemDados);
        
        // Histograma RGB (8 bins por canal = 24 características)
        for (int canal = 0; canal < 3; canal++) {
            for (int bin = 0; bin < 8; bin++) {
                caracteristicas[index++] = histogramRGB[canal][bin];
            }
        }
        
        // Estatísticas de cor
        caracteristicas[index++] = calcularBrilhoMedio(imagemDados);
        caracteristicas[index++] = calcularContraste(imagemDados);
        caracteristicas[index++] = calcularSaturacao(imagemDados);
        
        // ========== ANÁLISE DE TEXTURA ==========
        caracteristicas[index++] = calcularEntropia(imagemDados);
        caracteristicas[index++] = calcularVariancia(imagemDados);
        caracteristicas[index++] = calcularHomogeneidade(imagemDados);
        caracteristicas[index++] = calcularEnergia(imagemDados);
        caracteristicas[index++] = calcularRugosidade(imagemDados);
        
        // ========== ANÁLISE DE BORDAS E GRADIENTES ==========
        caracteristicas[index++] = detectarBordasSobel(imagemDados);
        caracteristicas[index++] = detectarBordasLaplaciano(imagemDados);
        caracteristicas[index++] = calcularGradienteMagnitude(imagemDados);
        caracteristicas[index++] = calcularDirecaoGradiente(imagemDados);
        
        // ========== ANÁLISE DE FORMAS ==========
        caracteristicas[index++] = detectarLinhasHorizontais(imagemDados);
        caracteristicas[index++] = detectarLinhasVerticais(imagemDados);
        caracteristicas[index++] = detectarLinhasDiagonais(imagemDados);
        caracteristicas[index++] = detectarCantos(imagemDados);
        caracteristicas[index++] = analisarSimetria(imagemDados);
        caracteristicas[index++] = calcularCompacidade(imagemDados);
        
        // ========== ANÁLISE FREQUENCIAL ==========
        caracteristicas[index++] = analisarFrequenciaAlta(imagemDados);
        caracteristicas[index++] = analisarFrequenciaBaixa(imagemDados);
        caracteristicas[index++] = calcularEspectro(imagemDados);
        
        // ========== MOMENTOS ESTATÍSTICOS ==========
        caracteristicas[index++] = calcularMomento2(imagemDados); // Variância
        caracteristicas[index++] = calcularMomento3(imagemDados); // Assimetria
        caracteristicas[index++] = calcularMomento4(imagemDados); // Curtose
        
        return caracteristicas;
    }
    
    // ========== MÉTODOS DE ANÁLISE DE CORES ==========
    private double[][] calcularHistogramaRGB(byte[] dados) {
        double[][] histogram = new double[3][8]; // RGB x 8 bins
        
        for (int i = 0; i < dados.length - 2; i += 3) {
            if (i + 2 < dados.length) {
                int r = (dados[i] & 0xFF) / 32;     // 0-7
                int g = (dados[i + 1] & 0xFF) / 32; // 0-7
                int b = (dados[i + 2] & 0xFF) / 32; // 0-7
                
                if (r >= 8) r = 7;
                if (g >= 8) g = 7;
                if (b >= 8) b = 7;
                
                histogram[0][r]++;
                histogram[1][g]++;
                histogram[2][b]++;
            }
        }
        
        // Normalizar
        int totalPixels = dados.length / 3;
        for (int canal = 0; canal < 3; canal++) {
            for (int bin = 0; bin < 8; bin++) {
                histogram[canal][bin] /= totalPixels;
            }
        }
        
        return histogram;
    }
    
    private double calcularBrilhoMedio(byte[] dados) {
        long soma = 0;
        for (byte b : dados) {
            soma += (b & 0xFF);
        }
        return soma / (double) dados.length / 255.0;
    }
    
    private double calcularContraste(byte[] dados) {
        double media = calcularBrilhoMedio(dados) * 255.0;
        double soma = 0;
        for (byte b : dados) {
            double diff = (b & 0xFF) - media;
            soma += diff * diff;
        }
        return Math.sqrt(soma / dados.length) / 255.0;
    }
    
    private double calcularSaturacao(byte[] dados) {
        double maxDiff = 0;
        for (int i = 0; i < dados.length - 2; i += 3) {
            if (i + 2 < dados.length) {
                int r = dados[i] & 0xFF;
                int g = dados[i + 1] & 0xFF;
                int b = dados[i + 2] & 0xFF;
                
                int max = Math.max(Math.max(r, g), b);
                int min = Math.min(Math.min(r, g), b);
                maxDiff += (max - min);
            }
        }
        return maxDiff / (dados.length / 3.0) / 255.0;
    }
    
    // ========== MÉTODOS DE ANÁLISE DE TEXTURA ==========
    private double calcularHomogeneidade(byte[] dados) {
        double homogeneidade = 0;
        for (int i = 1; i < dados.length; i++) {
            int diff = Math.abs((dados[i] & 0xFF) - (dados[i-1] & 0xFF));
            homogeneidade += 1.0 / (1.0 + diff);
        }
        return homogeneidade / dados.length;
    }
    
    private double calcularEnergia(byte[] dados) {
        double energia = 0;
        for (byte b : dados) {
            double valor = (b & 0xFF) / 255.0;
            energia += valor * valor;
        }
        return energia / dados.length;
    }
    
    private double calcularRugosidade(byte[] dados) {
        double rugosidade = 0;
        for (int i = 2; i < dados.length; i++) {
            int val1 = dados[i-2] & 0xFF;
            int val2 = dados[i-1] & 0xFF;
            int val3 = dados[i] & 0xFF;
            
            // Segunda derivada aproximada
            int segundaDerivada = val1 - 2*val2 + val3;
            rugosidade += Math.abs(segundaDerivada);
        }
        return rugosidade / dados.length / 255.0;
    }
    
    // ========== MÉTODOS DE DETECÇÃO DE BORDAS ==========
    private double detectarBordasSobel(byte[] dados) {
        double bordas = 0;
        int width = (int) Math.sqrt(dados.length); // Assumir imagem quadrada
        
        for (int i = width + 1; i < dados.length - width - 1; i++) {
            // Operador Sobel horizontal
            int gx = -1*(dados[i-width-1] & 0xFF) + 1*(dados[i-width+1] & 0xFF) +
                     -2*(dados[i-1] & 0xFF) + 2*(dados[i+1] & 0xFF) +
                     -1*(dados[i+width-1] & 0xFF) + 1*(dados[i+width+1] & 0xFF);
            
            // Operador Sobel vertical
            int gy = -1*(dados[i-width-1] & 0xFF) + -2*(dados[i-width] & 0xFF) + -1*(dados[i-width+1] & 0xFF) +
                     1*(dados[i+width-1] & 0xFF) + 2*(dados[i+width] & 0xFF) + 1*(dados[i+width+1] & 0xFF);
            
            bordas += Math.sqrt(gx*gx + gy*gy);
        }
        
        return bordas / dados.length / 255.0;
    }
    
    private double detectarBordasLaplaciano(byte[] dados) {
        double bordas = 0;
        for (int i = 1; i < dados.length - 1; i++) {
            // Laplaciano 1D aproximado
            int laplaciano = (dados[i-1] & 0xFF) - 2*(dados[i] & 0xFF) + (dados[i+1] & 0xFF);
            bordas += Math.abs(laplaciano);
        }
        return bordas / dados.length / 255.0;
    }
    
    private double calcularGradienteMagnitude(byte[] dados) {
        double gradiente = 0;
        for (int i = 1; i < dados.length; i++) {
            int diff = Math.abs((dados[i] & 0xFF) - (dados[i-1] & 0xFF));
            gradiente += diff;
        }
        return gradiente / dados.length / 255.0;
    }
    
    private double calcularDirecaoGradiente(byte[] dados) {
        double direcao = 0;
        for (int i = 1; i < dados.length; i++) {
            int diff = (dados[i] & 0xFF) - (dados[i-1] & 0xFF);
            if (diff > 0) direcao += 1;
            else if (diff < 0) direcao -= 1;
        }
        return Math.abs(direcao) / dados.length;
    }
    
    // ========== MÉTODOS DE DETECÇÃO DE FORMAS ==========
    private double detectarLinhasHorizontais(byte[] dados) {
        int width = (int) Math.sqrt(dados.length);
        double linhas = 0;
        
        for (int row = 0; row < width - 1; row++) {
            double consistencia = 0;
            for (int col = 1; col < width; col++) {
                int pos1 = row * width + col - 1;
                int pos2 = row * width + col;
                if (pos1 >= 0 && pos2 < dados.length) {
                    int diff = Math.abs((dados[pos1] & 0xFF) - (dados[pos2] & 0xFF));
                    consistencia += (diff < 10) ? 1 : 0; // Threshold
                }
            }
            linhas += consistencia / width;
        }
        
        return linhas / width;
    }
    
    private double detectarLinhasVerticais(byte[] dados) {
        int width = (int) Math.sqrt(dados.length);
        double linhas = 0;
        
        for (int col = 0; col < width; col++) {
            double consistencia = 0;
            for (int row = 1; row < width; row++) {
                int pos1 = (row - 1) * width + col;
                int pos2 = row * width + col;
                if (pos1 >= 0 && pos2 < dados.length) {
                    int diff = Math.abs((dados[pos1] & 0xFF) - (dados[pos2] & 0xFF));
                    consistencia += (diff < 10) ? 1 : 0;
                }
            }
            linhas += consistencia / width;
        }
        
        return linhas / width;
    }
    
    private double detectarLinhasDiagonais(byte[] dados) {
        int width = (int) Math.sqrt(dados.length);
        double diagonais = 0;
        
        // Diagonal principal
        for (int i = 1; i < width; i++) {
            int pos1 = (i - 1) * width + (i - 1);
            int pos2 = i * width + i;
            if (pos1 >= 0 && pos2 < dados.length) {
                int diff = Math.abs((dados[pos1] & 0xFF) - (dados[pos2] & 0xFF));
                diagonais += (diff < 10) ? 1 : 0;
            }
        }
        
        return diagonais / width;
    }
    
    private double detectarCantos(byte[] dados) {
        double cantos = 0;
        int width = (int) Math.sqrt(dados.length);
        
        for (int i = width + 1; i < dados.length - width - 1; i++) {
            // Detector de cantos Harris simplificado
            int centro = dados[i] & 0xFF;
            int[] vizinhos = {
                dados[i-width-1] & 0xFF, dados[i-width] & 0xFF, dados[i-width+1] & 0xFF,
                dados[i-1] & 0xFF, dados[i+1] & 0xFF,
                dados[i+width-1] & 0xFF, dados[i+width] & 0xFF, dados[i+width+1] & 0xFF
            };
            
            double variacao = 0;
            for (int viz : vizinhos) {
                variacao += Math.abs(centro - viz);
            }
            
            if (variacao > 200) cantos++; // Threshold para canto
        }
        
        return cantos / dados.length;
    }
    
    private double calcularCompacidade(byte[] dados) {
        // Medida de quão compacta é a distribuição de intensidades
        double[] histogram = new double[256];
        for (byte b : dados) {
            histogram[b & 0xFF]++;
        }
        
        double entropia = 0;
        for (double freq : histogram) {
            if (freq > 0) {
                double p = freq / dados.length;
                entropia -= p * Math.log(p) / Math.log(2);
            }
        }
        
        return entropia / 8.0; // Normalizar
    }
    
    // ========== MÉTODOS DE ANÁLISE FREQUENCIAL ==========
    private double analisarFrequenciaAlta(byte[] dados) {
        double altaFreq = 0;
        for (int i = 1; i < dados.length; i++) {
            int diff = Math.abs((dados[i] & 0xFF) - (dados[i-1] & 0xFF));
            if (diff > 50) altaFreq++; // Mudanças bruscas = alta frequência
        }
        return altaFreq / dados.length;
    }
    
    private double analisarFrequenciaBaixa(byte[] dados) {
        double baixaFreq = 0;
        int janela = 5;
        
        for (int i = janela; i < dados.length - janela; i++) {
            double media = 0;
            for (int j = -janela; j <= janela; j++) {
                media += (dados[i + j] & 0xFF);
            }
            media /= (2 * janela + 1);
            
            double diff = Math.abs((dados[i] & 0xFF) - media);
            if (diff < 10) baixaFreq++; // Pouca variação = baixa frequência
        }
        
        return baixaFreq / dados.length;
    }
    
    private double calcularEspectro(byte[] dados) {
        // Análise espectral simplificada
        double energia = 0;
        for (int freq = 1; freq <= 10; freq++) {
            double component = 0;
            for (int i = 0; i < dados.length; i++) {
                component += (dados[i] & 0xFF) * Math.cos(2 * Math.PI * freq * i / dados.length);
            }
            energia += component * component;
        }
        return Math.sqrt(energia) / dados.length;
    }
    
    // ========== MOMENTOS ESTATÍSTICOS ==========
    private double calcularMomento2(byte[] dados) {
        double media = calcularBrilhoMedio(dados) * 255.0;
        double momento2 = 0;
        for (byte b : dados) {
            double diff = (b & 0xFF) - media;
            momento2 += diff * diff;
        }
        return momento2 / dados.length / (255.0 * 255.0);
    }
    
    private double calcularMomento3(byte[] dados) {
        double media = calcularBrilhoMedio(dados) * 255.0;
        double momento3 = 0;
        for (byte b : dados) {
            double diff = (b & 0xFF) - media;
            momento3 += diff * diff * diff;
        }
        return momento3 / dados.length / (255.0 * 255.0 * 255.0);
    }
    
    private double calcularMomento4(byte[] dados) {
        double media = calcularBrilhoMedio(dados) * 255.0;
        double momento4 = 0;
        for (byte b : dados) {
            double diff = (b & 0xFF) - media;
            momento4 += diff * diff * diff * diff;
        }
        return momento4 / dados.length / (255.0 * 255.0 * 255.0 * 255.0);
    }
    
    // ========== MÉTODOS AUXILIARES ==========
    private double detectarFormato(byte[] dados) {
        if (dados.length >= 2 && (dados[0] & 0xFF) == 0xFF && (dados[1] & 0xFF) == 0xD8) {
            return 0.0; // JPEG
        } else if (dados.length >= 4 && (dados[0] & 0xFF) == 0x89 && 
                   (dados[1] & 0xFF) == 0x50 && (dados[2] & 0xFF) == 0x4E && (dados[3] & 0xFF) == 0x47) {
            return 1.0; // PNG
        } else if (dados.length >= 3 && (dados[0] & 0xFF) == 0x47 && 
                   (dados[1] & 0xFF) == 0x49 && (dados[2] & 0xFF) == 0x46) {
            return 2.0; // GIF
        }
        return 3.0; // Outros
    }
    
    public static class ResultadoAnalise {
        private final String imagemId;
        private final String sentimento;
        private final double confianca;
        private final double[] caracteristicas;
        private final long timestamp;
        
        public ResultadoAnalise(String imagemId, String sentimento, double confianca, double[] caracteristicas) {
            this.imagemId = imagemId;
            this.sentimento = sentimento;
            this.confianca = confianca;
            this.caracteristicas = caracteristicas;
            this.timestamp = System.currentTimeMillis();
        }
        
        public String getImagemId() { return imagemId; }
        public String getSentimento() { return sentimento; }
        public double getConfianca() { return confianca; }
        public double[] getCaracteristicas() { return caracteristicas; }
        public long getTimestamp() { return timestamp; }
        
        @Override
        public String toString() {
            return String.format("Análise[%s]: %s (%.1f%% confiança)", 
                               imagemId, sentimento, confianca * 100);
        }
    }
    
    // ========== MÉTODOS AUXILIARES ADICIONAIS ==========
    
    private double analisarSimetria(byte[] dados) {
        int matches = 0;
        int meio = dados.length / 2;
        for (int i = 0; i < meio; i++) {
            if (dados[i] == dados[dados.length - 1 - i]) matches++;
        }
        return (double) matches / meio;
    }
    
    private double calcularEntropia(byte[] dados) {
        int[] freq = new int[256];
        for (byte b : dados) {
            freq[b & 0xFF]++;
        }
        
        double entropia = 0.0;
        for (int f : freq) {
            if (f > 0) {
                double p = (double) f / dados.length;
                entropia -= p * Math.log(p) / Math.log(2);
            }
        }
        return entropia / 8.0; // Normalizar
    }
    
    private double calcularVariancia(byte[] dados) {
        double media = calcularBrilhoMedio(dados) * 255.0;
        double soma = 0;
        for (byte b : dados) {
            double diff = (b & 0xFF) - media;
            soma += diff * diff;
        }
        return Math.sqrt(soma / dados.length) / 255.0; // Normalizar
    }
}