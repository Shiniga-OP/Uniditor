package com.uniditor.nucleo.util;

import java.io.InputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import com.uniditor.nucleo.Util;

public class Arquivos {
	public static File copiarArquivoAssets(String caminho) {
		File arquivo = new File(Util.assets.obterCache(), caminho);
		InputStream is = Util.assets.obter(caminho);

		// se o arquivo ja existe, retorna ele
		if(arquivo.exists()) {
			return arquivo;
		}
		try {
			FileOutputStream fos = new FileOutputStream(arquivo);

			byte[] buffer = new byte[8192];
			int tam;
			while((tam = is.read(buffer)) > 0) {
				fos.write(buffer, 0, tam);
			}
			fos.close();
			is.close();

			return arquivo;
		} catch(IOException e) {
			e.printStackTrace();
			return null;
		}
	}
}
