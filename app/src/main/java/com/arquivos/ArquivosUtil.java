package com.arquivos;

import java.io.InputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class ArquivosUtil {
	public static File copiarArquivoAssets(InputStream is, File diretorio, String nome) {
		File arquivo = new File(diretorio, nome);

		// se o arquivo ja existe, retorna ele
		if(arquivo.exists()) {
			return arquivo;
		}
		try {
			FileOutputStream fos = new FileOutputStream(arquivo);

			byte[] buffer = new byte[8192];
			int length;
			while ((length = is.read(buffer)) > 0) {
				fos.write(buffer, 0, length);
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
