package br.com.constance.app.appkstore.backend.services.imagem;

import android.graphics.Bitmap;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;

import android.graphics.BitmapFactory;

import java.net.URL;

public class ImagemService {

    public byte[] buscaImagem(String urlImagem) {
        if (urlImagem == null || urlImagem.isEmpty()) {
            throw new IllegalArgumentException("A URL fornecida está nula ou vazia.");
        }

        Bitmap bitmap = null;
        try {
            URL url = new URL(urlImagem);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            bitmap = BitmapFactory.decodeStream(input);

            if (bitmap == null) {
                throw new IOException("Falha ao decodificar a imagem.");
            }

            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            return stream.toByteArray();
        } catch (IOException ex) {
            throw new RuntimeException("Erro ao buscar a imagem: " + ex.getMessage(), ex);
        }
    }
}
