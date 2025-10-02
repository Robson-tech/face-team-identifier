package com.sistema.ia.consumidor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class ConsumidorTeam {
    
    private static final Logger logger = LoggerFactory.getLogger(ConsumidorTeam.class);
    private static final String EXCHANGE_NAME = "image_processing_exchange";
    private static final String QUEUE_NAME = "team_identification_queue";
    private static final String ROUTING_KEY = "team";
    
    private final ConnectionFactory factory;
    private final ObjectMapper objectMapper;
    private final IdentificadorTimeSmile identificador;
    
    public ConsumidorTeam() {
        this.factory = new ConnectionFactory();
        this.objectMapper = new ObjectMapper();
        this.identificador = new IdentificadorTimeSmile();
        
        // Configurar conexão RabbitMQ
        factory.setHost(System.getenv().getOrDefault("RABBITMQ_HOST", "localhost"));
        factory.setPort(Integer.parseInt(System.getenv().getOrDefault("RABBITMQ_PORT", "5672")));
        factory.setUsername(System.getenv().getOrDefault("RABBITMQ_USER", "admin"));
        factory.setPassword(System.getenv().getOrDefault("RABBITMQ_PASS", "admin123"));
    }
    
    public void iniciarConsumo() {
        try (Connection connection = factory.newConnection();
             Channel channel = connection.createChannel()) {
            
            // Declarar exchange e queue
            channel.exchangeDeclare(EXCHANGE_NAME, "topic", true);
            channel.queueDeclare(QUEUE_NAME, true, false, false, null);
            channel.queueBind(QUEUE_NAME, EXCHANGE_NAME, ROUTING_KEY);
            
            System.out.println("Consumidor Team conectado! Fila: " + QUEUE_NAME);
            
            // Configurar QoS para processar uma mensagem por vez
            channel.basicQos(1);
            
            System.out.println("=== CONSUMIDOR TEAM INICIADO - Aguardando mensagens de identificação de times ===");
            
            // Criar consumer
            DeliverCallback deliverCallback = (consumerTag, delivery) -> {
                try {
                    processarMensagem(delivery.getBody());
                    channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
                } catch (Exception e) {
                    logger.error("Erro ao processar mensagem", e);
                    channel.basicNack(delivery.getEnvelope().getDeliveryTag(), false, true);
                }
            };
            
            CancelCallback cancelCallback = consumerTag -> {
                logger.warn("Consumer foi cancelado: {}", consumerTag);
            };
            
            // Iniciar consumo
            channel.basicConsume(QUEUE_NAME, false, deliverCallback, cancelCallback);
            
            // Manter o programa rodando
            logger.info("Pressione CTRL+C para parar o consumidor");
            Thread.currentThread().join();
            
        } catch (IOException | TimeoutException | InterruptedException e) {
            logger.error("Erro fatal no consumidor team", e);
        }
    }
    
    private void processarMensagem(byte[] messageBody) {
        try {
            // Deserializar mensagem
            MensagemImagem mensagem = objectMapper.readValue(messageBody, MensagemImagem.class);
            
            System.out.println("[TEAM] Processando imagem de brasão: " + mensagem.getId());
            
            // Identificar time usando Smile ML
            IdentificadorTimeSmile.ResultadoIdentificacao resultado = identificador.identificarTime(
                mensagem.getDados(), mensagem.getId());
            
            System.out.println("[TEAM] Resultado da identificação: " + resultado);
            
            // Aqui você poderia salvar o resultado em um banco de dados,
            // enviar para outro serviço, etc.
            
        } catch (Exception e) {
            logger.error("Erro ao processar mensagem", e);
            throw new RuntimeException(e);
        }
    }
    
    public static void main(String[] args) {
        System.out.println("=== INICIANDO CONSUMIDOR TEAM ===");
        
        // Aguardar RabbitMQ estar pronto
        try {
            System.out.println("Aguardando RabbitMQ e gerador inicializarem...");
            Thread.sleep(15000); // 15 segundos
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        ConsumidorTeam consumidor = new ConsumidorTeam();
        consumidor.iniciarConsumo();
    }
}