package com.sistema.ia.gerador;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class GeradorMensagens {
    
    private static final Logger logger = LoggerFactory.getLogger(GeradorMensagens.class);
    private static final String EXCHANGE_NAME = "image_processing_exchange";
    private static final String ROUTING_KEY_FACE = "face";
    private static final String ROUTING_KEY_TEAM = "team";
    
    private final ConnectionFactory factory;
    private final ObjectMapper objectMapper;
    private final Random random;
    private final List<File> faceImages;
    private final List<File> teamImages;
    
    public GeradorMensagens() {
        this.factory = new ConnectionFactory();
        this.objectMapper = new ObjectMapper();
        this.random = new Random();
        this.faceImages = new ArrayList<>();
        this.teamImages = new ArrayList<>();
        
        // Configurar conexão RabbitMQ
        factory.setHost(System.getenv().getOrDefault("RABBITMQ_HOST", "localhost"));
        factory.setPort(Integer.parseInt(System.getenv().getOrDefault("RABBITMQ_PORT", "5672")));
        factory.setUsername(System.getenv().getOrDefault("RABBITMQ_USER", "admin"));
        factory.setPassword(System.getenv().getOrDefault("RABBITMQ_PASS", "admin123"));
        
        // Carregar arquivos de imagem
        carregarImagens();
    }
    
    private void carregarImagens() {
        String imagensPath = "/shared-images";
        
        // Carregar imagens de faces (happy e sad)
        Path facesHappyPath = Paths.get(imagensPath, "faces", "happy");
        Path facesSadPath = Paths.get(imagensPath, "faces", "sad");
        
        if (Files.exists(facesHappyPath)) {
            try {
                Files.list(facesHappyPath)
                     .filter(Files::isRegularFile)
                     .filter(path -> isImageFile(path.toString()))
                     .forEach(path -> faceImages.add(path.toFile()));
                logger.info("Carregadas {} imagens de faces felizes", faceImages.size());
            } catch (IOException e) {
                logger.error("Erro ao carregar imagens de faces felizes", e);
            }
        }
        
        if (Files.exists(facesSadPath)) {
            try {
                Files.list(facesSadPath)
                     .filter(Files::isRegularFile)
                     .filter(path -> isImageFile(path.toString()))
                     .forEach(path -> faceImages.add(path.toFile()));
                logger.info("Total de imagens de faces: {}", faceImages.size());
            } catch (IOException e) {
                logger.error("Erro ao carregar imagens de faces tristes", e);
            }
        }
        
        // Carregar imagens de times
        Path teamsPath = Paths.get(imagensPath, "teams");
        if (Files.exists(teamsPath)) {
            try {
                Files.list(teamsPath)
                     .filter(Files::isRegularFile)
                     .filter(path -> isImageFile(path.toString()))
                     .forEach(path -> teamImages.add(path.toFile()));
                logger.info("Carregadas {} imagens de teams", teamImages.size());
            } catch (IOException e) {
                logger.error("Erro ao carregar imagens de teams", e);
            }
        }
        
        // Se não há imagens, gerar dados simulados
        if (faceImages.isEmpty() && teamImages.isEmpty()) {
            System.out.println("AVISO: Nenhuma imagem encontrada, usando dados simulados");
        } else {
            System.out.println("Imagens carregadas - Faces: " + faceImages.size() + ", Teams: " + teamImages.size());
        }
    }
    
    private boolean isImageFile(String fileName) {
        String lower = fileName.toLowerCase();
        return lower.endsWith(".jpg") || lower.endsWith(".jpeg") || 
               lower.endsWith(".png") || lower.endsWith(".gif");
    }
    
    public void iniciarGeracao() {
        try (Connection connection = factory.newConnection();
             Channel channel = connection.createChannel()) {
            
            System.out.println("Conectado ao RabbitMQ!");
            
            // Declarar exchange
            channel.exchangeDeclare(EXCHANGE_NAME, "topic", true);
            System.out.println("Exchange declarado: " + EXCHANGE_NAME);
            
            System.out.println("=== GERADOR INICIADO - Enviando exatamente 5 mensagens por segundo ===");
            
            ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(2);
            
            // Agendar envio de mensagens a cada 200ms (exatamente 5 msgs/segundo)
            scheduler.scheduleAtFixedRate(() -> {
                try {
                    enviarMensagem(channel);
                } catch (Exception e) {
                    System.err.println("Erro ao enviar mensagem: " + e.getMessage());
                }
            }, 0, 200, TimeUnit.MILLISECONDS);
            
            // Manter o programa rodando
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                System.out.println("Parando gerador de mensagens...");
                scheduler.shutdown();
            }));
            
            // Aguardar indefinidamente
            Thread.currentThread().join();
            
        } catch (Exception e) {
            System.err.println("Erro fatal no gerador de mensagens: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void enviarMensagem(Channel channel) throws Exception {
        // Decidir tipo de mensagem (50% face, 50% team)
        boolean isFace = random.nextBoolean();
        String tipo = isFace ? "face" : "team";
        String routingKey = isFace ? ROUTING_KEY_FACE : ROUTING_KEY_TEAM;
        
        MensagemImagem mensagem;
        
        if (isFace && !faceImages.isEmpty()) {
            // Enviar imagem real de face
            File faceFile = faceImages.get(random.nextInt(faceImages.size()));
            byte[] imageData = Files.readAllBytes(faceFile.toPath());
            mensagem = new MensagemImagem(
                UUID.randomUUID().toString(),
                "face",
                faceFile.getName(),
                imageData
            );
        } else if (!isFace && !teamImages.isEmpty()) {
            // Enviar imagem real de team
            File teamFile = teamImages.get(random.nextInt(teamImages.size()));
            byte[] imageData = Files.readAllBytes(teamFile.toPath());
            mensagem = new MensagemImagem(
                UUID.randomUUID().toString(),
                "team",
                teamFile.getName(),
                imageData
            );
        } else {
            // Enviar dados simulados
            mensagem = new MensagemImagem(
                UUID.randomUUID().toString(),
                tipo,
                tipo + "_simulado_" + System.currentTimeMillis() + ".jpg",
                gerarDadosSimulados()
            );
        }
        
        // Converter para JSON
        byte[] messageBody = objectMapper.writeValueAsBytes(mensagem);
        
        // Publicar mensagem
        channel.basicPublish(EXCHANGE_NAME, routingKey, null, messageBody);
        
        System.out.println("[" + System.currentTimeMillis() + "] Mensagem enviada: " + routingKey + " -> " + mensagem.getId());
    }
    
    private byte[] gerarDadosSimulados() {
        // Gerar dados binários simulados para imagem
        byte[] dados = new byte[1024 + random.nextInt(2048)]; // Entre 1KB e 3KB
        random.nextBytes(dados);
        return dados;
    }
    
    public static void main(String[] args) {
        System.out.println("=== INICIANDO GERADOR DE MENSAGENS ===");
        
        // Aguardar RabbitMQ estar pronto
        try {
            System.out.println("Aguardando RabbitMQ inicializar...");
            Thread.sleep(10000); // 10 segundos
            System.out.println("Iniciando gerador após aguardar RabbitMQ...");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        GeradorMensagens gerador = new GeradorMensagens();
        gerador.iniciarGeracao();
    }
}