package br.com.hitic.service;

import java.io.IOException;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import br.com.hitic.enums.SeverityStatus;
import br.com.hitic.exception.CustomException;
import br.com.hitic.model.Archive;
import br.com.hitic.model.Operator;
import br.com.hitic.repository.ArchiveRepository;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Data
@Slf4j
@Service
public class ArchiveService {

	private final OCRService ocrService;

	private final ArchiveRepository archiveRepository;

	public String processArchives(MultipartFile[] archives, Operator operator) throws IOException, CustomException {

		validateOnlyPdf(archives);

		StringBuilder processedArchives = new StringBuilder();

		for (MultipartFile archive : archives) {

			String content = ocrService.sendToOcr(archive);

			Archive newArchive = new Archive();

			newArchive.setContent(content);
			newArchive.setOperator(operator);
			newArchive.setName(archive.getOriginalFilename());
			newArchive.setArchiveBytes(archive.getBytes());

			archiveRepository.save(newArchive);
			processedArchives.append(content);
		}

		return processedArchives.toString();
	}

	public void validateOnlyPdf(MultipartFile[] files) throws CustomException {

		for (MultipartFile file : files) {
			String contentType = file.getContentType();

			if (contentType == null || !contentType.equalsIgnoreCase("application/pdf")) {
				throw new CustomException("Apenas arquivos PDF são permitidos. Inválido: " + file.getOriginalFilename(),
						SeverityStatus.ERROR, HttpStatus.BAD_REQUEST);
			}
		}
	}
}
