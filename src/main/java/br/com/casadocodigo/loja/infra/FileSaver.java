package br.com.casadocodigo.loja.infra;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectRequest;

@Component
public class FileSaver {

//Implementando para gravar na AWS S3
	@Autowired
	private AmazonS3 amazonS3;
	
	private static final String REGION="us-east-1";
	/*
	 *Usei essa primeira chave para o backet criado na aula
	 *depois substitui pelo abaixo para o meu teste.
	 * private static final String BUCKET="casodocodifo-fabianoss";
	 */
		
	private static final String BUCKET="casadocodigo-fabianoss-2";

	public String write(MultipartFile file) {
		try {
			amazonS3.putObject(new PutObjectRequest(BUCKET, file.getOriginalFilename(), file.getInputStream(),null)
					.withCannedAcl(CannedAccessControlList.PublicRead));
						
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		return "https://s3."+REGION+".amazonaws.com/"+BUCKET+"/"+file.getOriginalFilename();
	}
	
	
	
	
//Implementação gravar imagem local no tomcat	
//	@Autowired
//	private HttpServletRequest request;
//
//	public String write(MultipartFile file) {
//		try {
//			File arquivo = new File(request.getServletContext().getRealPath(
//					"/resources/imagens"), file.getOriginalFilename());
//			file.transferTo(arquivo);
//			
//			return file.getOriginalFilename();
//			
//		} catch (IllegalStateException | IOException e) {
//			throw new RuntimeException(e);
//		}
//	}
	
}









