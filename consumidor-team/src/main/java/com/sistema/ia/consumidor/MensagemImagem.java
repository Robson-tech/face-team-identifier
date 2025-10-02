package com.sistema.ia.consumidor;

import com.fasterxml.jackson.annotation.JsonProperty;

public class MensagemImagem {
    
    @JsonProperty("id")
    private String id;
    
    @JsonProperty("tipo")
    private String tipo;
    
    @JsonProperty("nomeArquivo")
    private String nomeArquivo;
    
    @JsonProperty("dados")
    private byte[] dados;
    
    @JsonProperty("timestamp")
    private long timestamp;
    
    public MensagemImagem() {}
    
    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }
    
    public String getNomeArquivo() { return nomeArquivo; }
    public void setNomeArquivo(String nomeArquivo) { this.nomeArquivo = nomeArquivo; }
    
    public byte[] getDados() { return dados; }
    public void setDados(byte[] dados) { this.dados = dados; }
    
    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
    
    @Override
    public String toString() {
        return String.format("MensagemImagem{id='%s', tipo='%s', arquivo='%s', timestamp=%d}", 
                           id, tipo, nomeArquivo, timestamp);
    }
}