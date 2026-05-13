package com.girellidev.ironwatchserver.security;

public class loginAttempt {
    public int tentativas;
    public long tempodesbloqueio;

    public int getTentativas() {
        return tentativas;
    }
    public void incrementar() {
        this.tentativas++;
    }
    public void resetar() {
        this.tentativas = 0;
        this.tempodesbloqueio = 0;
    }

    public boolean estaBloqueado() {
        return System.currentTimeMillis() < tempodesbloqueio;
    }

    public void bloquearPor(long millis) {
        this.tempodesbloqueio = System.currentTimeMillis() + millis;
    }

}
