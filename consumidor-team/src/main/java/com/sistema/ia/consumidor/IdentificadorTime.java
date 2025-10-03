package com.sistema.ia.consumidor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class IdentificadorTime {
    
    private static final Logger logger = LoggerFactory.getLogger(IdentificadorTime.class);
    private final Random random;
    private final List<String> timesConhecidos;
    
    public IdentificadorTime() {
        this.random = new Random();
        this.timesConhecidos = Arrays.asList(
            // ========== TIMES BRASILEIROS - SÉRIE A ==========
            "Flamengo", "Palmeiras", "Corinthians", "São Paulo", "Santos", "Vasco da Gama",
            "Grêmio", "Internacional", "Atlético Mineiro", "Cruzeiro", "Botafogo", "Fluminense",
            "Athletico Paranaense", "Coritiba", "Bahia", "Vitória", "Sport Recife", "Náutico",
            "Ceará", "Fortaleza", "Goiás", "Vila Nova", "Cuiabá", "Red Bull Bragantino",
            
            // ========== TIMES BRASILEIROS - SÉRIE B E TRADICIONAIS ==========
            "Chapecoense", "Criciúma", "Avaí", "Figueirense", "Joinville", "Ponte Preta",
            "Guarani", "XV de Piracicaba", "Ituano", "Mirassol", "Novorizontino", "Oeste",
            "ABC", "América-RN", "Campinense", "Treze", "CSA", "CRB", "Sampaio Corrêa",
            "Moto Club", "Remo", "Paysandu", "Tuna Luso", "Rio Branco-AC", "Atlético-GO",
            
            // ========== TIMES INTERNACIONAIS - EUROPA ==========
            "Real Madrid", "Barcelona", "Manchester United", "Manchester City", "Liverpool",
            "Chelsea", "Arsenal", "Tottenham", "Bayern München", "Borussia Dortmund",
            "Paris Saint-Germain", "Olympique Marseille", "AC Milan", "Inter Milan", "Juventus",
            "AS Roma", "Napoli", "Lazio", "Ajax", "PSV Eindhoven", "Benfica", "Porto",
            "Sporting CP", "Atlético Madrid", "Valencia", "Sevilla", "Athletic Bilbao",
            
            // ========== TIMES INTERNACIONAIS - AMÉRICA DO SUL ==========
            "Boca Juniors", "River Plate", "Racing Club", "Independiente", "San Lorenzo",
            "Estudiantes", "Lanús", "Vélez Sarsfield", "Colo-Colo", "Universidad de Chile",
            "Universidad Católica", "Cobreloa", "Peñarol", "Nacional", "Olimpia", "Cerro Porteño",
            "Libertad", "Guaraní", "LDU Quito", "Barcelona SC", "Emelec", "El Nacional",
            "Millonarios", "América de Cali", "Nacional", "Independiente Santa Fe",
            
            // ========== TIMES INTERNACIONAIS - AMÉRICA DO NORTE ==========
            "LA Galaxy", "New York City FC", "Atlanta United", "Seattle Sounders", "Portland Timbers",
            "Toronto FC", "Montreal Impact", "Vancouver Whitecaps", "Club América", "Chivas Guadalajara",
            "Cruz Azul", "Pumas UNAM", "Tigres UANL", "Monterrey", "Santos Laguna",
            
            // ========== TIMES AFRICANOS E ASIÁTICOS ==========
            "Al Ahly", "Zamalek", "Wydad Casablanca", "Raja Casablanca", "Esperance Tunis",
            "Kaizer Chiefs", "Orlando Pirates", "Mamelodi Sundowns", "Al Hilal", "Al Nassr",
            "Persepolis", "Esteghlal", "Urawa Red Diamonds", "Kashima Antlers", "Guangzhou Evergrande"
        );
        
        logger.info("Identificador de times inicializado com {} times conhecidos", timesConhecidos.size());
    }
    
    public ResultadoIdentificacao identificarTime(MensagemImagem mensagem) {
        // Simular extração de características da imagem do brasão
        double[] caracteristicas = extrairCaracteristicas(mensagem.getDados());
        
        // Simular tempo de processamento (para que a fila encha)
        try {
            Thread.sleep(3000 + random.nextInt(4000)); // 3-7 segundos
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        // Fazer predição baseada nas características
        int predicao = analisarCaracteristicas(caracteristicas);
        
        String timeIdentificado;
        double confianca = 0.65 + random.nextDouble() * 0.35; // 65-100%
        
        if (predicao >= 0 && predicao < timesConhecidos.size()) {
            timeIdentificado = timesConhecidos.get(predicao);
        } else {
            timeIdentificado = "TIME_DESCONHECIDO";
            confianca = 0.3 + random.nextDouble() * 0.4; // Menor confiança para desconhecidos
        }
        
        return new ResultadoIdentificacao(mensagem.getId(), timeIdentificado, confianca, caracteristicas);
    }
    
    private int analisarCaracteristicas(double[] caracteristicas) {
        // Algoritmo simples baseado em características
        // Em uma implementação real, aqui seria usado o modelo SVM treinado
        double hash = 0;
        for (int i = 0; i < caracteristicas.length; i++) {
            hash += caracteristicas[i] * (i + 1);
        }
        
        // Mapear para um dos times conhecidos
        return Math.abs((int)hash) % timesConhecidos.size();
    }
    
    private double[] extrairCaracteristicas(byte[] imagemDados) {
        // Extrair características REAIS de um brasão/logo de time
        double[] caracteristicas = new double[15];
        
        // Característica 0: Tamanho da imagem
        caracteristicas[0] = imagemDados.length / 1024.0; // KB
        
        // Características 1-3: Análise de distribuição de cores (RGB simulado)
        int[] histogramR = new int[4]; // 0-63, 64-127, 128-191, 192-255
        int[] histogramG = new int[4];
        int[] histogramB = new int[4];
        
        for (int i = 0; i < imagemDados.length; i += 3) {
            if (i + 2 < imagemDados.length) {
                int r = (imagemDados[i] & 0xFF) / 64;
                int g = (imagemDados[i + 1] & 0xFF) / 64;
                int b = (imagemDados[i + 2] & 0xFF) / 64;
                if (r >= 4) r = 3;
                if (g >= 4) g = 3;
                if (b >= 4) b = 3;
                histogramR[r]++;
                histogramG[g]++;
                histogramB[b]++;
            }
        }
        
        // Cores predominantes (normalizado)
        caracteristicas[1] = encontrarCorPredominante(histogramR) / 3.0;
        caracteristicas[2] = encontrarCorPredominante(histogramG) / 3.0;
        caracteristicas[3] = encontrarCorPredominante(histogramB) / 3.0;
        
        // Características 4-6: Complexidade da imagem
        caracteristicas[4] = calcularEntropia(imagemDados);
        caracteristicas[5] = contarTransicoes(imagemDados) / (double) imagemDados.length;
        caracteristicas[6] = calcularVariancia(imagemDados);
        
        // Características 7-9: Detecção de padrões específicos
        caracteristicas[7] = detectarPadrao(imagemDados, new byte[]{(byte)0xFF, (byte)0xD8}) ? 1.0 : 0.0; // JPEG
        caracteristicas[8] = detectarPadrao(imagemDados, new byte[]{(byte)0x89, 0x50, 0x4E, 0x47}) ? 1.0 : 0.0; // PNG
        caracteristicas[9] = contarSequenciasRepetidas(imagemDados) / (double) imagemDados.length;
        
        // Características 10-12: Análise de simetria (baseada em bytes)
        caracteristicas[10] = analisarSimetria(imagemDados);
        caracteristicas[11] = calcularMedia(imagemDados);
        caracteristicas[12] = detectarBordas(imagemDados);
        
        // Características 13-14: Assinatura única da imagem
        caracteristicas[13] = calcularChecksum(imagemDados) / 1000000.0;
        caracteristicas[14] = analisarEstrutura(imagemDados);
        
        return caracteristicas;
    }
    
    private int encontrarCorPredominante(int[] histogram) {
        int max = 0;
        int indice = 0;
        for (int i = 0; i < histogram.length; i++) {
            if (histogram[i] > max) {
                max = histogram[i];
                indice = i;
            }
        }
        return indice;
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
        return entropia / 8.0;
    }
    
    private int contarTransicoes(byte[] dados) {
        int transicoes = 0;
        for (int i = 1; i < dados.length; i++) {
            if (dados[i] != dados[i-1]) transicoes++;
        }
        return transicoes;
    }
    
    private double calcularVariancia(byte[] dados) {
        double media = calcularMedia(dados) * 255.0;
        double soma = 0;
        for (byte b : dados) {
            double diff = (b & 0xFF) - media;
            soma += diff * diff;
        }
        return Math.sqrt(soma / dados.length) / 255.0;
    }
    
    private double calcularMedia(byte[] dados) {
        long soma = 0;
        for (byte b : dados) {
            soma += (b & 0xFF);
        }
        return soma / (double) dados.length / 255.0;
    }
    
    private boolean detectarPadrao(byte[] dados, byte[] padrao) {
        if (dados.length < padrao.length) return false;
        for (int i = 0; i < padrao.length; i++) {
            if (dados[i] != padrao[i]) return false;
        }
        return true;
    }
    
    private int contarSequenciasRepetidas(byte[] dados) {
        int sequencias = 0;
        for (int i = 0; i < dados.length - 1; i++) {
            if (dados[i] == dados[i + 1]) sequencias++;
        }
        return sequencias;
    }
    
    private double analisarSimetria(byte[] dados) {
        int matches = 0;
        int meio = dados.length / 2;
        for (int i = 0; i < meio; i++) {
            if (dados[i] == dados[dados.length - 1 - i]) matches++;
        }
        return (double) matches / meio;
    }
    
    private double detectarBordas(byte[] dados) {
        int bordas = 0;
        for (int i = 1; i < dados.length - 1; i++) {
            int diff = Math.abs((dados[i-1] & 0xFF) - (dados[i+1] & 0xFF));
            if (diff > 50) bordas++; // Threshold para detecção de borda
        }
        return (double) bordas / dados.length;
    }
    
    private long calcularChecksum(byte[] dados) {
        long checksum = 0;
        for (int i = 0; i < dados.length; i++) {
            checksum += (dados[i] & 0xFF) * (i + 1);
        }
        return Math.abs(checksum);
    }
    
    private double analisarEstrutura(byte[] dados) {
        // Análise da estrutura de dados da imagem
        int zeros = 0;
        int alta_freq = 0;
        for (byte b : dados) {
            if (b == 0) zeros++;
            if ((b & 0xFF) > 200) alta_freq++;
        }
        return ((double) zeros + alta_freq) / dados.length;
    }
    
    public static class ResultadoIdentificacao {
        private final String imagemId;
        private final String timeIdentificado;
        private final double confianca;
        private final double[] caracteristicas;
        private final long timestamp;
        
        public ResultadoIdentificacao(String imagemId, String timeIdentificado, 
                                    double confianca, double[] caracteristicas) {
            this.imagemId = imagemId;
            this.timeIdentificado = timeIdentificado;
            this.confianca = confianca;
            this.caracteristicas = caracteristicas;
            this.timestamp = System.currentTimeMillis();
        }
        
        public String getImagemId() { return imagemId; }
        public String getTimeIdentificado() { return timeIdentificado; }
        public double getConfianca() { return confianca; }
        public double[] getCaracteristicas() { return caracteristicas; }
        public long getTimestamp() { return timestamp; }
        
        @Override
        public String toString() {
            return String.format("Identificação[%s]: %s (%.1f%% confiança)", 
                               imagemId, timeIdentificado, confianca * 100);
        }
    }
}